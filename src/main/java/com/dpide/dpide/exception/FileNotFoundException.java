package com.dpide.dpide.exception;

import lombok.Getter;

@Getter
public class FileNotFoundException extends RuntimeException{
    private final Long fileId;

    public FileNotFoundException(Long fileId) {
        this.fileId = fileId;
    }
}
