package com.razzzil.sphynx.coordinator.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.razzzil.sphynx.commons.socket.HandlerInvocation;
import com.razzzil.sphynx.commons.socket.HandlerPool;
import com.razzzil.sphynx.commons.socket.ReservedHandlerNames;
import com.razzzil.sphynx.commons.util.Condition;
import com.razzzil.sphynx.coordinator.model.form.request.worker.Worker;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.TWO;

@Component
@Scope("singleton")
public class ServerHandlerPool extends HandlerPool {

    public ServerHandlerPool(ApplicationContext applicationContext, @Qualifier("socketObjectMapper") ObjectMapper objectMapper) {
        super(applicationContext, objectMapper);
    }

    @Override
    public void parametersCheck(String handlerName, Class<?>[] parameters) {
        Condition.of(ReservedHandlerNames.containsName(handlerName))
                .ifTrueThrow(() -> new IllegalStateException(String.format("Handler %s is not permitted to use", handlerName)));
        Condition.of(parameters.length == TWO)
                .ifFalseThrow(() -> new IllegalStateException(String.format("Handler %s should have two parameters", handlerName)));
        Condition.of(parameters[1].equals(Worker.class))
                .ifFalseThrow(() -> new IllegalStateException(String.format("Second parameter in handler %s should be %s", handlerName,
                        Worker.class.getCanonicalName())));
    }

    @SneakyThrows
    public <T> Object jsonInvoke(String handlerName, Worker worker, Object json) {
        HandlerInvocation invocation = getHandler(handlerName);
        T parameter = objectMapper.convertValue(json, objectMapper.constructType(invocation.getParameterClass()));
        return invocation.invoke(parameter, worker);
    }

}
