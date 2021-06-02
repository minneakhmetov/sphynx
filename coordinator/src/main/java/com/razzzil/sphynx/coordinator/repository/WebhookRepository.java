package com.razzzil.sphynx.coordinator.repository;

import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.commons.model.webhook.WebhookModel;
import com.razzzil.sphynx.coordinator.jooq.tables.TestTable;
import com.razzzil.sphynx.coordinator.jooq.tables.Webhook;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.ONE;

@Repository
public class WebhookRepository {

    @Autowired
    private DSLContext dslContext;

    private static final Webhook WEBHOOK_TABLE = Webhook.WEBHOOK;
    private static final TestTable TEST_TABLE = TestTable.TEST_TABLE;

    public List<WebhookModel> getAll(){
        return dslContext.select()
                .from(WEBHOOK_TABLE)
//                .join(TEST_TABLE)
//                .on(TEST_TABLE.ID.eq(WEBHOOK_TABLE.TEST_ID))
                .fetchInto(WebhookModel.class);
    }

    public Map<TestModel, List<WebhookModel>> getAllByUserIdWithTest(Integer userId){
        return dslContext.select()
                .from(TEST_TABLE)
                .join(WEBHOOK_TABLE)
                .on(TEST_TABLE.ID.eq(WEBHOOK_TABLE.TEST_ID))
                .where(TEST_TABLE.USER_ID.eq(userId).and(WEBHOOK_TABLE.USER_ID.eq(userId)))
                .fetchGroups(
                        r -> r.into(TEST_TABLE).into(TestModel.class),
                        r -> r.into(WEBHOOK_TABLE).into(WebhookModel.class)
                );
    }

    public Map<TestModel, List<WebhookModel>> getAllByUserIdAndTestIdWithTest(Integer testId, Integer userId){
        return dslContext.select()
                .from(TEST_TABLE)
                .join(WEBHOOK_TABLE)
                .on(TEST_TABLE.ID.eq(WEBHOOK_TABLE.TEST_ID))
                .where(TEST_TABLE.USER_ID.eq(userId).and(WEBHOOK_TABLE.USER_ID.eq(userId)).and(WEBHOOK_TABLE.TEST_ID.eq(testId)))
                .fetchGroups(
                        r -> r.into(TEST_TABLE).into(TestModel.class),
                        r -> r.into(WEBHOOK_TABLE).into(WebhookModel.class)
                );
    }

    public List<WebhookModel> getAllByUserIdAndTestId(Integer testId, Integer userId){
        return dslContext.select()
                .from(WEBHOOK_TABLE)
                .where(WEBHOOK_TABLE.USER_ID.eq(userId).and(WEBHOOK_TABLE.TEST_ID.eq(testId)))
                .fetchInto(WebhookModel.class);
    }

    public Optional<WebhookModel> getByIdAndUserId(Integer id, Integer userId){
        return dslContext.select()
                .from(WEBHOOK_TABLE)
                .where(WEBHOOK_TABLE.USER_ID.eq(userId).and(WEBHOOK_TABLE.ID.eq(id)))
                .fetchOptionalInto(WebhookModel.class);
    }

    public WebhookModel save(WebhookModel webhookModel){
        return dslContext.insertInto(WEBHOOK_TABLE)
                .set(WEBHOOK_TABLE.ALIAS, webhookModel.getAlias())
                .set(WEBHOOK_TABLE.CONFIGS, webhookModel.getConfigs())
                .set(WEBHOOK_TABLE.TEST_ID, webhookModel.getTestId())
                .set(WEBHOOK_TABLE.USER_ID, webhookModel.getUserId())
                .set(WEBHOOK_TABLE.WEBHOOK_TYPE, webhookModel.getWebhookType().name())
                .returning(WEBHOOK_TABLE.ALIAS, WEBHOOK_TABLE.CONFIGS, WEBHOOK_TABLE.TEST_ID, WEBHOOK_TABLE.USER_ID, WEBHOOK_TABLE.WEBHOOK_TYPE, WEBHOOK_TABLE.ID)
                .fetchOne()
                .map(r -> r.into(WEBHOOK_TABLE).into(WebhookModel.class));
    }

    public boolean update(WebhookModel webhookModel){
        return dslContext.update(WEBHOOK_TABLE)
                .set(WEBHOOK_TABLE.ALIAS, webhookModel.getAlias())
                .set(WEBHOOK_TABLE.CONFIGS, webhookModel.getConfigs())
                .set(WEBHOOK_TABLE.TEST_ID, webhookModel.getTestId())
                .set(WEBHOOK_TABLE.USER_ID, webhookModel.getUserId())
                .set(WEBHOOK_TABLE.WEBHOOK_TYPE, webhookModel.getWebhookType().name())
                .where(WEBHOOK_TABLE.ID.eq(webhookModel.getId()))
                .execute() == ONE;
    }

    public boolean deleteByIdAndUserId(Integer id, Integer userId){
        return dslContext.delete(WEBHOOK_TABLE)
                .where(WEBHOOK_TABLE.ID.eq(id).and(WEBHOOK_TABLE.USER_ID.eq(userId)))
                .execute() == ONE;
    }

    public boolean deleteByTestIdAndUserId(Integer testId, Integer userId){
        return dslContext.delete(WEBHOOK_TABLE)
                .where(WEBHOOK_TABLE.TEST_ID.eq(testId).and(WEBHOOK_TABLE.USER_ID.eq(userId)))
                .execute() == ONE;
    }


}
