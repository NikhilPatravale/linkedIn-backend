package com.linkedIn.linkedIn.features.feed.services;

import com.linkedIn.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.authentication.repository.AuthenticationUserRepository;
import com.linkedIn.linkedIn.features.feed.dto.CommentDto;
import com.linkedIn.linkedIn.features.feed.dto.PostDto;
import com.linkedIn.linkedIn.features.feed.model.Comment;
import com.linkedIn.linkedIn.features.feed.model.Post;
import com.linkedIn.linkedIn.features.feed.repository.CommentRepository;
import com.linkedIn.linkedIn.features.feed.repository.PostRepository;
import com.linkedIn.linkedIn.features.notifications.services.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PostsService {
    private final PostRepository postRepository;
    private final AuthenticationUserRepository authenticationUserRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    public PostsService(PostRepository postRepository, AuthenticationUserRepository authenticationUserRepository, CommentRepository commentRepository, NotificationService notificationService) {
        this.postRepository = postRepository;
        this.authenticationUserRepository = authenticationUserRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
    }

    public Post create(PostDto postDto, Long authorId) {
        AuthenticationUser user = authenticationUserRepository.findById(authorId).orElseThrow(() -> new IllegalArgumentException("Incorrect user id"));
        Post post = new Post(postDto.getContent(), user);
        if(null != postDto.getContent()) post.setPicture(postDto.getPicture());
        return postRepository.save(post);
    }

    public Post update(Long postId, PostDto postDto, Long authorId) {
        AuthenticationUser user = authenticationUserRepository.findById(authorId).orElseThrow(() -> new IllegalArgumentException("Incorrect user id"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getAuthor().equals(user)) {
            throw new IllegalArgumentException("User is not author of post");
        }

        if(null != postDto.getContent()) post.setContent(postDto.getContent());
        if(null != postDto.getPicture()) post.setPicture(postDto.getPicture());
        return postRepository.save(post);
    }

    public List<Post> getFeed(Long authorId) {
        AuthenticationUser user = authenticationUserRepository.findById(authorId).orElseThrow(() -> new IllegalArgumentException("Incorrect user id"));
        return postRepository.findAllByAuthorIdNotOrderByCreationDateTimeDesc(authorId);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreationDateTimeDesc();
    }

    public void deletePost(Long userId, Long postId) {
        AuthenticationUser user = authenticationUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post postToDelete = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (!user.getId().equals(postToDelete.getAuthor().getId())) {
            throw new IllegalArgumentException("User is not author of post");
        }
        postRepository.deleteById(postId);
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    public List<Post> getPostsByUserId(Long userId) {
        AuthenticationUser user = authenticationUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return postRepository.findAllByAuthorIdOrderByCreationDateTimeDesc(userId);
    }

    public Post addPostLike(Long userId, Long postId) {
        AuthenticationUser user = authenticationUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (post.getLikes().contains(user)) {
            post.getLikes().remove(user);
        } else {
            post.getLikes().add(user);
            notificationService.sendLikeNotification(user, post.getAuthor(), post.getId());
        }
        Post savedPost = postRepository.save(post);
        notificationService.sendLikeToPost(post.getId(), post.getLikes());
        return savedPost;
    }

    public Comment addComment(Long postId, Long userId, CommentDto commentDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        AuthenticationUser user = authenticationUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Comment comment = new Comment(commentDto.getContent(), user, post);
        notificationService.sendCommentNotification(user, post.getAuthor(), post.getId());
        Comment savedComment = commentRepository.save(comment);
        notificationService.sendCommentToPost(post.getId(), savedComment);
        return savedComment;
    }

    public Comment updateComment(Long commentId, Long userId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        AuthenticationUser user = authenticationUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!comment.getAuthor().equals(user)) {
            throw new IllegalArgumentException("User is not author of comment");
        }
        comment.setContent(commentDto.getContent());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        AuthenticationUser user = authenticationUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!comment.getAuthor().equals(user)) {
            throw new IllegalArgumentException("User is not author of comment");
        }
        commentRepository.deleteById(commentId);
    }

    public List<Comment> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return post.getComments();
    }

    public Set<AuthenticationUser> getPostLikes(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return post.getLikes();
    }
}
