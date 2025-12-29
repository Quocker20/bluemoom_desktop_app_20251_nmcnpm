package com.bluemoon.app.util;

public class Result {
    private boolean success;
    private String message;

    public Result(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public Result() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
