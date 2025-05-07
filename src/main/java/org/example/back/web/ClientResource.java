package org.example.back.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.back.domain.Client;
import org.example.back.domain.Ville;
import org.example.back.domain.enums.ActionType;
import org.example.back.domain.enums.EntityType;
import org.example.back.repository.ClientRepository;
import org.example.back.repository.VilleRepository;
import org.example.back.service.HistoriqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@Transactional
@Slf4j
@AllArgsConstructor
public class ClientResource {

    private final ClientRepository clientRepository;
    private final VilleRepository villeRepository;
    private final HistoriqueService historiqueService;

    @PostMapping
    public ResponseEntity<Client> create(@RequestBody Client client) {
        Client savedClient = clientRepository.save(client);
        historiqueService.saveHistorique(
                EntityType.CLIENT,
                ActionType.CREATE,
                savedClient.getId(),
                savedClient.getRaisonSociale(),
                "Création d'un nouveau client"
        );
        return ResponseEntity.ok(savedClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable Long id, @RequestBody Client client) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        if (client.getRaisonSociale() != null) {
            existingClient.setRaisonSociale(client.getRaisonSociale());
        }
        if (client.getAdresse() != null) {
            existingClient.setAdresse(client.getAdresse());
        }
        if (client.getVille() != null) {
            existingClient.setVille(client.getVille());
        }
        if (client.getIce() != null) {
            existingClient.setIce(client.getIce());
        }
        if (client.getTelephone() != null) {
            existingClient.setTelephone(client.getTelephone());
        }
        if (client.getEmail() != null) {
            existingClient.setEmail(client.getEmail());
        }


        Client savedClient = clientRepository.save(existingClient);
        historiqueService.saveHistorique(
                EntityType.CLIENT,
                ActionType.UPDATE,
                savedClient.getId(),
                savedClient.getRaisonSociale(),
                "Modification d'un client"
        );

        return ResponseEntity.ok(clientRepository.save(existingClient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        Client savedClient = clientRepository.findById(id).orElse(null);
        clientRepository.deleteById(id);
        historiqueService.saveHistorique(
                EntityType.CLIENT,
                ActionType.DELETE,
                savedClient.getId(),
                savedClient.getRaisonSociale(),
                "Suppression d'un client"
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientRepository.findById(id).orElse(null));
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAll() {
        return ResponseEntity.ok(clientRepository.findAll());
    }

    @GetMapping("/villes")
    public ResponseEntity<List<Ville>> getVilles() {
        return ResponseEntity.ok(villeRepository.findAll());
    }
}