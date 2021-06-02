package com.razzzil.sphynx.coordinator.model.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mariuszgromada.math.mxparser.Expression;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JwtConfig {
    public static final String AUTHORIZATION_HEADER = "Token";

    private int expiration;
    private String secret;

    public void setExpiration(String mathExpression) {
        Expression e = new Expression(mathExpression);
        this.expiration = (int) Math.round(e.calculate());
    }
}
