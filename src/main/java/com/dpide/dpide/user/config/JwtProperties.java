package com.dpide.dpide.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")  // "jwt"라는 prefix에 해당하는 설정을 매핑
public class JwtProperties {

    private String issuer;      // 발급자 정보
    private String secretKey;   // 시크릿 키

}
