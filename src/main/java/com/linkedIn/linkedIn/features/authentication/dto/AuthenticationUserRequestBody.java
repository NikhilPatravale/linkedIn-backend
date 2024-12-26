package com.linkedIn.linkedIn.features.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthenticationUserRequestBody {
    @NotBlank(message = "Email is mandatory field")
    @Email(message = "Email should be in correct format")
    private String email;
    @NotBlank(message = "Password is mandatory field")
    private String password;

    public AuthenticationUserRequestBody(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
