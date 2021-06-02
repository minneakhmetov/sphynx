package com.razzzil.sphynx.commons.model.test.configs;

import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.validation.Validated;

public abstract class TestConfigs implements Validated {

    public abstract DatabaseType databaseType();


}
