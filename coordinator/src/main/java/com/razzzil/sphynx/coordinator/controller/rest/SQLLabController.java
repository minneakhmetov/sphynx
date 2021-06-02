package com.razzzil.sphynx.coordinator.controller.rest;

import com.razzzil.sphynx.commons.model.savedquery.SavedQueryModel;
import com.razzzil.sphynx.commons.model.user.role.UserRole;
import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import com.razzzil.sphynx.coordinator.model.form.request.query.SQLLabQueryForm;
import com.razzzil.sphynx.coordinator.model.jwt.JwtConfig;
import com.razzzil.sphynx.coordinator.service.SQLLabService;
import com.razzzil.sphynx.coordinator.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sqllab")
@Secured({UserRole.Names.ADMIN, UserRole.Names.USER})
public class SQLLabController {

    @Autowired
    private SQLLabService sqlLabService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/query")
    public ResponseEntity<SavedQueryModel> query(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @RequestBody SQLLabQueryForm body){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        return ResponseEntity.ok(sqlLabService.query(body, claims.getId()));
    }

    @PostMapping("/save/{id}")
    public ResponseEntity<Object> query(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id, @RequestParam("name") String name){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        sqlLabService.saveQuery(id, claims.getId(), name);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<SavedQueryModel>> getAll(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        return ResponseEntity.ok(sqlLabService.getSavedQueries(claims.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavedQueryModel> getDetailed(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        return ResponseEntity.ok(sqlLabService.getDetailed(id, claims.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        sqlLabService.deleteById(id, claims.getId());
        return ResponseEntity.ok().build();
    }


}
