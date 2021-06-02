package com.razzzil.sphynx.coordinator.model.form.websocket;

import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.messaging.simp.user.SimpUser;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserPrincipalClaimsSubs {
    private UserPrincipalClaims userPrincipalClaims;
    private Set<String> subscriptions;
    private SimpUser simpUser;

}
