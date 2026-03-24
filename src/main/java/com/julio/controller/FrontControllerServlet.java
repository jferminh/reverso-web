package com.julio.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur frontal de l'application (Pattern MVC : Front Controller).
 * Point d'entrée unique pour toutes les requêtes sécurisées "/app".
 */
@Slf4j
@WebServlet(name = "FrontController", urlPatterns = {"/app"})
public class FrontControllerServlet extends HttpServlet {

  // 🗑️ SUPPRESSION : Plus besoin de la Map ici, ni de la méthode init() !
  // L'usine s'en occupe maintenant.

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

    // ✅ OPTIMISATION : Encodage défini une seule fois ici
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    // 1. On lit ce que l'utilisateur veut faire
    String cmd = request.getParameter("cmd");

    // 2. 🪄 PATTERN FACTORY : On demande à l'usine la bonne commande
    Icommand command = CommandFactory.getCommand(cmd);

    try {
      // 3. On exécute la commande (Polymorphisme)
      String vue = command.execute(request, response);

      // 4. On redirige vers la vue JSP (si la commande n'a pas déjà fait un sendRedirect)
      if (vue != null) {
        request.getRequestDispatcher(vue).forward(request, response);
      }

    } catch (Exception e) {
      log.error("Erreur critique lors de l'exécution de la commande '{}'", cmd, e);
      // Redirection générique en cas de crash serveur (Erreur 500)
      request.setAttribute("erreurMessage", "Une erreur interne est survenue sur le serveur.");
      request.getRequestDispatcher("/WEB-INF/views/common/erreur.jsp").forward(request, response);
    }
  }
}