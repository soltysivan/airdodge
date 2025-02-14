package com.airdodge.postgateway.service;

import com.airdodge.postgateway.dto.MastodonPost;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostWebSocketHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebSocketSession webSocketSession;

    @InjectMocks
    private PostWebSocketHandler postWebSocketHandler;

    @Test
    void handlePost_shouldBroadcastValidPost() throws IOException {
        String postJson = "{\"id\":\"1\",\"content\":\"Post Content\"}";
        MastodonPost mastodonPost = new MastodonPost("1", "Post Content", Instant.now());

        WebSocketSession webSocketSession = mock(WebSocketSession.class);
        when(objectMapper.readValue(postJson, MastodonPost.class)).thenReturn(mastodonPost);
        when(webSocketSession.isOpen()).thenReturn(true);

        postWebSocketHandler.afterConnectionEstablished(webSocketSession);
        postWebSocketHandler.handlePost(postJson);

        verify(webSocketSession).sendMessage(any(TextMessage.class));
    }

    @Test
    void handlePost_shouldNotBroadcastInvalidPost() throws IOException {
        String invalidPostJson = "{\"id\":\"1\",\"content\":\"Invalid Content\"}";
        MastodonPost mastodonPost = new MastodonPost(null, "Invalid Content",  Instant.now());

        when(objectMapper.readValue(invalidPostJson, MastodonPost.class)).thenReturn(mastodonPost);

        postWebSocketHandler.handlePost(invalidPostJson);

        verify(webSocketSession, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    void afterConnectionEstablished_shouldAddSession() {
        when(webSocketSession.getId()).thenReturn("session1");

        postWebSocketHandler.afterConnectionEstablished(webSocketSession);

        assertTrue(postWebSocketHandler.getSessions().contains(webSocketSession));
    }

    @Test
    void afterConnectionClosed_shouldRemoveSession() {
        when(webSocketSession.getId()).thenReturn("session1");
        postWebSocketHandler.afterConnectionEstablished(webSocketSession);

        postWebSocketHandler.afterConnectionClosed(webSocketSession, CloseStatus.NORMAL);

        assertFalse(postWebSocketHandler.getSessions().contains(webSocketSession));
    }
}
