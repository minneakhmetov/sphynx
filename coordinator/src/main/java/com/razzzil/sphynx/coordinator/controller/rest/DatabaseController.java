package com.razzzil.sphynx.coordinator.controller.rest;

import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.model.user.role.UserRole;
import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import com.razzzil.sphynx.coordinator.model.jwt.JwtConfig;
import com.razzzil.sphynx.coordinator.service.DatabaseService;
import com.razzzil.sphynx.coordinator.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database")
@Secured({UserRole.Names.ADMIN, UserRole.Names.USER})
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private JwtUtil jwtUtil;

    @PutMapping("/{databaseType}")
    public ResponseEntity<DatabaseModel> createDatabase(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable DatabaseType databaseType,
                                                        @RequestBody String configs, @RequestParam Integer workerId, @RequestParam String alias) {
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        DatabaseModel model = databaseService.createDatabase(claims.getId(), databaseType, configs, workerId, alias);
        return ResponseEntity.ok(model);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DatabaseModel> updateUser(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token,
                                                    @RequestBody(required = false) String configs, @PathVariable Integer id,
                                                    @RequestParam(required = false) Integer workerId, @RequestParam(required = false) String alias) {
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        DatabaseModel model = databaseService.updateDatabase(claims.getId(), configs, id, workerId, alias);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DatabaseModel> deleteUser(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id) {
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        databaseService.delete(claims.getId(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatabaseModel> getDatabaseById(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id) {
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        DatabaseModel model = databaseService.getDatabaseByIdAndUser(claims.getId(), id);
        return ResponseEntity.ok(model);
    }

    @PostMapping("/health")
    public ResponseEntity<Object> health(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        databaseService.requestCheckDatabases(claims.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<DatabaseModel>> getDatabaseById(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token) {
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        List<DatabaseModel> models = databaseService.getUsersDatabases(claims.getId());
        return ResponseEntity.ok(models);
    }


}
