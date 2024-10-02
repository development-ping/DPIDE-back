package com.dpide.dpide.websocket.config;

import com.dpide.dpide.user.config.TokenProvider;
import com.dpide.dpide.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // JWT 토큰이 요청 헤더에 있는지 확인
        String token = request.getHeaders().getFirst("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            // "Bearer " 부분 제거
            token = token.substring(7);

            // JWT 검증
            if (tokenProvider.validToken(token)) {
                // 인증된 사용자 정보를 attributes에 저장
                Long userId = userService.getAuthenticatedUser(token).getId();
                attributes.put("userId", userId); // WebSocket 세션에 userId 저장
                return true; // 인증 성공
            }
        }

        // 인증 실패 시 핸드셰이크를 거부
        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        // Handshake 이후 처리 (필요 시)
    }
}
