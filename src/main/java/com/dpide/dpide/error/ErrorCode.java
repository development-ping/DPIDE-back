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
    PROJECT_OWNERSHIP("PROJECT_OWNERSHIP", "프로젝트 소유자만 가능한 요청입니다.", 400),
    DUPLICATE_FILE_NAME("DUPLICATE_FILE_NAME", "이미 존재하는 파일명입니다.", 400),
    FILE_OPERATION_FAILED("FILE_OPERATION_FAILED", "파일 작업에 실패하였습니다.", 400),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 JWT 토큰입니다.", 400),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다", 400),
    EMAIL_NOT_FOUND("EMAIL_NOT_FOUND", "이메일을 찾을 수 없습니다.", 400),
    INCORRECT_PASSWORD("INCORRECT_PASSWORD", "비밀번호가 틀렸습니다.", 400),
    USER_ALREADY_PARTICIPANT("USER_ALREADY_PARTICIPANT", "이미 참여중인 유저입니다", 400),
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