package com.razzzil.sphynx.commons.model.savedquery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SavedQueryModel {
    private Integer id;
    private Integer databaseId;
    private Integer userId;
    private String sql;
    private String result;
    private String message;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private SQLLabState state;
    private String name;
}
