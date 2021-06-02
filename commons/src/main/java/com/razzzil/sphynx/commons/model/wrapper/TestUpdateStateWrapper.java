package com.razzzil.sphynx.commons.model.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestUpdateStateWrapper {
    private Integer id;
    private Integer workerId;
}
