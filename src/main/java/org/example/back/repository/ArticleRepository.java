package org.example.back.repository;

import org.example.back.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>{
    List<Article> findByCategoryArticleId(Long categoryId);
}