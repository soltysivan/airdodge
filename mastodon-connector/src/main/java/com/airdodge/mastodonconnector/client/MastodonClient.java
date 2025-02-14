package com.airdodge.mastodonconnector.client;

import com.airdodge.mastodonconnector.dto.MastodonPost;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "mastodon-api", url = "${mastodon.api.url}")
public interface MastodonClient {

    @CircuitBreaker(name = "mastodon-api", fallbackMethod = "fallbackForPosts")
    @GetMapping("/api/v1/timelines/public")
    List<MastodonPost> getPublicTimeline(@RequestParam(value = "since_id", required = false) String sinceId);

    default void fallbackForPosts(String sinceId, Exception e) {
        // Implement fallback logic (e.g., return cached posts)
    }

}
