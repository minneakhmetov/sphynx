package com.razzzil.sphynx.coordinator.handler;

import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import com.razzzil.sphynx.coordinator.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class AssignPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected UserPrincipalClaims determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        return (UserPrincipalClaims) attributes.get("claims");
    }

}
