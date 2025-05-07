package org.example.back.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.back.domain.CategoryArticle;
import org.example.back.repository.BlRepository;
import org.example.back.repository.ClientRepository;
import org.example.back.repository.DevisRepository;
import org.example.back.repository.FactureRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shared")
@Transactional
@Slf4j
@AllArgsConstructor
@Builder
public class SharedResource {


    private final FactureRepository factureRepository;
    private final DevisRepository devisRepository;
    private final BlRepository blRepository;
    private final ClientRepository clientRepository;

    @GetMapping("/cptfacture")
    public long getCptFacture() {
        return factureRepository.countFacture();
    }


    @GetMapping("/cptdevis")
    public long getCptDevis() {
        return devisRepository.countDevis();
    }

    @GetMapping("/cptbl")
    public long getCptBl() {
        return blRepository.countBl();
    }

    @GetMapping("/cptclient")
    public long getCptClient() {
        return clientRepository.countClient();
    }


    @GetMapping("/check-facture-exists")
    public boolean checkFactureExists(@RequestParam String numFacture) {
        return factureRepository.existsByNumFacture(numFacture);
    }

    @GetMapping("/check-devis-exists")
    public boolean checkDevisExists(@RequestParam String numDev) {
        return devisRepository.existsByNumDevis(numDev);
    }

    @GetMapping("/check-bl-exists")
    public boolean checkBlExists(@RequestParam String numBl) {
        return blRepository.existsByNumBl(numBl);
    }
}
