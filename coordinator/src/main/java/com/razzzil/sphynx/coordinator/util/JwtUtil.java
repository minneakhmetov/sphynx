package com.razzzil.sphynx.coordinator.util;

import com.razzzil.sphynx.commons.model.user.UserModel;
import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import com.razzzil.sphynx.coordinator.model.jwt.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Autowired
    private JwtConfig jwtConfig;

    private final Clock clock = DefaultClock.INSTANCE;

    public Claims getAllClaimsFromToken(final String token) {
        return Jwts.parser()
                .setSigningKey(this.jwtConfig.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserModel userModel) {
        final Date createdDate = this.clock.now();
        final Date expirationDate = new Date(createdDate.getTime() + this.jwtConfig.getExpiration());
        final Map<String, Object> claims = UserPrincipalClaims.fromUserModel(userModel).toMap();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userModel.getLogin())
                .setIssuedAt(createdDate)
                .setId(String.valueOf(userModel.getId()))
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, this.jwtConfig.getSecret())
                .compact();
    }

    public Optional<Claims> validateToken(final String token) {
        try {
            if (Objects.nonNull(token)) {
                Claims claims = this.getAllClaimsFromToken(token);
                return !isTokenExpired(claims) ? Optional.of(claims) : Optional.empty();
            } return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean isTokenExpired(Claims claims){
        return claims.getExpiration().before(Date.from(Instant.now()));
    }

    public String getLoginByClaims(Claims claims){
        return claims.getSubject();
    }

    public UserPrincipalClaims getPrincipalByClaims(Claims claims) {
        return UserPrincipalClaims.fromMap(claims);
    }

    public <T> T getByClaims(Claims claims, final Function<UserPrincipalClaims, T> claimsResolver){
        UserPrincipalClaims principalClaims = UserPrincipalClaims.fromMap(claims);
        return claimsResolver.apply(principalClaims);
    }

    public UserPrincipalClaims getPrincipalClaimsByToken(String token) {
        return getPrincipalByClaims(getAllClaimsFromToken(token));
    }


}
