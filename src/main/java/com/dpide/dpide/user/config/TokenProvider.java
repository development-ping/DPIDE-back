package com.dpide.dpide.user.config;

import com.dpide.dpide.exception.InvalidTokenException;
import com.dpide.dpide.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    // JWT 토큰 생성 메서드
    public String generateToken(User user, Duration expiredAt) {
        log.info("Generating JWT token for user: {}", user.getEmail());

        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    // JWT 토큰 생성 로직
    private String makeToken(Date expiry, User user) {
        log.debug("Creating JWT token with expiry date: {} for user: {}", expiry, user.getEmail());
        Date now = new Date();
        Key key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")  // 헤더 typ : JWT
                .setIssuer(jwtProperties.getIssuer())  // 발급자 설정
                .setIssuedAt(now)  // 발급 시간 설정
                .setExpiration(expiry)  // 만료 시간 설정
                .setSubject(user.getEmail())  // 이메일을 subject로 설정
                .claim("id", user.getId())  // 클레임에 유저 ID 추가
                .signWith(key, SignatureAlgorithm.HS256)  // 서명 알고리즘 설정
                .compact();
    }

    // JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        log.info("Validating JWT token");

        try {
            Key key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());

            Jwts.parserBuilder()
                    .setSigningKey(key)  // 서명 키 설정
                    .build()              // 빌더를 완성
                    .parseClaimsJws(token);  // 토큰을 검증 및 파싱

            log.info("JWT token is valid");
            return true;  // 유효한 토큰
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", token);
            throw new InvalidTokenException();
        }
    }

    // JWT 토큰에서 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        log.info("Extracting authentication details from JWT token");

        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities),
                token,
                authorities
        );
    }

    // 클레임을 추출하는 메서드
    private Claims getClaims(String token) {
        log.info("Parsing claims from JWT token");

        try {
            Key key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());

            return Jwts.parserBuilder()
                    .setSigningKey(key)  // 서명 키 설정
                    .build()              // 파서 빌더 완성
                    .parseClaimsJws(token)  // 토큰을 파싱하여 클레임 추출
                    .getBody();            // 클레임의 본문(body) 반환
        } catch (Exception e) {
            log.error("Error parsing claims from JWT token: {}", token, e);
            throw new InvalidTokenException();
        }
    }
}
