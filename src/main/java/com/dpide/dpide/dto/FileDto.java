package com.dpide.dpide.dto;

import com.dpide.dpide.domain.File;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Builder
    @Getter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileTreeRes {
        private String id;
        private String name;
        private String extension;
        private String parentId;
        private String path;
        private List<FileTreeRes> children;

        public static FileTreeRes of(File file) {
            return FileTreeRes.builder()
                    .id(file.getId().toString())
                    .name(file.getName())
                    .extension(file.getExtension())
                    .parentId(file.getParentFile() == null ? "-1" : file.getParentFile().getId().toString())
                    .path(file.getPath())
                    .children(file.getChildFiles().isEmpty() ? List.of() :
                            file.getChildFiles().stream().map(FileTreeRes::of).collect(Collectors.toList()))
                    .build();
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileTreeListRes {
        private List<FileTreeRes> files;
    }
}
