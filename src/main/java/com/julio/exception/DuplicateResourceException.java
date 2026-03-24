package com.julio.exception;

/**
 * Exception déclenchée lorsqu'une tentative de création ou de modification
 * enfreint une contrainte d'unicité dans le système.
 *
 * <p>Utilisée typiquement par le {@code UnicityService} lorsqu'une
 * raison sociale ou un email existe déjà en base de données.
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
public class DuplicateResourceException extends BusinessException {

  /**
   * Construit une nouvelle exception de ressource dupliquée.
   *
   * @param ressource Le nom de la ressource en doublon (ex : "Raison sociale", "Email").
   * @param valeur    La valeur qui pose problème (ex: "ACME Corp").
   */
  public DuplicateResourceException(String ressource, String valeur) {
    // Appel du constructeur parent avec un message formaté automatiquement
    super(String.format(
        "La ressource '%s' avec la valeur '%s' existe déjà dans le systeme.",
        ressource, valeur));
  }
}
