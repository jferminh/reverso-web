package com.julio.model;

import com.julio.exception.ValidationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JavaBean représentant un client.
 *
 * @author Julio
 * @version 2.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true, of = {})
public class Client extends Societe {
  @Serial
  private static final long serialVersionUID = 1L;

  @NotNull(message = "Le chiffre d'affaires est obligatoire")
  @Min(value = 200, message = "Le chiffre d'affaires doit être >= 200")
  private Long chiffreAffaires;

  @NotNull(message = "Le nombre d'employés est obligatoire")
  @Min(value = 1, message = "Le nombre d'employés doit être >= 1")
  private Integer nbEmployes;

  private List<Contrat> contrats;

  /**
   * Getter liste de contrats.
   *
   * @return liste de contrats
   */
  public List<Contrat> getContrats() {
    return new ArrayList<>(contrats);
  }

  /**
   * Ajoute un contrat à la liste des contrats du client.
   *
   * @param contrat le contrat à ajouter
   */
  public void ajouterContrat(Contrat contrat) {
    if (contrat != null && !contrats.contains(contrat)) {
      contrats.add(contrat);
    }
  }

  /**
   * Retourne le type de cette société.
   *
   * @return la chaîne "Client"
   */
  @Override
  public String getTypeSociete() {
    return "Client";
  }
}
