package com.ltl.mpmp_lab3.constants;

public enum IntentExtra {

//    public static final String POINTS_EXTRA = "points";
//    public static final String RECORD_EXTRA = "record";
//    public static final String DISPLAY_NAME_EXTRA = "displayName";
//    public static final String IS_EMAIL_ENABLED_EXTRA = "email";
//    public static final String USER_EMAIL_EXTRA = "userEmail";

    POINTS_EXTRA("points"),
    RECORD_EXTRA("record"),
    DISPLAY_NAME_EXTRA("displayName"),
    IS_EMAIL_ENABLED_EXTRA("email"),
    USER_EMAIL_EXTRA("userEmail");

    private final String value;

    IntentExtra(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
