package com.dpide.dpide.exception;

import lombok.Getter;

@Getter
public class EmailNotFoundException extends RuntimeException{
    private final String email;
    public EmailNotFoundException(String email) { this.email = email; }
}
