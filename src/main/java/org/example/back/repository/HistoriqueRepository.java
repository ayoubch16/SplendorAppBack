package org.example.back.repository;

import org.example.back.domain.Historique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueRepository extends JpaRepository<Historique, Long> {
    List<Historique> findByEntityTypeOrderByDateActionDesc(String entityType);
    List<Historique> findAllByOrderByDateActionDesc();
}