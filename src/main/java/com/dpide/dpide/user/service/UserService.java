package com.dpide.dpide.user.service;

import com.dpide.dpide.exception.*;
import com.dpide.dpide.user.config.TokenProvider;
import com.dpide.dpide.user.domain.User;
import com.dpide.dpide.user.dto.UserDto;
import com.dpide.dpide.user.dto.TokenDto;
import com.dpide.dpide.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@RequiredArgsConstructor
@Service
@Slf4j  // SLF4J 로깅 추가
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    // 회원가입 로직
    @Transactional
    public UserDto.UserResponse registerUser(UserDto.RegisterRequest request) {
        log.info("Registering user with email: {}", request.getEmail());

        // 중복된 이메일 확인 및 예외 던지기
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new DuplicateEmailException(request.getEmail());
                });

        // 중복된 닉네임 확인 및 예외 던지기
        userRepository.findByNickname(request.getNickname())
                .ifPresent(user -> {
                    throw new DuplicateNicknameException(request.getNickname());
                });

        // 회원 정보 저장
        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))  // 패스워드 암호화
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // UserDto.UserInfo 반환
        return UserDto.UserResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .build();
    }

    // 사용자 인증 로직
    public UserDto.UserResponseWithToken authenticateAndGenerateTokens(String email, String password) {
        log.info("Authenticating user with email: {}", email);

        // 이메일로 사용자 조회 및 인증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException();
        }

        // JWT 액세스 토큰 및 리프레시 토큰 생성
        String accessToken = tokenProvider.generateToken(user, Duration.ofHours(2));
        String refreshToken = tokenProvider.generateToken(user, Duration.ofDays(7));

        // 리프레시 토큰 저장
        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

        log.info("Login successful for user ID: {}", user.getId());

        // UserResponseWithToken 반환 (토큰 정보와 사용자 정보를 모두 반환)
        return UserDto.UserResponseWithToken.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .tokenResponse(TokenDto.TokenResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())
                .build();
    }


    // 회원탈퇴 기능
    @Transactional
    public void deleteUserById(Long userId) {
        log.info("Deleting user with ID: {}", userId);

        // 사용자를 조회하고 없으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 사용자 삭제
        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", userId);
    }

    // 닉네임 업데이트
    @Transactional
    public void updateNickname(Long userId, UserDto.updateRequest updateRequest) {
        log.info("Updating user nickname for user ID: {}", userId);

        // 사용자를 조회하고 없으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setNickname(updateRequest.getNickname());
        userRepository.save(user);
        log.info("User nickname updated successfully for user ID: {}", userId);
    }


    // 사용자 정보 업데이트 로직
    @Transactional
    public void updatePassword(Long userId, UserDto.updateRequest updateRequest) {
        log.info("Updating user password for user ID: {}", userId);

        // 사용자를 조회하고 없으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // oldPassword 확인
        if (!bCryptPasswordEncoder.matches(updateRequest.getOldPassword(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }

        user.setPassword(bCryptPasswordEncoder.encode(updateRequest.getNewPassword()));
        userRepository.save(user);
        log.info("User password updated successfully for user ID: {}", userId);
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
}
