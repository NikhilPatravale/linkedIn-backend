package com.linkedIn.linkedIn.features.authentication.controller;

import com.linkedIn.linkedIn.features.authentication.dto.*;
import com.linkedIn.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.authentication.service.AuthenticationUserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationUserController {

    public static final Logger logger = LoggerFactory.getLogger(AuthenticationUserService.class);

    @Autowired
    private AuthenticationUserService authenticationUserService;

    @GetMapping("/user")
    public AuthenticationUser getUser(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser) {
        return authenticationUserService.findByEmail(authenticationUser.getEmail());
    }

    @GetMapping("/users")
    public List<AuthenticationUser> getAllUsersExceptAuthenticated(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        return authenticationUserService.getAllUsersExceptAuthenticated(user.getId());
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
    public ResponseEntity<GenericMessageResponseBody> sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationUserService.sendEmailVerificationToken(user.getEmail());
        return ResponseEntity.ok(new GenericMessageResponseBody("Email verification token sent successfully"));
    }

    @PutMapping("/validate-email-verification-token")
    public ResponseEntity<GenericMessageResponseBody> validateEmailVerificationToken(@RequestParam String token, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationUserService.validateEmailVerificationToken(token, user.getEmail());
        return ResponseEntity.ok(new GenericMessageResponseBody("Email verified successfully"));
    }

    @PutMapping("/send-password-reset-token")
    public ResponseEntity<GenericMessageResponseBody>  sendResetPasswordToken(@RequestParam String email) {
        authenticationUserService.sendPasswordResetToken(email);
        return ResponseEntity.ok(new GenericMessageResponseBody("Password reset token sent successfully"));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<GenericMessageResponseBody> resetPassword(@RequestBody PasswordResetRequestBody passwordResetRequestBody) {
        authenticationUserService.resetPassword(passwordResetRequestBody.getToken(), passwordResetRequestBody.getEmail(), passwordResetRequestBody.getNewPassword());
        return ResponseEntity.ok(new GenericMessageResponseBody("Password reset successfully"));
    }

    @PutMapping("/profile/{id}")
    public AuthenticationUser updateProfile(@PathVariable Long id, @RequestBody UserProfileUpdateRequest profileUpdateRequest) {
        return authenticationUserService.updateProfile(id, profileUpdateRequest);
    }

    @DeleteMapping("/profile/{id}")
    public ResponseEntity<String> deleteProfile(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser) {
        authenticationUserService.delete(authenticationUser.getId());
        return ResponseEntity.ok("User deleted successfully");
    }
}
