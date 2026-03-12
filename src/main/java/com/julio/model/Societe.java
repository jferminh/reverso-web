package com.julio.model;

import com.julio.util.RegexPatterns;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Classe abstraite parente de Client et Prospect.
 *
 * @author Julio
 * @version 2.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public abstract class Societe implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private Integer id;

  @NotBlank(message = "La raison sociale est obligatoire")
  @Size(min = 2, max = 100, message = "La raison sociale doit avoir entre 2 et 100 caractères")
  private String raisonSociale;

  @Valid
  @NotNull(message = "L'adresse est obligatoire")
  private Adresse adresse;

  @NotBlank(message = "Le téléphone est obligatoire")
  @Pattern(regexp = RegexPatterns.TELEPHONE, message = "Téléphone invalide (format : 0XXXXXXXXX)")
  private String telephone;

  @NotBlank(message = "L'email est obligatoire")
  @Email(regexp = RegexPatterns.EMAIL, message = "Format email invalide")
  private String email;

  private String commentaires;

  /**
   * Constructeur principal de la classe Societe.
   * Initialise une société avec validation des données obligatoires.
   *
   * @param raisonSociale raison sociale de la société (ne peut pas être vide)
   * @param adresse       adresse complète de la société (ne peut pas être null)
   * @param telephone     numéro de téléphone (doit respecter le format validé)
   * @param email         adresse email (doit respecter le format validé)
   * @param commentaires  notes additionnelles (peut être null ou vide)
   */
  public Societe(String raisonSociale, Adresse adresse,
                 String telephone, String email,
                 String commentaires) {
    this.raisonSociale = raisonSociale;
    this.adresse = adresse;
    this.telephone = telephone;
    this.email = email;
    this.commentaires = commentaires;
  }

  /**
   * Retourne le type spécifique de société.
   * Cette méthode abstraite doit être implémentée par les sous-classes
   * pour identifier leur nature (Client, Prospect).
   *
   * @return une chaîne de caractères représentant le type de société
   */
  public abstract String getTypeSociete();
}
