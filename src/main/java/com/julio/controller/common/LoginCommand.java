package com.julio.controller.common;

import com.julio.controller.Icommand;
import com.julio.dao.UtilisateurDao;
import com.julio.exception.DaoException;
import com.julio.model.Utilisateur;
import com.julio.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande gérant l'authentification des utilisateurs.
 * Valide le mot de passe (Hash+Sel+Poivre) et initialise la Session et le Token CSRF.
 */
@Slf4j
public class LoginCommand implements Icommand {

    // ⚠️ Adapte ce chemin selon l'emplacement exact du fichier JSP de connexion
    private static final String VUE_LOGIN = "/WEB-INF/views/common/login.jsp";
    private final UtilisateurDao utilisateurDao;

    public LoginCommand() {
        try {
            this.utilisateurDao = new UtilisateurDao();
        } catch (DaoException e) {
            throw new RuntimeException("Erreur d'initialisation du LoginCommand", e);
        }
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // 1. Si l'utilisateur arrive sur la page (Méthode GET), on affiche juste le formulaire
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return VUE_LOGIN;
        }

        // 2. Si l'utilisateur a cliqué sur "Se connecter" (Méthode POST).
        String identifiant = request.getParameter("identifiant");
        String motDePasse = request.getParameter("motDePasse");

        if (identifiant == null || identifiant.trim().isEmpty() || motDePasse == null || motDePasse.isEmpty()) {
            request.setAttribute("erreurMessage", "Veuillez remplir tous les champs.");
            return VUE_LOGIN;
        }

        try {
            Utilisateur utilisateur = utilisateurDao.findByIdentifiant(identifiant);

            // Vérification cryptographique : Hash(Poivre + MDP_Saisi + Sel_En_BDD) == Hash_En_BDD ?
            if (utilisateur != null && SecurityUtil.verifyPassword(motDePasse, utilisateur.getSel(), utilisateur.getMotDePasse())) {

                // ✅ CRÉATION DE LA SESSION
                HttpSession session = request.getSession(true);
                session.setAttribute("utilisateurActif", utilisateur.getIdentifiant());

                // ✅ GÉNÉRATION DU TOKEN CSRF (Exigence ECF)
                String csrfToken = SecurityUtil.generateCsrfToken();
                session.setAttribute("csrfToken", csrfToken);

                log.info("Connexion réussie pour l'utilisateur : {}", identifiant);

                // Redirection vers l'accueil pour éviter la soumission double de formulaire (Pattern PRG)
                response.sendRedirect(request.getContextPath() + "/app?cmd=accueil");
                return null; // On retourne null car on a déjà fait un sendRedirect

            } else {
                // Sécurité : Ne jamais préciser si c'est l'identifiant OU le mot de passe qui est faux
                log.warn("Échec de connexion pour l'identifiant : {}", identifiant);
                request.setAttribute("erreurMessage", "Identifiant ou mot de passe incorrect.");
                return VUE_LOGIN;
            }

        } catch (DaoException e) {
            log.error("Erreur technique lors de la connexion", e);
            request.setAttribute("erreurMessage", "Une erreur technique est survenue.");
            return VUE_LOGIN;
        }
    }
}