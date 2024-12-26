package com.linkedIn.linkedIn.features.feed.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentDto {
    @NotBlank
    private String content;

    CommentDto(String content) {
        this.content = content;
    }

    CommentDto() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
