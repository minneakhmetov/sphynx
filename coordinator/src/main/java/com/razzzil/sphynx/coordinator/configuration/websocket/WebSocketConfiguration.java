package com.razzzil.sphynx.coordinator.configuration.websocket;

import com.razzzil.sphynx.coordinator.handler.AssignPrincipalHandshakeHandler;
import com.razzzil.sphynx.coordinator.handler.AuthHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private AuthHandshakeInterceptor handshakeInterceptor;

    @Value("${sphynx.allowedOrigin}")
    private String allowedOrigin;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(handshakeInterceptor)
                .setHandshakeHandler(new AssignPrincipalHandshakeHandler())
                .setAllowedOrigins(allowedOrigin)
                .withSockJS();
    }

}
