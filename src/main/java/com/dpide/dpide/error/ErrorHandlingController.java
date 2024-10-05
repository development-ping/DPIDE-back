package com.dpide.dpide.error;

import com.dpide.dpide.exception.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
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

    @ExceptionHandler(DuplicateFileNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleDuplicateFileNameException(DuplicateFileNameException e) {
        log.error("이미 존재하는 파일명입니다.");
        return buildError(ErrorCode.DUPLICATE_FILE_NAME);
    }

    @ExceptionHandler(FileOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleFileCreationException(FileOperationException e) {
        log.error("파일 작업에 실패하였습니다.");
        return buildError(ErrorCode.FILE_OPERATION_FAILED);
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

    @ExceptionHandler(UserAlreadyParticipantException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleUserAlreadyParticipantException(UserAlreadyParticipantException e) {
        log.warn("이미 참여중인 유저입니다.");
        return buildError(ErrorCode.USER_ALREADY_PARTICIPANT);
    }

    @ExceptionHandler(InvalidAlarmIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleInvalidAlarmIdException(InvalidAlarmIdException e) {
        log.warn("존재하지 않는 알람 ID 입니다.");
        return buildError(ErrorCode.INVALID_ALARM);
    }

    @ExceptionHandler(DuplicateAlarmException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleDuplicateAlarmException(DuplicateAlarmException e) {
        log.warn("이미 보낸 알림이 존재합니다.");
        return buildError(ErrorCode.DUPLICATE_ALARM);
    }

    @ExceptionHandler(UnsupportedFileTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleUnsupportedFileTypeException(UnsupportedFileTypeException e) {
        log.warn("지원하지 않는 파일 형식입니다.");
        return buildError(ErrorCode.UNSUPPORTED_FILE_TYPE);
    }
}