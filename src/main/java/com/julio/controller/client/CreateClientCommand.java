package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (GET) pour afficher le formulaire de création d'un client.
 * Prépare un objet Client vide pour les data binding dans le JSP.
 */
@Slf4j
public class CreateClientCommand implements Icommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
    log.info("Affichage du formulaire de création client");

    // Client vide pour que le JSP sache qu'on est en mode création
    // et pour éviter les NullPointerException dans les attributs 'value'
    request.setAttribute("client", new Client());
    request.setAttribute("pageTitle", "Nouveau Client - Reverso CRM");
    request.setAttribute("modeEdit", false);

    return "/WEB-INF/views/client/form-client.jsp";
  }
}