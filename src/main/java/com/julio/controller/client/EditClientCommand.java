package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.dao.ClientDao;
import com.julio.exception.DaoException;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (GET) pour charger un client existant et afficher le formulaire de modification.
 */
@Slf4j
public class EditClientCommand implements Icommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. On s'assure que c'est bien une requête GET
    if (!"GET".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return null;
    }

    String idStr = request.getParameter("id");

    // 2. Vérification de l'ID
    if (idStr == null || idStr.isBlank()) {
      log.warn("Tentative de modification sans ID");
      request.getSession().setAttribute("erreurMessage",
          "ID du client manquant pour la modification.");
      response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
      return null;
    }

    try {
      Integer id = Integer.parseInt(idStr);
      log.info("Chargement du client ID={} pour modification", id);

      // 3. Récupération du client en base de données
      ClientDao clientDao = new ClientDao();
      Client client = clientDao.findById(id);

      // 4. Si le client n'existe pas (ex: supprimé entre temps par un autre utilisateur)
      if (client == null) {
        log.warn("Client ID={} introuvable en base de données", id);
        request.getSession().setAttribute("erreurMessage",
            "Le client que vous essayez de modifier n'existe plus.");
        response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
        return null;
      }

      // 5. On place le client dans la requête pour le "Data-Binding" du JSP
      request.setAttribute("client", client);

      // 6. On configure les métadonnées de la page pour le mode Édition
      request.setAttribute("modeEdit", true);
      request.setAttribute("pageTitle", "Modifier " + client.getRaisonSociale()
          + " - Reverso CRM");

      // On retourne la vue du formulaire (qui va s'auto-remplir !)
      return "/WEB-INF/views/client/form-client.jsp";

    } catch (NumberFormatException e) {
      log.error("Format d'ID invalide : '{}'", idStr);
      request.getSession().setAttribute("erreurMessage",
          "Identifiant du client invalide.");
      response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
      return null;

    } catch (DaoException e) {
      log.error("Erreur technique lors du chargement du client ID={}", idStr, e);
      request.getSession().setAttribute("erreurMessage",
          "Une erreur technique a empêché le chargement du client.");
      response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
      return null;
    }
  }
}