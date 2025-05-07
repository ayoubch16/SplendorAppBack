package org.example.back.web;

import lombok.AllArgsConstructor;
import org.example.back.domain.Historique;
import org.example.back.domain.enums.EntityType;
import org.example.back.service.HistoriqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historique")
@AllArgsConstructor
public class HistoriqueResource {
    private final HistoriqueService historiqueService;

    @GetMapping
    public ResponseEntity<List<Historique>> getAllHistorique() {
        return ResponseEntity.ok(historiqueService.getAllHistorique());
    }

    @GetMapping("/{entityType}")
    public ResponseEntity<List<Historique>> getHistoriqueByEntityType(
            @PathVariable EntityType entityType) {
        return ResponseEntity.ok(historiqueService.getHistoriqueByEntityType(entityType));
    }
}