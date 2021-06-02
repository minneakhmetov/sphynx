package com.razzzil.sphynx.commons.model.savedquery;

import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RequestQueryForm {
    private DatabaseModel databaseModel;
    private SavedQueryModel savedQueryModel;
}
