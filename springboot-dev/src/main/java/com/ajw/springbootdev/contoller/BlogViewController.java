package com.ajw.springbootdev.contoller;

import com.ajw.springbootdev.domain.Article;
import com.ajw.springbootdev.dto.ArticleListViewResponse;
import com.ajw.springbootdev.dto.ArticleResponse;
import com.ajw.springbootdev.dto.ArticleViewResponse;
import com.ajw.springbootdev.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BlogViewController {

    private final BlogService blogService;

    // 블로그 글 목록 조회 화면으로
    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll().stream()
                .map(ArticleListViewResponse::new)
                .collect(Collectors.toList());

        model.addAttribute("articles", articles); // 블로그 글 리스트 저장

        return "articleList";  // articleList.html 뷰 조회
    }

    // 블로그 글 조회 화면으로
    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id);
        // 화면에서 사용할 모델 데이터를 저장
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }

    // 블로그 글 수정/생성 화면으로
    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model) {
        if(id == null){  // id가 없으면 생성
            model.addAttribute("article", new ArticleViewResponse());
        }else {  // id가 있으면 수정
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }

        return "newArticle";
    }
}