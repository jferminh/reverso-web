package com.julio.model;

import com.julio.exception.ValidationException;
import com.julio.util.RegexPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

/**
 * Classe représentant une adresse postale dans le système de gestion.
 *
 * <p>Une adresse contient tous les éléments nécessaires pour identifier
 * une localisation : numéro de rue, nom de rue, code postal et ville.
 * Les identifiants des adresses sont générés automatiquement via un compteur
 * statique incrémental.
 * </p>
 *
 * <p>Contraintes métier :
 * </p>
 * <ul>
 *   <li>Le numéro de rue est obligatoire (non vide)</li>
 *   <li>Le nom de rue est obligatoire (non vide)</li>
 *   <li>Le code postal doit contenir exactement 5 chiffres</li>
 *   <li>La ville est obligatoire (non vide)</li>
 * </ul>
 *
 * @author Julio FERMIN
 * @version 1.0
 * @see Societe
 * @since 19/11/2025
 */
public class Adresse implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  //    private int id;
  private Integer id;

  /**
   * Numéro dans la rue (peut inclure bis, ter, etc.). Obligatoire
   */
  @NotBlank(message = "Le numéro de voie est obligatoire")
  private String numeroRue;

  /**
   * Nom de la voie (rue, avenue, boulevard, etc.). Obligatoire
   */
  @NotBlank(message = "Le nom de la voie est obligatoire")
  private String nomRue;

  /**
   * Code postal français à 5 chiffres - obligatoire et validé.
   */
  @NotBlank(message = "Le code postal est obligatoire")
  @Pattern(regexp = RegexPatterns.CODE_POSTAL, message = "Code postal invalide (5 chiffres)")
  private String codePostal;

  /**
   * Nom de la ville - obligatoire.
   */
  @NotBlank(message = "La ville est obligatoire")
  private String ville;

  /**
   * Constructeur principal de la classe Adresse.
   *
   * <p>Crée une nouvelle adresse avec un identifiant auto-généré et valide
   * tous les champs selon les règles métier. Le compteur d'identifiant est
   * automatiquement incrémenté après la création.
   * </p>
   *
   * @param numeroRue  numéro de la rue (ne peut pas être vide)
   * @param nomRue     nom de la voie (ne peut pas être vide)
   * @param codePostal code postal à 5 chiffres (doit respecter le format)
   * @param ville      nom de la ville (ne peut pas être vide)
   * @throws ValidationException si une des validations échoue
   */
  public Adresse(String numeroRue, String nomRue, String codePostal, String ville)
      throws ValidationException {
    this.id = null; // ID null avant insertion en BDD
    setNumeroRue(numeroRue);
    setNomRue(nomRue);
    setCodePostal(codePostal);
    setVille(ville);
  }

  /**
   * Constructeur vide.
   *
   */
  public Adresse() {
  }

  /**
   * Getter id.
   *
   * @return Id
   */
  public Integer getId() {
    return id;
  }

  /**
   * Setter id.
   *
   * @param id paramètre id
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Get rue.
   *
   * @return numero de rue
   */
  public String getNumeroRue() {
    return numeroRue;
  }

  /**
   * Set numéro de rue.
   *
   * @param numeroRue numero de rue
   */
  public void setNumeroRue(String numeroRue) {
    this.numeroRue = numeroRue;
  }

  /**
   * Getter nom de rue.
   *
   * @return nom de rue
   */
  public String getNomRue() {
    return nomRue;
  }

  /**
   * Setter nom de rue.
   *
   * @param nomRue nom de rue
   */
  public void setNomRue(String nomRue) {
    this.nomRue = nomRue;
  }

  /**
   * Getter code postal.
   *
   * @return code postal
   */
  public String getCodePostal() {
    return codePostal;
  }

  /**
   * Setter code postal.
   *
   * @param codePostal code postal
   */
  public void setCodePostal(String codePostal) {
    this.codePostal = codePostal;
  }

  /**
   * Getter ville.
   *
   * @return ville
   */
  public String getVille() {
    return ville;
  }

  /**
   * Setter ville.
   *
   * @param ville ville
   */
  public void setVille(String ville) {
    this.ville = ville;
  }

  /**
   * Retourne une représentation textuelle complète de l'adresse.
   *
   * <p>Le format retourné est standardisé pour l'affichage :
   * "numéro nom_rue code_postal ville"
   * </p>
   *
   * @return une chaîne représentant l'adresse complète (ex: "12 Rue de la Paix 75001 Paris")
   */
  @Override
  public String toString() {
    return numeroRue + " " + nomRue + " " + codePostal + " " + ville;
  }


}
