package com.razzzil.sphynx.coordinator.repository;

import com.razzzil.sphynx.commons.model.metrics.MetricsModel;
import com.razzzil.sphynx.coordinator.jooq.tables.IterationTable;
import com.razzzil.sphynx.coordinator.jooq.tables.TestMetrics;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.ONE;
import static org.jooq.impl.DSL.max;

@Repository
public class MetricsRepository {

    @Autowired
    private DSLContext dsl;

    private static final TestMetrics TEST_METRICS = TestMetrics.TEST_METRICS;

    public Optional<MetricsModel> getByIdAndUserId(Integer id, Integer userId){
        return dsl.select()
                .from(TEST_METRICS)
                .where(TEST_METRICS.ID.eq(id).and(TEST_METRICS.USER_ID.eq(userId)))
                .fetchOptionalInto(MetricsModel.class);
    }

//    public List<MetricsModel> getByTestIdAndUserId(Integer testId, Integer userId){
//        return dsl.select()
//                .from(TEST_METRICS)
//                .where(TEST_METRICS.TEST_CONFIGS_ID.eq(testId).and())
//                .fetchInto(MetricsModel.class);
//    }

    public List<MetricsModel> getByTestExecutionIdAndUserId(Integer testExecution, Integer userId){
        return dsl.select()
                .from(TEST_METRICS)
                .where(TEST_METRICS.TEST_EXECUTION_ID.eq(testExecution).and(TEST_METRICS.USER_ID.eq(userId)))
                .fetchInto(MetricsModel.class);
    }

    public MetricsModel save(MetricsModel metricsModel){
         return dsl.insertInto(TEST_METRICS)
                .set(TEST_METRICS.ITERATION_ID, metricsModel.getIterationId())
                .set(TEST_METRICS.TIME_WITH_CONNECTIONS, metricsModel.getTimeWithConnections())
                .set(TEST_METRICS.TIME_WITHOUT_CONNECTIONS, metricsModel.getTimeWithoutConnections())
                .set(TEST_METRICS.ADDITIONAL_METRICS, metricsModel.getAdditionalMetrics())
                .set(TEST_METRICS.TEST_EXECUTION_ID, metricsModel.getTestExecutionId())
                .set(TEST_METRICS.TIME, metricsModel.getTime())
                .set(TEST_METRICS.USER_ID, metricsModel.getUserId())
                .set(TEST_METRICS.MESSAGE, metricsModel.getMessage())
                .set(TEST_METRICS.TEST_ID, metricsModel.getTestId())
                .set(TEST_METRICS.PROCESS, metricsModel.getProcess())
                .set(TEST_METRICS.STEP_ID, metricsModel.getStepId())
                .set(TEST_METRICS.ITERATION_NAME, metricsModel.getIterationName())
                .returning(TEST_METRICS.ID, TEST_METRICS.TIME_WITH_CONNECTIONS, TEST_METRICS.TIME_WITHOUT_CONNECTIONS, TEST_METRICS.ADDITIONAL_METRICS, TEST_METRICS.TEST_EXECUTION_ID, TEST_METRICS.TIME, TEST_METRICS.MESSAGE, TEST_METRICS.TEST_ID, TEST_METRICS.USER_ID, TEST_METRICS.PROCESS)
                .fetchOne()
                .map(r -> r.into(TEST_METRICS).into(MetricsModel.class));
    }

    public boolean update(MetricsModel metricsModel){
        return dsl.update(TEST_METRICS)
                .set(TEST_METRICS.ITERATION_ID, metricsModel.getId())
                .set(TEST_METRICS.TIME_WITH_CONNECTIONS, metricsModel.getTimeWithConnections())
                .set(TEST_METRICS.TIME_WITHOUT_CONNECTIONS, metricsModel.getTimeWithoutConnections())
                .set(TEST_METRICS.ADDITIONAL_METRICS, metricsModel.getAdditionalMetrics())
                .set(TEST_METRICS.TEST_EXECUTION_ID, metricsModel.getTestExecutionId())
                .set(TEST_METRICS.TIME, metricsModel.getTime())
                .set(TEST_METRICS.USER_ID, metricsModel.getUserId())
                .set(TEST_METRICS.MESSAGE, metricsModel.getMessage())
                .set(TEST_METRICS.TEST_ID, metricsModel.getTestId())
                .set(TEST_METRICS.STEP_ID, metricsModel.getStepId())
                .set(TEST_METRICS.ITERATION_NAME, metricsModel.getIterationName())
                .where(TEST_METRICS.ID.eq(metricsModel.getId()))
                .execute() == ONE;
    }

    public boolean deleteByTestExecutionIdAndUserId(Integer executionId, Integer userId){
        return dsl.delete(TEST_METRICS)
                .where(TEST_METRICS.TEST_EXECUTION_ID.eq(executionId).and(TEST_METRICS.USER_ID.eq(userId)))
                .execute() == ONE;
    }

    public boolean update(List<MetricsModel> iterationModels){
        return iterationModels
                .stream()
                .map(this::update)
                .collect(Collectors.toList())
                .stream()
                .reduce(true, (a, b) -> a && b);
    }

    public boolean deleteByIdsAndUserId(List<Integer> ids, Integer userId){
        return dsl.delete(TEST_METRICS)
                .where(TEST_METRICS.ID.in(ids).and(TEST_METRICS.USER_ID.eq(userId)))
                .execute() == ONE;
    }

    public boolean deleteByTestIdAndUserId(Integer testId, Integer userId){
        return dsl.delete(TEST_METRICS)
                .where(TEST_METRICS.TEST_ID.in(testId).and(TEST_METRICS.USER_ID.eq(userId)))
                .execute() >= ONE;
    }

    public Optional<Integer> getMaxProcessByExecutionIdAndUserId(Integer executionId, Integer userId){
        return dsl.select(max(TEST_METRICS.PROCESS))
                .from(TEST_METRICS)
                .where(TEST_METRICS.TEST_EXECUTION_ID.eq(executionId).and(TEST_METRICS.USER_ID.eq(userId)))
                .fetchOptionalInto(Integer.class);
    }






}
