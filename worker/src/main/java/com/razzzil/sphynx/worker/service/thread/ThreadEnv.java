package com.razzzil.sphynx.worker.service.thread;

import com.razzzil.sphynx.worker.service.processors.database.MySQLDatabaseProcessor;
import com.razzzil.sphynx.worker.service.processors.database.PostgreSQLDatabaseProcessor;
import com.razzzil.sphynx.worker.service.processors.testmode.SteppedTestModeProcessor;
import com.razzzil.sphynx.worker.service.processors.testmode.TimedTestModeProcessor;
import com.razzzil.sphynx.worker.socket.SocketClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Getter
public class ThreadEnv {

    @Autowired
    private TimedTestModeProcessor timedTestModeProcessor;
    @Autowired
    private SteppedTestModeProcessor steppedTestModeProcessor;
    @Autowired
    private PostgreSQLDatabaseProcessor postgreSQLDatabaseProcessor;
    @Autowired
    private MySQLDatabaseProcessor mySQLDatabaseProcessor;
    @Autowired
    private SocketClient socketClient;
    @Value("${sphynx.maxOverdueTime}")
    private Integer maxOverdueTime;



}
