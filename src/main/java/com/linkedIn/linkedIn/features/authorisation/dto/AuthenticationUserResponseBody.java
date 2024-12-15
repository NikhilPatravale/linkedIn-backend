package com.linkedIn.linkedIn.features.authorisation.dto;

public class AuthenticationUserResponseBody {
    private String token;
    private String message;

    public AuthenticationUserResponseBody(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}
