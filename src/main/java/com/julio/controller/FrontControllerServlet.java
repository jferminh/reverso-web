package com.julio.controller;

import com.julio.controller.common.AccueilCommand;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Front Controller — unique servlet de l'application.
 * Toutes les requêtes passent par /app et sont dispatchées
 * vers une ICommand via une HashMap cmd → ICommand.
 */
@WebServlet(name = "FrontController", urlPatterns = {"/app"})
public class FrontControllerServlet extends HttpServlet {
  private static final Logger LOGGER =
      Logger.getLogger(FrontControllerServlet.class.getName());

  private static final String VUE_ERREUR = "/WEB-INF/views/common/erreur.jsp";

  /**
   * Routeur : nom de commande → implémentation ICommand.
   */
  private Map<String, Icommand> commands;

  /**
   * Validator Bean Validation partagée (instancié une fois).
   */
  private Validator validator;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }

  private void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    String cmd = request.getParameter("cmd");
    String vue = VUE_ERREUR;

    try {
      Icommand command = commands.get(cmd);
      if (command == null) {
        LOGGER.warning("Commande inconnue ou null, cmd=[" + cmd + "]");
        // fallback temporaire : on utilisera AccueilCommand plus tard
        request.setAttribute("erreurMessage", "Commande inconnue : " + cmd);

      } else {
        vue = command.execute(request, response);
      }
    } catch (Exception ex) {
      LOGGER.severe("Erreur lors de l'exécution de la commande [" + cmd + "] : "
          + ex.getClass().getSimpleName() + " - " + ex.getMessage());
      request.setAttribute("erreurMessage", ex.getMessage());
      vue = VUE_ERREUR;
    } finally {
      // Si aucune redirection n'a été faite, on forward
      if (!response.isCommitted()) {
        request.getRequestDispatcher(vue).forward(request, response);
      }
    }
  }

  @Override
  public void destroy() {
    LOGGER.info("FrontControllerServlet détruit");
  }

  @Override
  public void init() {
    commands = new HashMap<>();

    // 1) Commande par défaut (accueil)
    commands.put(null, new AccueilCommand());
    commands.put("accueil", new AccueilCommand());

    // 2) Commandes Clients (à implémenter ensuite)
    // commands.put("listClients", new ListClientsCommand());
    // commands.put("createClient", new CreateClientCommand());
    // commands.put("saveClient", new SaveClientCommand());
    // etc.

    // 3) Commandes Prospects (à implémenter ensuite)
    // ...

    // Bean Validation : Validator global
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    // On le met à disposition des Commands via le ServletContext
    getServletContext().setAttribute("validator", validator);

    LOGGER.info("Front Controller initialisé");
  }
}
