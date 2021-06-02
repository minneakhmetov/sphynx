package com.razzzil.sphynx.coordinator.controller.rest;

import com.razzzil.sphynx.commons.model.iteration.IterationModel;
import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.commons.model.user.role.UserRole;
import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import com.razzzil.sphynx.coordinator.model.form.request.test.IterationConfigForm;
import com.razzzil.sphynx.coordinator.model.form.request.test.TestConfigurationForm;
import com.razzzil.sphynx.coordinator.model.form.request.test.TestIterationConfigurationForm;
import com.razzzil.sphynx.coordinator.model.form.request.test.TestIterationConfigurationUpdateForm;
import com.razzzil.sphynx.commons.model.wrapper.TestIterationModel;
import com.razzzil.sphynx.coordinator.model.jwt.JwtConfig;
import com.razzzil.sphynx.coordinator.service.TestIterationWebhookService;
import com.razzzil.sphynx.coordinator.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
@Secured({UserRole.Names.ADMIN, UserRole.Names.USER})
public class TestIterationController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TestIterationWebhookService testIterationWebhookService;

    @GetMapping
    public ResponseEntity<List<TestModel>> getAll(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        return ResponseEntity.ok(testIterationWebhookService.getAllTests(claims.getId()));
    }

    @GetMapping("/{id}/iterations")
    public ResponseEntity<List<IterationModel>> getIterationsById(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        return ResponseEntity.ok(testIterationWebhookService.getIterationsByTestAndUserId(id, claims.getId()));
    }

    @PostMapping
    public ResponseEntity<TestModel> createTest(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @RequestBody TestConfigurationForm testModel){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        return ResponseEntity.ok(testIterationWebhookService.createTest(testModel, claims.getId()));
    }

    @PostMapping("/{id}/iteration")
    public ResponseEntity<IterationModel> createIteration(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @RequestBody IterationConfigForm configs,
                                                          @PathVariable Integer id){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        return ResponseEntity.ok(testIterationWebhookService.createIteration(configs, claims.getId(), id));
    }

//    @PutMapping
//    public ResponseEntity<TestIterationModel> save(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token,
//                                                   @RequestBody TestIterationConfigurationForm testIterationConfigurationForm){
//        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
//        return ResponseEntity.ok(testIterationWebhookService.saveTestIterations(testIterationConfigurationForm, claims.getId()));
//    }

//    @PatchMapping
//    public ResponseEntity<TestIterationModel> update(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token,
//                                                     @RequestBody TestIterationConfigurationUpdateForm testIterationConfigurationForm){
//        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
//        return ResponseEntity.ok(testIterationWebhookService.updateTestIterations(testIterationConfigurationForm, claims.getId()));
//    }

    @PatchMapping("/{id}/iteration/{iterationId}")
    public ResponseEntity<IterationModel> update(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @RequestBody(required = false) IterationConfigForm configs,
                                                 @PathVariable Integer id, @PathVariable Integer iterationId){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        return ResponseEntity.ok(testIterationWebhookService.updateIteration(configs, claims.getId(), iterationId, id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TestModel> update(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @RequestBody TestConfigurationForm testModel, @PathVariable Integer id){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        return ResponseEntity.ok(testIterationWebhookService.updateTest(testModel, claims.getId(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTest(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        testIterationWebhookService.deleteTestById(id, claims.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/iteration/{iterationId}")
    public ResponseEntity<Object> deleteIteration(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id, @PathVariable Integer iterationId){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        testIterationWebhookService.deleteIterationById(id, iterationId, claims.getId());
        return ResponseEntity.ok().build();
    }

}
