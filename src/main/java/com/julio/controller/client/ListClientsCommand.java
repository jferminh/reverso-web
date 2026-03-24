package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.dao.ClientDao;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande responsable de la récupération et de l'affichage de la liste complète des clients.
 *
 * <p>Conformément au principe de Responsabilité Unique (SRP), cette classe se concentre
 * uniquement sur le chemin critique (Happy Path). Les exceptions techniques
 * (ex : {@link com.julio.exception.DaoException}) remontent automatiquement
 * vers le {@link com.julio.service.ExceptionService} via le contrôleur frontal.
 * </p>
 *
 * @author Julio
 * @version 2.0
 */
@Slf4j
public class ListClientsCommand implements Icommand {

  /**
   * Exécute l'action de listage des clients.
   *
   * @param request  La requête HTTP entrante.
   * @param response La réponse HTTP sortante.
   * @return Le chemin relatif vers la vue JSP (liste des clients).
   * @throws Exception Si une erreur de base de données survient (traitée globalement).
   */
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // ✅ DEBUG : début de l'exécution, utile pour tracer les flux en dev
    log.info("Chargement de la liste clients...");

    // 1. Initialisation du DAO et récupération
    ClientDao clientDao = new ClientDao();
    List<Client> clients = clientDao.findAll();

    log.debug("{} client(s) chargé(s)", clients.size());

    // 2. Passage des données à la vue
    request.setAttribute("clients", clients);
    request.setAttribute("nbClients", clients.size());
    request.setAttribute("pageTitle", "Liste des clients - Reverso CRM");

    // 3. Routage vers la vue
    return "/WEB-INF/views/client/list-clients.jsp";
  }
}