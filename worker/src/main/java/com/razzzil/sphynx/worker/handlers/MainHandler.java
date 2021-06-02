package com.razzzil.sphynx.worker.handlers;

import com.razzzil.sphynx.commons.model.database.DatabaseDisplayForm;
import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.database.configs.DatabaseConfigs;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.model.key.WorkerCredential;
import com.razzzil.sphynx.commons.model.payload.Payload;
import com.razzzil.sphynx.commons.model.savedquery.RequestQueryForm;
import com.razzzil.sphynx.commons.model.savedquery.SavedQueryModel;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.model.worker.connectionstate.ConnectionState;
import com.razzzil.sphynx.commons.model.wrapper.TestStartWrapper;
import com.razzzil.sphynx.commons.model.wrapper.TestUpdateStateWrapper;
import com.razzzil.sphynx.commons.socket.annotation.SocketHandler;
import com.razzzil.sphynx.commons.socket.annotation.SocketMapping;
import com.razzzil.sphynx.worker.model.TestConfigsWrapper;
import com.razzzil.sphynx.worker.service.TestsExecutionManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

@SocketHandler
public class MainHandler {

    @Autowired
    private WorkerCredential workerCredential;

    @Autowired
    private TestsExecutionManager<? extends TestConfigs, ? extends IterationConfigs, ? extends TestMode.AbstractTestingModeConfig, ? extends DatabaseConfigs<? extends DataSource>> testsExecutionManager;

    @SocketMapping("startTest")
    public void startTest(TestStartWrapper testStartWrapper){
        testsExecutionManager.start(new TestConfigsWrapper<>(testStartWrapper, workerCredential.getWorkerConfigurationModel().getId()));
    }

    @SocketMapping("pauseTest")
    public void pauseTest(TestUpdateStateWrapper testUpdateStateWrapper){
        testsExecutionManager.pauseTest(testUpdateStateWrapper);
    }

    @SocketMapping("resumeTest")
    public void resumeTest(TestUpdateStateWrapper testUpdateStateWrapper){
        testsExecutionManager.resumeTest(testUpdateStateWrapper);
    }

    @SocketMapping("terminateTest")
    public void terminateTest(TestUpdateStateWrapper testUpdateStateWrapper){
        testsExecutionManager.terminateTest(testUpdateStateWrapper);
    }

    @SocketMapping("databasePing")
    public DatabaseDisplayForm ping(DatabaseModel databaseModel){
        return testsExecutionManager.ping(databaseModel);
    }

    @SocketMapping("query")
    public SavedQueryModel query(RequestQueryForm requestQueryForm){
        return testsExecutionManager.query(requestQueryForm);
    }

}
