package com.airdodge.mastodonconnector.service;

import com.airdodge.mastodonconnector.client.MastodonClient;
import com.airdodge.mastodonconnector.domain.PostOffset;
import com.airdodge.mastodonconnector.dto.MastodonPost;
import com.airdodge.mastodonconnector.reporsitory.PostOffsetRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MastodonStreamServiceTest {

    private static final String DEFAULT_OFFSET = "1";

    @Mock
    private MastodonClient mastodonClient;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private PostOffsetRepository offsetRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MastodonStreamService mastodonStreamService;

    @Value("${kafka.topic.posts-stream}")
    private String postsTopic;

    @Test
    void streamPosts_shouldPublishPostsToKafka() throws JsonProcessingException {
        MastodonPost post = new MastodonPost("1", "Post Content", Instant.now());
        List<MastodonPost> posts = Collections.singletonList(post);

        PostOffset postOffset = new PostOffset("1");

        when(offsetRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.empty());
        when(mastodonClient.getPublicTimeline(DEFAULT_OFFSET)).thenReturn(posts);
        when(objectMapper.writeValueAsString(post)).thenReturn("{\"id\":\"1\",\"content\":\"Post Content\",\"created_at\":\"2025-02-14T10:30:19.000Z\"}");

        mastodonStreamService.streamPosts();

        verify(kafkaTemplate).send(eq(postsTopic), eq(post.getId()), anyString());
        verify(offsetRepository).save(any(PostOffset.class));
    }

    @Test
    void streamPosts_shouldHandleEmptyPosts() {
        when(offsetRepository.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.empty());
        when(mastodonClient.getPublicTimeline(DEFAULT_OFFSET)).thenReturn(Collections.emptyList());

        mastodonStreamService.streamPosts();

        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void streamPosts_shouldHandleException() {
        when(mastodonClient.getPublicTimeline(DEFAULT_OFFSET)).thenThrow(new RuntimeException("Test Exception"));

        assertThrows(RuntimeException.class, () -> mastodonStreamService.streamPosts());
    }
}
