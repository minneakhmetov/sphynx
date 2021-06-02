package com.razzzil.sphynx.commons.model.worker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkerModel {
    private Integer id;
    private String alias;
    private String key;
    private String version;
    private String token;

}
