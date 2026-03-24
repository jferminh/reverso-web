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
 * Commande (GET) permettant de charger les données d'un client existant
 * et de préremplir dans le formulaire de modification.
 *
 * <p>Les erreurs de paramètres (ID manquant, format invalide) ou d'absence de données (404)
 * lèvent des exceptions métier qui sont interceptées et traduites par le ExceptionService.
 * Les exceptions techniques (DaoException) remontent également de manière transparente.
 * </p>
 *
 * @author Julio
 * @version 2.0
 */
@Slf4j
public class EditClientCommand implements Icommand {

  /**
   * Exécute la préparation du formulaire d'édition.
   *
   * @param request  La requête HTTP contenant le paramètre 'id'.
   * @param response La réponse HTTP.
   * @return Le chemin vers la vue du formulaire (form-client.jsp).
   * @throws Exception Si l'ID est invalide, le client introuvable, ou en cas d'erreur BDD.
   */
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. Sécurité : Vérification de la méthode HTTP
    if (!"GET".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return null;
    }

    String idStr = request.getParameter("id");

    // 2. Validation de la présence de l'identifiant
    // (Lève une exception gérée par ExceptionService)
    if (idStr == null || idStr.isBlank()) {
      throw new InvalidParameterException(
          "L'identifiant du client est manquant pour la modification.");
    }

    Integer id;
    try {
      id = Integer.parseInt(idStr);
    } catch (NumberFormatException e) {
      throw new InvalidParameterException("Le format de l'identifiant client est invalide.");
    }

    log.info("Chargement du client ID={} pour modification", id);

    // 3. Appel au DAO (les DaoException remontent automatiquement)
    ClientDao clientDao = new ClientDao();
    Client client = clientDao.findById(id);

    // 4. Vérification de l'existence de la ressource
    if (client == null) {
      throw new ResourceNotFoundException(
          "Le client que vous essayez de modifier n'existe plus en base de données.");
    }

    // 5. Data-Binding : on place le client dans la requête pour que le JSP s'auto-remplisse
    request.setAttribute("client", client);

    // 6. Configuration des métadonnées de la page pour le mode Édition
    request.setAttribute("modeEdit", true);
    request.setAttribute("pageTitle", "Modifier " + client.getRaisonSociale() + " - Reverso CRM");

    return "/WEB-INF/views/client/form-client.jsp";
  }
}