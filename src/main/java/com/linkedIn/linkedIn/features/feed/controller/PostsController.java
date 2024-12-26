package com.linkedIn.linkedIn.features.feed.controller;

import com.linkedIn.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.linkedIn.features.feed.dto.CommentDto;
import com.linkedIn.linkedIn.features.feed.dto.PostDto;
import com.linkedIn.linkedIn.features.feed.model.Comment;
import com.linkedIn.linkedIn.features.feed.model.Post;
import com.linkedIn.linkedIn.features.feed.services.PostsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feed")
public class PostsController {
    public final PostsService postsService;

    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    // Endpoints for post related actions

    @GetMapping
    public ResponseEntity<List<Post>> getFeed(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        List<Post> posts = postsService.getFeed(user.getId());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        List<Post> posts = postsService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable Long postId) {
        Post post = postsService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable Long userId) {
        List<Post> posts = postsService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody PostDto postDto, @RequestAttribute("authenticatedUser")AuthenticationUser authenticationUser) {
        Post post = postsService.create(postDto, authenticationUser.getId());
        return ResponseEntity.ok(post);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody PostDto postDto, @RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser) {
        Post post = postsService.update(postId, postDto, authenticationUser.getId());
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@RequestAttribute("authenticatedUser") AuthenticationUser user,  @PathVariable Long postId) {
        postsService.deletePost(user.getId(), postId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint for post like

    @PutMapping("/posts/{postId}/like")
    public ResponseEntity<Post> addPostLike(@RequestAttribute("authenticatedUser") AuthenticationUser user, @PathVariable Long postId) {
        Post post = postsService.addPostLike(user.getId(), postId);
        return ResponseEntity.ok(post);
    }

    // Endpoints for post comments

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Comment>> getAllCommentsForPost(@PathVariable Long postId) {
        List<Comment> comments = postsService.getAllCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment> addComment(@RequestAttribute("authenticatedUser") AuthenticationUser user, @PathVariable Long postId, @RequestBody CommentDto commentDto) {
        Comment comment = postsService.addComment(postId, user.getId(), commentDto);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/posts/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@RequestAttribute("authenticatedUser") AuthenticationUser user, @PathVariable Long commentId, @RequestBody CommentDto commentDto) {
        Comment comment = postsService.updateComment(commentId, user.getId(), commentDto);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/posts/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@RequestAttribute("authenticatedUser") AuthenticationUser user, @PathVariable Long commentId) {
        postsService.deleteComment(user.getId(), commentId);
        return ResponseEntity.noContent().build();
    }
}
