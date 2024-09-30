package com.dpide.dpide.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "해당 유저를 찾을 수 없습니다.", 400),
    DUPLICATE_NICKNAME("DUPLICATE_NICKNAME", "이미 존재하는 닉네임입니다.", 400),
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "이미 존재하는 이메일입니다.", 400),
    AUTHENTICATION_FAILED("AUTHENTICATION_FAILED", "인증에 실패하였습니다.", 400),
    PROJECT_NOT_FOUND("PROJECT_NOT_FOUND", "해당 프로젝트를 찾을 수 없습니다.", 400),
    FILE_NOT_FOUND("FILE_NOT_FOUND", "해당 파일을 찾을 수 없습니다.", 400),
    ;

    private final String code;
    private final String message;
    private final Integer status;

    ErrorCode(String code, String message, Integer status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
