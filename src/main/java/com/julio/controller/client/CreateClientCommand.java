package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.model.Adresse;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (GET) chargée de préparer et d'afficher le formulaire vierge
 * pour la création d'un nouveau client.
 *
 * <p>Initialise une grappe d'objets vide (Client + Adresse) pour faciliter
 * le Data-Binding dans la vue JSP et éviter les NullPointerException.
 * </p>
 *
 * @author Julio
 * @version 1.1
 */
@Slf4j
public class CreateClientCommand implements Icommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

    // 1. Sécurité : On s'assure que c'est bien une requête GET
    if (!"GET".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return null;
    }

    log.info("Affichage du formulaire de création de client");

    // 2. Préparation d'un objet vide pour le formulaire
    Client clientVide = new Client();
    clientVide.setAdresse(new Adresse()); // Évite une erreur null sur prospect.adresse.rue

    // 3. Injection dans la requête
    request.setAttribute("client", new Client());
    request.setAttribute("modeEdit", false); // Indique à la vue qu'on est en mode Création
    request.setAttribute("pageTitle", "Nouveau Client - Reverso CRM");

    // 4. Routage vers le JSP
    return "/WEB-INF/views/client/form-client.jsp";
  }
}