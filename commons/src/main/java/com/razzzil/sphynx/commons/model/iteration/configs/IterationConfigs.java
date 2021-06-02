package com.razzzil.sphynx.commons.model.iteration.configs;

import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.validation.Validated;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public abstract class IterationConfigs implements Validated {

    public abstract DatabaseType databaseType();

}
