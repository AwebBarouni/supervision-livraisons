package com.supervision.livraisons.websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supervision.livraisons.model.Message;

@Component
public class EmergencyWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(EmergencyWebSocketHandler.class);

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        if (session.getPrincipal() == null) {
            closeQuietly(session);
            return;
        }
        String userId = session.getPrincipal().getName();
        sessions.put(userId, session);
        log.info("Driver {} connected to emergency WebSocket", userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (session.getPrincipal() != null) {
            sessions.remove(session.getPrincipal().getName());
            log.info("Driver {} disconnected from emergency WebSocket", session.getPrincipal().getName());
        }
    }

    public void pushEmergencyMessage(Message message) {
        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize emergency message", e);
            return;
        }

        if (message.getReceiverId() == null) {
            sessions.values().forEach(s -> sendText(s, json));
        } else {
            WebSocketSession target = sessions.get(message.getReceiverId());
            if (target != null) {
                sendText(target, json);
            }
        }
    }

    private void sendText(WebSocketSession session, String text) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(text));
            }
        } catch (IOException e) {
            log.warn("Failed to push emergency message to driver", e);
        }
    }

    private void closeQuietly(WebSocketSession session) {
        try {
            session.close(CloseStatus.NOT_ACCEPTABLE);
        } catch (IOException ignored) {
        }
    }
}
