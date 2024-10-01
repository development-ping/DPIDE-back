package com.dpide.dpide.user.service;

import com.dpide.dpide.user.config.TokenProvider;
import com.dpide.dpide.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    // 리프레시 토큰으로 새 액세스 토큰을 생성하는 로직
    public String createNewAccessToken(String refreshToken) {
        log.info("Received request to create a new Access Token using refresh token: {}", refreshToken);

        // 토큰 유효성 검사에 실패하면 예외 발생
        if (!tokenProvider.validToken(refreshToken)) {
            log.error("Invalid refresh token: {}", refreshToken);
            throw new IllegalArgumentException("Unexpected token");
        }

        log.info("Refresh token validated successfully");

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        log.info("Fetched user ID: {} using refresh token", userId);

        User user = userService.findById(userId);
        log.info("User information retrieved for ID: {}", userId);

        log.info("Creating new Access Token for user ID: {}", userId);
        String newAccessToken = tokenProvider.generateToken(user, Duration.ofHours(2));
        log.info("New Access Token created successfully for user ID: {}", userId);

        return newAccessToken;
    }
}
