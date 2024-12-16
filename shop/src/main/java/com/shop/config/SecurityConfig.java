package com.shop.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Controller;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("---------filterChain---------");
        http
                .authorizeHttpRequests(config ->
                        config.requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                              .requestMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
                              .requestMatchers("/admin/**").hasRole("ADMIN")
                              .anyRequest().authenticated()
                )
                .formLogin(config ->
                        config.loginPage("/members/login")
                              .defaultSuccessUrl("/")
                              .usernameParameter("email")  // 로그인 화면에서 name=username이면 생략 가능
                              .failureUrl("/members/login/error")
                )
                .logout(config ->
                    config.logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                          .logoutSuccessUrl("/")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
