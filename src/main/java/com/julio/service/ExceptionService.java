package com.julio.service;

import com.julio.exception.BusinessException;
import com.julio.exception.DaoException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Service centralisé pour la gestion et la traduction des exceptions (Exception Handler).
 *
 * <p>Ce service applique le principe de Responsabilité Unique (SRP). Au lieu de laisser
 * les contrôleurs deviner comment traiter chaque erreur, ce service analyse l'exception,
 * la loggue avec le bon niveau de sévérité, et traduit l'erreur technique en un
 * message compréhensible pour l'utilisateur final.
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
@Slf4j
public class ExceptionService {

  /**
   * Analyse l'exception remontée, prépare le message d'erreur et détermine la vue de redirection.
   *
   * @param e L'exception interceptée par le FrontController.
   * @param request La requête HTTP pour y injecter le message d'erreur.
   * @return Le chemin de la vue JSP à afficher (généralement la page d'erreur).
   */
  public String handleException(Exception e, HttpServletRequest request) {

    // 1. Exceptions Métier (Règles non respectées par l'utilisateur)
    if (e instanceof BusinessException) {
      // Un simple warning suffit, ce n'est pas un crash du serveur
      log.warn("Règle métier non respectée : {}", e.getMessage());
      request.setAttribute("erreurMessage", e.getMessage());
      return "/WEB-INF/views/common/erreur.jsp";
    }

    // 2. Exceptions d'accès aux données (Problème de base de données)
    if (e instanceof DaoException) {
      DaoException daoEx = (DaoException) e;
      // On loggue en ERROR car c'est un problème technique
      log.error("Erreur technique BDD [Code: {}] : {}",
          daoEx.getErrorCode(), daoEx.getMessage(), e);

      // Traduction d'une erreur technique SQL en message "User-Friendly"
      if (daoEx.getErrorCode() == DaoException.ErrorCode.FOREIGN_KEY_VIOLATION) {
        request.setAttribute("erreurMessage",
            "Action impossible : Cette donnée est liée à d'autres éléments (ex: contrats).");
      } else {
        request.setAttribute("erreurMessage",
            "Une erreur de communication avec la base de données est survenue.");
      }
      return "/WEB-INF/views/common/erreur.jsp";
    }

    // 3. Exceptions inattendues (Ex : NullPointerException)
    log.error("Erreur système critique et inattendue", e);
    request.setAttribute("erreurMessage",
        "Le serveur a rencontré un problème inattendu. Veuillez réessayer plus tard.");
    return "/WEB-INF/views/common/erreur.jsp";
  }
}
