package com.razzzil.sphynx.coordinator.security.filter;

import com.razzzil.sphynx.coordinator.exception.EntityNotFoundException;
import com.razzzil.sphynx.coordinator.exception.WrongCredentialsException;
import com.razzzil.sphynx.coordinator.model.form.response.exception.WrongCredentialsExceptionResponse;
import com.razzzil.sphynx.coordinator.model.jwt.JwtConfig;
import com.razzzil.sphynx.coordinator.security.details.UserDetailsImpl;
import com.razzzil.sphynx.coordinator.security.details.UserDetailsServiceImpl;
import com.razzzil.sphynx.coordinator.service.AuthUserService;
import com.razzzil.sphynx.coordinator.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

@Component
@Log
public class JwtFilter extends GenericFilterBean {

    @Autowired
    private JwtConfig config;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = ((HttpServletRequest) servletRequest).getHeader(JwtConfig.AUTHORIZATION_HEADER);
        Optional<Claims> claimsCandidate = jwtUtil.validateToken(token);
        claimsCandidate.ifPresent(claims -> {
            String login = jwtUtil.getLoginByClaims(claims);
            try {
                UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(login);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (EntityNotFoundException e){
                throw new WrongCredentialsException(new WrongCredentialsExceptionResponse(AuthUserService.ENTITY_NAME + " [" + login + "] is not correct"));
            }
        });
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
