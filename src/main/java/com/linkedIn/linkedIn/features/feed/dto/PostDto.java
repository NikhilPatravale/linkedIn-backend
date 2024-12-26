package com.linkedIn.linkedIn.features.feed.dto;

import jakarta.validation.constraints.NotNull;

public class PostDto {
    @NotNull
    private String content;
    private String picture;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
