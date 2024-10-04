package com.dpide.dpide.exception;

import lombok.Getter;

@Getter
public class FileOperationException extends RuntimeException{
    private final String fileName;

    public FileOperationException(String fileName) {
        this.fileName = fileName;
    }
}
