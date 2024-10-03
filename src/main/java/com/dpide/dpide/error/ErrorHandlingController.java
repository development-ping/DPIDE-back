package com.dpide.dpide.error;

import com.dpide.dpide.exception.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.dpide.dpide.util.ErrorBuildFactory.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandlingController {
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleEntityNotFoundException() {
        log.error("해당 엔티티를 찾을 수 없습니다.");
        return buildError(ErrorCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        log.error("해당 유저를 찾을 수 없습니다.");
        return buildError(ErrorCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleDuplicateNicknameException(DuplicateNicknameException e) {
        log.error("이미 존재하는 닉네임입니다.");
        return buildError(ErrorCode.DUPLICATE_NICKNAME);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleDuplicateEmailException(DuplicateEmailException e) {
        log.error("이미 존재하는 이메일입니다. {}", e.getEmail());
        return buildError(ErrorCode.DUPLICATE_EMAIL);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleProjectNotFoundException(ProjectNotFoundException e) {
        log.error("해당 프로젝트를 찾을 수 없습니다.");
        return buildError(ErrorCode.PROJECT_NOT_FOUND);
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleFileNotFoundException(FileNotFoundException e) {
        log.error("해당 파일을 찾을 수 없습니다.");
        return buildError(ErrorCode.FILE_NOT_FOUND);
    }

    @ExceptionHandler(ProjectOwnershipException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleProjectOwnershipException(ProjectOwnershipException e) {
        log.error("프로젝트 소유자만 가능한 요청입니다.");
        return buildError(ErrorCode.PROJECT_OWNERSHIP);
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ErrorResponse handleInvalidTokenException(InvalidTokenException e) {
        log.error("유효하지 않은 JWT 토큰입니다.");
        return buildError(ErrorCode.INVALID_TOKEN);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        log.warn("유효하지 않은 리프레시 토큰입니다.");
        return buildError(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse handleEmailNotFoundException(EmailNotFoundException e) {
        log.warn("이메일을 찾을 수 없습니다. : {}", e.getEmail());
        return buildError(ErrorCode.EMAIL_NOT_FOUND);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ErrorResponse handleIncorrectPasswordException(IncorrectPasswordException e) {
        log.warn("비밀번호가 틀렸습니다.");
        return buildError(ErrorCode.INCORRECT_PASSWORD);
    }
}