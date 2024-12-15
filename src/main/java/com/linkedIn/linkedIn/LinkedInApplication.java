package com.linkedIn.linkedIn;

import com.linkedIn.linkedIn.features.authorisation.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.authorisation.repository.AuthenticationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LinkedInApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkedInApplication.class, args);
	}

}
