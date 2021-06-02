package com.razzzil.sphynx.commons.socket;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class SocketHandlerConfiguration {

    @Bean(value = "socketObjectMapper")
    public ObjectMapper objectMapper(){
        return new ObjectMapper().registerModules(new JavaTimeModule());
    }

}
