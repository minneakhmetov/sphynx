package com.razzzil.sphynx.coordinator.model.form.response.auth;


import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthSuccessForm {

    private String token;
    private UserPrincipalClaims claims;
}
