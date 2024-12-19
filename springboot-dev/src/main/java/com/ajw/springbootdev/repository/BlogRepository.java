package com.ajw.springbootdev.repository;

import com.ajw.springbootdev.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {

}
