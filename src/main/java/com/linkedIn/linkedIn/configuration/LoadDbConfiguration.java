package com.linkedIn.linkedIn.configuration;

import com.linkedIn.linkedIn.features.authorisation.utils.Encoder;
import com.linkedIn.linkedIn.features.authorisation.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.authorisation.repository.AuthenticationUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDbConfiguration {
    private final Encoder encoder;

    public LoadDbConfiguration(Encoder encoder) {
        this.encoder = encoder;
    }

    @Bean
    public CommandLineRunner initDb(AuthenticationUserRepository authenticationUserRepository) {
        return args -> {
            AuthenticationUser authenticationUser = new AuthenticationUser("nikhil.p@example.com", encoder.encode("2373"));
            authenticationUserRepository.save(authenticationUser);
        };
    }
}
