package com.julio.exception;

/**
 * Exception métier levée lorsqu'une entité (ex: Client, Prospect) demandée par l'utilisateur
 * n'existe pas ou n'existe plus dans la base de données.
 *
 * <p>Utilisée pour gérer proprement les erreurs logiques de type "404 Not Found" au sein des
 * commandes, sans pour autant déclencher une erreur technique critique.
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
public class ResourceNotFoundException extends BusinessException {

  /**
   * Construit une nouvelle exception avec un message explicatif.
   *
   * @param message Le message détaillé (ex: "Ce client n'existe plus").
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
