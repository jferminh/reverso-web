package com.julio.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entité représentant un contrat commercial lié à un client.
 *
 * <p>Utilise Lombok pour éliminer le code boilerplate (Getters, Setters, Equals).
 * La validation des contraintes (Jakarta) est gérée par les contrôleurs.
 * </p>
 *
 * @author Julio
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Contrat implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /** Identifiant unique du contrat en base de données. */
  private Integer id;

  /** Identifiant du client propriétaire. */
  @NotNull(message = "L'identifiant du client est obligatoire.")
  private Integer clientId;

  /** Nom ou désignation du contrat. */
  @NotBlank(message = "Le nom du contrat est obligatoire.")
  private String nomContrat;

  /** Montant financier du contrat en euros. */
  @NotNull(message = "Le montant du contrat est obligatoire.")
  @Positive(message = "Le montant doit être strictement supérieur à 0.")
  private Double montant;

  @Override
  public String toString() {
    return nomContrat + " (" + montant + "€)";
  }
}