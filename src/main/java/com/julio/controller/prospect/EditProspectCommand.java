package com.julio.controller.prospect;

import com.julio.dao.ProspectDao;
import com.julio.exception.InvalidParameterException;
import com.julio.exception.ResourceNotFoundException;
import com.julio.model.Prospect;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (GET) chargée de préparer le formulaire de modification d'un prospect.
 *
 * <p>Extrait les données du prospect ciblé par l'ID fourni et configure la vue JSP
 * en mode Édition.
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
@Slf4j
public class EditProspectCommand extends AbstractProspectCommand {

  /**
   * Exécute la préparation du formulaire d'édition.
   *
   * @param request  La requête HTTP entrante contenant l'ID du prospect.
   * @param response La réponse HTTP sortante.
   * @return Le chemin de la vue JSP du formulaire prospect ({@link #VUE_FORM}).
   * @throws Exception Si les paramètres sont invalides ou si le prospect est introuvable.
   */
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. Sécurité
    if (!"GET".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return null;
    }

    // 2. Extraction et validation de l'ID
    String idStr = request.getParameter("id");
    if (idStr == null || idStr.isBlank()) {
      throw new InvalidParameterException("L'identifiant du prospect est manquant dans l'URL.");
    }

    int id;
    try {
      id = Integer.parseInt(idStr);
    } catch (NumberFormatException e) {
      log.warn("Tentative d'accès avec un ID prospect mal formaté : {}", idStr);
      throw new InvalidParameterException("Le format de l'identifiant est invalide.");
    }

    log.info("Chargement des données pour l'édition du prospect ID={}", id);

    // 3. Récupération des données via le DAO
    ProspectDao prospectDao = new ProspectDao();
    Prospect prospect = prospectDao.findById(id);

    // 4. Validation métier
    if (prospect == null) {
      log.warn("Aucun prospect trouvé en base pour l'ID={}", id);
      throw new ResourceNotFoundException(
          "Le prospect que vous souhaitez modifier n'existe pas ou a été supprimé.");
    }

    // 5. Préparation du contexte JSP
    request.setAttribute("prospect", prospect);
    request.setAttribute("modeEdit", true);
    request.setAttribute("pageTitle", "Modifier " + prospect.getRaisonSociale()
        + " - Reverso CRM");

    return VUE_FORM;
  }
}