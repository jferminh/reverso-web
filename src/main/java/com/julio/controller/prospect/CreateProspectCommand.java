package com.julio.controller.prospect;

import com.julio.controller.Icommand;
import com.julio.model.Adresse;
import com.julio.model.Prospect;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (GET) chargée de préparer et d'afficher le formulaire vierge
 * pour la création d'un nouveau prospect.
 *
 * <p>Initialise une grappe d'objets vide (Prospect + Adresse) pour faciliter
 * le Data-Binding dans la vue JSP et éviter les NullPointerException.
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
@Slf4j
public class CreateProspectCommand implements Icommand {

  /**
   * Exécute l'initialisation du formulaire de création.
   *
   * @param request  La requête HTTP entrante.
   * @param response La réponse HTTP sortante.
   * @return Le chemin vers la vue JSP du formulaire (form-prospect.jsp).
   * @throws Exception Si une erreur inattendue survient.
   */
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. Sécurité : On s'assure que c'est bien une requête GET
    if (!"GET".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return null;
    }

    log.info("Affichage du formulaire de création de prospect");

    // 2. Préparation d'un objet vide pour le formulaire
    Prospect prospectVide = new Prospect();
    prospectVide.setAdresse(new Adresse()); // Évite une erreur null sur prospect.adresse.rue

    // 3. Injection dans la requête
    request.setAttribute("prospect", prospectVide);
    request.setAttribute("modeEdit", false); // Indique à la vue qu'on est en mode Création
    request.setAttribute("pageTitle", "Nouveau Prospect - Reverso CRM");

    // 4. Routage vers le JSP
    return "/WEB-INF/views/prospect/form-prospect.jsp";
  }
}