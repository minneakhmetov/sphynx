package com.razzzil.sphynx.coordinator.configuration.socket;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

@Configuration
@ConfigurationProperties("sphynx.socket")
@Data
public class SocketConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketConfiguration.class);

    private Integer port;
    private Integer maxWorkers;

    @Value("${sphynx.host}")
    private String host;

    @Bean
    public ServerSocket serverSocket() throws IOException {
        LOGGER.info("Initializing socket server {} on port {} with max workers {}", host, port, maxWorkers);
        return new ServerSocket(port);
    }

    @Bean("workersThreadPool")
    public TaskExecutor workersThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(maxWorkers);
        executor.setMaxPoolSize(maxWorkers);
        executor.setThreadNamePrefix("workersThreadPool");
        executor.initialize();
        return executor;
    }





}
