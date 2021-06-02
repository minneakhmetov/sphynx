package com.razzzil.sphynx.commons.model.worker;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkerConfigurationModel {
    private Integer id;
    private String token;
    private String version;

    public boolean isEqualToWorkerModel(WorkerModel workerModel){
        return workerModel.getId().equals(id) && workerModel.getToken().equals(token) && workerModel.getVersion().equals(version);
    }

    public static WorkerConfigurationModel fromWorkerModel(WorkerModel workerModel){
        return WorkerConfigurationModel.builder()
                .id(workerModel.getId())
                .token(workerModel.getToken())
                .version(workerModel.getVersion())
                .build();
    }
}
