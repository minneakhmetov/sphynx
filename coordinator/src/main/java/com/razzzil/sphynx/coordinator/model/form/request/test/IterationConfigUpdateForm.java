package com.razzzil.sphynx.coordinator.model.form.request.test;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class IterationConfigUpdateForm extends IterationConfigForm {
    private Integer id;
}
