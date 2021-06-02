package com.razzzil.sphynx.worker.model;

import com.razzzil.sphynx.commons.model.iteration.IterationModel;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.model.savedquery.SavedQueryModel;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class IterationWrapper<V extends IterationConfigs> {
    private IterationModel iterationModel;
    private SavedQueryModel savedQueryModel;
    private V iterationConfigs;

    public IterationWrapper(IterationModel iterationModel, SavedQueryModel savedQueryModel, DatabaseType databaseType) {
        this.iterationModel = iterationModel;
        this.savedQueryModel = savedQueryModel;
        this.iterationConfigs = iterationModel.deserialize(databaseType);
    }
}
