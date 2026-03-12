package com.julio.model;

import com.julio.exception.ValidationException;
import com.julio.util.RegexPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

/**
 * Classe abstraite représentant une société dans le système de gestion.
 * Cette classe encapsule les informations communes à toutes les sociétés
 * (clients et prospects) et assure la validation des données métier.
 *
 * @author Julio FERMIN
 * @version 1.0
 * @since 19/11/2025
 */
public abstract class Societe implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private Integer id;

  @NotBlank(message = "La raison sociale est obligatoire")
  @Size(min = 2, max = 100, message = "La raison sociale doit avoir entre 2 et 100 caractères")
  private String raisonSociale;

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
   * @param adresse adresse complète de la société (ne peut pas être null)
   * @param telephone numéro de téléphone (doit respecter le format validé)
   * @param email adresse email (doit respecter le format validé)
   * @param commentaires notes additionnelles (peut être null ou vide)
   * @throws ValidationException si une des validations échoue
   */
  public Societe(String raisonSociale, Adresse adresse, String telephone,
                 String email, String commentaires) throws ValidationException {
    setRaisonSociale(raisonSociale);
    setAdresse(adresse);
    setTelephone(telephone);
    setEmail(email);
    this.commentaires = commentaires;
  }

  /**
   * Constructeur vide.
   *
   */
  public Societe() {
  }

  /**
   * Getter id.
   *
   * @return id
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getRaisonSociale() {
    return raisonSociale;
  }

  public void setRaisonSociale(String raisonSociale) {
    this.raisonSociale = raisonSociale;
  }

  public Adresse getAdresse() {
    return adresse;
  }

  public void setAdresse(Adresse adresse) {
    this.adresse = adresse;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCommentaires() {
    return commentaires;
  }

  public void setCommentaires(String commentaires) {
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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Societe{");
    sb.append("id=").append(id);
    sb.append(", raisonSociale='").append(raisonSociale).append('\'');
    sb.append(", adresse=").append(adresse);
    sb.append(", telephone='").append(telephone).append('\'');
    sb.append(", email='").append(email).append('\'');
    sb.append(", commentaires='").append(commentaires).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
