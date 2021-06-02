package com.razzzil.sphynx.coordinator.controller.rest;

import com.razzzil.sphynx.commons.model.user.UserModel;
import com.razzzil.sphynx.commons.model.user.role.UserRole;
import com.razzzil.sphynx.coordinator.model.form.request.auth.UserForm;
import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import com.razzzil.sphynx.coordinator.model.jwt.JwtConfig;
import com.razzzil.sphynx.coordinator.service.AuthUserService;
import com.razzzil.sphynx.coordinator.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    @Secured({UserRole.Names.ADMIN, UserRole.Names.USER})
    public ResponseEntity<UserPrincipalClaims> getCurrentUsers(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token) {
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        return ResponseEntity.ok(claims);
    }

    @GetMapping("/all")
    @Secured({UserRole.Names.ADMIN, UserRole.Names.USER})
    public ResponseEntity<List<UserPrincipalClaims>> getUsers(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token) {
        List<UserPrincipalClaims> users = authUserService.getUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping
    @Secured(UserRole.Names.ADMIN)
    public ResponseEntity<UserPrincipalClaims> createUser(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @RequestBody UserForm userForm){
        UserModel userModel = authUserService.createUser(userForm);
        return ResponseEntity.ok(UserPrincipalClaims.fromUserModel(userModel));
    }

    @PatchMapping("/{id}")
    @Secured(UserRole.Names.ADMIN)
    public ResponseEntity<UserPrincipalClaims> updateUser(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token,
                                                @RequestBody UserForm userForm, @PathVariable Integer id){
        UserModel userModel = authUserService.updateUser(userForm, id);
        return ResponseEntity.ok(UserPrincipalClaims.fromUserModel(userModel));
    }

    @DeleteMapping("/id/{id}")
    @Secured(UserRole.Names.ADMIN)
    public ResponseEntity<Object> deleteUser(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id){
        authUserService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/id/{id}")
    @Secured({UserRole.Names.ADMIN, UserRole.Names.USER})
    public ResponseEntity<UserPrincipalClaims> getUserById(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id) {
        UserModel userModel = authUserService.getUserById(id);
        return ResponseEntity.ok(UserPrincipalClaims.fromUserModel(userModel));
    }

    @GetMapping("/login/{login}")
    @Secured({UserRole.Names.ADMIN, UserRole.Names.USER})
    public ResponseEntity<UserPrincipalClaims> getUserByLogin(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable String login) {
        UserModel userModel = authUserService.getUserByLogin(login);
        return ResponseEntity.ok(UserPrincipalClaims.fromUserModel(userModel));
    }

    @GetMapping("/email/{email}")
    @Secured({UserRole.Names.ADMIN, UserRole.Names.USER})
    public ResponseEntity<UserPrincipalClaims> getUserByEmail(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable String email) {
        UserModel userModel = authUserService.getUserByEmail(email);
        return ResponseEntity.ok(UserPrincipalClaims.fromUserModel(userModel));
    }

    @GetMapping("/loginOrEmail/{loginOrEmail}")
    @Secured({UserRole.Names.ADMIN, UserRole.Names.USER})
    public ResponseEntity<UserPrincipalClaims> getUserByLoginByEmail(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable String loginOrEmail) {
        UserModel userModel = authUserService.getUserByLoginOrEmail(loginOrEmail);
        return ResponseEntity.ok(UserPrincipalClaims.fromUserModel(userModel));
    }



}
