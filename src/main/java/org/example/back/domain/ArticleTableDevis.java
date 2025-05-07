package org.example.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "article_table")
public class ArticleTableDevis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String designation;
    private String description;
    private int quantite;
    private double prixUnitaire;
    private double prixTotal;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "devis_id")  // Ensure this matches your DB column
    private Devis devis;

    // Business logic for price calculation
    @PrePersist
    @PreUpdate
    private void calculatePrixTotal() {
        this.prixTotal = this.prixUnitaire * this.quantite;
    }
}