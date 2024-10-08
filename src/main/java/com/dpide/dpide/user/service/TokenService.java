package com.dpide.dpide.user.service;

import com.dpide.dpide.exception.InvalidRefreshTokenException;
import com.dpide.dpide.user.config.TokenProvider;
import com.dpide.dpide.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    // 리프레시 토큰으로 새 액세스 토큰을 생성하는 로직
    // 리프레시 토큰으로 새 액세스 토큰을 생성하는 로직
    public String createNewAccessToken(String refreshToken) {
        log.info("Received request to create a new Access Token");

        validateRefreshToken(refreshToken); // 리프레시 토큰 유효성 검사

        Long userId = getUserIdFromRefreshToken(refreshToken);
        User user = getUserById(userId);

        log.info("Creating new Access Token for user ID: {}", userId);
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }

    // 리프레시 토큰 유효성 검증
    private void validateRefreshToken(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
        log.info("Refresh token validated successfully");
    }

    // 리프레시 토큰에서 사용자 ID를 추출
    private Long getUserIdFromRefreshToken(String refreshToken) {
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        log.info("Fetched user ID: {}", userId);
        return userId;
    }

    // 사용자 ID로 사용자 정보 조회
    private User getUserById(Long userId) {
        User user = userService.findById(userId);
        log.info("User information retrieved for ID: {}", userId);
        return user;
    }

}
