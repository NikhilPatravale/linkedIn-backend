package com.linkedIn.linkedIn.features.authorisation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class PasswordResetRequestBody {
    @NotBlank(message = "Email should be present")
    @Email(message = "Email should be in correct format")
    String email;

    @NotBlank(message = "New password can not be empty")
    String newPassword;

    @NotBlank(message = "Password reset failed due to empty token")
    String token;

    public PasswordResetRequestBody(String email, String newPassword, String token) {
        this.email = email;
        this.newPassword = newPassword;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
