package com.razzzil.sphynx.commons.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@AllArgsConstructor
@Getter
public enum ReservedHandlerNames {
    HANDSHAKE("handshake"),
    CLOSE("close");
    private String name;

    public static ReservedHandlerNames valueOfNames(String name){
        return Arrays
                .stream(values())
                .filter(value -> value.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static boolean containsName(String name){
        return Objects.nonNull(valueOfNames(name));
    }


}
