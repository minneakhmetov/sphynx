package com.razzzil.sphynx.commons.model.wrapper;

import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.execution.TestExecutionModel;
import com.razzzil.sphynx.commons.model.savedquery.SavedQueryModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestStartWrapper {
    private TestExecutionModel testExecutionModel;
    private DatabaseModel databaseModel;
    private TestIterationModel testIterationModel;
}
