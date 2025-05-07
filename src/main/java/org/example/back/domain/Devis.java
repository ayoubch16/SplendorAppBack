package org.example.back.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.back.domain.enums.StatutDevis;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "devis")
public class Devis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numDevis;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private String montant;

    @Enumerated(EnumType.STRING)
    private StatutDevis statut;

    private LocalDate date;

    @OneToMany(mappedBy = "devis", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleTableDevis> articles = new ArrayList<>();

}