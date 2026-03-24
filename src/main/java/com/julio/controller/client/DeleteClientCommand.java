package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.dao.ClientDao;
import com.julio.exception.BusinessException;
import com.julio.exception.DaoException;
import com.julio.exception.InvalidParameterException;
import com.julio.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande traitant la suppression définitive d'un client via une requête HTTP POST.
 *
 * <p>Implémente le pattern PRG (Post-Redirect-Get) pour éviter la double soumission.
 * Lève des {@link com.julio.exception.BusinessException} si les paramètres sont invalides,
 * déléguant ainsi l'affichage des erreurs au service centralisé.
 * </p>
 *
 * @author Julio
 * @version 2.0
 */
@Slf4j
public class DeleteClientCommand implements Icommand {

  /**
   * Exécute l'action de suppression d'un client.
   *
   * @param request  La requête HTTP entrante contenant l'ID du client.
   * @param response La réponse HTTP sortante pour la redirection.
   * @return null car la méthode effectue une redirection (sendRedirect).
   * @throws Exception Si les paramètres sont invalides ou si la BDD renvoie une erreur.
   */
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. Contrôle de sécurité du verbe HTTP
    if (!"POST".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return null;
    }

    String idStr = request.getParameter("id");
    HttpSession session = request.getSession();

    // 2. Validation de la présence de la donnée
    // (Lève une exception métier attrapée par le service)
    if (idStr == null || idStr.isBlank()) {
      throw new InvalidParameterException("Le format de l'identifiant est invalide.");
    }

    try {
      Integer id = Integer.parseInt(idStr);
      log.info("Demande de suppression pour le client ID={}", id);

      // 3. Appel du DAO pour la suppression
      ClientDao clientDao = new ClientDao();
      boolean estSupprime = clientDao.delete(id);

      if (estSupprime) {
        log.info("Client ID={} supprimé avec succès.", id);
        // Utilisation de la session pour la persistance du message flash (Pattern PRG)
        session.setAttribute("successMessage", "Le client a été supprimé définitivement.");
      } else {
        throw new ResourceNotFoundException(
            "Le client que vous essayez de supprimer n'existe plus.");
      }
    } catch (NumberFormatException e) {
      throw new InvalidParameterException("Le format de l'identifiant est invalide.");
    }

    // 4. Redirection vers la liste (Vide l'historique POST du navigateur)
    response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
    return null;
  }
}