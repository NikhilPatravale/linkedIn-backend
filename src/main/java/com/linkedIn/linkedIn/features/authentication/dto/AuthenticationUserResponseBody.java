package com.linkedIn.linkedIn.features.authentication.dto;

public class AuthenticationUserResponseBody {
    private final String token;
    private final String message;

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
