package com.razzzil.sphynx.worker.configuration;

import com.razzzil.sphynx.commons.model.key.WorkerCredential;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@ConfigurationProperties(prefix = "sphynx.access")
@Data
public class MainConfiguration {

    private String key;

    @Autowired
    private BuildProperties buildProperties;

    @Bean
    public WorkerCredential workerCredential(){
        if (Objects.nonNull(key)){
            WorkerCredential workerCredential = WorkerCredential.deserialize(key);
            if (workerCredential.getWorkerConfigurationModel().getVersion().equals(buildProperties.getVersion())){
                return workerCredential;
            } else throw new IllegalArgumentException(String.format("Version mismatch: Config %s; Actual %s",
                    workerCredential.getWorkerConfigurationModel().getVersion(), buildProperties.getVersion()));
        } else throw new IllegalArgumentException("Access key is not present");
    }
}
