package com.razzzil.sphynx.coordinator.model.form.request.worker;

import com.razzzil.sphynx.commons.model.worker.connectionstate.ConnectionState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkerDisplayForm {
    private Integer id;
    private String alias;
    private String version;
    private ConnectionState connectionState;

}
