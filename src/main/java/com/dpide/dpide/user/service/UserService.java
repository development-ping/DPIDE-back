package com.dpide.dpide.user.service;

import com.dpide.dpide.exception.AuthenticationException;
import com.dpide.dpide.exception.DuplicateEmailException;
import com.dpide.dpide.exception.DuplicateNicknameException;
import com.dpide.dpide.exception.UserNotFoundException;
import com.dpide.dpide.user.config.TokenProvider;
import com.dpide.dpide.user.domain.User;
import com.dpide.dpide.user.dto.Request.UpdateUserRequest;
import com.dpide.dpide.user.dto.Request.UserRequest;
import com.dpide.dpide.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j  // SLF4J 로깅 추가
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;

    // 새 유저 생성
    public User makeUser(UserRequest request) {
        log.info("Creating a new user with email: {}", request.getEmail());
        return userRepository.save(User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))  // 패스워드 암호화
                .build());
    }

    // 회원가입 로직
    @Transactional
    public User registerUser(UserRequest request) {
        log.info("Registering user with email: {}", request.getEmail());

        // 중복된 이메일 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Duplicate email found: {}", request.getEmail());
            throw new DuplicateEmailException("Email already in use");
        }

        // 중복된 닉네임 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            log.error("Duplicate nickname found: {}", request.getNickname());
            throw new DuplicateNicknameException("Nickname already in use");
        }

        // 회원 정보 저장
        User user = makeUser(request);
        log.info("User registered successfully with ID: {}", user.getId());
        return userRepository.save(user);
    }

    // 사용자 인증 로직
    public User authenticate(String email, String password) {
        log.info("Authenticating user with email: {}", email);

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Authentication failed, invalid email: {}", email);
                    return new AuthenticationException("Invalid email");
                });

        // 비밀번호 검증
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            log.error("Authentication failed, invalid password for email: {}", email);
            throw new AuthenticationException("Invalid password");
        }

        log.info("User authenticated successfully with email: {}", email);
        return user;
    }

    // 회원탈퇴 기능 (userId로 사용자 삭제)
    @Transactional
    public void deleteUserById(Long userId) {
        log.info("Deleting user with ID: {}", userId);

        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            log.error("User not found with ID: {}", userId);
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        // 사용자 삭제
        userRepository.deleteById(userId);
        log.info("User deleted successfully with ID: {}", userId);
    }

    // 사용자 정보 업데이트 로직
    @Transactional
    public void updateUser(Long userId, UpdateUserRequest updateRequest) {
        log.info("Updating user information for user ID: {}", userId);

        // 1. 기존 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new IllegalArgumentException("User not found");
                });

        // 2. oldPassword 확인
        if (!bCryptPasswordEncoder.matches(updateRequest.getOldPassword(), user.getPassword())) {
            log.error("Invalid old password provided for user ID: {}", userId);
            throw new IllegalArgumentException("Invalid old password");
        }

        // 4. 닉네임 업데이트
        if (updateRequest.getNickname() != null && !updateRequest.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(updateRequest.getNickname())) {
                log.error("Nickname already in use: {}", updateRequest.getNickname());
                throw new IllegalArgumentException("Nickname already in use");
            }
            user.setNickname(updateRequest.getNickname());
            log.info("Nickname updated successfully for user ID: {}", userId);
        }

        // 5. 비밀번호 업데이트 (newPassword 설정)
        if (updateRequest.getNewPassword() != null) {
            user.setPassword(bCryptPasswordEncoder.encode(updateRequest.getNewPassword()));
            log.info("Password updated successfully for user ID: {}", userId);
        }

        // 6. 변경된 사용자 정보 저장
        userRepository.save(user);
        log.info("User information updated successfully for user ID: {}", userId);
    }

    public User findById(Long userId) {
        log.info("Finding user by ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new IllegalArgumentException("User not found with ID: " + userId);
                });
    }

    public User getAuthenticatedUser(String token) {
        log.info("Retrieving authenticated user from token");
        // 토큰에서 인증 정보 추출
        Authentication authentication = tokenProvider.getAuthentication(token.substring(7)); // 'Bearer ' 부분을 제외하고 토큰을 전달
        String email = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername(); // email 가져오기

        // email을 통해 실제 User 정보 조회
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new IllegalArgumentException("User not found with email: " + email);
                });
    }

    public User findByEmail(String email) {
        log.info("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new IllegalArgumentException("User not found with email: " + email);
                });
    }
}
