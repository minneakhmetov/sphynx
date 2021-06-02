package com.razzzil.sphynx.commons.model.wrapper;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestUserUpdateStateWrapper extends TestUpdateStateWrapper {
    private Integer userId;
    private String message;
}
