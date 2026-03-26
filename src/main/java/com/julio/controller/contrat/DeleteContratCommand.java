package com.julio.controller.contrat;

import com.julio.controller.Icommand;
import com.julio.dao.ContratDao;
import com.julio.exception.InvalidParameterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (POST) pour supprimer définitivement un contrat.
 *
 * <p>Implémente le pattern PRG. Nécessite l'ID du contrat à supprimer
 * ET l'ID du client pour la redirection.
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
@Slf4j
public class DeleteContratCommand implements Icommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. Sécurité
    if (!"POST".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return null;
    }

    // 2. Extraction des identifiants
    String idStr = request.getParameter("id");
    String clientIdStr = request.getParameter("clientId");

    if (idStr == null || idStr.isBlank() || clientIdStr == null || clientIdStr.isBlank()) {
      throw new InvalidParameterException("Paramètres manquants pour la suppression du contrat.");
    }

    int id;
    int clientId;
    try {
      id = Integer.parseInt(idStr);
      clientId = Integer.parseInt(clientIdStr);
    } catch (NumberFormatException e) {
      throw new InvalidParameterException("Format d'identifiant invalide.");
    }

    // 3. Suppression via le DAO
    ContratDao contratDao = new ContratDao();
    contratDao.delete(id);

    // 4. Succès et Redirection (PRG)
    HttpSession session = request.getSession();
    session.setAttribute("successMessage", "Le contrat a été supprimé définitivement.");
    log.info("Contrat ID={} supprimé avec succès.", id);

    response.sendRedirect(request.getContextPath() + "/app?cmd=viewClient&id=" + clientId);
    return null;
  }
}