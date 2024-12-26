package com.linkedIn.linkedIn.configuration;

import com.linkedIn.linkedIn.features.authentication.utils.Encoder;
import com.linkedIn.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.authentication.repository.AuthenticationUserRepository;
import com.linkedIn.linkedIn.features.feed.model.Post;
import com.linkedIn.linkedIn.features.feed.repository.PostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class LoadDbConfiguration {
    private final Encoder encoder;

    public LoadDbConfiguration(Encoder encoder) {
        this.encoder = encoder;
    }

    @Bean
    public CommandLineRunner initDb(AuthenticationUserRepository authenticationUserRepository, PostRepository postRepository) {
        return args -> {
            List<AuthenticationUser> authenticationUsers = createUsers(authenticationUserRepository);
            createPosts(postRepository, authenticationUsers);
        };
    }

    public List<AuthenticationUser> createUsers(AuthenticationUserRepository authenticationUserRepository) {
        List<AuthenticationUser> users = new ArrayList<>();
        users.add(createUser(authenticationUserRepository,"nikhil.p@example.com", "nikhil", "Nikhil", "Patravale", "American Express", "Bangalore", "Engineer II", ""));
        users.add(createUser(authenticationUserRepository, "rohit.p@example.com", "rohit", "Rohit", "Patil", "Rheomold", "Pune", "Design Engineer", ""));
        users.add(createUser(authenticationUserRepository, "prasad.m@example.com", "prasad", "Prasad", "Matade", "Deollite", "Pune", "Consultant", ""));
        users.add(createUser(authenticationUserRepository, "suraj.p@example.com", "suraj", "Suraj", "Patil", "Tata Motors", "Pune", "Supply Chain Manager", ""));
        users.add(createUser(authenticationUserRepository, "ruturaj.j@example.com", "ruturaj", "Ruturaj", "Jagtap", "Self employed", "Pune", "CA", ""));
        users.add(createUser(authenticationUserRepository, "rajvardhan.g@example.com", "rajvardhan", "Rajvardhan", "Gadekar", "Aven Engineering", "Pune", "Co-founder", ""));
        return users;
    }

    public AuthenticationUser createUser(AuthenticationUserRepository authenticationUserRepository, String email, String password, String firstName, String lastName, String company, String location, String position, String profilePicture) {
        AuthenticationUser user = new AuthenticationUser(email, encoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCompany(company);
        user.setPosition(position);
        user.setLocation(location);
        user.setProfilePicture(profilePicture);

        return authenticationUserRepository.save(user);
    }

    public void createPosts(PostRepository postRepository, List<AuthenticationUser> users) {
        Random random = new Random();
        for(int i=1; i<=6; i++) {
            AuthenticationUser user = users.get(random.nextInt(users.size()));
            Post post = new Post("Hi there, this is my first post", user);
            post.setLikes(getPostLikes(users, i, random));
            postRepository.save(post);
        }
    }

    private Set<AuthenticationUser> getPostLikes(List<AuthenticationUser> users, int postId, Random random) {
        Set<AuthenticationUser> postLikes = new HashSet<>();

        if (postId < 3) {
            while (postLikes.size() < 3) {
                AuthenticationUser user = users.get(random.nextInt(users.size()));
                postLikes.add(user);
            }
        } else {
            int likeCount = switch (postId % 4) {
                case 0 -> 2;
                case 2, 3 -> 3;
                default -> 1;
            };
            for (int k=1; k <= likeCount; k++) {
                AuthenticationUser user = users.get(random.nextInt(users.size()));
                postLikes.add(user);
            }
        }

        return postLikes;
    }
}
