package com.razzzil.sphynx.commons.constant;

import java.util.regex.Pattern;

public class Regex {
    public static final Pattern VALID_IPV4_ADDRESS_REGEX =
            Pattern.compile("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_IPV6_ADDRESS_REGEX =
            Pattern.compile("(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,10}$", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_LOGIN_REGEX =
            Pattern.compile("[a-zA-Z0-9]", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_PORT_NUMBER =
            Pattern.compile("^((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{0,5})|([0-9]{1,4}))$", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_HOST_REGEX =
            Pattern.compile("^((?!-)[A-Za-z0–9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_ADDRESS_REGEX =
            Pattern.compile("^(" + VALID_IPV4_ADDRESS_REGEX.pattern() + "|" + VALID_IPV6_ADDRESS_REGEX.pattern() + "|" + VALID_HOST_REGEX.pattern() + ")$", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_INCREMENT_REGEX =
            Pattern.compile("^([*+])\\d\\b", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_VERSION_REGEX =
            Pattern.compile("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_VALUE =
            Pattern.compile("(?!\\s*$).+|null|undefined", Pattern.CASE_INSENSITIVE);
}
