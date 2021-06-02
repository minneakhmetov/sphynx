package com.razzzil.sphynx.commons.model.worker;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SupportedVersions {
    private static final List<String> VERSIONS;

    static {
        VERSIONS = new ArrayList<>();
        VERSIONS.add("1.0.0");
    }

    public static List<String> getVersions() {
        return VERSIONS;
    }
}
