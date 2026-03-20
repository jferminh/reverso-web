package com.julio.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JavaBean représentant un utilisateur de l'application.
 * Utilisé pour l'authentification.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"motDePasse", "sel"}) // Sécurité : on n'affiche jamais les mots de passe dans les logs
@EqualsAndHashCode(of = "identifiant")
@Builder
public class Utilisateur implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private Integer id;
  private String identifiant;
  private String motDePasse; // Le Hash
  private String sel;        // Le Salt
}