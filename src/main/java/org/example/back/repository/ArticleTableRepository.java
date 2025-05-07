package org.example.back.repository;

import org.example.back.domain.ArticleTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleTableRepository extends JpaRepository<ArticleTable, Long> {
}