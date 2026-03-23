package com.julio.controller.common;

import com.julio.controller.Icommand;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande gérant la déconnexion d'un utilisateur.
 * Invalide la session en cours et détruit les tokens de sécurité (CSRF).
 */
@Slf4j
public class LogoutCommand implements Icommand {
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    // On récupère la session actuelle
    // (le 'false' évite d'en créer une nouvelle si elle n'existe pas).
    HttpSession session = request.getSession(false);

    if (session != null) {
      // On récupère le nom pour le log avant de détruire la session
      String utilisateur = (String) session.getAttribute("utilisateurActif");

      // ✅ LA COMMANDE MAGIQUE : Détruit la session et vide la mémoire du serveur
      session.invalidate();

      log.info("Déconnexion réussie pour l'utilisateur : {}", utilisateur);
    } else {
      log.warn("Tentative de déconnexion détectée alors qu'aucune session n'étais active.");
    }

    // On redirige proprement vers la page de login
    response.sendRedirect(request.getContextPath() + "/app?cmd=login");

    // On retourne null car on a fait une redirection (Pattern PRG)
    return null;
  }
}
