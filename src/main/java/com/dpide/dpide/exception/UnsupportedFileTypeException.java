package com.dpide.dpide.exception;

import lombok.Getter;

@Getter
public class UnsupportedFileTypeException extends RuntimeException{
    private final String extension;

    public UnsupportedFileTypeException(String extension) {
        this.extension = extension;
    }
}
