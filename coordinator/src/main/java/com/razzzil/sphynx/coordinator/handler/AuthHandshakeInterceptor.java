package com.razzzil.sphynx.coordinator.handler;

import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import com.razzzil.sphynx.coordinator.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        try {
            MultiValueMap<String, String> parameters = UriComponentsBuilder
                    .fromUri(serverHttpRequest.getURI())
                    .build()
                    .getQueryParams();
            UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(parameters.get("token").get(0));
            map.put("claims", claims);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException expiredJwtException) {
            serverHttpResponse.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
