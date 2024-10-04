package com.dpide.dpide.user.controller;

import com.dpide.dpide.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j  // SLF4J 로그 사용
public class TokenApiController {

    private final TokenService tokenService;

    // 리프레시 토큰으로 액세스 토큰 재발급
    @PostMapping("/user/token")
    public ResponseEntity<Void> createNewAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        log.info("Received request to create new access token");

        String newAccessToken = tokenService.createNewAccessToken(refreshToken);

        // 새로운 액세스 토큰을 헤더에 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .build();
    }
}
