package com.supervision.livraisons.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.supervision.livraisons.websocket.EmergencyWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final EmergencyWebSocketHandler emergencyWebSocketHandler;

    public WebSocketConfig(EmergencyWebSocketHandler emergencyWebSocketHandler) {
        this.emergencyWebSocketHandler = emergencyWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(emergencyWebSocketHandler, "/ws/emergency")
                .setAllowedOrigins("*");
    }
}
