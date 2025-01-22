package com.linkedIn.linkedIn.features.authentication.dto;

public class GenericMessageResponseBody {
    private String message;

    public GenericMessageResponseBody(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
