package org.example.back.domain;

import jakarta.persistence.*;
import lombok.*;

import javax.management.relation.Role;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")  // Exclure le mot de passe du toString
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "first_name", length = 50)
  private String firstName;

  @Column(name = "last_name", length = 50)
  private String lastName;

  @Column(name = "is_active")
  private boolean isActive = true;

  @Column(name = "role")
  private String role;


}