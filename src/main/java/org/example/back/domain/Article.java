package org.example.back.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@NoArgsConstructor // Constructeur sans arguments (requis par JPA)
@AllArgsConstructor // Constructeur avec tous les arguments (optionnel)
@Builder // Pour créer des objets facilement (design pattern Builder)
@Data
@Table(name = "article")
public class Article implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unite") // Ajout de contraintes
    private String unite;

    @ManyToOne(fetch = FetchType.EAGER) // Optimisation des performances
    @JoinColumn(name = "category_article_id")
    private CategoryArticle categoryArticle;

    @Column(name = "name_article") // Taille max
    private String nameArticle;

    @Column(name = "description_article") // Pour du texte long
    private String descriptionArticle;

    @Column(name = "price_article") // Format décimal
    private double priceArticle;

}