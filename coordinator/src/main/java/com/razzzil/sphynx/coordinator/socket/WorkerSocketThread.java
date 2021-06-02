package com.razzzil.sphynx.coordinator.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razzzil.sphynx.commons.exception.HandlerNotSupportedException;
import com.razzzil.sphynx.commons.exception.IllegalKeyException;
import com.razzzil.sphynx.commons.model.handshake.HandshakeResponse;
import com.razzzil.sphynx.commons.model.handshake.HandshakeStatus;
import com.razzzil.sphynx.commons.model.payload.Payload;
import com.razzzil.sphynx.commons.model.worker.WorkerConfigurationModel;
import com.razzzil.sphynx.commons.socket.ReservedHandlerNames;
import com.razzzil.sphynx.commons.util.KeyUtil;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import com.razzzil.sphynx.coordinator.model.form.request.worker.Worker;
import com.razzzil.sphynx.coordinator.service.WorkerService;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.ONE;

@Component
@Scope("prototype")
public class WorkerSocketThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerSocketThread.class);
    @Setter
    @Getter
    private Socket socket;
    private Worker worker;
    private BufferedReader in;

    @Autowired
    private ServerHandlerPool handlerPool;
    @Autowired
    @Qualifier("socketObjectMapper")
    private ObjectMapper objectMapper;
    @Autowired
    private WorkerService workerService;

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (Objects.isNull(worker)) {
                    handshakeHandling(inputLine);
                } else {
                    String jsonPayload = KeyUtil.decrypt(inputLine, worker.getSecretKey());
                    Payload payload = objectMapper.readValue(jsonPayload, Payload.class);
                    Stack<ValidationResult> validationResults = payload.validate();
                    if (!validationResults.empty()){
                        LOGGER.warn("Received incorrect payload worker {} with id {}: {}",
                                worker.getWorkerModel().getId(), worker.getWorkerModel().getId(), jsonPayload);
                    } else if (payload.getSessionKey().equals(worker.getSessionKey())) {
                        if (payload.getEndpoint().equals(ReservedHandlerNames.CLOSE.getName())) {
                            closeConnection();
                        } else {
                            Object response = null;
                            try {
                                response = handlerPool.jsonInvoke(payload.getEndpoint(), worker, payload.getBody());
                            } catch (HandlerNotSupportedException e) {

                            }
                            if (Objects.nonNull(response)) {
                                if (response instanceof Payload) {
                                    workerService.sendMessage(worker, (Payload) response);
                                } else {
                                    Payload responsePayload = Payload.builder()
                                            .body(response)
                                            .endpoint(payload.getEndpoint())
                                            .build();
                                    workerService.sendMessage(worker, responsePayload);
                                }
                            }
                        }
                    } else {
                        LOGGER.warn("Expired session key of worker id: {}; Alias: {}", worker.getWorkerModel().getId(),
                                worker.getWorkerModel().getAlias());
                    }
                }
            }
        } catch (SocketException e) {
            if (e.getMessage().equals("Connection reset")) {
                closeConnection();
            } else throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void handshakeHandling(String inputLine){
        for (Worker candidate : workerService.getWorkers()) {
            String jsonPayload;
            try {
                jsonPayload = KeyUtil.decrypt(inputLine, candidate.getSecretKey());
            } catch (IllegalKeyException e) {
                continue;
            }
            Stack<Payload> payloadCandidate = new Stack<>();
            try {
                payloadCandidate.add(objectMapper.readValue(jsonPayload, Payload.class));
            } catch (JsonProcessingException ignored) {}
            if (payloadCandidate.size() == ONE) {
                Payload payload = payloadCandidate.pop();
                try {
                    WorkerConfigurationModel workerConfigurationModel = objectMapper.convertValue(payload.getBody(), WorkerConfigurationModel.class);
                    if (payload.getEndpoint().equals(ReservedHandlerNames.HANDSHAKE.getName())
                            && workerConfigurationModel.isEqualToWorkerModel(candidate.getWorkerModel())) {
                        setWorker(candidate);
                        candidate.open(payload.getSessionKey(), socket);
                        Payload handshakeSuccess = Payload.builder()
                                .endpoint(ReservedHandlerNames.HANDSHAKE.getName())
                                .body(new HandshakeResponse(HandshakeStatus.SUCCESS))
                                .sessionKey(payload.getSessionKey())
                                .build();
                        workerService.sendMessage(candidate, handshakeSuccess);
                        LOGGER.info("Connected worker with config: {}", workerConfigurationModel);
                    } else closeConnection();
                } catch (IllegalArgumentException e) {
                    closeConnection();
                }
//                catch (JsonProcessingException e){
//                    e.printStackTrace();
//                }
            } else {
                closeConnection();
            }
        }
    }

    @SneakyThrows
    private void closeConnection() {
        if (Objects.nonNull(in)) {
            in.close();
        }
        if (Objects.nonNull(worker)){
            worker.close();
        }
        if (Objects.nonNull(worker)) {
            LOGGER.info("Disconnected worker with config: {}", WorkerConfigurationModel.fromWorkerModel(worker.getWorkerModel()));
        } else LOGGER.info("Disconnected unknown worker");
        setWorker(null);
    }

    private void setWorker(Worker worker) {
        this.worker = worker;
    }

}
