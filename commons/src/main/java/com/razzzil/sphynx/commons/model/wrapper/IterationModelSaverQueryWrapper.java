package com.razzzil.sphynx.commons.model.wrapper;

import com.razzzil.sphynx.commons.model.iteration.IterationModel;
import com.razzzil.sphynx.commons.model.savedquery.SavedQueryModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IterationModelSaverQueryWrapper {
    private IterationModel iterationModel;
    private SavedQueryModel savedQueryModel;
}
