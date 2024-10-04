package com.dpide.dpide.user.controller;

import com.dpide.dpide.user.dto.UserDto;
import com.dpide.dpide.user.dto.TokenDto;
import com.dpide.dpide.user.service.RefreshTokenService;
import com.dpide.dpide.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Slf4j  // SLF4J 로그 사용
public class UserApiController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<UserDto.UserResponse> signup(@RequestBody UserDto.RegisterRequest request) {
        log.info("Received signup request: {}", request.getEmail());
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto.UserResponse> login(@RequestBody UserDto.LoginRequest request) {
        log.info("Received login request for email: {}", request.getEmail());

        // 사용자 인증 및 토큰 생성 처리
        UserDto.UserResponseWithToken userResponseWithToken = userService.authenticateAndGenerateTokens(request.getEmail(), request.getPassword());

        // 토큰 정보 가져오기
        TokenDto.TokenResponse tokens = userResponseWithToken.getTokenResponse();

        log.info("Login successful for user ID: {}", userResponseWithToken.getId());

        // 응답 반환 (토큰은 헤더에 포함, 사용자 정보는 바디에 포함)
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getAccessToken())
                .header("Refresh-Token", tokens.getRefreshToken())
                .body(new UserDto.UserResponse(userResponseWithToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Refresh-Token") String refreshToken) {
        log.info("Logout request received with refresh token: {}", refreshToken);
        refreshTokenService.deleteByToken(refreshToken);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String token, @RequestHeader("Refresh-Token") String refreshToken) {
        log.info("Delete user request received");
        // 서비스에서 인증된 사용자 정보 가져오기
        Long userId = userService.getAuthenticatedUser(token).getId();
        // 회원 탈퇴 처리
        userService.deleteUserById(userId);
        refreshTokenService.deleteByToken(refreshToken);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/nickname")
    public ResponseEntity<Void> updateNickname (@RequestHeader("Authorization") String token, @RequestBody UserDto.updateRequest updateRequest) {
        Long userId = userService.getAuthenticatedUser(token).getId();
        userService.updateNickname(userId, updateRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword (@RequestHeader("Authorization") String token, @RequestBody UserDto.updateRequest updateRequest) {
        Long userId = userService.getAuthenticatedUser(token).getId();
        userService.updatePassword(userId, updateRequest);
        return ResponseEntity.ok().build();
    }

}
