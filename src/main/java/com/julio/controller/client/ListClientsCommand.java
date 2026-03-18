package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.dao.ClientDao;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Charge et expose la liste de tous les clients.
 * SOLID SRP : une seule responsabilité = récupérer la liste.
 */
@Slf4j
public class ListClientsCommand implements Icommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    // ✅ DEBUG : début de l'exécution, utile pour tracer les flux en dev
    log.info("Chargement de la liste clients");

    // Obtenir liste de clients
    var clients = ClientDao.getInstance().findAll();
    log.debug("{} client(s) chargé(s)", clients.size());

    // Passer à la vue
    request.setAttribute("clients", clients);
    request.setAttribute("bbClients", clients.size());
    request.setAttribute("pageTitle", "Liste des clients");

    // Flash message
    HttpSession session = request.getSession();
    String flashMessage = (String) session.getAttribute("flashMessage");
    if (flashMessage != null) {
      request.setAttribute("flashMessage", flashMessage);
      session.removeAttribute("flashMessage");
      log.debug("Flash message affiché et supprimé de la session");
    }

    return "/WEB-INF/views/client/list-clients.jsp";
  }
}
