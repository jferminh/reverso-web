package com.julio.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interface du pattern Command utilisée par le Front Controller.
 * Chaque action de l'application implémente cette interface.
 */
public interface Icommand {
  /**
   * Exécute la logique métier et retourne le chemin de la JSP à afficher.
   *
   * @param request  requête HTTP entrante
   * @param response réponse HTTP sortante
   * @return chemin relatif de la JSP (ex: "/WEB-INF/views/common/accueil.jsp")
   * @throws Exception en cas d'erreur métier ou DAO
   */
  String execute(HttpServletRequest request,
                 HttpServletResponse response) throws Exception;
}
