package org.example.back.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.back.domain.Article;
import org.example.back.domain.CategoryArticle;
import org.example.back.domain.enums.ActionType;
import org.example.back.domain.enums.EntityType;
import org.example.back.repository.ArticleRepository;
import org.example.back.repository.CategoryArticleRepository;
import org.example.back.service.HistoriqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@Transactional
@Slf4j
@AllArgsConstructor
@Builder
public class ArticleResource {

    private final ArticleRepository articleRepository;
    private final CategoryArticleRepository categoryArticleRepository;
    private final HistoriqueService historiqueService;


    @PostMapping
    public ResponseEntity<Article> create(@RequestBody Article articles) {
        Article savedArticles = articleRepository.save(articles);
        historiqueService.saveHistorique(
                EntityType.ARTICLE,
                ActionType.CREATE,
                savedArticles.getId(),
                savedArticles.getNameArticle(),
                "Création d'un nouvel article"
        );
        return ResponseEntity.ok(savedArticles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> update(@PathVariable Long id, @RequestBody Article article) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        if (article.getUnite() != null) {
            existingArticle.setUnite(article.getUnite());
        }
        if (article.getNameArticle() != null) {
            existingArticle.setNameArticle(article.getNameArticle());
        }
        if (article.getDescriptionArticle() != null) {
            existingArticle.setDescriptionArticle(article.getDescriptionArticle());
        }
        if (article.getCategoryArticle() != null) {
            existingArticle.setCategoryArticle(article.getCategoryArticle());
        }
        if (article.getPriceArticle() != 0.0) { // Pour un double, vérifiez une valeur par défaut
            existingArticle.setPriceArticle(article.getPriceArticle());
        }
        Article savedArticles = articleRepository.save(existingArticle);
        historiqueService.saveHistorique(
                EntityType.ARTICLE,
                ActionType.UPDATE,
                savedArticles.getId(),
                savedArticles.getNameArticle(),
                "Modification d'un  article"
        );
        return ResponseEntity.ok(savedArticles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        articleRepository.deleteById(id);

        historiqueService.saveHistorique(
                EntityType.ARTICLE,
                ActionType.DELETE,
                existingArticle.getId(),
                existingArticle.getNameArticle(),
                "Suppression d'un article"
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleRepository.findById(id).orElse(null));
    }

    @GetMapping
    public ResponseEntity<List<Article>> getAll() {
        return ResponseEntity.ok(articleRepository.findAll());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryArticle>> getCategories() {
        return ResponseEntity.ok(categoryArticleRepository.findAll());
    }

}
