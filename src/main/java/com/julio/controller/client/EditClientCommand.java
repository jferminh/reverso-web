package com.julio.controller.client;

import com.julio.dao.ClientDao;
import com.julio.exception.ResourceNotFoundException;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (GET) chargée de préparer le formulaire de modification d'un client.
 *
 * <p>Elle récupère l'identifiant depuis la requête HTTP, extrait le client correspondant
 * depuis la base de données, et l'injecte dans le contexte pour le Data-Binding JSP.
 * </p>
 *
 * @author Julio
 * @version 2.0
 */
@Slf4j
public class EditClientCommand extends AbstractClientCommand {

  /**
   * Exécute la préparation du formulaire d'édition.
   *
   * @param request  La requête HTTP entrante contenant l'ID du client.
   * @param response La réponse HTTP sortante.
   * @return Le chemin de la vue JSP du formulaire ({@link #VUE_FORM}).
   * @throws Exception Si les paramètres sont invalides ou si le client est introuvable.
   */
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. Validation de la méthode HTTP (DRY)
    if (!isGetMethodValid(request, response)) {
      return null;
    }

    // 2. Extraction sécurisée de l'ID (DRY)
    int id = validerEtExtraireId(request, "client");

    log.info("Chargement des données pour l'édition du client ID={}", id);

    // 3. Récupération des données via le DAO
    ClientDao clientDao = new ClientDao();
    Client client = clientDao.findById(id);

    // 4. Validation métier : Le client existe-t-il vraiment ?
    if (client == null) {
      log.warn("Aucun client trouvé en base pour l'ID={}", id);
      throw new ResourceNotFoundException(
          "Le client que vous souhaitez modifier n'existe pas ou a été supprimé.");
    }

    // 5. Préparation du contexte pour la vue JSP
    request.setAttribute("client", client);
    request.setAttribute("modeEdit", true);
    request.setAttribute("pageTitle", "Modifier " + client.getRaisonSociale()
        + " - Reverso CRM");

    // Retourne la constante définie dans AbstractClientCommand
    return VUE_FORM;
  }
}