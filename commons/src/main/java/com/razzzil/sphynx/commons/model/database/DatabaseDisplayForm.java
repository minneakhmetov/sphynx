package com.razzzil.sphynx.commons.model.database;

import com.razzzil.sphynx.commons.model.worker.connectionstate.ConnectionState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DatabaseDisplayForm {
    private Integer id;
    private Integer userId;
    private ConnectionState connectionState;
}
