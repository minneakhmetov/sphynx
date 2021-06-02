package com.razzzil.sphynx.coordinator.repository;

import com.razzzil.sphynx.commons.model.savedquery.SQLLabState;
import com.razzzil.sphynx.commons.model.savedquery.SavedQueryModel;
import com.razzzil.sphynx.coordinator.jooq.tables.SavedQueries;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.ONE;

@Repository
public class SavedQueriesRepository {

    private static final SavedQueries SAVED_QUERIES = SavedQueries.SAVED_QUERIES;

    @Autowired
    private DSLContext dslContext;

    public List<SavedQueryModel> getQueriesByUserId(Integer userId) {
        return dslContext
                .select()
                .from(SAVED_QUERIES)
                .where(SAVED_QUERIES.USER_ID.eq(userId))
                .fetchInto(SavedQueryModel.class);
    }

    public Optional<SavedQueryModel> getDetailedSavedQueriesByIdAndUserId(Integer id, Integer userId) {
        return dslContext
                .select()
                .from(SAVED_QUERIES)
                .where(SAVED_QUERIES.USER_ID.eq(userId).and(SAVED_QUERIES.STATE.eq(SQLLabState.SAVED.name())).and(SAVED_QUERIES.ID.eq(id)))
                .fetchOptionalInto(SavedQueryModel.class);
    }

    public Optional<SavedQueryModel> getSavedQueriesByIdAndUserId(Integer id, Integer userId) {
        return dslContext
                .select(SAVED_QUERIES.DATABASE_ID, SAVED_QUERIES.SQL, SAVED_QUERIES.TIME_END, SAVED_QUERIES.TIME_START, SAVED_QUERIES.USER_ID, SAVED_QUERIES.NAME, SAVED_QUERIES.STATE, SAVED_QUERIES.ID, SAVED_QUERIES.RESULT)
                .from(SAVED_QUERIES)
                .where(SAVED_QUERIES.USER_ID.eq(userId).and(SAVED_QUERIES.STATE.eq(SQLLabState.SAVED.name())).and(SAVED_QUERIES.ID.eq(id)))
                .fetchOptionalInto(SavedQueryModel.class);
    }

    public List<SavedQueryModel> getSavedQueriesByUserId(Integer userId) {
        return dslContext
                .select(SAVED_QUERIES.DATABASE_ID, SAVED_QUERIES.SQL, SAVED_QUERIES.TIME_END, SAVED_QUERIES.TIME_START, SAVED_QUERIES.USER_ID, SAVED_QUERIES.NAME, SAVED_QUERIES.STATE, SAVED_QUERIES.ID)
                .from(SAVED_QUERIES)
                .where(SAVED_QUERIES.USER_ID.eq(userId).and(SAVED_QUERIES.STATE.eq(SQLLabState.SAVED.name())))
                .fetchInto(SavedQueryModel.class);
    }

    public SavedQueryModel save(SavedQueryModel model) {
        return dslContext
                .insertInto(SAVED_QUERIES)
                .set(SAVED_QUERIES.DATABASE_ID, model.getDatabaseId())
                .set(SAVED_QUERIES.MESSAGE, model.getMessage())
                .set(SAVED_QUERIES.RESULT, model.getResult())
                .set(SAVED_QUERIES.SQL, model.getSql())
                .set(SAVED_QUERIES.TIME_END, model.getTimeEnd())
                .set(SAVED_QUERIES.TIME_START, model.getTimeStart())
                .set(SAVED_QUERIES.USER_ID, model.getUserId())
                .set(SAVED_QUERIES.NAME, model.getName())
                .set(SAVED_QUERIES.STATE, model.getState().name())
                .returning(SAVED_QUERIES.DATABASE_ID, SAVED_QUERIES.MESSAGE, SAVED_QUERIES.RESULT, SAVED_QUERIES.SQL, SAVED_QUERIES.TIME_END, SAVED_QUERIES.TIME_START, SAVED_QUERIES.USER_ID, SAVED_QUERIES.NAME, SAVED_QUERIES.STATE, SAVED_QUERIES.ID)
                .fetchOne()
                .map(r -> r.into(SAVED_QUERIES).into(SavedQueryModel.class));
    }

    public boolean update(SavedQueryModel model) {
        return dslContext
                .update(SAVED_QUERIES)
                .set(SAVED_QUERIES.DATABASE_ID, model.getDatabaseId())
                .set(SAVED_QUERIES.MESSAGE, model.getMessage())
                .set(SAVED_QUERIES.RESULT, model.getResult())
                .set(SAVED_QUERIES.SQL, model.getSql())
                .set(SAVED_QUERIES.TIME_END, model.getTimeEnd())
                .set(SAVED_QUERIES.TIME_START, model.getTimeStart())
                .set(SAVED_QUERIES.USER_ID, model.getUserId())
                .set(SAVED_QUERIES.NAME, model.getName())
                .set(SAVED_QUERIES.STATE, model.getState().name())
                .where(SAVED_QUERIES.ID.eq(model.getId()))
                .execute() == ONE;
    }

    public boolean updateSaved(Integer id, Integer userId, String name) {
        return dslContext
                .update(SAVED_QUERIES)
                .set(SAVED_QUERIES.STATE, SQLLabState.SAVED.name())
                .set(SAVED_QUERIES.NAME, name)
                .where(SAVED_QUERIES.ID.eq(id).and(SAVED_QUERIES.USER_ID.eq(userId)).and(SAVED_QUERIES.STATE.eq(SQLLabState.SUCCESS.name())))
                .execute() == ONE;
    }

    public boolean deleteUnused(Integer userId) {
        return dslContext
                .delete(SAVED_QUERIES)
                .where(SAVED_QUERIES.USER_ID.eq(userId).and(SAVED_QUERIES.STATE.in(SQLLabState.SUCCESS.name(), SQLLabState.FAILED.name())))
                .execute() >= ONE;
    }

    public boolean deleteByIdAndUserId(Integer id, Integer userId) {
        return dslContext
                .delete(SAVED_QUERIES)
                .where(SAVED_QUERIES.ID.eq(id).and(SAVED_QUERIES.USER_ID.eq(userId)))
                .execute() == ONE;
    }

    public boolean deleteByUserId(Integer userId) {
        return dslContext
                .delete(SAVED_QUERIES)
                .where(SAVED_QUERIES.USER_ID.eq(userId))
                .execute() >= ONE;
    }


}
