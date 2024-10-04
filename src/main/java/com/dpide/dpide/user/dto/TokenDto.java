package com.dpide.dpide.user.dto;

import lombok.*;

public class TokenDto {

    @Builder
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
    }
}
