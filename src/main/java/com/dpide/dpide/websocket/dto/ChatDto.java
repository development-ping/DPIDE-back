package com.dpide.dpide.websocket.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class ChatDto {
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessage {
        private String sender;
        private String content;
        private Long projectId;
        private Long userId;
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatSearch {
        private Long projectId;
        private String keyword;
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatInfo {
        private String senderName;
        private String content;
        private LocalDateTime createdAt;
    }
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatListRes {
        private List<ChatInfo> chatInfoList;
    }
}
