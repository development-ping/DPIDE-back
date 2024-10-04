package com.dpide.dpide.user.dto;

import lombok.*;

public class UserDto {

    @Builder
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResponse {
        private Long id;
        private String email;
        private String nickname;

        public  UserResponse(UserResponseWithToken userResponseWithToken) {
            this.id = userResponseWithToken.getId();
            this.email = userResponseWithToken.getEmail();
            this.nickname = userResponseWithToken.getNickname();
        }
    }

    @Builder
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResponseWithToken {
        private Long id;
        private String email;
        private String nickname;
        private TokenDto.TokenResponse tokenResponse;
    }

    @Builder
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegisterRequest {
        private String email;
        private String nickname;
        private String password;
    }

    @Builder
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Builder
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class updateRequest {
        private String nickname;
        private String oldPassword;
        private String newPassword;
    }

}
