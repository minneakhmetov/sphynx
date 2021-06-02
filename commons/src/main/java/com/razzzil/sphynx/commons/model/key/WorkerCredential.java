package com.razzzil.sphynx.commons.model.key;

import com.razzzil.sphynx.commons.model.worker.WorkerConfigurationModel;
import com.razzzil.sphynx.commons.util.KeyUtil;
import lombok.*;

import javax.crypto.SecretKey;
import java.util.Base64;

import static com.razzzil.sphynx.commons.constant.StaticsConstants.OM;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkerCredential {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    private static class SerializedKeyModel {
        private String coordinatorHost;
        private String key;
        private WorkerConfigurationModel workerConfigurationModel;
    }

    private String coordinatorHost;
    private SecretKey key;
    private WorkerConfigurationModel workerConfigurationModel;

    @SneakyThrows
    public String serialize() {
        return Base64
                .getEncoder()
                .encodeToString(OM.writeValueAsString(
                        SerializedKeyModel.builder()
                                .coordinatorHost(coordinatorHost)
                                .key(KeyUtil.serializeKey(key))
                                .workerConfigurationModel(workerConfigurationModel)
                                .build()).getBytes());
    }

    @SneakyThrows
    public static WorkerCredential deserialize(String string){
        SerializedKeyModel serializedKeyModel = OM.readValue(new String(Base64.getDecoder().decode(string)), SerializedKeyModel.class);
        return WorkerCredential.builder()
                .coordinatorHost(serializedKeyModel.getCoordinatorHost())
                .key(KeyUtil.deserializeKey(serializedKeyModel.getKey()))
                .workerConfigurationModel(serializedKeyModel.getWorkerConfigurationModel())
                .build();
    }

    public void setKey(String key){
        this.key = KeyUtil.deserializeKey(key);
    }

    public String key(){
        return KeyUtil.serializeKey(key);
    }


}
