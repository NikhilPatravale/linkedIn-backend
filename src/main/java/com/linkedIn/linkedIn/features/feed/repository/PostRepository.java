package com.linkedIn.linkedIn.features.feed.repository;

import com.linkedIn.linkedIn.features.feed.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByAuthorIdNotOrderByCreationDateTimeDesc(Long authorId);

    List<Post> findAllByAuthorIdOrderByCreationDateTimeDesc(Long authorId);

    List<Post> findAllByOrderByCreationDateTimeDesc();
}
