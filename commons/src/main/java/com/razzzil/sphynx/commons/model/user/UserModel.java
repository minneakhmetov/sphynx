package com.razzzil.sphynx.commons.model.user;

import com.razzzil.sphynx.commons.model.user.role.UserRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserModel {
    private Integer id;
    private String email;
    private String login;
    private String hashedPassword;
    private UserRole role;

}
