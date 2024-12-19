package com.ajw.springbootdev.service;

import com.ajw.springbootdev.domain.Article;
import com.ajw.springbootdev.dto.AddArticleRequest;
import com.ajw.springbootdev.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service  // 빈으로 등록
@RequiredArgsConstructor  // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
public class BlogService {

    private final BlogRepository blogRepository;

    // 블로그 글 추가 메서드
    public Article save(AddArticleRequest request){
        return blogRepository.save(request.toEntity());
    }

    // 블로그 글 목록 조회
}
