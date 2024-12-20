package com.ajw.springbootdev.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity  // 엔티티로 지정
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder  // 빌더 패턴으로 객체 생성
public class Article {

    @Id  // 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본키 자동으로 1씩 증가
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)  // Not Null
    private String title;

    @Column(nullable = false)
    private String content;

    // 엔티티에 요청받은 내용으로 값을 수정하는 메서드
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
