package com.razzzil.sphynx.coordinator.model.form.request.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginForm {
    private String loginOrEmail;
    private String password;
}
