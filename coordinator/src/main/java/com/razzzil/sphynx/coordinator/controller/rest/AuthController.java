package com.razzzil.sphynx.coordinator.controller.rest;

import com.razzzil.sphynx.coordinator.model.form.request.auth.LoginForm;
import com.razzzil.sphynx.coordinator.model.form.response.auth.AuthSuccessForm;
import com.razzzil.sphynx.coordinator.service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthUserService authUserService;

    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<AuthSuccessForm> login(@RequestBody LoginForm loginForm) {
        AuthSuccessForm authSuccessForm = authUserService.login(loginForm);
        return ResponseEntity.ok(authSuccessForm);
    }


}
