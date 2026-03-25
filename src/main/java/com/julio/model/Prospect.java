package com.julio.model;

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
 * Entité représentant un Prospect dans le système CRM.
 *
 * <p>Hérite des propriétés communes de la classe abstraite {@link Societe}.
 * Cette classe utilise Lombok pour générer les accesseurs et un constructeur via
 * le pattern Builder, tout en incluant explicitement les champs du parent dans
 * les méthodes equals, hashCode et toString.
 * </p>
 *
 * @author Julio
 * @version 2.0
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
  @PastOrPresent(message = "La date de prospection ne peut pas être dans le future.")
  private LocalDate dateProspection;

  @NotNull(message = "Le niveau d'intérêt est obligatoire.")
  private Interesse interesse;

  /**
   * Retourne le type concret de la société.
   * Utile pour l'affichage dynamique ou la logique polymorphique.
   *
   * @return La chaîne de caractères "Prospect"
   */
  @Override
  public String getTypeSociete() {
    return "Prospect";
  }

}