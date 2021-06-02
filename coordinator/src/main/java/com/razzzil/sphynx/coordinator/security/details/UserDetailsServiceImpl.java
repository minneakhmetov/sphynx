package com.razzzil.sphynx.coordinator.security.details;

import com.razzzil.sphynx.coordinator.exception.EntityNotFoundException;
import com.razzzil.sphynx.coordinator.service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AuthUserService authUserService;

    @Override
    public UserDetailsImpl loadUserByUsername(final String loginOrEmail) throws EntityNotFoundException {
        return new UserDetailsImpl(authUserService.getUserByLoginOrEmail(loginOrEmail));
    }

}
