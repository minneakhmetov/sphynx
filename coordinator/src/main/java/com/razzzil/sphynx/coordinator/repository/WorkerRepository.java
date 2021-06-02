package com.razzzil.sphynx.coordinator.repository;

import com.razzzil.sphynx.commons.model.worker.WorkerModel;
import com.razzzil.sphynx.coordinator.jooq.tables.Worker;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.ONE;

@Repository
public class WorkerRepository {

    private static final Worker WORKER = Worker.WORKER;

    @Autowired
    private DSLContext dsl;

    public List<WorkerModel> getAllWorkers() {
        return dsl.select()
                .from(WORKER)
                .fetchInto(WorkerModel.class);
    }

    public Optional<WorkerModel> getById(Integer id) {
        return dsl.select()
                .from(WORKER)
                .where(WORKER.ID.eq(id))
                .fetchOptionalInto(WorkerModel.class);
    }

    public WorkerModel save(WorkerModel model){
        return dsl.insertInto(WORKER)
                .set(WORKER.ALIAS, model.getAlias())
                .set(WORKER.KEY, model.getKey())
                .set(WORKER.VERSION, model.getVersion())
                .returning(WORKER.ALIAS, WORKER.KEY, WORKER.ID, WORKER.VERSION)
                .fetchOne()
                .map(r -> r.into(WORKER).into(WorkerModel.class));
    }

    public boolean update(WorkerModel model){
        return dsl.update(WORKER)
                .set(WORKER.ALIAS, model.getAlias())
                .set(WORKER.KEY, model.getKey())
                .set(WORKER.VERSION, model.getVersion())
                .where(WORKER.ID.eq(model.getId()))
                .execute() == ONE;
    }

    public boolean delete(Integer id){
        int a = dsl.delete(WORKER)
                .where(WORKER.ID.eq(id))
                .execute() ;
        return a == ONE;
    }




}
