package com.dpide.dpide.exception;

import lombok.Getter;

import java.security.Principal;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final Object userId;
    private Principal principal;

    public UserNotFoundException(Object userId) {
        this.userId = userId;
    }

    public UserNotFoundException(Object userId, Principal principal) {
        this.userId = userId;
        this.principal = principal;
    }
}