package com.linkedIn.linkedIn.features.authorisation.controller;

import com.linkedIn.linkedIn.features.authorisation.dto.AuthenticationUserRequestBody;
import com.linkedIn.linkedIn.features.authorisation.dto.AuthenticationUserResponseBody;
import com.linkedIn.linkedIn.features.authorisation.dto.PasswordResetRequestBody;
import com.linkedIn.linkedIn.features.authorisation.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.authorisation.service.AuthenticationUserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationUserController {

    public static final Logger logger = LoggerFactory.getLogger(AuthenticationUserService.class);

    @Autowired
    private AuthenticationUserService authenticationUserService;

    @GetMapping("/user")
    public AuthenticationUser getUser(@RequestAttribute("user") AuthenticationUser authenticationUser) {
        return authenticationUserService.findByEmail(authenticationUser.getEmail());
    }

    @PostMapping("/login")
    public AuthenticationUserResponseBody login(@Valid @RequestBody AuthenticationUserRequestBody authenticationUserRequestBody) {
        return authenticationUserService.login(authenticationUserRequestBody);
    }

    @PostMapping("/register")
    public AuthenticationUserResponseBody registerUser(@Valid @RequestBody AuthenticationUserRequestBody authenticationUserRequestBody) throws MessagingException, UnsupportedEncodingException {
        return authenticationUserService.register(authenticationUserRequestBody);
    }

    @PutMapping("/send-email-verification-token")
    public String sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationUserService.sendEmailVerificationToken(user.getEmail());
        return "Email verification token sent successfully";
    }

    @PutMapping("/validate-email-verification-token")
    public String validateEmailVerificationToken(@RequestParam String token, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationUserService.validateEmailVerificationToken(token, user.getEmail());
        return "Email verified successfully";
    }

    @PutMapping("/send-password-reset-token")
    public String  sendResetPasswordToken(@RequestParam String email) {
        authenticationUserService.sendPasswordResetToken(email);
        return "Password reset token sent successfully";
    }

    @PutMapping("/reset-password")
    public String resetPassword(@RequestBody PasswordResetRequestBody passwordResetRequestBody) {
        authenticationUserService.resetPassword(passwordResetRequestBody.getToken(), passwordResetRequestBody.getEmail(), passwordResetRequestBody.getNewPassword());
        return "Password reset successfully";
    }
}
