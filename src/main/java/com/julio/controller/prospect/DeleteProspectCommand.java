package com.julio.controller.prospect;

import com.julio.dao.ProspectDao;
import com.julio.exception.DaoException;
import com.julio.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (POST) traitant la suppression définitive d'un prospect.
 *
 * <p>Implémente le pattern PRG (Post-Redirect-Get) et utilise l'extraction
 * factorisée des données pour un code sécurisé et concis.
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
@Slf4j
public class DeleteProspectCommand extends AbstractProspectCommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. Validation HTTP (POST) et extraction sécurisée de l'ID
    if (!isPostMethodValid(request, response)) {
      return null;
    }
    int id = validerEtExtraireId(request, "prospect");

    log.info("Demande de suppression pour le prospect ID={}", id);

    // 2. Appel du DAO pour la suppression
    try {
      ProspectDao prospectDao = new ProspectDao();
      prospectDao.delete(id); // Si l'ID n'existe pas, delete() lève une DaoException

      // 3. Succès et Redirection (PRG)
      log.info("Prospect ID={} supprimé avec succès.", id);
      HttpSession session = request.getSession();
      session.setAttribute("successMessage", "Le prospect a été supprimé définitivement.");

      response.sendRedirect(request.getContextPath() + "/app?cmd=listProspects");
      return null;

    } catch (DaoException e) {
      // 4. Traduction d'une erreur technique "Non Trouvé" en erreur métier
      if (e.getErrorCode() == DaoException.ErrorCode.ENTITY_NOT_FOUND) {
        log.warn("Échec de la suppression : Le prospect ID={} est introuvable.", id);
        throw new ResourceNotFoundException(
            "Le prospect que vous essayez de supprimer n'existe plus.");
      }
      throw e; // Laisse remonter les vraies erreurs techniques (BDD éteinte, etc.)
    }
  }
}