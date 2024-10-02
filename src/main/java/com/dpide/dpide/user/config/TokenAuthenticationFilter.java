package com.dpide.dpide.user.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 요청 헤더에서 Authorization 값 조회
            String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
            log.debug("Authorization header found: {}", authorizationHeader);

            // Bearer 접두사 제거 후 토큰 추출
            String token = getAccessToken(authorizationHeader);
            log.debug("Extracted JWT token: {}", token);

            // 토큰이 유효한지 확인하고, 인증 정보가 없는 경우 인증 설정
            if (token != null && tokenProvider.validToken(token) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("JWT token is valid. Authentication set for user: {}", authentication.getName());
            }
        } catch (Exception ex) {
            // 예외 발생 시 로그 남기기
            log.error("Error occurred in JWT filter", ex);
        }

        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 Bearer 토큰을 추출하는 메서드
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            log.debug("Valid Authorization header found: {}", authorizationHeader);
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        log.warn("Authorization header is missing or does not start with Bearer");
        return null;
    }
}
