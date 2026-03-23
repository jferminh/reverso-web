package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.dao.ClientDao;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (GET) pour afficher la fiche détaillée d'un client.
 */
@Slf4j
public class ViewClientCommand implements Icommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String idStr = request.getParameter("id");

    if (idStr == null || idStr.isBlank()) {
      request.getSession().setAttribute("erreurMessage", "ID du client manquant.");
      response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
      return null;
    }

    try {
      Integer id = Integer.parseInt(idStr);
      ClientDao clientDao = new ClientDao();
      Client client = clientDao.findById(id);

      if (client == null) {
        request.getSession().setAttribute("erreurMessage", "Ce client n'existe plus.");
        response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
        return null;
      }

      // On transmet le client à la JSP
      request.setAttribute("client", client);
      request.setAttribute("pageTitle", client.getRaisonSociale() + " - Détails");

      return "/WEB-INF/views/client/detail-client.jsp";

    } catch (Exception e) {
      log.error("Erreur lors du chargement des détails du client ID={}", idStr, e);
      request.getSession().setAttribute("erreurMessage",
          "Erreur technique lors du chargement de la fiche.");
      response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
      return null;
    }
  }
}