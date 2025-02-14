package com.linkedIn.linkedIn.features.messaging.dto;

import jakarta.validation.constraints.NotNull;

public class MessageDto {
    @NotNull
    public String content;

    public Long receiverId;

    public MessageDto() {
    }

    public MessageDto(String content, Long receiverId) {
        this.content = content;
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
}
