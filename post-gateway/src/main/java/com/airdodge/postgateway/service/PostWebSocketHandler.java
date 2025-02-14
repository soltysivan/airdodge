package com.airdodge.postgateway.service;

import com.airdodge.postgateway.dto.MastodonPost;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostWebSocketHandler extends TextWebSocketHandler {

    @Getter
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper;
    private final AtomicInteger connectedUsers = new AtomicInteger(0);

    private static final int MAX_CONCURRENT_USERS = 1_000_000;

    @KafkaListener(
            topics = "${kafka.topic.posts-stream}",
            groupId = "${kafka.consumer.group-id}",
            concurrency = "10"
    )
    public void handlePost(String post) throws JsonProcessingException {
        MastodonPost mastodonPost = objectMapper.readValue(post, MastodonPost.class);
        if (!mastodonPost.isValid()) {
            log.warn("Received invalid post: {}", mastodonPost.getId());
            return;
        }

        broadcastMessage(post);
    }

    private void broadcastMessage(String message) {
        TextMessage textMessage = new TextMessage(message);

        sessions.parallelStream()
                .filter(WebSocketSession::isOpen)
                .forEach(session -> {
                    try {
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        log.error("Failed to send message to session {}", session.getId(), e);
                        closeSession(session);
                    }
                });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        if (connectedUsers.get() >= MAX_CONCURRENT_USERS) {
            closeSession(session);
            return;
        }

        sessions.add(session);
        connectedUsers.incrementAndGet();
        log.info("New connection: {}. Total users: {}", session.getId(), connectedUsers.get());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        connectedUsers.decrementAndGet();
        log.info("Connection closed: {}. Total users: {}", session.getId(), connectedUsers.get());
    }

    private void closeSession(WebSocketSession session) {
        try {
            session.close(CloseStatus.SERVICE_OVERLOAD);
        } catch (IOException e) {
            log.error("Error closing session", e);
        }
    }

}
