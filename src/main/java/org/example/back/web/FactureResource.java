package org.example.back.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.back.domain.Article;
import org.example.back.domain.ArticleTableFacture;
import org.example.back.domain.Devis;
import org.example.back.domain.Facture;
import org.example.back.domain.enums.ActionType;
import org.example.back.domain.enums.EntityType;
import org.example.back.domain.enums.StatutDevis;
import org.example.back.domain.enums.StatutFacture;
import org.example.back.repository.ArticleRepository;
import org.example.back.repository.DevisRepository;
import org.example.back.repository.FactureRepository;
import org.example.back.service.HistoriqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/factures")
@Transactional
@Slf4j
@AllArgsConstructor
@Builder
public class FactureResource {
    private final FactureRepository factureRepository;
    private final DevisRepository devisRepository;
    private final HistoriqueService historiqueService;


    @GetMapping
    public ResponseEntity<List<Facture>> getAllFactures() {
        List<Facture> factures = factureRepository.findAll();
        return ResponseEntity.ok().body(factures);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facture> getFacture(@PathVariable Long id) {
        Optional<Facture> facture = factureRepository.findById(id);
        return facture.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Facture> createFacture(@PathVariable Long id) {
        // Trouver le devis par son ID
        Devis devis = devisRepository.findById(id).orElse(null);


        // Créer une nouvelle facture à partir des informations du devis
        Facture facture = new Facture();
        facture.setClient(devis.getClient());
        facture.setMontant(devis.getMontant());
        facture.setDate(LocalDate.now()); // Date actuelle ou date du devis selon votre besoin
        facture.setStatut(StatutFacture.NON_PAYEE); // Statut par défaut

        // Générer un numéro de facture unique (à adapter selon votre logique)
        String newNumFact=devis.getNumDevis().replace("DEV","FACT");
        facture.setNumFacture(newNumFact);

        // Copier les articles du devis vers la facture
        if (devis.getArticles() != null && !devis.getArticles().isEmpty()) {
            List<ArticleTableFacture> articlesFacture = devis.getArticles().stream()
                    .map(articleDevis -> {
                        ArticleTableFacture articleFacture = new ArticleTableFacture();
                        // Copier les propriétés de l'article
                        articleFacture.setDesignation(articleDevis.getDesignation());
                        articleFacture.setQuantite(articleDevis.getQuantite());
                        articleFacture.setPrixUnitaire(articleDevis.getPrixUnitaire());
                        articleFacture.setFacture(facture);
                        return articleFacture;
                    })
                    .collect(Collectors.toList());

            facture.setArticles(articlesFacture);
        }

        // Enregistrer la facture dans la base de données
        Facture savedFacture = factureRepository.save(facture);
        historiqueService.saveHistorique(
                EntityType.FACTURE,
                ActionType.CREATE,
                savedFacture.getId(),
                savedFacture.getNumFacture(),
                "Création d'une facture à partir du devis " + devis.getNumDevis());

        return ResponseEntity.ok(savedFacture);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Facture> updateFacture(@PathVariable Long id, @RequestBody Facture facture) {
        Facture existingFacture= factureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture not found"));

        // Mise à jour partielle des champs si ils sont présents dans la requête
        if (facture.getNumFacture() != null) {
            existingFacture.setNumFacture(facture.getNumFacture());
        }
        if (facture.getClient() != null) {
            existingFacture.setClient(facture.getClient());
        }
        if (facture.getMontant() != null) {
            existingFacture.setMontant(facture.getMontant());
        }
        if (facture.getStatut() != null) {
            existingFacture.setStatut(facture.getStatut());
        }
        if (facture.getDate() != null) {
            existingFacture.setDate(facture.getDate());
        }
        if (facture.getArticles() != null && !facture.getArticles().isEmpty()) {
            // Pour les collections, on peut soit remplacer complètement, soit fusionner
            existingFacture.getArticles().clear();
            existingFacture.getArticles().addAll(facture.getArticles());
            // Mettre à jour la référence du devis pour chaque article
            existingFacture.getArticles().forEach(article -> article.setFacture(existingFacture));
        }

        Facture updatedFacture = factureRepository.save(existingFacture);
        historiqueService.saveHistorique(
                EntityType.FACTURE,
                ActionType.UPDATE,
                updatedFacture.getId(),
                updatedFacture.getNumFacture(),
                "Modification d'un devis "
        );
        return ResponseEntity.ok(updatedFacture);
    }


    @PutMapping("/{id}/{statut}")
    public ResponseEntity<Facture> updateStatutFacture(@PathVariable Long id, @PathVariable StatutFacture statut) {
        Facture result = factureRepository.findById(id).orElse(null);
        result.setStatut(statut);

        Facture updated = factureRepository.save(result);
        historiqueService.saveHistorique(
                EntityType.FACTURE,
                ActionType.UDATE_STATUS,
                updated.getId(),
                updated.getNumFacture(),
                "Changement de statut vers " + statut
        );


        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        Facture updatedFacture = factureRepository.findById(id).orElse(null);
        factureRepository.deleteById(id);
        historiqueService.saveHistorique(
                EntityType.DEVIS,
                ActionType.DELETE,
                updatedFacture.getId(),
                updatedFacture.getNumFacture(),
                "Suppression d'un facture"
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Facture>> getFacturesByClient(@PathVariable Long clientId) {
        List<Facture> factures = factureRepository.findAllByClientId(clientId);
        return ResponseEntity.ok().body(factures);
    }
}
