package com.razzzil.sphynx.commons.model.database.wrapper;

import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.database.configs.DatabaseConfigs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DatabaseWrapper<T extends DatabaseConfigs<V>, V extends DataSource> {
    private DatabaseModel databaseModel;
    private T databaseConfigs;
    private V datasource;
}
