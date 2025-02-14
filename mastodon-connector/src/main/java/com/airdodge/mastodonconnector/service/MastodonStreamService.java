package com.airdodge.mastodonconnector.service;

import com.airdodge.mastodonconnector.client.MastodonClient;
import com.airdodge.mastodonconnector.domain.PostOffset;
import com.airdodge.mastodonconnector.dto.MastodonPost;
import com.airdodge.mastodonconnector.reporsitory.PostOffsetRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@RequiredArgsConstructor
@Service
@Slf4j
public class MastodonStreamService {

    private static final String DEFAULT_OFFSET = "1";

    private final MastodonClient mastodonClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PostOffsetRepository offsetRepository;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.posts-stream}")
    private String postsTopic;

    private static final int BATCH_SIZE = 100;
    private static final Duration POLL_INTERVAL = Duration.ofMillis(100);

    @Scheduled(fixedRate = 10000)
    public void streamPosts() {
        try {
            List<MastodonPost> posts = offsetRepository.findTopByOrderByCreatedAtDesc()
                    .map(offset -> mastodonClient.getPublicTimeline(offset.getId()))
                    .orElseGet(() -> mastodonClient.getPublicTimeline(DEFAULT_OFFSET));

            if (!posts.isEmpty()) {
                CompletableFuture<?>[] futures = posts.stream()
                        .map(post -> CompletableFuture.runAsync(() -> {
                            try {
                                String postJson = objectMapper.writeValueAsString(post);
                                kafkaTemplate.send(postsTopic, post.getId(), postJson);
                                offsetRepository.save(new PostOffset(post.getId()));
                            } catch (JsonProcessingException e) {
                                log.error("Failed to serialize MastodonPost", e);
                            }
                        }))
                        .toArray(CompletableFuture[]::new);

                CompletableFuture.allOf(futures).join();
                log.info("Published {} posts to Kafka", posts.size());
            }
        } catch (Exception e) {
            log.error("Error in post streaming", e);
            throw e;
        }
    }
}
