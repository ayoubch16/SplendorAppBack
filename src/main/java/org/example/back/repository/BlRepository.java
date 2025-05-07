package org.example.back.repository;

import org.example.back.domain.Bl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlRepository extends JpaRepository<Bl, Long> {



    @Query("select count(b) from Bl b")
    long countBl();

    List<Bl> findAllByClientId(Long clientId);

    boolean existsByNumBl(String numBl);
}