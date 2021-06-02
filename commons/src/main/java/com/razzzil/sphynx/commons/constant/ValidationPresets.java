package com.razzzil.sphynx.commons.constant;

public class ValidationPresets {

    private static final String FIELD_IS_NULL = "%s is null";
    private static final String FIELD_IS_INCORRECT = "%s is incorrect";

    public static String formatCustom(String format, Object... objects){
        return String.format(format, objects);
    }

    public static String formatIsNull(String fieldName){
        return formatCustom(FIELD_IS_NULL, fieldName);
    }

    public static String formatIsIncorrect(String fieldName){
        return formatCustom(FIELD_IS_INCORRECT, fieldName);
    }


}
