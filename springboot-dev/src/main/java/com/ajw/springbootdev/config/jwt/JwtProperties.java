package com.ajw.springbootdev.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("jwt")  // application.yml에 프로퍼티값을 가져와서 사용하는 어노테이션
@Getter @Setter
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
