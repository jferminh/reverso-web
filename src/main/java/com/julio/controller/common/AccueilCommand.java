package com.julio.controller.common;

import com.julio.controller.Icommand;
import com.julio.dao.ClientDao;
import com.julio.dao.ProspectDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur de la page d'accueil (tableau de bord).
 * Charge les statistiques de base et redirige vers accueil.jsp.
 */
@Slf4j
public class AccueilCommand implements Icommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    log.info("Chargement du tableau de bord");
    request.setAttribute("pageTitle", "Tableau de bord - Reverso CRM");

    try {
      ClientDao clientDao = new ClientDao();
      int nbClients = clientDao.findAll().size();
      request.setAttribute("nbClients", nbClients);
      ProspectDao prospectDao = new ProspectDao();
      int nbProspects = prospectDao.findAll().size();
      request.setAttribute("nbProspects", nbProspects);

    } catch (Exception e) {
      log.error("Erreur lors du comptage des client pour le tableau de bord", e);
      request.setAttribute("nbClients", 0); // Valeur par défault de sécurité
      request.setAttribute("nbProspects", 0);
      request.setAttribute("erreurMessage", "Impossible de charger certaines statistiques.");
    }

    return "/WEB-INF/views/common/accueil.jsp";
  }
}
