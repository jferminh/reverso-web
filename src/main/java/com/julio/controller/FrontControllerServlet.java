package com.julio.controller;

import com.julio.controller.client.CreateClientCommand;
import com.julio.controller.client.ListClientsCommand;
import com.julio.controller.common.AccueilCommand;
import com.julio.controller.common.LoginCommand;
import com.julio.dao.DatabaseConnexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Front Controller — unique servlet de l'application.
 * Toutes les requêtes passent par /app et sont dispatchées
 * vers une ICommand via une HashMap cmd → ICommand.
 */
@Slf4j
@WebServlet(urlPatterns = {"/app"})
public class FrontControllerServlet extends HttpServlet {

  private static final String VUE_ERREUR = "/WEB-INF/views/common/erreur.jsp";

  private Map<String, Icommand> commands;

  @Override
  public void init() {
    commands = new HashMap<>();

    // 1) Commandes par défaut (accueil et authentification)
    commands.put(null, new AccueilCommand());
    commands.put("accueil", new AccueilCommand());
    commands.put("login", new LoginCommand());

    // 2) Commandes Clients
    commands.put("listClients", new ListClientsCommand());
    commands.put("createClient", new CreateClientCommand());

    // Bean Validation : Validator global
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      Validator validator = factory.getValidator();
      getServletContext().setAttribute("validator", validator);
    }

    log.info("Front Controller initialisé avec succès");
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    processRequest(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    processRequest(request, response);
  }

  private void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    // ✅ OPTIMISATION : Encodage défini une seule fois ici
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    String cmd = request.getParameter("cmd");
    Icommand command = commands.get(cmd);
    String vue;

    if (command == null) {
      log.warn("Commande inconnue reçue - cmd='{}', fallback vers accueil", cmd);
      command = commands.get("accueil");
    } else {
      log.debug("Dispatch - cmd='{}' vers {}", cmd, command.getClass().getSimpleName());
    }

    try {
      vue = command.execute(request, response);
    } catch (Exception ex) {
      log.error("Erreur lors de l'exécution de la commande cmd='{}'", cmd, ex);
      request.setAttribute("erreurMessage", "Une erreur interne est survenue.");
      vue = VUE_ERREUR;
    }

    if (vue != null && !response.isCommitted()) {
      try {
        request.getRequestDispatcher(vue).forward(request, response);
      } catch (ServletException | IOException ex) {
        log.error("Erreur lors du forward vers '{}'", vue, ex);
      }
    }
  }

  @Override
  public void destroy() {
    log.info("FrontController détruit — libération des ressources");

    // ✅ CORRECTION CRITIQUE : Fermer le pool HikariCP d'abord !
    try {
      DatabaseConnexion.getInstance().closePool();
    } catch (Exception e) {
      log.error("Erreur lors de la fermeture du pool de connexions", e);
    }

    // Libération du driver MySQL (bonne pratique pour Tomcat)
    try {
      com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
      Enumeration<Driver> drivers = DriverManager.getDrivers();
      while (drivers.hasMoreElements()) {
        Driver driver = drivers.nextElement();
        if (driver.getClass().getName().equals("com.mysql.cj.jdbc.Driver")) {
          DriverManager.deregisterDriver(driver);
          log.info("Driver JDBC deregistered : {}", driver);
        }
      }
    } catch (Exception ex) {
      log.error("Erreur lors de la libération du driver MySQL", ex);
    }
  }
}