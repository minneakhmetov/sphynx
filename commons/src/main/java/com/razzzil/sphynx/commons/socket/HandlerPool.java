package com.razzzil.sphynx.commons.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.razzzil.sphynx.commons.exception.HandlerNotSupportedException;
import com.razzzil.sphynx.commons.socket.annotation.SocketHandler;
import com.razzzil.sphynx.commons.socket.annotation.SocketMapping;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public abstract class HandlerPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlerPool.class);

    private Map<String, HandlerInvocation> map;
    protected ObjectMapper objectMapper;

    public HandlerPool(ApplicationContext applicationContext, @Qualifier("socketObjectMapper") ObjectMapper objectMapper) {
        this.map = getHandlersMappings(applicationContext);
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public Object invoke(String handlerName, Object... args) {
        return getHandler(handlerName).invoke(args);
    }

    @SneakyThrows
    public <T> Object jsonInvoke(String handlerName, String json) {
        HandlerInvocation invocation = getHandler(handlerName);
        T parameter = objectMapper.readValue(json, objectMapper.constructType(invocation.getParameterClass()));
        return invocation.invoke(parameter);
    }

    @SneakyThrows
    public <T> Object jsonInvoke(String handlerName, Object json) {
        HandlerInvocation invocation = getHandler(handlerName);
        T parameter = objectMapper.convertValue(json, objectMapper.constructType(invocation.getParameterClass()));
        return invocation.invoke(parameter);
    }

    private Map<String, HandlerInvocation> getHandlersMappings(ApplicationContext applicationContext){
        try {
            LOGGER.info("Initializing handlers");
            Map<String, HandlerInvocation> map = new HashMap<>();
            Set<Class<?>> handlers = new HashSet<>();
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(SocketHandler.class));
            Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("com.razzzil.sphynx");
            for (BeanDefinition bd : beanDefinitions) {
                Class<?> handlerClass = Class.forName(bd.getBeanClassName());
                handlers.add(handlerClass);
                for (Method method: handlerClass.getMethods()){
                    for (Annotation annotation : method.getAnnotations()){
                        if (annotation.annotationType().equals(SocketMapping.class)){
                            SocketMapping handlerMapping = method.getAnnotation(SocketMapping.class);
                            String handlerName = handlerMapping.value();
                            if (!map.containsKey(handlerName)) {
                                parametersCheck(handlerName, method.getParameterTypes());
                                map.put(handlerName, new HandlerInvocation(method, applicationContext.getBean(handlerClass)));
                            } else
                                throw new IllegalStateException(String.format("Handler %s is duplicated in methods [%s, %s]", handlerName,
                                        handlerClass + "#" + method.getName(),
                                        map.get(handlerName).getCanonicalClass() + "#" + method.getName()));
                        }
                    }
                }
            }
            LOGGER.info("{} handlers with {} mappings were initialized", handlers.size(), map.size());
            return map;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error during initialization. Cannot find class: " + e.getMessage());
        }
    }

    public abstract void parametersCheck(String handlerName, Class<?>[] parameters);

    protected HandlerInvocation getHandler(String handlerName){
        return Optional
                .ofNullable(map.get(handlerName))
                .orElseThrow(() -> new HandlerNotSupportedException(String.format("Handler %s is not supported", handlerName)));
    }

}
