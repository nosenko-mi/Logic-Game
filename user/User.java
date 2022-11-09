package com.ltl.mpmp_lab3.user;

public class User {

    private String displayName;
    private String email;
    private Boolean sendResult = false;
    private Long  record = 0L;

    @Override
    public String toString() {
        return "User{" +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", sendResult=" + sendResult +
                '}';
    }

    public User() {
    }

    public User(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getSendResult() {
        return sendResult;
    }

    public void setSendResult(Boolean sendResult) {
        this.sendResult = sendResult;
    }

    public Long getRecord() {
        return record;
    }

    public void setRecord(Long record) {
        this.record = record;
    }
}
