package com.dpide.dpide.exception;

import lombok.Getter;

@Getter
public class ProjectOwnershipException extends RuntimeException{
    private final Long projectId;
    private final Long userId;

    public ProjectOwnershipException(Long projectId, Long userId) {
        this.projectId = projectId;
        this.userId = userId;
    }
}
