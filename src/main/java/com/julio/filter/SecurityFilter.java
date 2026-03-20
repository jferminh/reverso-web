package com.julio.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtre de sécurité global pour l'application.
 * Intercepte toutes les requêtes vers le FrontController (/app) pour valider :
 * 1. L'authentification (Sessions).
 * 2. La protection CSRF sur les requêtes POST.
 *
 * @author Julio
 * @version 1.0
 */
@Slf4j
@WebFilter(urlPatterns = {"/app"})
public class SecurityFilter implements Filter {

  // ✅ Liste des commandes "publiques" qui ne nécessitent pas de connexion
  // Si tu fais l'option ECF, tu peux ajouter "listClients" ici.
  private static final Set<String> PUBLIC_COMMANDS = Set.of(
      "login",
      "doLogin",
      "accueil"
  );

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    // On récupère la session sans la créer si elle n'existe pas
    HttpSession session = req.getSession(false);

    // Extraction de la commande demandée
    String cmd = req.getParameter("cmd");
    if (cmd == null) {
      cmd = "accueil";
    }

    boolean isPublicCmd = PUBLIC_COMMANDS.contains(cmd);
    boolean isAuthenticated =
        (session != null && session.getAttribute("utilisateurActif") != null);

    // ==========================================
    // 1. VÉRIFICATION DE L'AUTHENTIFICATION
    // ==========================================
    if (!isPublicCmd && !isAuthenticated) {
      log.warn("Accès refusé : utilisateur non authentifié tente d'accéder à cmd='{}'", cmd);
      // On redirige vers la page de login
      res.sendRedirect(req.getContextPath() + "/app?cmd=login");
      return; // ⛔ On bloque la requête ici
    }

    // ==========================================
    // 2. VÉRIFICATION DU TOKEN CSRF (Seulement POST)
    // ==========================================
    // Les requêtes POST modifient la BDD, elles doivent être protégées !
    if (req.getMethod().equalsIgnoreCase("POST") && isAuthenticated) {
      String sessionToken = (String) session.getAttribute("csrfToken");
      String requestToken = req.getParameter("csrfToken");

      if (sessionToken == null || !sessionToken.equals(requestToken)) {
        log.error("ALERTE SÉCURITÉ : Échec de la validation CSRF pour cmd='{}' (IP: {})",
            cmd, req.getRemoteAddr());
        // On renvoie une erreur 403 Forbidden
        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Action non autorisée (Erreur CSRF)");
        return; // ⛔ On bloque la requête ici
      }
    }

    // ==========================================
    // 3. TOUT EST OK -> ON LAISSE PASSER
    // ==========================================
    chain.doFilter(request, response);
  }
}