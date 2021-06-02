package com.razzzil.sphynx.coordinator.repository;

import com.razzzil.sphynx.commons.model.execution.TestExecutionModel;
import com.razzzil.sphynx.coordinator.jooq.tables.TestExecution;
import com.razzzil.sphynx.coordinator.jooq.tables.TestMetrics;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.ONE;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.max;

@Repository
public class TestExecutionRepository {

    @Autowired
    private DSLContext dsl;

    private static final TestExecution TEST_EXECUTION = TestExecution.TEST_EXECUTION;
    private static final TestMetrics TEST_METRICS = TestMetrics.TEST_METRICS;

    public Optional<TestExecutionModel> getTestExecutionModelByIdAndUserId(Integer id, Integer userId){
        return dsl.select(TEST_EXECUTION.ID, TEST_EXECUTION.END_TIME, TEST_EXECUTION.START_TIME, TEST_EXECUTION.STATE, TEST_EXECUTION.TEST_CONFIGS_ID, TEST_EXECUTION.USER_ID, max(TEST_METRICS.PROCESS).as("process"), TEST_EXECUTION.MESSAGE.as("message"))
                .from(TEST_EXECUTION)
                .leftJoin(TEST_METRICS)
                .on(TEST_EXECUTION.ID.eq(TEST_METRICS.TEST_EXECUTION_ID))
                .where(TEST_EXECUTION.ID.eq(id).and(TEST_EXECUTION.USER_ID.eq(userId)))
                .groupBy(TEST_EXECUTION.ID)
                .fetchOptionalInto(TestExecutionModel.class);
    }

    public List<TestExecutionModel> getTestExecutionModelsUserId(Integer userId){
        return dsl.select(TEST_EXECUTION.ID, TEST_EXECUTION.END_TIME, TEST_EXECUTION.START_TIME, TEST_EXECUTION.STATE, TEST_EXECUTION.TEST_CONFIGS_ID, TEST_EXECUTION.USER_ID, max(TEST_METRICS.PROCESS).as("process"), TEST_EXECUTION.MESSAGE.as("message"))
                .from(TEST_EXECUTION)
                .leftJoin(TEST_METRICS)
                .on(TEST_EXECUTION.ID.eq(TEST_METRICS.TEST_EXECUTION_ID))
                .where(TEST_EXECUTION.USER_ID.eq(userId))
                .groupBy(TEST_EXECUTION.ID)
                .fetchInto(TestExecutionModel.class);
    }

    public List<TestExecutionModel> getTestExecutionModelsByUserIdAndTestConfigsId(Integer testId, Integer userId){
        return dsl.select(TEST_EXECUTION.ID, TEST_EXECUTION.END_TIME, TEST_EXECUTION.START_TIME, TEST_EXECUTION.STATE, TEST_EXECUTION.TEST_CONFIGS_ID, TEST_EXECUTION.USER_ID, max(TEST_METRICS.PROCESS).as("process"), TEST_EXECUTION.MESSAGE.as("message"))
                .from(TEST_EXECUTION)
                .leftJoin(TEST_METRICS)
                .on(TEST_EXECUTION.ID.eq(TEST_METRICS.TEST_EXECUTION_ID))
                .where(TEST_EXECUTION.USER_ID.eq(userId).and(TEST_EXECUTION.TEST_CONFIGS_ID.eq(testId)))
                .groupBy(TEST_EXECUTION.ID)
                .fetchInto(TestExecutionModel.class);
    }

    public TestExecutionModel save(TestExecutionModel testExecutionModel){
        return dsl.insertInto(TEST_EXECUTION)
                .set(TEST_EXECUTION.TEST_CONFIGS_ID, testExecutionModel.getTestConfigsId())
                .set(TEST_EXECUTION.USER_ID, testExecutionModel.getUserId())
                .set(TEST_EXECUTION.END_TIME, testExecutionModel.getEndTime())
                .set(TEST_EXECUTION.START_TIME, testExecutionModel.getStartTime())
                .set(TEST_EXECUTION.STATE, testExecutionModel.getState().name())
                .set(TEST_EXECUTION.MESSAGE, testExecutionModel.getMessage())
                .returning(TEST_EXECUTION.ID, TEST_EXECUTION.TEST_CONFIGS_ID, TEST_EXECUTION.USER_ID, TEST_EXECUTION.END_TIME,TEST_EXECUTION.START_TIME, TEST_EXECUTION.STATE, TEST_EXECUTION.MESSAGE )
                .fetchOne()
                .map(r -> r.into(TEST_EXECUTION).into(TestExecutionModel.class));
    }

    public boolean update(TestExecutionModel testExecutionModel){
        return dsl.update(TEST_EXECUTION)
                .set(TEST_EXECUTION.TEST_CONFIGS_ID, testExecutionModel.getTestConfigsId())
                .set(TEST_EXECUTION.USER_ID, testExecutionModel.getUserId())
                .set(TEST_EXECUTION.END_TIME, testExecutionModel.getEndTime())
                .set(TEST_EXECUTION.START_TIME, testExecutionModel.getStartTime())
                .set(TEST_EXECUTION.STATE, testExecutionModel.getState().name())
                .set(TEST_EXECUTION.MESSAGE, testExecutionModel.getMessage())
                .where(TEST_EXECUTION.ID.eq(testExecutionModel.getId()))
                .execute() == ONE;
    }

    public boolean deleteByIdAndUserId(Integer id, Integer userId){
        return dsl.delete(TEST_EXECUTION)
                .where(TEST_EXECUTION.ID.eq(id).and(TEST_EXECUTION.USER_ID.eq(userId)))
                .execute() == ONE;
    }

    public boolean deleteByTestIdAndUserId(Integer testId, Integer userId){
        return dsl.delete(TEST_EXECUTION)
                .where(TEST_EXECUTION.TEST_CONFIGS_ID.eq(testId).and(TEST_EXECUTION.USER_ID.eq(userId)))
                .execute() >= ONE;
    }














}
