package org.example.back.repository;

import org.example.back.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {


    @Query("select count(c) from Client  c")
    long countClient();
}