package com.razzzil.sphynx.worker.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.razzzil.sphynx.commons.exception.HandlerNotSupportedException;
import com.razzzil.sphynx.commons.socket.HandlerInvocation;
import com.razzzil.sphynx.commons.socket.HandlerPool;
import com.razzzil.sphynx.commons.socket.ReservedHandlerNames;
import com.razzzil.sphynx.commons.util.Condition;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.ONE;

@Component
@Scope("singleton")
public class WorkerHandlerPool extends HandlerPool {

    public WorkerHandlerPool(ApplicationContext applicationContext, @Qualifier("socketObjectMapper") ObjectMapper objectMapper) {
        super(applicationContext, objectMapper);
    }

    @Override
    public void parametersCheck(String handlerName, Class<?>[] parameters) {
        Condition.of(ReservedHandlerNames.CLOSE.getName().equals(handlerName))
                .ifTrueThrow(() -> new IllegalStateException(String.format("Handler %s is not permitted to use", handlerName)));
        Condition.of(parameters.length == ONE)
                .ifFalseThrow(() -> new IllegalStateException(String.format("Handler %s should have one parameter", handlerName)));
    }

    @SneakyThrows
    public <T> Object jsonInvoke(String handlerName, Object json) {
        HandlerInvocation invocation = getHandler(handlerName);
        T parameter = objectMapper.convertValue(json, objectMapper.constructType(invocation.getParameterClass()));
        return invocation.invoke(parameter);
    }

}
