package com.razzzil.sphynx.coordinator.model.form.request.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.razzzil.sphynx.commons.model.user.UserModel;
import com.razzzil.sphynx.commons.model.user.role.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;
import java.util.Map;

import static com.razzzil.sphynx.coordinator.constants.StaticConstants.OBJECT_MAPPER;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserPrincipalClaims implements Principal {

    private Integer id;
    private String login;
    private String email;
    private UserRole role;

    public Map<String, Object> toMap(){
        return OBJECT_MAPPER.convertValue(this, new TypeReference<Map<String, Object>>() {});
    }

    public static UserPrincipalClaims fromMap(Map<String, Object> map){
        return OBJECT_MAPPER.convertValue(map, UserPrincipalClaims.class);
    }

    public static UserPrincipalClaims fromUserModel(UserModel userModel){
        return UserPrincipalClaims.builder()
                .email(userModel.getEmail())
                .id(userModel.getId())
                .login(userModel.getLogin())
                .role(userModel.getRole())
                .build();
    }

    @Override
    public String getName() {
        return login;
    }
}
