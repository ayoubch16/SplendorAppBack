package org.example.back.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.back.domain.enums.ActionType;
import org.example.back.domain.enums.EntityType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Historique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50) // Suffisant pour "DEVIS", "CLIENT", etc.
    private EntityType entityType; // DEVIS, CLIENT, FACTURE, BL, ARTICLE

    @Enumerated(EnumType.STRING)
    @Column(length = 50) // Suffisant pour "CREATE", "UPDATE", "DELETE"
    private ActionType actionType;

    private Long entityId; // ID de l'entité concernée
    private String entityName; // Nom/identifiant de l'entité (ex: numéro de devis)
    private String details; // Détails supplémentaires

    @CreatedDate
    private LocalDateTime dateAction;

    private String user; // Utilisateur qui a effectué l'action
}