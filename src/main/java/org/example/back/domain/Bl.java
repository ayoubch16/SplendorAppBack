package org.example.back.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.back.domain.enums.StatutBL;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "bl")
public class Bl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numBl;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    private StatutBL statut;

    private LocalDate date;


    @OneToMany(mappedBy = "bl", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleTableBl> articles = new ArrayList<>();
}