package com.razzzil.sphynx.coordinator.repository;

import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.coordinator.jooq.tables.DatabaseTable;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.ONE;

@Repository
public class DatabaseRepository {

    private static final DatabaseTable DATABASE_TABLE = DatabaseTable.DATABASE_TABLE;

    @Autowired
    private DSLContext dsl;

    public List<DatabaseModel> getAll() {
        return this.dsl
                .select()
                .from(DATABASE_TABLE)
                .fetchInto(DatabaseModel.class);
    }

    public List<DatabaseModel> getAllByWorkerId(Integer workerId) {
        return this.dsl
                .select()
                .from(DATABASE_TABLE)
                .where(DATABASE_TABLE.WORKER_ID.eq(workerId))
                .fetchInto(DatabaseModel.class);
    }

    public List<DatabaseModel> getAllByUser(Integer userId) {
        return this.dsl
                .select()
                .from(DATABASE_TABLE)
                .where(DATABASE_TABLE.USER_ID.eq(userId))
                .fetchInto(DatabaseModel.class);
    }

    public Optional<DatabaseModel> getByIdAndUserId(Integer id, Integer userId) {
        return this.dsl
                .select()
                .from(DATABASE_TABLE)
                .where(DATABASE_TABLE.ID.eq(id).and(DATABASE_TABLE.USER_ID.eq(userId)))
                .fetchOptionalInto(DatabaseModel.class);
    }

    public Optional<DatabaseModel> getById(Integer id) {
        return this.dsl
                .select()
                .from(DATABASE_TABLE)
                .where(DATABASE_TABLE.ID.eq(id))
                .fetchOptionalInto(DatabaseModel.class);
    }

    public boolean update(DatabaseModel databaseModel) {
        return this.dsl
                .update(DATABASE_TABLE)
                .set(DATABASE_TABLE.USER_ID, databaseModel.getUserId())
                .set(DATABASE_TABLE.TYPE, databaseModel.getType().name())
                .set(DATABASE_TABLE.CONFIGS, databaseModel.getConfigs())
                .set(DATABASE_TABLE.ALIAS, databaseModel.getAlias())
                .set(DATABASE_TABLE.WORKER_ID, databaseModel.getWorkerId())
                .set(DATABASE_TABLE.CONNECTION_STATE, databaseModel.getConnectionState().name())
                .where(DATABASE_TABLE.ID.eq(databaseModel.getId()))
                .execute() == ONE;
    }

    public boolean delete(Integer id) {
        return dsl.delete(DATABASE_TABLE)
                .where(DATABASE_TABLE.ID.eq(id))
                .execute() == ONE;
    }

    public boolean deleteByUser(Integer id, Integer userId) {
        return dsl.delete(DATABASE_TABLE)
                .where(DATABASE_TABLE.ID.eq(id).and(DATABASE_TABLE.USER_ID.eq(userId)))
                .execute() == ONE;
    }

    public DatabaseModel save(DatabaseModel databaseModel) {
        return dsl.insertInto(DATABASE_TABLE)
                .set(DATABASE_TABLE.USER_ID, databaseModel.getUserId())
                .set(DATABASE_TABLE.TYPE, databaseModel.getType().name())
                .set(DATABASE_TABLE.CONFIGS, databaseModel.getConfigs())
                .set(DATABASE_TABLE.ALIAS, databaseModel.getAlias())
                .set(DATABASE_TABLE.WORKER_ID, databaseModel.getWorkerId())
                .set(DATABASE_TABLE.CONNECTION_STATE, databaseModel.getConnectionState().name())
                .returning(DATABASE_TABLE.ID, DATABASE_TABLE.USER_ID, DATABASE_TABLE.TYPE, DATABASE_TABLE.CONFIGS, DATABASE_TABLE.ALIAS, DATABASE_TABLE.WORKER_ID, DATABASE_TABLE.CONNECTION_STATE)
                .fetchOne()
                .map(r -> r.into(DATABASE_TABLE).into(DatabaseModel.class));
    }

}
