package com.linkedIn.linkedIn.features.authorisation.service;

import com.linkedIn.linkedIn.features.authorisation.utils.EmailService;
import com.linkedIn.linkedIn.features.authorisation.utils.Encoder;
import com.linkedIn.linkedIn.features.authorisation.dto.AuthenticationUserRequestBody;
import com.linkedIn.linkedIn.features.authorisation.dto.AuthenticationUserResponseBody;
import com.linkedIn.linkedIn.features.authorisation.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.authorisation.repository.AuthenticationUserRepository;
import com.linkedIn.linkedIn.features.authorisation.utils.JsonWebToken;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthenticationUserService {

    @Autowired
    private Encoder encoder;

    @Autowired
    private JsonWebToken jsonWebToken;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationUserRepository authenticationUserRepository;

    public static final Logger logger = LoggerFactory.getLogger(AuthenticationUserService.class);
    private final int tokenExpiryDurationInMinutes = 1;

    private static String generateVerificationToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(random.nextInt(10));
        }
        return token.toString();
    }

    private void sendEmailVerificationMail(String email) {
        Optional<AuthenticationUser> user = authenticationUserRepository.findByEmail(email);
        if (user.isPresent() && !user.get().isEmailVerified()) {
           String verificationToken = generateVerificationToken();
           String hashedToken = encoder.encode(verificationToken);
           user.get().setEmailVerificationToken(hashedToken);
           user.get().setEmailVerificationTokenExpiry(LocalDateTime.now().plusMinutes(tokenExpiryDurationInMinutes));
           authenticationUserRepository.save(user.get());
           String subject = "Email verification";
           String content = String.format("""
                   Only one step to take full advantage of linkedIn.\s
                   
                   Enter this code to verify your email: \
                   %s
                   
                   The code expires in %s minutes""", verificationToken, tokenExpiryDurationInMinutes);
           try {
               emailService.sendEmail(user.get().getEmail(), subject, content);
           } catch (Exception e) {
               logger.info("Error while sending mail: {}", e.getMessage());
           }
        } else {
            throw new IllegalArgumentException("Email verification failed. Email is already verified");
        }
    }

    public void sendPasswordResetTokenMail(String email) {
        Optional<AuthenticationUser> user = authenticationUserRepository.findByEmail(email);

        if (user.isPresent()) {
            String token = generateVerificationToken();
            String hashedToken = encoder.encode(token);
            user.get().setPasswordResetToken(hashedToken);
            user.get().setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(tokenExpiryDurationInMinutes));
            authenticationUserRepository.save(user.get());

            String subject = "Password reset token";
            String content = String.format("""
                    Enter this code to reset your password: %s
                    
                    This token expires in %s minutes
                    """, token, tokenExpiryDurationInMinutes);
            try{
                emailService.sendEmail(email, subject, content);
            } catch (Exception e) {
                logger.info("Error while sending password reset token email: {}", e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Password reset failed");
        }
    }

    public void validateEmailVerificationToken(String token, String email) {
        Optional<AuthenticationUser> user = authenticationUserRepository.findByEmail(email);
        if (user.isPresent() && encoder.matches(token, user.get().getEmailVerificationToken()) && !user.get().getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            user.get().setEmailVerified(true);
            user.get().setEmailVerificationToken(null);
            user.get().setEmailVerificationTokenExpiry(null);
            authenticationUserRepository.save(user.get());
        } else if (user.isPresent() && encoder.matches(token, user.get().getEmailVerificationToken()) && user.get().getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Email verification token is expired");
        } else {
            throw new IllegalArgumentException("Email verification failed");
        }
    }

    public AuthenticationUser findByEmail(String email) throws IllegalArgumentException{
        return authenticationUserRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not present"));
    }

    public AuthenticationUserResponseBody register(AuthenticationUserRequestBody authenticationUserRequestBody) throws MessagingException, UnsupportedEncodingException {
        AuthenticationUser newUser = new AuthenticationUser(authenticationUserRequestBody.getEmail(), encoder.encode(authenticationUserRequestBody.getPassword()));
        authenticationUserRepository.save(newUser);
        String token = jsonWebToken.generateToken(newUser.getEmail());
        sendEmailVerificationMail(newUser.getEmail());
        return new AuthenticationUserResponseBody(token, "User is successfully registered");
    }

    public AuthenticationUserResponseBody login(AuthenticationUserRequestBody authenticationUserRequestBody) {
        AuthenticationUser user = authenticationUserRepository.findByEmail(authenticationUserRequestBody.getEmail()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!encoder.matches(authenticationUserRequestBody.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }
        String token = jsonWebToken.generateToken(user.getEmail());
        return new AuthenticationUserResponseBody(token, "Authentication successful");
    }

    public void sendPasswordResetToken(String email) {
        sendPasswordResetTokenMail(email);
    }

    public void sendEmailVerificationToken(String email) {
        sendEmailVerificationMail(email);
    }

    public void resetPassword(String token, String email, String newPassword) {
        Optional<AuthenticationUser> user = authenticationUserRepository.findByEmail(email);

        if (user.isPresent()) {
            boolean isTokenMatching = encoder.matches(token, user.get().getPasswordResetToken());
            boolean isTokenExpired = user.get().getPasswordResetTokenExpiry().isBefore(LocalDateTime.now());
            if (isTokenMatching && !isTokenExpired) {
                String hashedPassword = encoder.encode(newPassword);
                user.get().setPassword(hashedPassword);
                authenticationUserRepository.save(user.get());
            } else if (isTokenMatching){
                throw new IllegalArgumentException("Password reset token expired");
            } else {
                throw new IllegalArgumentException("Password reset failed");
            }
        } else {
            throw new IllegalArgumentException("Password reset failed");
        }
    }
}
