package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.dao.ClientDao;
import com.julio.exception.DaoException;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Charge et expose la liste de tous les clients.
 * SOLID SRP : une seule responsabilité = récupérer la liste et l'envoyer à la vue.
 */
@Slf4j
public class ListClientsCommand implements Icommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

    // ✅ DEBUG : début de l'exécution, utile pour tracer les flux en dev
    log.info("Chargement de la liste clients...");

    try {
      // 1. Initialisation du DAO et récupération
      ClientDao clientDao = new ClientDao();
      List<Client> clients = clientDao.findAll();

      log.debug("{} client(s) chargé(s) depuis la base de données", clients.size());

      // 2. Passage des données à la vue
      request.setAttribute("clients", clients);
      request.setAttribute("nbClients", clients.size()); // Correction de bbClients
      request.setAttribute("pageTitle", "Liste des clients - Reverso CRM");

    } catch (DaoException e) {
      // 3. Sécurité : En cas d'erreur SQL, on ne fait pas planter Tomcat
      log.error("Erreur technique lors de la récupération de la liste des clients", e);
      request.setAttribute("erreurMessage",
          "Impossible de charger les données depuis la base de données.");
    }

    // 💡 Astuce : Plus besoin de gérer le Flash Message ici !
    // Le tag <c:remove> dans list-clients.jsp s'en occupe tout seul.

    return "/WEB-INF/views/client/list-clients.jsp";
  }
}