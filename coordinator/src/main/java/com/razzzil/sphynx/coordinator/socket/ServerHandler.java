package com.razzzil.sphynx.coordinator.socket;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.ServerSocket;
import java.net.SocketException;

@Component
public class ServerHandler extends Thread {

    @Autowired
    private ServerSocket serverSocket;

    @Autowired
    private ApplicationContext applicationContext;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    @Autowired
    @Qualifier("workersThreadPool")
    private TaskExecutor taskExecutor;

    @PostConstruct
    private void listen() {
        setDaemon(true);
        start();
    }

    @Override
    @SneakyThrows
    public void run() {
        LOGGER.info("Starting listening socket");
        while (true) {
            try {
                WorkerSocketThread workerSocketThread = applicationContext.getBean(WorkerSocketThread.class);
                workerSocketThread.setSocket(serverSocket.accept());
                taskExecutor.execute(workerSocketThread);
            } catch (SocketException socketException){
                if (socketException.getMessage().equals("Interrupted function call: accept failed")){
                    LOGGER.info("Socket server closed");
                    break;
                }
            }
        }
    }
}
