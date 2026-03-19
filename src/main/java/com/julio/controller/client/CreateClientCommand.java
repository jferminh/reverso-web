package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Crée un client.
 */
@Slf4j
public class CreateClientCommand implements Icommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
    log.info("Affichage formulaire création client");

    // Client vide pour que le JSP sache qu'on est en mode création
    request.setAttribute("client", new Client());
    request.setAttribute("pageTitle", "Nouveau Client");
    request.setAttribute("modeEdit", false);

    return "/WEB-INF/views/client/form-client.jsp";
  }
}
