package com.razzzil.sphynx.worker.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.razzzil.sphynx.commons.model.handshake.HandshakeResponse;
import com.razzzil.sphynx.commons.model.handshake.HandshakeStatus;
import com.razzzil.sphynx.commons.model.key.WorkerCredential;
import com.razzzil.sphynx.commons.model.payload.Payload;
import com.razzzil.sphynx.commons.socket.ReservedHandlerNames;
import com.razzzil.sphynx.commons.socket.annotation.SocketHandler;
import com.razzzil.sphynx.commons.socket.annotation.SocketMapping;
import com.razzzil.sphynx.commons.util.KeyUtil;
import com.razzzil.sphynx.worker.exception.CoordinatorIsNotAvailableException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;
import java.util.UUID;


@SocketHandler
public class SocketClient {
    // поле, содержащее сокет-клиента
    private String sessionKey;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    @Getter
    private Boolean connected = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);
    @Autowired
    @Qualifier("socketObjectMapper")
    private ObjectMapper objectMapper;
    @Autowired
    private WorkerHandlerPool handlerPool;
    @Autowired
    private WorkerCredential workerCredential;
    @Value("${sphynx.port}")
    private Integer port;

    @PostConstruct
    public void init(){
        startConnection(workerCredential.getCoordinatorHost(), port);
    }

    // начало сессии - получаем ip-сервера и его порт
    public void startConnection(String host, int port) {
        try {
            LOGGER.info("Trying to connect to Coordinator");
            // создаем подключение
            clientSocket = new Socket(host, port);
            // получили выходной поток
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            // входной поток
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // запустили слушателя сообщений
            new Thread(receiverMessagesTask).start();
            connected = true;
            LOGGER.info("Connected to Coordinator");
            sessionKey = UUID.randomUUID().toString();
            Payload payload = Payload.builder()
                    .body(workerCredential.getWorkerConfigurationModel())
                    .endpoint(ReservedHandlerNames.HANDSHAKE.getName())
                    .sessionKey(sessionKey)
                    .build();
            String encrypted = KeyUtil.encrypt(objectMapper.writeValueAsString(payload), workerCredential.getKey());
            out.println(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SneakyThrows
    public void sendMessage(Payload payload) {
        if (connected) {
            payload.setSessionKey(sessionKey);
            String encrypted = KeyUtil.encrypt(objectMapper.writeValueAsString(payload), workerCredential.getKey());
            out.println(encrypted);
        } else throw new CoordinatorIsNotAvailableException();
    }

    public void sendMessage(Object object, String endpoint){
        Payload payload = Payload.builder()
                .body(object)
                .endpoint(endpoint)
                .build();
        sendMessage(payload);
    }

    private Runnable receiverMessagesTask = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    String inputLine = in.readLine();
                    if (inputLine != null) {
                        String jsonPayload = KeyUtil.decrypt(inputLine, workerCredential.getKey());
                        Payload payload = objectMapper.readValue(jsonPayload, Payload.class);
                        if (payload.getSessionKey().equals(sessionKey)) {
                            if (payload.getEndpoint().equals(ReservedHandlerNames.CLOSE.getName())) {
                                closeConnection();
                            } else {
                                Object response = handlerPool.jsonInvoke(payload.getEndpoint(), payload.getBody());
                                if (Objects.nonNull(response)) {
                                    if (response instanceof Payload) {
                                        sendMessage((Payload) response);
                                    } else {
                                        Payload responsePayload = Payload.builder()
                                                .body(response)
                                                .endpoint(payload.getEndpoint())
                                                .sessionKey(sessionKey)
                                                .build();
                                        sendMessage(responsePayload);
                                    }
                                }
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
        }
    };

    public void closeConnection() {
        try {
            connected = false;
            sessionKey = null;
            in.close();
            out.close();
            clientSocket.close();
            LOGGER.warn("Disconnected from Coordinator");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @SocketMapping("handshake")
    public void handshakeSuccessHandler(HandshakeResponse response){
        if(new HandshakeResponse(HandshakeStatus.SUCCESS).equals(response)){
            connected = true;
            LOGGER.info("Successful handshake from Coordinator");
        } else {
            throw new IllegalStateException("Illegal Handshake: " + response);
        }
    }
}
