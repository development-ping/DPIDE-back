package com.dpide.dpide.dto;

import com.dpide.dpide.domain.File;
import lombok.*;

import java.time.LocalDateTime;

public class FileDto {
    @Builder
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreationReq {
        private String name;
        private String extension;
        private String path;
        private Long parentId;
    }

    @Builder
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExecutionReq {
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
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileInfoRes {
        private Long id;
        private String name;
        private String extension;
        private Long projectId;
        private Long parentId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static FileInfoRes of(File file) {
            return FileInfoRes.builder()
                    .id(file.getId())
                    .name(file.getName())
                    .extension(file.getExtension())
                    .projectId(file.getProject().getId())
                    .parentId(file.getParentFile() == null ? -1 : file.getParentFile().getId())
                    .createdAt(file.getCreatedAt())
                    .updatedAt(file.getUpdatedAt())
                    .build();
        }
    }
}
