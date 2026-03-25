package com.julio.controller.prospect;

import com.julio.controller.Icommand;
import com.julio.dao.ProspectDao;
import com.julio.exception.InvalidParameterException;
import com.julio.exception.ResourceNotFoundException;
import com.julio.model.Prospect;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (GET) chargée d'afficher la vue détaillée d'un prospect (Mode lecture seule).
 *
 * <p>Extrait les données du prospect ciblé par l'ID fourni et configure la vue JSP
 * pour afficher les cartes, la météo et le suivi commercial.
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
@Slf4j
public class ViewProspectCommand implements Icommand {

  private static final String VUE_DETAIL = "/WEB-INF/views/prospect/detail-prospect.jsp";

  /**
   * Exécute le chargement des détails du prospect.
   *
   * @param request  La requête HTTP entrante contenant l'ID du prospect.
   * @param response La réponse HTTP sortante.
   * @return Le chemin de la vue JSP des détails prospect ({@link #VUE_DETAIL}).
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
      log.warn("Tentative de consultation avec un ID prospect mal formaté : {}", idStr);
      throw new InvalidParameterException("Le format de l'identifiant est invalide.");
    }

    log.info("Consultation des détails du prospect ID={}", id);

    // 3. Récupération des données via le DAO
    ProspectDao prospectDao = new ProspectDao();
    Prospect prospect = prospectDao.findById(id);

    // 4. Validation métier
    if (prospect == null) {
      log.warn("Consultation échouée : Aucun prospect trouvé pour l'ID={}", id);
      throw new ResourceNotFoundException("Le prospect que vous souhaitez consulter n'existe pas ou a été supprimé.");
    }

    // 5. Injection et Routage
    request.setAttribute("prospect", prospect);
    request.setAttribute("pageTitle", prospect.getRaisonSociale() + " - Détails");

    return VUE_DETAIL;
  }
}