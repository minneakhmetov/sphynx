package com.razzzil.sphynx.coordinator.repository;

import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.commons.model.test.state.TestState;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.model.webhook.WebhookModel;
import com.razzzil.sphynx.coordinator.jooq.tables.TestExecution;
import com.razzzil.sphynx.coordinator.jooq.tables.TestMetrics;
import com.razzzil.sphynx.coordinator.jooq.tables.TestTable;
import com.razzzil.sphynx.coordinator.jooq.tables.Webhook;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.ONE;
import static org.jooq.impl.DSL.max;

@Repository
public class TestRepository {

    private static final TestTable TEST_TABLE = TestTable.TEST_TABLE;
    private static final TestExecution TEST_EXECUTION = TestExecution.TEST_EXECUTION;
    private static final TestMetrics TEST_METRICS = TestMetrics.TEST_METRICS;
    private static final Webhook WEBHOOK_TABLE = Webhook.WEBHOOK;
    private Table<?> testExecutionTable;
    private Table<?> testMetricsTable;
    private Table<?> testExecutionMetrics;

    @Autowired
    private DSLContext dsl;

    public TestRepository(DSLContext dsl) {
        this.dsl = dsl;
        this.testExecutionTable =
                dsl.select(TEST_EXECUTION.STATE, TEST_EXECUTION.TEST_CONFIGS_ID, max(TEST_EXECUTION.ID).as("id"))
                        .from(TEST_EXECUTION)
                        .groupBy(TEST_EXECUTION.TEST_CONFIGS_ID)
                        .asTable("test_execution_table");
        this.testMetricsTable =
                dsl.select(TEST_METRICS.TEST_EXECUTION_ID, max(TEST_METRICS.PROCESS).as("process"))
                        .from(TEST_METRICS)
                        .groupBy(TEST_METRICS.TEST_EXECUTION_ID)
                        .asTable("test_metrics_table");
        this.testExecutionMetrics =
                dsl.select(TEST_TABLE.ID, TEST_TABLE.WORKER_ID, TEST_TABLE.CRON, TEST_TABLE.USER_ID, TEST_TABLE.TEST_MODE_CONFIGS, TEST_TABLE.TEST_MODE, TEST_TABLE.DATABASE_ID,
                        TEST_TABLE.NAME, TEST_TABLE.DATABASE_TYPE, TEST_TABLE.CONFIGS, TEST_TABLE.NEXT_EXECUTION_TIME, testExecutionTable.field("id").as("last_execution_id"), testExecutionTable.field("state"), testMetricsTable.field("process"))
                        .from(TEST_TABLE)
                        .leftJoin(testExecutionTable)
                        .on(TEST_TABLE.ID.eq(testExecutionTable.field("test_configs_id").coerce(Integer.class)))
                        .leftJoin(testMetricsTable)
                        .on(testMetricsTable.field("test_execution_id").coerce(Integer.class).eq(testExecutionTable.field("id").coerce(Integer.class)))
                        .asTable("test_execution_metrics");
    }

    public List<TestModel> getAllByDatabaseIdAndUserId(Integer databaseId, Integer userId) {
        return this.dsl
                .select()
                .from(TEST_TABLE)
                .where(TEST_TABLE.DATABASE_ID.eq(databaseId).and(TEST_TABLE.USER_ID.eq(userId)))
                .fetchInto(TestModel.class);
    }

    public List<TestModel> getAllCronTests() {
        return this.dsl
                .select()
                .from(TEST_TABLE)
                .where(TEST_TABLE.NEXT_EXECUTION_TIME.isNotNull().and(TEST_TABLE.CRON.isNotNull()))
                .fetchInto(TestModel.class);
    }

//    private static final String GET_SQL = "select test.id as id, test.user_id as user_id, database_id, name, database_type, configs, test_mode, test_mode_configs, worker_id, state, test_exec.id as last_execution_id, process, cron, next_execution_time " +
//            "from test_table test" +
//            "         LEFT JOIN (select max(id) as id, state, test_configs_id from test_execution GROUP BY test_configs_id) as test_exec" +
//            "              on test_exec.test_configs_id = test.id" +
//            "         LEFT JOIN (SELECT max(process) as process, test_execution_id from test_metrics GROUP BY test_execution_id) as test_metrics" +
//            "            on test_exec.id = test_metrics.test_execution_id ";
//
//    private RowMapper<TestModel> testModelRowMapper = (rs, rowNum) -> TestModel.builder()
//            .workerId(rs.getInt("worker_id"))
//            .id(rs.getInt("id"))
//            .userId(rs.getInt("user_id"))
//            .databaseId(rs.getInt("database_id"))
//            .name(rs.getString("name"))
//            .testModeConfigs(rs.getString("test_mode_configs"), TestMode.get(rs.getString("test_mode")))
//            .configs(rs.getString("configs"), DatabaseType.get(rs.getString("database_type")))
//            .testState(Objects.nonNull(rs.getString("state")) ? TestState.get(rs.getString("state")) : null)
//            .process(rs.getInt("process"))
//            .lastExecutionId(rs.getInt("last_execution_id"))
//            .cron(rs.getString("cron"))
//            .nextExecutionTime(Objects.nonNull(rs.getTimestamp("next_execution_time")) ? rs.getTimestamp("next_execution_time").toLocalDateTime() : null)
//            .build();

    public List<TestModel> getAll() {
        Map<TestModel, List<WebhookModel>> response = dsl.select()
                .from(testExecutionMetrics)
                .leftJoin(WEBHOOK_TABLE)
                .on(WEBHOOK_TABLE.TEST_ID.eq(testExecutionMetrics.field("id").coerce(Integer.class)).and(WEBHOOK_TABLE.USER_ID.eq(testExecutionMetrics.field("user_id").coerce(Integer.class))))
                .fetchGroups(
                        r -> r.into(testExecutionMetrics).into(TestModel.class),
                        r -> r.into(WEBHOOK_TABLE).into(WebhookModel.class)
                );
        response.forEach((testModel, webhookModels) -> testModel.setWebhookModels(webhookModels.stream().filter(model -> !model.isNull()).collect(Collectors.toList())));
        return new ArrayList<>(response.keySet());
    }

//    public List<TestModel> getAll() {
//        return jdbcTemplate.query(GET_SQL, testModelRowMapper);
//    }

    public List<TestModel> getAllByUser(Integer userId) {
        Map<TestModel, List<WebhookModel>> response = dsl.select()
                .from(testExecutionMetrics)
                .leftJoin(WEBHOOK_TABLE)
                .on(WEBHOOK_TABLE.TEST_ID.eq(testExecutionMetrics.field("id").coerce(Integer.class)).and(WEBHOOK_TABLE.USER_ID.eq(testExecutionMetrics.field("user_id").coerce(Integer.class))))
                .where(testExecutionMetrics.field("user_id").coerce(Integer.class).eq(userId))
                .fetchGroups(
                        r -> r.into(testExecutionMetrics).into(TestModel.class),
                        r -> r.into(WEBHOOK_TABLE).into(WebhookModel.class)
                );
        response.forEach((testModel, webhookModels) -> testModel.setWebhookModels(webhookModels.stream().filter(model -> !model.isNull()).collect(Collectors.toList())));
        return new ArrayList<>(response.keySet());
    }

//    public List<TestModel> getAllByUser(Integer userId) {
//        return jdbcTemplate.query(GET_SQL + String.format("WHERE test.user_id = %d", userId), testModelRowMapper);
//    }


    public Optional<TestModel> getTestByIdAndUserId(Integer id, Integer userId) {
        Map<TestModel, List<WebhookModel>> response = dsl.select()
                .from(testExecutionMetrics)
                .leftJoin(WEBHOOK_TABLE)
                .on(WEBHOOK_TABLE.TEST_ID.eq(testExecutionMetrics.field("id").coerce(Integer.class)).and(WEBHOOK_TABLE.USER_ID.eq(testExecutionMetrics.field("user_id").coerce(Integer.class))))
                .where(testExecutionMetrics.field("user_id").coerce(Integer.class).eq(userId).and(testExecutionMetrics.field("id").coerce(Integer.class).eq(id)))
                .fetchGroups(
                        r -> r.into(testExecutionMetrics).into(TestModel.class),
                        r -> r.into(WEBHOOK_TABLE).into(WebhookModel.class)
                );
        response.forEach((testModel, webhookModels) -> testModel.setWebhookModels(webhookModels.stream().filter(model -> !model.isNull()).collect(Collectors.toList())));
        return Optional.ofNullable(response.keySet().iterator().next());
    }

//    public Optional<TestModel> getTestByIdAndUserId(Integer id, Integer userId) {
//        return Optional.ofNullable(jdbcTemplate.queryForObject(GET_SQL + String.format("WHERE test.user_id = %d and test.id = %d", userId, id), testModelRowMapper));
////        return dsl
////                .fetch(GET_SQL + String.format("WHERE test.user_id = %d and test.id = %d", userId, id))
////                .into(TestModel.class)
////                .stream()
////                .findFirst();
//    }

    public boolean update(TestModel testModel) {
        return this.dsl.update(TEST_TABLE)
                .set(TEST_TABLE.USER_ID, testModel.getUserId())
                .set(TEST_TABLE.DATABASE_ID, testModel.getDatabaseId())
                .set(TEST_TABLE.NAME, testModel.getName())
                .set(TEST_TABLE.DATABASE_TYPE, testModel.getDatabaseType().name())
                .set(TEST_TABLE.CONFIGS, testModel.getConfigs())
                .set(TEST_TABLE.TEST_MODE, testModel.getTestMode().name())
                .set(TEST_TABLE.TEST_MODE_CONFIGS, testModel.getTestModeConfigs())
                .set(TEST_TABLE.WORKER_ID, testModel.getWorkerId())
                .set(TEST_TABLE.NEXT_EXECUTION_TIME, testModel.getNextExecutionTime())
                .set(TEST_TABLE.CRON, testModel.getCron())
                .where(TEST_TABLE.ID.eq(testModel.getId()))
                .execute() == ONE;
    }

    public boolean delete(Integer id) {
        return dsl.delete(TEST_TABLE)
                .where(TEST_TABLE.ID.eq(id))
                .execute() == ONE;
    }

    public boolean deleteByDatabaseIdAndUserId(Integer databaseId, Integer userId) {
        return dsl.delete(TEST_TABLE)
                .where(TEST_TABLE.DATABASE_ID.eq(databaseId).and(TEST_TABLE.USER_ID.eq(userId)))
                .execute() >= ONE;
    }

    public boolean deleteByUser(Integer id, Integer userId) {
        return dsl.delete(TEST_TABLE)
                .where(TEST_TABLE.ID.eq(id).and(TEST_TABLE.USER_ID.eq(userId)))
                .execute() == ONE;
    }

    public TestModel save(TestModel testModel) {
        return dsl.insertInto(TEST_TABLE)
                .set(TEST_TABLE.USER_ID, testModel.getUserId())
                .set(TEST_TABLE.DATABASE_ID, testModel.getDatabaseId())
                .set(TEST_TABLE.NAME, testModel.getName())
                .set(TEST_TABLE.DATABASE_TYPE, testModel.getDatabaseType().name())
                .set(TEST_TABLE.CONFIGS, testModel.getConfigs())
                .set(TEST_TABLE.TEST_MODE, testModel.getTestMode().name())
                .set(TEST_TABLE.TEST_MODE_CONFIGS, testModel.getTestModeConfigs())
                .set(TEST_TABLE.WORKER_ID, testModel.getWorkerId())
                .set(TEST_TABLE.NEXT_EXECUTION_TIME, testModel.getNextExecutionTime())
                .set(TEST_TABLE.CRON, testModel.getCron())
                .returning(TEST_TABLE.ID, TEST_TABLE.USER_ID, TEST_TABLE.DATABASE_ID, TEST_TABLE.DATABASE_TYPE, TEST_TABLE.CONFIGS, TEST_TABLE.NAME, TEST_TABLE.TEST_MODE, TEST_TABLE.TEST_MODE_CONFIGS, TEST_TABLE.WORKER_ID, TEST_TABLE.NEXT_EXECUTION_TIME, TEST_TABLE.CRON)
                .fetchOne()
                .map(r -> r.into(TEST_TABLE).into(TestModel.class));
    }

    public boolean updateNextExecutionTime(Integer testId, LocalDateTime nextExecutionTime) {
        return dsl.update(TEST_TABLE)
                .set(TEST_TABLE.NEXT_EXECUTION_TIME, nextExecutionTime)
                .where(TEST_TABLE.ID.eq(testId))
                .execute() == ONE;
    }

}
