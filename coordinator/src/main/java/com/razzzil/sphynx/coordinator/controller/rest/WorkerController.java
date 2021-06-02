package com.razzzil.sphynx.coordinator.controller.rest;

import com.razzzil.sphynx.commons.model.user.role.UserRole;
import com.razzzil.sphynx.commons.model.worker.WorkerModel;
import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import com.razzzil.sphynx.coordinator.model.form.request.worker.WorkerChangeVersionForm;
import com.razzzil.sphynx.coordinator.model.form.request.worker.WorkerDisplayForm;
import com.razzzil.sphynx.coordinator.model.form.request.worker.WorkerForm;
import com.razzzil.sphynx.coordinator.model.form.request.worker.WorkerRenameForm;
import com.razzzil.sphynx.coordinator.model.jwt.JwtConfig;
import com.razzzil.sphynx.coordinator.service.WorkerService;
import com.razzzil.sphynx.coordinator.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.util.List;

@RestController
@RequestMapping("/worker")
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @GetMapping
    @Secured({UserRole.Names.USER, UserRole.Names.ADMIN})
    public ResponseEntity<List<WorkerDisplayForm>> getAll(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token) {
        return ResponseEntity.ok(workerService.getDisplayWorkers());
    }

    @GetMapping("/{id}")
    @Secured({UserRole.Names.ADMIN})
    public ResponseEntity<WorkerDisplayForm> getById(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token, @PathVariable Integer id) {
        return ResponseEntity.ok(workerService.getDisplayWorkerById(id));
    }

//    @PatchMapping("/{id}/rename")
//    @Secured({UserRole.Names.ADMIN})
//    public ResponseEntity<WorkerRenameForm> rename(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token,
//                                                   @PathVariable Integer id, @RequestParam String alias) {
//        WorkerRenameForm workerRenameForm = WorkerRenameForm.builder()
//                .alias(alias)
//                .id(id)
//                .build();
//        return ResponseEntity.ok(workerService.renameWorker(workerRenameForm));
//    }
//
//    @PatchMapping("/{id}/changeVersion")
//    @Secured({UserRole.Names.ADMIN})
//    public ResponseEntity<WorkerChangeVersionForm> changeVersion(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token,
//                                                                 @PathVariable Integer id, @RequestParam String version) {
//        WorkerChangeVersionForm workerChangeVersionForm = WorkerChangeVersionForm.builder()
//                .version(version)
//                .id(id)
//                .build();
//        return ResponseEntity.ok(workerService.changeVersion(workerChangeVersionForm));
//    }

    @PatchMapping("/{id}")
    @Secured({UserRole.Names.ADMIN})
    public ResponseEntity<WorkerDisplayForm> changeVersion(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token,
                                                                 @RequestBody WorkerForm workerForm, @PathVariable Integer id) {
        return ResponseEntity.ok(workerService.update(workerForm, id));
    }

    @PostMapping("/{id}/resetKey")
    @Secured({UserRole.Names.ADMIN})
    public ResponseEntity<WorkerModel> resetKey(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token,
                                                @PathVariable Integer id) {
        return ResponseEntity.ok(workerService.resetKey(id));
    }

    @PutMapping
    @Secured({UserRole.Names.ADMIN})
    public ResponseEntity<WorkerModel> save(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token,
                                            @RequestBody WorkerForm workerForm) {
        return ResponseEntity.ok(workerService.save(workerForm));
    }

    @DeleteMapping("/{id}")
    @Secured({UserRole.Names.ADMIN})
    public ResponseEntity<Object> delete(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token,
                                         @PathVariable Integer id) {
        workerService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/versions")
    @Secured({UserRole.Names.USER, UserRole.Names.ADMIN})
    public ResponseEntity<List<String>> versions(@RequestHeader(JwtConfig.AUTHORIZATION_HEADER) String token) {
        return ResponseEntity.ok(workerService.versions());
    }
}
