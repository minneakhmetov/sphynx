package com.razzzil.sphynx.coordinator.model.form.request.worker;

import com.razzzil.sphynx.commons.util.KeyUtil;
import com.razzzil.sphynx.commons.model.worker.WorkerModel;
import com.razzzil.sphynx.commons.model.worker.connectionstate.ConnectionState;
import lombok.*;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Worker {
    private WorkerModel workerModel;
    private ConnectionState connectionState;
    private SecretKey secretKey;
    private Socket socket;
    private String sessionKey;

    public Worker(WorkerModel workerModel, ConnectionState connectionState) {
        this.workerModel = workerModel;
        this.connectionState = connectionState;
        this.secretKey = KeyUtil.deserializeKey(workerModel.getKey());
        this.socket = null;
        this.sessionKey = null;
    }

    public void close() throws IOException {
        if (Objects.nonNull(socket)) {
//            InputStream inputStream = socket.getInputStream();
//            if (Objects.nonNull(inputStream)){
//                inputStream.close();
//            }
//            OutputStream outputStream = socket.getOutputStream();
//            if (Objects.nonNull(outputStream)) {
//                outputStream.close();
//            }
            try {
                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
            } catch (SocketException exception){
                if (!exception.getMessage().equals("Socket is closed")){
                    exception.printStackTrace();
                }
            }
        }
        connectionState = ConnectionState.DISCONNECTED;
        setSocket(null);
        setSessionKey(null);
    }

    public void open(String sessionKey, Socket clientSocket){
        setSessionKey(sessionKey);
        setSocket(clientSocket);
        connectionState = ConnectionState.CONNECTED;
    }

    //todo: test!
    public boolean isConnectionAlive(){
        return connectionState.equals(ConnectionState.CONNECTED)
                && Objects.nonNull(socket)
                && Objects.nonNull(secretKey)
                && socket.isConnected()
                && socket.isBound();
    }

}
