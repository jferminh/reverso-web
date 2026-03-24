package com.julio.exception;

/**
 * Exception racine abstraite pour toutes les erreurs liées aux règles de gestion (Métier).
 *
 * <p>Contrairement aux exceptions techniques (comme {@link java.sql.SQLException}),
 * ces exceptions sont déclenchées lorsque l'utilisateur enfreint une règle métier
 * (ex : création d'un doublon, données incohérentes). Elles sont destinées à être
 * rattrapées par les contrôleurs pour afficher un message clair à l'utilisateur.
 * </p>
 *
 * @author Julio
 * @version 1.0
 * @see com.julio.exception.DaoException
 */
public abstract class BusinessException extends Exception {
  /**
   * Constructeur avec message d'erreur.
   *
   * @param message Le message explicatif destiné à être affiché à l'utilisateur ou loggué.
   */
  public BusinessException(String message) {
    super(message);
  }

  /**
   * Constructeur avec message d'erreur et cause originelle.
   *
   * @param message Le message explicatif.
   * @param cause   L'exception technique ou d'origine ayant provoqué cette erreur métier.
   */
  public BusinessException(String message, Throwable cause) {
    super(message, cause);
  }
}
