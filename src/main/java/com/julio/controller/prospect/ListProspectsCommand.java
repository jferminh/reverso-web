package com.julio.controller.prospect;

import com.julio.controller.Icommand;
import com.julio.dao.ProspectDao;
import com.julio.model.Prospect;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande responsable de la récupération et de l'affichage de la liste complète des prospects.
 *
 * <p>Conformément au principe de Responsabilité Unique (SRP), cette classe se concentre
 * uniquement sur le chemin critique (Happy Path). Les exceptions techniques
 * (ex : {@link com.julio.exception.DaoException}) remontent automatiquement
 * vers le {@link com.julio.service.ExceptionService} via le contrôleur frontal.
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
@Slf4j
public class ListProspectsCommand implements Icommand {

  /**
   * Exécute l'action de listage des prospects.
   *
   * @param request  La requête HTTP entrante.
   * @param response La réponse HTTP sortante.
   * @return Le chemin relatif vers la vue JSP (liste des prospects).
   * @throws Exception Si une erreur de base de données survient (traitée globalement).
   */
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    log.info("Chargement de la liste des prospects...");

    // 1. Instanciation du DAO et appel de la méthode de lecture
    ProspectDao prospectDao = new ProspectDao();
    List<Prospect> prospects = prospectDao.findAll();

    log.debug("{} prospect(s) extrait(s) de la base de données", prospects.size());

    // 2. Préparation du contexte pour la vue JSP (Data-Binding)
    request.setAttribute("prospects", prospects);
    request.setAttribute("nbProspects", prospects.size());
    request.setAttribute("pageTitle", "Liste des prospects - Reverso CRM");

    // 3. Routage vers la vue dédiée aux prospects
    return "/WEB-INF/views/prospect/list-prospects.jsp";
  }
}