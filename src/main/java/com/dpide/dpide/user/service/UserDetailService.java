package com.dpide.dpide.user.service;

import com.dpide.dpide.exception.EmailNotFoundException;
import com.dpide.dpide.user.domain.User;
import com.dpide.dpide.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j  // SLF4J 로깅 추가
// 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    // 사용자 이름(email)으로 사용자의 정보를 가져오는 메서드
    @Override
    @Transactional(readOnly = true)
    public User loadUserByUsername(String email) {
        log.info("Attempting to load user by email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));
    }
}
