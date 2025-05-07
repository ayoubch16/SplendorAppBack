package org.example.back.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.back.domain.Bl;
import org.example.back.domain.ArticleTableBl;
import org.example.back.domain.Devis;
import org.example.back.domain.enums.ActionType;
import org.example.back.domain.enums.EntityType;
import org.example.back.domain.enums.StatutBL;
import org.example.back.repository.BlRepository;
import org.example.back.repository.DevisRepository;
import org.example.back.repository.FactureRepository;
import org.example.back.service.HistoriqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bls")
@Transactional
@Slf4j
@AllArgsConstructor
@Builder
public class BlResource {
    private final BlRepository blRepository;
    private final FactureRepository factureRepository;
    private final DevisRepository devisRepository;
    private final HistoriqueService historiqueService;

    @GetMapping
    public ResponseEntity<List<Bl>> getAllBls() {
        List<Bl> bls = blRepository.findAll();
        return ResponseEntity.ok().body(bls);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bl> getBl(@PathVariable Long id) {
        Optional<Bl> bl = blRepository.findById(id);
        return bl.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{devisId}")
    public ResponseEntity<Bl> createBl(@PathVariable Long devisId) {
        // Trouver le devis par son ID
        Devis devis = devisRepository.findById(devisId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Devis non trouvé"));

        // Créer un nouveau BL à partir des informations du devis
        Bl bl = new Bl();
        bl.setClient(devis.getClient());
        bl.setDate(LocalDate.now());
        bl.setStatut(StatutBL.NON_LIVRE);

        // Générer un numéro de BL unique à partir du numéro de devis
        String newNumBl = devis.getNumDevis().replace("DEV", "BL");
        bl.setNumBl(newNumBl);

        // Copier les articles du devis vers le BL
        if (devis.getArticles() != null && !devis.getArticles().isEmpty()) {
            List<ArticleTableBl> articlesBl = devis.getArticles().stream()
                    .map(articleDevis -> {
                        ArticleTableBl articleBl = new ArticleTableBl();
                        articleBl.setDesignation(articleDevis.getDesignation());
                        articleBl.setQuantite(articleDevis.getQuantite());
                        articleBl.setPrixUnitaire(articleDevis.getPrixUnitaire());
                        articleBl.setBl(bl);
                        return articleBl;
                    })
                    .collect(Collectors.toList());

            bl.setArticles(articlesBl);
        }

        Bl savedBl = blRepository.save(bl);
        historiqueService.saveHistorique(
                EntityType.BL,
                ActionType.CREATE,
                savedBl.getId(),
                savedBl.getNumBl(),
                "Création d'un BL à partir du devis " + devis.getNumDevis()
        );

        return ResponseEntity.ok(savedBl);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bl> updateBl(@PathVariable Long id, @RequestBody Bl bl) {
        Bl existingBl = blRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BL not found"));

        // Mise à jour partielle des champs si ils sont présents dans la requête
        if (bl.getNumBl() != null) {
            existingBl.setNumBl(bl.getNumBl());
        }
        if (bl.getClient() != null) {
            existingBl.setClient(bl.getClient());
        }

        if (bl.getStatut() != null) {
            existingBl.setStatut(bl.getStatut());
        }
        if (bl.getDate() != null) {
            existingBl.setDate(bl.getDate());
        }
        if (bl.getArticles() != null && !bl.getArticles().isEmpty()) {
            // Pour les collections, on peut soit remplacer complètement, soit fusionner
            existingBl.getArticles().clear();
            existingBl.getArticles().addAll(bl.getArticles());
            // Mettre à jour la référence du devis pour chaque article
            existingBl.getArticles().forEach(article -> article.setBl(existingBl));
        }

        Bl updatedBl = blRepository.save(existingBl);
        historiqueService.saveHistorique(
                EntityType.BL,
                ActionType.UPDATE,
                updatedBl.getId(),
                updatedBl.getNumBl(),
                "Modification d'un BL"
        );
        return ResponseEntity.ok(updatedBl);
    }

    @PutMapping("/{id}/{statut}")
    public ResponseEntity<Bl> updateStatutBl(@PathVariable Long id, @PathVariable StatutBL statut) {
        Bl result = blRepository.findById(id).orElse(null);
        result.setStatut(statut);

        Bl updated = blRepository.save(result);
        historiqueService.saveHistorique(
                EntityType.BL,
                ActionType.UDATE_STATUS,
                updated.getId(),
                updated.getNumBl(),
                "Changement de statut vers " + statut
        );

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBl(@PathVariable Long id) {

        Bl updatedBl = blRepository.findById(id).orElse(null);
        blRepository.deleteById(id);
        historiqueService.saveHistorique(
                EntityType.BL,
                ActionType.DELETE,
                updatedBl.getId(),
                updatedBl.getNumBl(),
                "Suppression d'un BL"
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Bl>> getBlsByClient(@PathVariable Long clientId) {
        List<Bl> bls = blRepository.findAllByClientId(clientId);
        return ResponseEntity.ok().body(bls);
    }
}