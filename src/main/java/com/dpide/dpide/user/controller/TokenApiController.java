package com.dpide.dpide.user.controller;

import com.dpide.dpide.dto.Response.ErrorResponse;
import com.dpide.dpide.dto.Response.SuccessResponse;
import com.dpide.dpide.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@Slf4j  // SLF4J 로그 사용
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/user/token")
    public ResponseEntity<?> createNewAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        log.info("Received request to create new access token with refresh token: {}", refreshToken);

        try {
            // 새로운 액세스 토큰 생성
            String newAccessToken = tokenService.createNewAccessToken(refreshToken);
            log.info("New access token created successfully for refresh token: {}", refreshToken);

            // 새로 발급된 액세스 토큰을 헤더에 추가
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);

            // 성공적인 응답 객체 반환
            SuccessResponse responseBody = new SuccessResponse(201, "New access token created successfully");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .headers(headers)
                    .body(responseBody);

        } catch (IllegalArgumentException e) {
            // 리프레시 토큰이 유효하지 않을 때
            log.warn("Invalid refresh token provided: {}", refreshToken);
            ErrorResponse errorResponse = new ErrorResponse(401, "Invalid refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);

        } catch (Exception e) {
            // 서버 오류 처리
            log.error("Server error occurred while creating new access token", e);
            ErrorResponse errorResponse = new ErrorResponse(500, "Server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

}
