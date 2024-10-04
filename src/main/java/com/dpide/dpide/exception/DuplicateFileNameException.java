package com.dpide.dpide.exception;

import lombok.Getter;

@Getter
public class DuplicateFileNameException extends RuntimeException{
    private final String fileName;

    public DuplicateFileNameException(String fileName) {
        this.fileName = fileName;
    }
}
