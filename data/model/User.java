package com.ltl.mpmp_lab3.data.model;

public class User {

    private String displayName;
    private String email;
    private Boolean sendResult = false;
    private Long  record = 0L;
    private String password;



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

    public User(String displayName, String email, String password) {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
