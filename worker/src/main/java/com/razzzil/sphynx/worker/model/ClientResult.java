package com.razzzil.sphynx.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ClientResult {
    private Boolean success;
    private Throwable error;
    private ResultSet result;
    private Boolean returnable;
    private long timeStart;
    private long timeEnd;
    private long timeStartWithoutConnection;
    private long timeEndWithoutConnection;
}
