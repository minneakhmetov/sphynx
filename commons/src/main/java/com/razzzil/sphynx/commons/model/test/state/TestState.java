package com.razzzil.sphynx.commons.model.test.state;

import java.util.Arrays;
import java.util.List;

public enum TestState {
    CREATED, STARTED, PENDING, ORDERED, RUNNING, OVERDUE, CLEANING, SKIPPED, PAUSED, FAILED, FINISHED, TERMINATED, UNKNOWN;

    private static final List<TestState> FILTERED = Arrays.asList(TestState.CREATED);

    public static TestState get(String value){
        for (TestState testState: values()){
            if (testState.name().equals(value))
                return testState;
        }
       // return null;
        throw new IllegalArgumentException("Test state " + value + " is not supported");
    }

    public boolean isFiltered(){
        return FILTERED.contains(this);
    }

}
