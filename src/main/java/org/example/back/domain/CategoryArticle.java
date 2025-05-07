package org.example.back.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@NoArgsConstructor // Constructeur sans arguments (requis par JPA)
@AllArgsConstructor // Constructeur avec tous les arguments (optionnel)
@Builder // Pour créer des objets facilement (design pattern Builder)
@Table(name = "category_article")
public class CategoryArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}