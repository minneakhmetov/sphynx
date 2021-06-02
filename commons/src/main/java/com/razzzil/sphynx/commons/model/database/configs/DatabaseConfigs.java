package com.razzzil.sphynx.commons.model.database.configs;

import com.razzzil.sphynx.commons.validation.Validated;
import com.razzzil.sphynx.commons.model.type.DatabaseType;

import javax.sql.DataSource;

public abstract class DatabaseConfigs<T extends DataSource> implements Validated {

    public abstract T datasource();

    public abstract DatabaseType type();

}
