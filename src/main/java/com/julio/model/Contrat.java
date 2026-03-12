package com.julio.model;

import com.julio.exception.ValidationException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Classe représentant un contrat commercial dans le système de gestion.
 *
 * <p>Un contrat est associé à un client et contient les informations essentielles
 * telles que le nom du contrat et son montant financier. Les identifiants
 * des contrats sont générés automatiquement via un compteur statique incrémental.
 * </p>
 *
 * <p>Contraintes métier :
 * </p>
 * <ul>
 *   <li>L'ID du client doit être strictement positif</li>
 *   <li>Le nom du contrat est obligatoire (non vide).</li>
 *   <li>Le montant doit être strictement positif</li>
 * </ul>
 *
 * @author Julio FERMIN
 * @version 1.0
 * @see Client
 * @since 19/11/2025
 */
public class Contrat implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Identifiant unique du contrat.
   */
  private Integer id;

  /**
   * Identifiant du client auquel le contrat est associé (doit être > 0).
   */
  private Integer clientId;

  /**
   * Nom ou désignation du contrat (obligatoire).
   */
  private String nomContrat;

  /**
   * Montant financier du contrat en euros (doit être > 0).
   */
  private double montant;

  /**
   * Constructeur principal de la classe Contrat.
   *
   * <p>Crée un nouveau contrat avec un identifiant auto-généré et valide
   * toutes les données métier. Le compteur d'identifiant est automatiquement
   * incrémenté après la création.
   * </p>
   *
   * @param clientId   identifiant du client propriétaire du contrat (doit être > 0)
   * @param nomContrat nom ou désignation du contrat (ne peut pas être vide)
   * @param montant    montant financier du contrat en euros (doit être > 0)
   * @throws ValidationException si une des validations échoue
   */
  public Contrat(Integer clientId, String nomContrat, double montant) throws ValidationException {
    this.id = null;
    setClientId(clientId);
    setNomContrat(nomContrat);
    setMontant(montant);
  }

  /**
   * Constructeur vide.
   *
   */
  public Contrat() {
  }

  /**
   * Getter id.
   *
   * @return id
   */
  public Integer getId() {
    return id;
  }

  /**
   * Setter id.
   *
   * @param id id
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Getter id client.
   *
   * @return id client
   */
  public Integer getClientId() {
    return clientId;
  }

  /**
   * Setter client id.
   *
   * @param clientId client id
   */
  public void setClientId(Integer clientId) {
    this.clientId = clientId;
  }

  /**
   * Getter nom client.
   *
   * @return nom client
   */
  public String getNomContrat() {
    return nomContrat;
  }

  /**
   * Setter nom contrat.
   *
   * @param nomContrat nom contrat
   */
  public void setNomContrat(String nomContrat) {
    this.nomContrat = nomContrat;
  }

  /**
   * Getter montant.
   *
   * @return montant
   */
  public double getMontant() {
    return montant;
  }

  /**
   * Setter montant.
   *
   * @param montant montant
   */
  public void setMontant(double montant) {
    this.montant = montant;
  }

  /**
   * Retourne une représentation textuelle du contrat.
   *
   * <p>Le format inclut le nom du contrat suivi du montant en euros.
   * </p>
   *
   * @return une chaîne au format "Nom Contrat (montant€)"
   */
  public String toString() {
    return nomContrat + " (" + montant + "€)";
  }

  /**
   * Méthode equals.
   *
   * @param o the reference object with which to compare.
   * @return Object
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Contrat)) {
      return false;
    }
    Contrat contrat = (Contrat) o;
    return Objects.equals(id, contrat.id);
  }

  /**
   * Méthode hashCode.
   *
   * @return Object
   */
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
