package com.julio.model;

import com.julio.exception.ValidationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un client dans le système de gestion.
 *
 * <p>Un client est une société avec des informations spécifiques telles que
 * le chiffre d'affaires, le nombre d'employés et une liste de contrats associés.
 * Les identifiants des clients sont générés automatiquement via un compteur
 * statique incrémental.
 * </p>
 *
 * <p>Contraintes métier :
 * </p>
 * <ul>
 *   <li>Le chiffre d'affaires doit être supérieur ou égal à 200</li>
 *   <li>Le nombre d'employés doit être supérieur ou égal à 1</li>
 * </ul>
 *
 * @author Julio FERMIN
 * @version 2.0
 * @since 19/11/2025
 * @see Societe
 * @see Contrat
 */
public class Client extends Societe {
  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Chiffre d'affaires du client en euros (minimum 200).
   */
  @NotNull(message = "Le chiffre d'affaires est obligatoire")
  @Min(value = 200, message = "Le chiffre d'affaires doit être >= 200")
  private Long chiffreAffaires;

  /**
   * Nombre d'employés du client (minimum 1).
   */
  @NotNull(message = "Le nombre d'employés est obligatoire")
  @Min(value = 1, message = "Le nombre d'employés doit être >= 1")
  private Integer nbEmployes;

  /**
   * Liste des contrats associés au client.
   */
  private List<Contrat> contrats;

  /**
   * Constructeur principal de la classe Client.
   *
   * <p>Crée un nouveau client avec un identifiant auto-généré et valide
   * toutes les données métier. Le compteur d'identifiant est automatiquement
   * incrémenté après la création.
   * </p>
   *
   * @param raisonSociale raison sociale du client (ne peut pas être vide)
   * @param adresse adresse complète du client (ne peut pas être null)
   * @param telephone numéro de téléphone (doit respecter le format validé)
   * @param email adresse email (doit respecter le format validé)
   * @param commentaires notes additionnelles (peut être null ou vide)
   * @param chiffreAffaires chiffre d'affaires du client (doit être >= 200)
   * @param nbEmployes nombre d'employés du client (doit être >= 1)
   * @throws ValidationException si une des validations échoue
   */
  public Client(String raisonSociale, Adresse adresse, String telephone,
                String email, String commentaires, long chiffreAffaires,
                int nbEmployes) throws ValidationException {
    super(raisonSociale, adresse, telephone, email, commentaires);
    setChiffreAffaires(chiffreAffaires);
    setNbEmployes(nbEmployes);
    this.contrats = new ArrayList<>();
  }

  /**
   * Constructeur vide.
   *
   * @param raisonSociale raison sociale
   * @param adresse adresse
   * @param telephone téléphone
   * @param email email
   * @param commentaires commentaires
   * @throws ValidationException validation
   */
  public Client(String raisonSociale, Adresse adresse,
                String telephone, String email, String commentaires)
      throws ValidationException {
    super(raisonSociale, adresse, telephone, email, commentaires);
  }

  /**
   * Getter chiffres d'affaires.
   *
   * @return chiffres d'affaires
   */
  public long getChiffreAffaires() {
    return chiffreAffaires;
  }

  /**
   * Setter chiffres d'affaires.
   *
   * @param chiffreAffaires chiffres d'affaires
   */
  public void setChiffreAffaires(long chiffreAffaires) {
    this.chiffreAffaires = chiffreAffaires;
  }

  /**
   * Getter numero d'employés.
   *
   * @return nbEmployes
   */
  public int getNbEmployes() {
    return nbEmployes;
  }

  /**
   * Setter numéro d'employés.
   *
   * @param nbEmployes nbEmployes
   */
  public void setNbEmployes(int nbEmployes) {
    this.nbEmployes = nbEmployes;
  }

  /**
   * Setter contrats.
   *
   * @param contrats contrats
   */
  public void setContrats(List<Contrat> contrats) {
    this.contrats = contrats;
  }

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
   * Retourne une représentation textuelle du client.
   *
   * @return une chaîne au format "Raison Sociale (Client)"
   */
  @Override
  public String toString() {
    return getRaisonSociale() + " (Client)";
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
