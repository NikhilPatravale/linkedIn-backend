package com.linkedIn.linkedIn.features.feed.repository;

import com.linkedIn.linkedIn.features.feed.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
