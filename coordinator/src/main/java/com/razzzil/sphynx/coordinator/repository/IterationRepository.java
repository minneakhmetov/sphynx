package com.razzzil.sphynx.coordinator.repository;

import com.razzzil.sphynx.commons.model.iteration.IterationModel;
import com.razzzil.sphynx.coordinator.jooq.tables.IterationTable;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.ONE;
import static org.jooq.impl.DSL.count;

@Repository
public class IterationRepository {

    private static final IterationTable ITERATION_TABLE = IterationTable.ITERATION_TABLE;

    @Autowired
    private DSLContext dsl;

    public List<IterationModel> getAll() {
        return this.dsl
                .select()
                .from(ITERATION_TABLE)
                .fetchInto(IterationModel.class);
    }

    public List<IterationModel> getAllByTestConfigsId(Integer testConfigsId) {
        return this.dsl
                .select()
                .from(ITERATION_TABLE)
                .where(ITERATION_TABLE.TEST_CONFIGS_ID.eq(testConfigsId))
                .fetchInto(IterationModel.class);
    }

    public List<IterationModel> getAllByTestConfigsIdAndUserId(Integer testConfigsId, Integer userId) {
        return this.dsl
                .select()
                .from(ITERATION_TABLE)
                .where(ITERATION_TABLE.TEST_CONFIGS_ID.eq(testConfigsId).and(ITERATION_TABLE.USER_ID.eq(userId)))
                .fetchInto(IterationModel.class);
    }

    public Optional<IterationModel> getTestByIdAndTestConfigsId(Integer id, Integer testConfigsId) {
        return this.dsl
                .select()
                .from(ITERATION_TABLE)
                .where(ITERATION_TABLE.ID.eq(id).and(ITERATION_TABLE.TEST_CONFIGS_ID.eq(testConfigsId)))
                .fetchOptionalInto(IterationModel.class);
    }

    public Optional<IterationModel> getById(Integer id) {
        return this.dsl
                .select()
                .from(ITERATION_TABLE)
                .where(ITERATION_TABLE.ID.eq(id))
                .fetchOptionalInto(IterationModel.class);
    }

    public Optional<IterationModel> getByIterationIdAndUserId(Integer id, Integer userId) {
        return this.dsl
                .select()
                .from(ITERATION_TABLE)
                .where(ITERATION_TABLE.ID.eq(id).and(ITERATION_TABLE.USER_ID.eq(userId)))
                .fetchOptionalInto(IterationModel.class);
    }

    public Optional<IterationModel> getByIterationIdUserIdAndTestId(Integer id, Integer userId, Integer testId) {
        return this.dsl
                .select()
                .from(ITERATION_TABLE)
                .where(ITERATION_TABLE.ID.eq(id).and(ITERATION_TABLE.USER_ID.eq(userId)).and(ITERATION_TABLE.TEST_CONFIGS_ID.eq(testId)))
                .fetchOptionalInto(IterationModel.class);
    }

    public boolean update(IterationModel iterationModel) {
        return this.dsl
                .update(ITERATION_TABLE)
                .set(ITERATION_TABLE.TEST_CONFIGS_ID, iterationModel.getTestConfigsId())
                .set(ITERATION_TABLE.CONFIGS, iterationModel.getConfigs())
                .set(ITERATION_TABLE.USER_ID, iterationModel.getUserId())
                .set(ITERATION_TABLE.DATABASE_ID, iterationModel.getDatabaseId())
                .set(ITERATION_TABLE.NAME, iterationModel.getName())
                .set(ITERATION_TABLE.SQL, iterationModel.getSql())
                .set(ITERATION_TABLE.CLEAN, iterationModel.getClean())
                .set(ITERATION_TABLE.SAVED_QUERY_ID, iterationModel.getSavedQueryId())
                .where(ITERATION_TABLE.ID.eq(iterationModel.getId()))
                .execute() == ONE;
    }

    public boolean update(List<IterationModel> iterationModels){
        return iterationModels
                .stream()
                .map(this::update)
                .collect(Collectors.toList())
                .stream()
                .reduce(true, (a, b) -> a && b);
    }

    public boolean delete(Integer id) {
        return dsl.delete(ITERATION_TABLE)
                .where(ITERATION_TABLE.ID.eq(id))
                .execute() == ONE;
    }

    public boolean deleteByTestIdAndUserId(Integer testId, Integer userId) {
        return dsl.delete(ITERATION_TABLE)
                .where(ITERATION_TABLE.TEST_CONFIGS_ID.eq(testId).and(ITERATION_TABLE.USER_ID.eq(userId)))
                .execute() >= ONE;
    }

    public boolean deleteByDatabaseIdAndUserId(Integer databaseId, Integer userId) {
        return dsl.delete(ITERATION_TABLE)
                .where(ITERATION_TABLE.DATABASE_ID.eq(databaseId).and(ITERATION_TABLE.USER_ID.eq(userId)))
                .execute() >= ONE;
    }

    public boolean deleteByTestConfigsId(Integer id, Integer testConfigsId) {
        return dsl.delete(ITERATION_TABLE)
                .where(ITERATION_TABLE.ID.eq(id).and(ITERATION_TABLE.TEST_CONFIGS_ID.eq(testConfigsId)))
                .execute() == ONE;
    }

    public boolean deleteByTestConfigsIdAndUserId(Integer id, Integer testConfigsId, Integer userId) {
        return dsl.delete(ITERATION_TABLE)
                .where(ITERATION_TABLE.ID.eq(id).and(ITERATION_TABLE.TEST_CONFIGS_ID.eq(testConfigsId)).and(ITERATION_TABLE.USER_ID.eq(userId)))
                .execute() == ONE;
    }

    public boolean deleteByTestConfigsId(List<Integer> ids, Integer testConfigsId) {
        return dsl.delete(ITERATION_TABLE)
                .where(ITERATION_TABLE.ID.in(ids).and(ITERATION_TABLE.TEST_CONFIGS_ID.eq(testConfigsId)))
                .execute() == ids.size();
    }

    public boolean deleteByTestConfigsIdAndUserId(List<Integer> ids, Integer testConfigsId, Integer userId) {
        return dsl.delete(ITERATION_TABLE)
                .where(ITERATION_TABLE.ID.in(ids).and(ITERATION_TABLE.TEST_CONFIGS_ID.eq(testConfigsId)).and(ITERATION_TABLE.USER_ID.eq(userId)))
                .execute() == ids.size();
    }

    public IterationModel save(IterationModel iterationModel) {
        return dsl.insertInto(ITERATION_TABLE)
                .set(ITERATION_TABLE.TEST_CONFIGS_ID, iterationModel.getTestConfigsId())
                .set(ITERATION_TABLE.CONFIGS, iterationModel.getConfigs())
                .set(ITERATION_TABLE.USER_ID, iterationModel.getUserId())
                .set(ITERATION_TABLE.DATABASE_ID, iterationModel.getDatabaseId())
                .set(ITERATION_TABLE.NAME, iterationModel.getName())
                .set(ITERATION_TABLE.SQL, iterationModel.getSql())
                .set(ITERATION_TABLE.CLEAN, iterationModel.getClean())
                .set(ITERATION_TABLE.SAVED_QUERY_ID, iterationModel.getSavedQueryId())
                .returning(ITERATION_TABLE.ID, ITERATION_TABLE.SAVED_QUERY_ID, ITERATION_TABLE.TEST_CONFIGS_ID, ITERATION_TABLE.CONFIGS, ITERATION_TABLE.USER_ID, ITERATION_TABLE.DATABASE_ID, ITERATION_TABLE.CLEAN, ITERATION_TABLE.SQL)
                .fetchOne()
                .map(r -> r.into(ITERATION_TABLE).into(IterationModel.class));
    }

    public List<IterationModel> save(List<IterationModel> iterationModels) {
        return iterationModels
                .stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    public Optional<Integer> haveSavedQueryConnectedIterations(Integer savedQueryId, Integer userId){
        return dsl.select(count())
                .from(ITERATION_TABLE)
                .where(ITERATION_TABLE.SAVED_QUERY_ID.eq(savedQueryId).and(ITERATION_TABLE.USER_ID.eq(userId)))
                .fetchOptionalInto(Integer.class);
    }




}
