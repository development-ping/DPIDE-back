package com.dpide.dpide.dto;

import lombok.*;

import java.time.LocalDateTime;

public class FileDto {
    @Builder
    @Getter
    @Setter @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreationReq {
        private Long userId;
        private Long projectId;
        private String name;
        private String extension;
        private String path;
        private Long parentId;
    }

    @Builder
    @Getter @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeletionReq {
        private Long userId;
        private Long projectId;
        private Long fileId;
    }

    @Builder
    @Getter @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReadReq {
        private Long userId;
        private Long projectId;
        private Long fileId;
    }

    @Builder
    @Getter @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExecutionReq {
        private Long userId;
        private Long projectId;
        private Long fileId;
    }

//    @Builder
//    @Getter @ToString
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class FileListRes {
//
//    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class FileInfoRes {
        private Long fileId;
        private String name;
        private String extension;
        private Long projectId;
        private Long parentId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
