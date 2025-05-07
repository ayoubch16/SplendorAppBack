package org.example.back.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.back.domain.ArticleTableDevis;
import org.example.back.domain.Devis;
import org.example.back.domain.enums.ActionType;
import org.example.back.domain.enums.EntityType;
import org.example.back.domain.enums.StatutDevis;
import org.example.back.repository.DevisRepository;
import org.example.back.service.HistoriqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/devis")
@Transactional
@Slf4j
@AllArgsConstructor
@Builder
public class DevisResource {
    private final DevisRepository devisRepository;
    private final HistoriqueService historiqueService;


    @GetMapping
    public ResponseEntity<List<Devis>> getAllDevis() {
        List<Devis> devisList = devisRepository.findAll();
        return ResponseEntity.ok().body(devisList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Devis> getDevis(@PathVariable Long id) {
        Optional<Devis> devis = devisRepository.findById(id);
        return devis.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Transactional
    @PostMapping
    public ResponseEntity<Devis> createDevis(@RequestBody Devis devis) throws URISyntaxException {
        if (devis.getId() != null) {
            return ResponseEntity.badRequest().build();
        }

        // Assurez-vous que les articles sont bien liés au devis
        if (devis.getArticles() != null) {
            devis.getArticles().forEach(article -> article.setDevis(devis));
        }

        // Générer le numéro de devis
        Long lastId = devisRepository.findTopByOrderByIdDesc()
                .map(d -> d.getId())
                .orElse(0L);
        String formattedNumber = String.format("%03d", lastId + 1);
        int currentYear = java.time.Year.now().getValue() % 100;
        devis.setNumDevis("DEV-" + formattedNumber + "/" + currentYear);

        Devis result = devisRepository.save(devis);
        historiqueService.saveHistorique(
                EntityType.DEVIS,
                ActionType.CREATE,
                result.getId(),
                result.getNumDevis(),
                "Création d'un nouveau devis"
        );

        return ResponseEntity.created(new URI("/api/devis/" + result.getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Devis> updateDevis(@PathVariable Long id, @RequestBody Devis devis) {
        Devis existingDevis = devisRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Devis not found"));

        // Mise à jour partielle des champs si ils sont présents dans la requête
        if (devis.getNumDevis() != null) {
            existingDevis.setNumDevis(devis.getNumDevis());
        }
        if (devis.getClient() != null) {
            existingDevis.setClient(devis.getClient());
        }
        if (devis.getMontant() != null) {
            existingDevis.setMontant(devis.getMontant());
        }
        if (devis.getStatut() != null) {
            existingDevis.setStatut(devis.getStatut());
        }
        if (devis.getDate() != null) {
            existingDevis.setDate(devis.getDate());
        }
        if (devis.getArticles() != null && !devis.getArticles().isEmpty()) {
            // Pour les collections, on peut soit remplacer complètement, soit fusionner
            existingDevis.getArticles().clear();
            existingDevis.getArticles().addAll(devis.getArticles());
            // Mettre à jour la référence du devis pour chaque article
            existingDevis.getArticles().forEach(article -> article.setDevis(existingDevis));
        }

        Devis updatedDevis = devisRepository.save(existingDevis);
        historiqueService.saveHistorique(
                EntityType.DEVIS,
                ActionType.UPDATE,
                updatedDevis.getId(),
                updatedDevis.getNumDevis(),
                "Modification d'un devis"
        );
        return ResponseEntity.ok(updatedDevis);
    }

    @PutMapping("/{id}/{statut}")
    public ResponseEntity<Devis> updateStatutDevis(@PathVariable Long id, @PathVariable StatutDevis statut) {
        Devis result = devisRepository.findById(id).orElse(null);
        result.setStatut(statut);

        Devis updatedDevis = devisRepository.save(result);
        historiqueService.saveHistorique(
                EntityType.DEVIS,
                ActionType.UDATE_STATUS,
                updatedDevis.getId(),
                updatedDevis.getNumDevis(),
                "Changement de statut vers " + statut
        );

        return ResponseEntity.ok(updatedDevis);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevis(@PathVariable Long id) {
        if (!devisRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Devis updatedDevis = devisRepository.findById(id).orElse(null) ;
        devisRepository.deleteById(id);

        historiqueService.saveHistorique(
                EntityType.DEVIS,
                ActionType.DELETE,
                updatedDevis.getId(),
                updatedDevis.getNumDevis(),
                "Suppression d'un  devis"
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Devis>> getDevisByClient(@PathVariable Long clientId) {
        List<Devis> devisList = devisRepository.findDevisByClientId(clientId);
        return ResponseEntity.ok().body(devisList);
    }

    @GetMapping("/{id}/articles")
    public ResponseEntity<List<ArticleTableDevis>> getArticlesForDevis(@PathVariable Long id) {
        Optional<Devis> devis = devisRepository.findById(id);
        return devis.map(value -> ResponseEntity.ok().body(value.getArticles()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/last-id")
    public ResponseEntity<Long> getLastDevisId() {
        Devis lastDevis = devisRepository.findLastDevis();
        return ResponseEntity.ok(lastDevis != null ? lastDevis.getId() : 0);
    }

}