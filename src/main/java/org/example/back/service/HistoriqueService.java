package org.example.back.service;

import lombok.AllArgsConstructor;
import org.example.back.domain.Historique;
import org.example.back.domain.enums.ActionType;
import org.example.back.domain.enums.EntityType;
import org.example.back.repository.HistoriqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class HistoriqueService {
    private final HistoriqueRepository historiqueRepository;

    @Transactional
    public void saveHistorique(EntityType entityType, ActionType actionType,
                               Long entityId, String entityName, String details) {
        Historique historique = Historique.builder()
                .entityType(entityType)
                .actionType(actionType)
                .entityId(entityId)
                .entityName(entityName)
                .dateAction(LocalDateTime.now())
                .details(details)
                .user("SYSTEM_USER") // À remplacer par l'utilisateur connecté
                .build();

        historiqueRepository.save(historique);
    }

    public List<Historique> getAllHistorique() {
        return historiqueRepository.findAllByOrderByDateActionDesc();
    }

    public List<Historique> getHistoriqueByEntityType(EntityType entityType) {
        return historiqueRepository.findByEntityTypeOrderByDateActionDesc(entityType.name());
    }
}