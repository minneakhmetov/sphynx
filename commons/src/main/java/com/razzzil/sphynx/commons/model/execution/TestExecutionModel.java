package com.razzzil.sphynx.commons.model.execution;

import com.razzzil.sphynx.commons.model.test.state.TestState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestExecutionModel {
    private Integer id;
    private Integer testConfigsId;
    private TestState state;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer userId;
    private Integer process;
    private String message;

}
