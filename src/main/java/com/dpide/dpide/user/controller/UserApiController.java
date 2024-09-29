package com.dpide.dpide.user.controller;

import com.dpide.dpide.dto.Response.SuccessResponse;
import com.dpide.dpide.exception.AuthenticationException;
import com.dpide.dpide.exception.DuplicateEmailException;
import com.dpide.dpide.exception.DuplicateNicknameException;
import com.dpide.dpide.user.config.TokenProvider;
import com.dpide.dpide.user.domain.User;
import com.dpide.dpide.user.dto.Request.LoginRequest;
import com.dpide.dpide.dto.Response.ErrorResponse;
import com.dpide.dpide.user.dto.Request.UpdateUserRequest;
import com.dpide.dpide.user.dto.Request.UserRequest;
import com.dpide.dpide.user.dto.Response.UserResponse;
import com.dpide.dpide.user.service.RefreshTokenService;
import com.dpide.dpide.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@CrossOrigin
@Slf4j  // SLF4J 로그 사용
public class UserApiController {

    private final UserService userService;
    private final TokenProvider tokenProvider; // JWT 생성기
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<?> signup(@RequestBody UserRequest request) {
        log.info("Received signup request: {}", request.getEmail());

        try {
            User user = userService.registerUser(request);
            UserResponse.UserInfo userInfo = new UserResponse.UserInfo(user.getId(), user.getEmail(), user.getNickname());
            UserResponse responseBody = new UserResponse(201, "User registered successfully", userInfo);
            log.info("User registered successfully with ID: {}", user.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        } catch (DuplicateEmailException e) {
            log.warn("Duplicate email found: {}", request.getEmail());
            ErrorResponse errorResponse = new ErrorResponse(409, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (DuplicateNicknameException e) {
            log.warn("Duplicate nickname found: {}", request.getNickname());
            ErrorResponse errorResponse = new ErrorResponse(409, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request data: {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(400, "Invalid request data");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            log.error("Server error occurred during registration", e);
            ErrorResponse errorResponse = new ErrorResponse(500, "Server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Received login request for email: {}", request.getEmail());

        try {
            // 사용자 인증
            User user = userService.authenticate(request.getEmail(), request.getPassword());

            // JWT 액세스 토큰 및 리프레시 토큰 생성
            String accessToken = tokenProvider.generateToken(user, Duration.ofHours(2));
            String refreshToken = tokenProvider.generateToken(user, Duration.ofDays(7));

            // 리프레시 토큰 저장
            refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

            // 로그인 응답 생성
            UserResponse.UserInfo userInfo = new UserResponse.UserInfo(user.getId(), user.getEmail(), user.getNickname());
            UserResponse responseBody = new UserResponse(200, "Login successful", userInfo);

            log.info("Login successful for user ID: {}", user.getId());

            // 응답 반환
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header("Refresh-Token", refreshToken)
                    .body(responseBody);

        } catch (AuthenticationException e) {
            log.warn("Authentication failed for email: {}", request.getEmail());
            ErrorResponse errorResponse = new ErrorResponse(401, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (Exception e) {
            log.error("Server error occurred during login", e);
            ErrorResponse errorResponse = new ErrorResponse(500, "Server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Refresh-Token") String refreshToken) {
        log.info("Logout request received with refresh token: {}", refreshToken);

        try {
            refreshTokenService.deleteByToken(refreshToken);
            SuccessResponse successResponse = new SuccessResponse(200, "Logout successfully");
            log.info("Logout successful");

            return ResponseEntity.ok().body(successResponse);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid token provided during logout");
            ErrorResponse errorResponse = new ErrorResponse(401, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            log.error("Server error occurred during logout", e);
            ErrorResponse errorResponse = new ErrorResponse(500, "Server error occurred during logout");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token) {
        log.info("Delete user request received");

        try {
            // 서비스에서 인증된 사용자 정보 가져오기
            User authenticatedUser = userService.getAuthenticatedUser(token);
            Long userId = authenticatedUser.getId();

            // 회원 탈퇴 처리
            userService.deleteUserById(userId);

            SuccessResponse response = new SuccessResponse(HttpStatus.OK.value(), "User deleted successfully");
            log.info("User with ID {} deleted", userId);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid token provided during user deletion");
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            log.error("Server error occurred during user deletion", e);
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PutMapping
    public ResponseEntity<?> updateUserInfo(@RequestHeader("Authorization") String token, @RequestBody UpdateUserRequest updateRequest) {
        log.info("Update user information request received");

        try {
            // 서비스에서 인증된 사용자 정보 가져오기
            User authenticatedUser = userService.getAuthenticatedUser(token);
            Long userId = authenticatedUser.getId();

            // 사용자 정보 업데이트
            userService.updateUser(userId, updateRequest);

            log.info("User with ID {} updated successfully", userId);
            return ResponseEntity.ok(new SuccessResponse(200, "User information updated successfully"));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request during user update");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(401, e.getMessage()));
        } catch (Exception e) {
            log.error("Server error occurred during user update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Server error occurred"));
        }
    }


}
