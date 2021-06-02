package com.razzzil.sphynx.coordinator.controller.rest;

import com.razzzil.sphynx.commons.model.execution.TestExecutionModel;
import com.razzzil.sphynx.commons.model.metrics.MetricsModel;
import com.razzzil.sphynx.commons.model.user.role.UserRole;
import com.razzzil.sphynx.commons.model.wrapper.TestUpdateStateWrapper;
import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import com.razzzil.sphynx.coordinator.model.jwt.JwtConfig;
import com.razzzil.sphynx.coordinator.service.ExecutionService;
import com.razzzil.sphynx.coordinator.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/execution")
@Secured({UserRole.Names.ADMIN, UserRole.Names.USER})
public class ExecutionController {

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/start/test/{id}")
    public ResponseEntity<TestExecutionModel> start(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        TestExecutionModel testExecutionModel = executionService.startTest(id, claims.getId());
        return ResponseEntity.ok(testExecutionModel);
    }

    @PostMapping("/terminate/{id}")
    public ResponseEntity<TestUpdateStateWrapper> terminate(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        TestUpdateStateWrapper testExecutionModel = executionService.terminate(id, claims.getId());
        return ResponseEntity.ok(testExecutionModel);
    }

    @PostMapping("/pause/{id}")
    public ResponseEntity<TestUpdateStateWrapper> pause(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        TestUpdateStateWrapper testExecutionModel = executionService.pause(id,  claims.getId());
        return ResponseEntity.ok(testExecutionModel);
    }

    @PostMapping("/resume/{id}")
    public ResponseEntity<TestUpdateStateWrapper> resume(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        TestUpdateStateWrapper testExecutionModel = executionService.resume(id, claims.getId());
        return ResponseEntity.ok(testExecutionModel);
    }

    @GetMapping("/test/{id}")
    public ResponseEntity<List<TestExecutionModel>> getAll(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id ){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        List<TestExecutionModel> testExecutionModels = executionService.getAllExecutionsByTestIdAndUserId(id, claims.getId());
        return ResponseEntity.ok(testExecutionModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestExecutionModel> getById(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id ){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        TestExecutionModel testExecutionModel = executionService.getTestExecutionModel(id, claims.getId());
        return ResponseEntity.ok(testExecutionModel);
    }

    @GetMapping("/{id}/metrics")
    public ResponseEntity<List<MetricsModel>> getMetricsByExecution(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id ){
        UserPrincipalClaims claims = jwtUtil.getPrincipalClaimsByToken(token);
        List<MetricsModel> metricsModels = executionService.getMetricsByExecutionIdAndUserId(id, claims.getId());
        return ResponseEntity.ok(metricsModels);
    }
}
