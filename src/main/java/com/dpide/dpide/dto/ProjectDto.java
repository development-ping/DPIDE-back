package com.dpide.dpide.dto;

import com.dpide.dpide.domain.Project;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectDto {
    @Builder
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreationReq {
        private String name;
        private String description;
        private String language;
    }

    @Builder
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectInfoRes {
        private Long id;
        private String name;
        private String description;
        private String language;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long userId;

        public static ProjectInfoRes of(Project project) {
            return ProjectInfoRes.builder()
                    .id(project.getId())
                    .name(project.getName())
                    .description(project.getDescription())
                    .language(project.getLanguage())
                    .createdAt(project.getCreatedAt())
                    .updatedAt(project.getUpdatedAt())
                    .userId(project.getUser().getId())
                    .build();
        }
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectListRes {
        private List<ProjectInfoRes> projects;
    }
}
