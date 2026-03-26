package com.julio.controller.client;

import com.julio.dao.ClientDao;
import com.julio.dao.ContratDao;
import com.julio.exception.ResourceNotFoundException;
import com.julio.model.Client;
import com.julio.model.Contrat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (GET) chargée d'afficher la vue détaillée d'un client (Mode lecture seule).
 *
 * <p>Récupère l'identifiant depuis la requête HTTP, extrait le client complet
 * depuis la base de données, et l'injecte dans le contexte pour l'affichage (Météo, Carte).
 * </p>
 *
 * @author Julio
 * @version 2.0
 */
@Slf4j
public class ViewClientCommand extends AbstractClientCommand {

  private static final String VUE_DETAIL = "/WEB-INF/views/client/detail-client.jsp";

  /**
   * Exécute le chargement des détails du client.
   *
   * @param request  La requête HTTP entrante contenant l'ID du client.
   * @param response La réponse HTTP sortante.
   * @return Le chemin de la vue JSP des détails ({@link #VUE_DETAIL}).
   * @throws Exception Si les paramètres sont invalides ou si le client est introuvable.
   */
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. & 2. Validation GET et extraction de l'ID (DRY)
    if (!isGetMethodValid(request, response)) {
      return null;
    }
    int id = validerEtExtraireId(request, "client");

    log.info("Consultation des détails du client ID={}", id);

    // 3. Récupération des données via le DAO
    ClientDao clientDao = new ClientDao();
    Client client = clientDao.findById(id);

    // 4. Validation métier
    if (client == null) {
      log.warn("Consultation échouée : Aucun client trouvé pour l'ID={}", id);
      throw new ResourceNotFoundException(
          "Le client que vous souhaitez consulter n'existe pas ou a été supprimé.");
    }

    ContratDao contratDao = new ContratDao();
    List<Contrat> contratsDuClient = contratDao.findByIdClient(id);
    client.setContrats(contratsDuClient);

    log.debug("{} contrat(s) chargé(s) pour le client ID={}", contratsDuClient.size(), id);

    // 5. Injection et Routage
    request.setAttribute("client", client);
    request.setAttribute("pageTitle", client.getRaisonSociale() + " - Détails");

    return VUE_DETAIL;
  }
}