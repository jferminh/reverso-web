package com.julio.model;

import com.julio.exception.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.io.Serial;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Classe représentant un prospect dans le système de gestion.
 *
 * <p>Un prospect est une société potentiellement intéressée par les services
 * de l'entreprise. Cette classe gère les informations spécifiques comme
 * la date de prospection et le niveau d'intérêt manifesté.
 * Les identifiants des prospects sont générés automatiquement via un compteur
 * statique incrémental.
 * </p>
 *
 * <p>Contraintes métier :</p>
 * <ul>
 *   <li>La date de prospection est obligatoire</li>
 *   <li>Le niveau d'intérêt (intéressé) est obligatoire</li>
 * </ul>
 *
 * @author Julio FERMIN
 * @version 1.0
 * @since 19/11/2025
 * @see Societe
 * @see Interesse
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, of = {})
@SuperBuilder
public class Prospect extends Societe {
  @Serial
  private static final long serialVersionUID = 1L;

  @NotNull(message = "La date de prospection est obligatoire.")
  @PastOrPresent(message = "La date doit être dans le passé ou présent")
  private LocalDate dateProspection;

  @NotBlank(message = "Intérêt est obligatoire")
  private Interesse interesse;

  @Override
  public String getTypeSociete() {
    return "Prospect";
  }

}
