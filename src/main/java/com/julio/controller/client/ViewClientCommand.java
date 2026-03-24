package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.dao.ClientDao;
import com.julio.exception.InvalidParameterException;
import com.julio.exception.ResourceNotFoundException;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande permettant d'afficher la fiche détaillée d'un client spécifique.
 *
 * <p>Valide la présence de l'identifiant dans la requête GET et s'assure de l'existence
 * du client en base de données avant de déléguer l'affichage à la vue JSP.
 * </p>
 *
 * @author Julio
 * @version 2.0
 */
@Slf4j
public class ViewClientCommand implements Icommand {

  /**
   * Exécute le chargement des détails d'un client.
   *
   * @param request  La requête HTTP contenant le paramètre 'id'.
   * @param response La réponse HTTP.
   * @return Le chemin vers la vue des détails (detail-client.jsp).
   * @throws Exception Si l'ID est invalide ou si la base de données est inaccessible.
   */
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String idStr = request.getParameter("id");

    // 1. Vérification de l'intégrité de la requête
    if (idStr == null || idStr.isBlank()) {
      throw new InvalidParameterException("ID du client manquant pour afficher la fiche.");
    }

    try {
      Integer id = Integer.parseInt(idStr);
      ClientDao clientDao = new ClientDao();

      // 2. Récupération de l'entité complète
      Client client = clientDao.findById(id);

      // 3. Gestion de la ressource non trouvée (404 logique)
      if (client == null) {
        throw new ResourceNotFoundException("Ce client est introuvable ou a été supprimé.");
      }

      // 4. Préparation du contexte d'affichage
      request.setAttribute("client", client);
      request.setAttribute("pageTitle", client.getRaisonSociale() + " - Détails");

      return "/WEB-INF/views/client/detail-client.jsp";

    } catch (NumberFormatException e) {
      throw new InvalidParameterException("Format d'identifiant invalide.");
    }

  }
}
