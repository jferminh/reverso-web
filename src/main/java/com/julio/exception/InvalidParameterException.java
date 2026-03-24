package com.julio.exception;

/**
 * Exception métier levée lorsqu'un paramètre fourni par l'utilisateur (ex: via
 * l'URL ou un formulaire) est manquant, vide ou ne respecte pas le format attendu
 * (ex: une lettre au lieu d'un chiffre pour un ID).
 *
 * <p>Hérite de {@link BusinessException} car il s'agit d'une erreur imputable à la requête
 * de l'utilisateur, et non à une défaillance technique du serveur.
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
public class InvalidParameterException extends BusinessException {

  /**
   * Construit une nouvelle exception avec un message explicatif.
   *
   * @param message Le message détaillé de l'erreur (ex: "L'identifiant est manquant").
   */
  public InvalidParameterException(String message) {
    super(message);
  }
}
