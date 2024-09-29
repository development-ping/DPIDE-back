package com.dpide.dpide.user.service;

import com.dpide.dpide.user.domain.RefreshToken;
import com.dpide.dpide.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    // 리프레시 토큰 검색
    @Transactional(readOnly = true)
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    // 리프레시 토큰 삭제
    @Transactional
    public void deleteByToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        refreshTokenRepository.delete(token);
    }

    // 리프레시 토큰 저장
    @Transactional
    public void saveRefreshToken(Long userId, String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByUserId(userId)
                .orElse(new RefreshToken(userId, refreshToken));

        tokenEntity.update(refreshToken);
        refreshTokenRepository.save(tokenEntity);
    }
}

