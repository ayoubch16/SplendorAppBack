package org.example.back.repository;

import org.example.back.domain.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    List<Facture> findAllByClientId(Long clientId);

    @Query("select count(f) from Facture f")
    long countFacture();

    @Query("SELECT COUNT(f) > 0 FROM Facture f WHERE f.numFacture = :numFacture")
    boolean existsByNumFacture(@Param("numFacture") String numFacture);
}