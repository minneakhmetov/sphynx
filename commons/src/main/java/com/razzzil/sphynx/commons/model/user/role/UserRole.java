package com.razzzil.sphynx.commons.model.user.role;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole {
    ROLE_USER(Names.USER),
    ROLE_ADMIN(Names.ADMIN);

    private String label;

    public static class Names {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }

}
