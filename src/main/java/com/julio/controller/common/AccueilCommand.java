package com.julio.controller.common;

import com.julio.controller.Icommand;
import com.julio.dao.ClientDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur de la page d'accueil (tableau de bord).
 * Charge les statistiques de base et redirige vers accueil.jsp.
 * SOLID SRP : une seule responsabilité = préparer le dashboard.
 */
@Slf4j
public class AccueilCommand implements Icommand {
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    log.info("Chargement du tableau de bord");

    // Stats pour les cartes du dashboard
    ClientDao clientDao = new ClientDao();
    int nbClients = clientDao.findAll().size();
    request.setAttribute("nbClients", nbClients);
    request.setAttribute("pageTitle", "Tableau de bord");

    return "/WEB-INF/views/common/accueil.jsp";
  }
}
