package com.razzzil.sphynx.coordinator.controller.advice;

import com.razzzil.sphynx.coordinator.model.form.response.exception.ExceptionPayload;
import com.razzzil.sphynx.coordinator.model.form.response.exception.TokenIsExpiredExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.razzzil.sphynx.coordinator.constants.StaticConstants.OBJECT_MAPPER;

public class TokenExpiredHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exc) throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(OBJECT_MAPPER.writeValueAsString(ExceptionPayload.singletonError(new TokenIsExpiredExceptionResponse())));
        out.flush();

    }
}
