package com.dpide.dpide.user.service;

import com.dpide.dpide.exception.InvalidRefreshTokenException;
import com.dpide.dpide.user.domain.RefreshToken;
import com.dpide.dpide.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 리프레시 토큰 삭제
    @Transactional
    public void deleteByToken(String refreshToken) {
        log.info("Attempting to delete refresh token: {}", refreshToken);

        // 토큰을 조회하고 바로 삭제
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(InvalidRefreshTokenException::new);
        refreshTokenRepository.delete(token);

        log.info("Refresh token deleted successfully: {}", refreshToken);
    }

    // 리프레시 토큰 검색
    @Transactional(readOnly = true)
    public RefreshToken findByRefreshToken(String refreshToken) {
        log.info("Searching for refresh token: {}", refreshToken);
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(InvalidRefreshTokenException::new);
    }

    // 리프레시 토큰 저장
    @Transactional
    public void saveRefreshToken(Long userId, String refreshToken) {
        log.info("Saving refresh token for user ID: {}", userId);
        RefreshToken tokenEntity = refreshTokenRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("No existing refresh token found for user ID: {}, creating a new one", userId);
                    return new RefreshToken(userId, refreshToken);
                });
        log.info("Refresh token found or created for user ID: {}", userId);
        tokenEntity.update(refreshToken);
        refreshTokenRepository.save(tokenEntity);
        log.info("Refresh token updated successfully for user ID: {}", userId);
    }
}
