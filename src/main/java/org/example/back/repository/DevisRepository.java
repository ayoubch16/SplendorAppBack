package org.example.back.repository;

import org.example.back.domain.Devis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DevisRepository extends JpaRepository<Devis, Long> {

    @Query("select count(d) from Devis  d")
    long countDevis();

    List<Devis> findDevisByClientId(Long clientId);

    @Query(value = "SELECT * FROM devis d ORDER BY d.id DESC", nativeQuery = true)
    Devis findLastDevis();


    Optional<Devis> findTopByOrderByIdDesc();


    boolean existsByNumDevis(String numDevis);
}