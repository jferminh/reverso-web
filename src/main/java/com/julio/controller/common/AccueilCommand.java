package com.julio.controller.common;

import com.julio.controller.Icommand;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Commande d'accueil — première page de l'application.
 * Retourne la JSP d'accueil au Front Controller.
 */
public class AccueilCommand implements Icommand {
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    return "/WEB-INF/views/common/accueil.jsp";
  }
}
