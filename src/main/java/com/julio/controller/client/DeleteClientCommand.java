package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.dao.ClientDao;
import com.julio.exception.DaoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (POST) pour supprimer un client.
 * Récupère l'ID depuis la modale et exécute la suppression en base.
 */
@Slf4j
public class DeleteClientCommand implements Icommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. Sécurité : Vérifier que c'est bien une requête POST (venant du formulaire de la modale)
    if (!"POST".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return null;
    }

    String idStr = request.getParameter("id");
    HttpSession session = request.getSession();

    // 2. Validation de l'ID
    if (idStr == null || idStr.isBlank()) {
      log.warn("Tentative de suppression de client sans fournir d'ID");
      session.setAttribute("erreurMessage",
          "Impossible de supprimer : ID du client manquant.");
      response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
      return null;
    }

    try {
      Integer id = Integer.parseInt(idStr);
      log.info("Demande de suppression pour le client ID={}", id);

      // 3. Appel du DAO pour la suppression
      ClientDao clientDao = new ClientDao();
      boolean estSupprime = clientDao.delete(id);

      // 4. Gestion de la réponse
      if (estSupprime) {
        log.info("Client ID={} supprimé avec succès.", id);
        session.setAttribute("successMessage",
            "Le client a été supprimé définitivement.");
      } else {
        log.warn("Échec de la suppression : Le client ID={} est introuvable.", id);
        session.setAttribute("erreurMessage",
            "Le client n'a pas pu être supprimé car il est introuvable.");
      }

    } catch (NumberFormatException e) {
      log.error("Format d'ID invalide pour la suppression : '{}'", idStr);
      session.setAttribute("erreurMessage",
          "Identifiant du client invalide.");

    } catch (DaoException e) {
      log.error("Erreur en base de données lors de la suppression du client ID={}", idStr, e);

      // ✅ GESTION AVANCÉE : Si le client a des contrats liés,
      // la base de données bloquera la suppression (Clé étrangère)
      if (e.getErrorCode() == DaoException.ErrorCode.FOREIGN_KEY_VIOLATION) {
        session.setAttribute("erreurMessage",
            "Impossible de supprimer ce client : "
                + "il est lié à d'autres éléments (comme des contrats).");
      } else {
        session.setAttribute("erreurMessage",
            "Une erreur technique a empêché la suppression du client.");
      }
    }

    // 5. Redirection (Pattern PRG)
    // On renvoie toujours vers la liste pour mettre à jour l'affichage
    response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
    return null;
  }
}