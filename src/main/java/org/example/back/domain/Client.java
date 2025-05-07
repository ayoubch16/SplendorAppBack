package org.example.back.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String raisonSociale;

    private String adresse;

    @ManyToOne
    @JoinColumn(name = "ville")
    private Ville ville;

    private String ice;
    private String telephone;
    private String email;
}