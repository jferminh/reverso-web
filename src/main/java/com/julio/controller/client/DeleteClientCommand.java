package com.julio.controller.client;

import com.julio.dao.ClientDao;
import com.julio.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (POST) traitant la suppression définitive d'un client.
 *
 * <p>Implémente le pattern PRG (Post-Redirect-Get) et utilise les méthodes
 * factorisées de AbstractClientCommand (DRY).
 * </p>
 *
 * @author Julio
 * @version 3.0
 */
@Slf4j
public class DeleteClientCommand extends AbstractClientCommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. Validation de la méthode HTTP (POST) et extraction de l'ID
    if (!isPostMethodValid(request, response)) {
      return null;
    }
    int id = validerEtExtraireId(request, "client");

    log.info("Demande de suppression pour le client ID={}", id);

    // 2. Appel du DAO pour la suppression
    ClientDao clientDao = new ClientDao();
    boolean estSupprime = clientDao.delete(id);

    // 3. Vérification métier
    if (!estSupprime) {
      log.warn("Échec de la suppression : Le client ID={} est introuvable.", id);
      throw new ResourceNotFoundException(
          "Le client que vous essayez de supprimer n'existe plus.");
    }

    // 4. Succès et Redirection (PRG)
    log.info("Client ID={} supprimé avec succès.", id);
    HttpSession session = request.getSession();
    session.setAttribute("successMessage", "Le client a été supprimé définitivement.");

    response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
    return null;
  }
}