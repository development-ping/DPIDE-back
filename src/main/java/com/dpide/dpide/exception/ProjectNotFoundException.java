package com.dpide.dpide.exception;

import lombok.Getter;

@Getter
public class ProjectNotFoundException extends RuntimeException{
    private final Long projectId;

    public ProjectNotFoundException(Long projectId) {
        this.projectId = projectId;
    }
}
