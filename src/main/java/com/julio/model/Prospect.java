package com.julio.model;

import com.julio.exception.ValidationException;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

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
public class Prospect extends Societe implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private LocalDate dateProspection;

  private Interesse interesse;

  /**
   * Constructeur principal de la classe Prospect.
   *
   * <p>Crée un nouveau prospect avec un identifiant auto-généré et valide
   * toutes les données métier. Le compteur d'identifiant est automatiquement
   * incrémenté après la création.
   * </p>
   *
   * @param raisonSociale raison sociale du prospect (ne peut pas être vide)
   * @param adresse adresse complète du prospect (ne peut pas être null)
   * @param telephone numéro de téléphone (doit respecter le format validé)
   * @param email adresse email (doit respecter le format validé)
   * @param commentaires notes additionnelles (peut être null ou vide)
   * @param dateProspection date de première prospection (ne peut pas être null)
   * @param interesse niveau d'intérêt du prospect (ne peut pas être null)
   * @throws ValidationException si une des validations échoue
   */
  public Prospect(String raisonSociale, Adresse adresse, String telephone,
                  String email, String commentaires, LocalDate dateProspection,
                  Interesse interesse) throws ValidationException {
    super(raisonSociale, adresse, telephone, email, commentaires);
    setDateProspection(dateProspection);
    setInteresse(interesse);
  }

  /**
   * Constructeur vide.
   *
   * @param raisonSociale raison sociale du prospect (ne peut pas être vide)
   * @param adresse adresse complète du prospect (ne peut pas être null)
   * @param telephone numéro de téléphone (doit respecter le format validé)
   * @param email adresse email (doit respecter le format validé)
   * @param commentaires notes additionnelles (peut être null ou vide)
   * @throws ValidationException si une des validations échoue
   */
  public Prospect(String raisonSociale, Adresse adresse, String telephone,
                  String email, String commentaires) throws ValidationException {
    super(raisonSociale, adresse, telephone, email, commentaires);
  }

  /**
   * Getter date de prospection.
   *
   * @return date de prospection
   */
  public LocalDate getDateProspection() {
    return dateProspection;
  }

  /**
   * Modifie la date de prospection avec validation métier.
   * La date de prospection est obligatoire et ne peut pas être null.
   *
   * @param dateProspection la nouvelle date de prospection
   */
  public void setDateProspection(LocalDate dateProspection) {
    this.dateProspection = dateProspection;
  }

  /**
   * Getter interest.
   *
   * @return interest
   */
  public Interesse getInteresse() {
    return interesse;
  }

  /**
   * Setter interest.
   *
   * @param interesse interest
   */
  public void setInteresse(Interesse interesse) {
    this.interesse = interesse;
  }

  /**
   * Retourne le type de cette société.
   *
   * @return la chaîne "Prospect"
   */
  @Override
  public String getTypeSociete() {
    return "Prospect";
  }

  /**
   * Retourne une représentation textuelle du prospect.
   *
   * @return une chaîne au format "Raison Sociale (Prospect)"
   */
  @Override
  public String toString() {
    return getRaisonSociale() + " (Prospect)";
  }

}
