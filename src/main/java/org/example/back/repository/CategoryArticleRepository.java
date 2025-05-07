package org.example.back.repository;

import org.example.back.domain.CategoryArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryArticleRepository extends JpaRepository<CategoryArticle, Long> {
}