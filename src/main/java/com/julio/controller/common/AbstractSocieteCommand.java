package com.julio.controller.common;

import com.julio.controller.Icommand;
import com.julio.exception.InvalidParameterException;
import com.julio.model.Adresse;
import com.julio.model.Societe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Super-classe abstraite pour toutes les commandes gérant des entités de type Société.
 *
 * <p>Centralise l'hydratation des données communes (Adresse, Raison Sociale, Contact)
 * pour éviter la duplication de code entre Clients et Prospects (Principe DRY).
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
public abstract class AbstractSocieteCommand implements Icommand {

  /**
   * Extrait les paramètres HTTP communs et les injecte dans l'entité fournie.
   *
   * @param request La requête HTTP contenant les données du formulaire.
   * @param societe L'instance (Client ou Prospect) à hydrater.
   */
  protected void hydraterSociete(HttpServletRequest request, Societe societe) {

    // 1. Récupération des clés primaires
    String idStr = request.getParameter("id");
    if (idStr != null && !idStr.isBlank()) {
      societe.setId(Integer.parseInt(idStr));
    }

    String idAdresseStr = request.getParameter("idAdresse");
    Integer idAdresse = (idAdresseStr != null && !idAdresseStr.isBlank())
        ? Integer.parseInt(idAdresseStr) : null;

    // 2. Construction de l'Adresse
    Adresse adresse = Adresse.builder()
        .numeroRue(request.getParameter("numeroRue"))
        .nomRue(request.getParameter("nomRue"))
        .codePostal(request.getParameter("codePostal"))
        .ville(request.getParameter("ville"))
        .build();
    adresse.setId(idAdresse);

    // 3. Hydratation des champs communs à toutes les sociétés
    societe.setAdresse(adresse);
    societe.setRaisonSociale(request.getParameter("raisonSociale"));
    societe.setTelephone(request.getParameter("telephone"));
    societe.setEmail(request.getParameter("email"));
    societe.setCommentaires(request.getParameter("commentaires"));
  }

  /**
   * Vérifie que la requête HTTP utilise bien la méthode GET.
   * Envoie automatiquement une erreur 405 (Method Not Allowed) au navigateur
   * si ce n'est pas le cas.
   *
   * @param request La requête HTTP.
   * @param response La réponse HTTP.
   * @return true si la méthode est GET, false sinon.
   * @throws Exception Si l'envoi de l'erreur échoue.
   */
  protected boolean isGetMethodValid(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    if (!"GET".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return false;
    }
    return true;
  }

  /**
   * Vérifie que la requête HTTP utilise bien la méthode POST.
   * Envoie automatiquement une erreur 405 (Method Not Allowed)
   * au navigateur si ce n'est pas le cas.
   *
   * @param request La requête HTTP.
   * @param response La réponse HTTP.
   * @return true si la méthode est POST, false sinon.
   * @throws Exception Si l'envoi de l'erreur échoue.
   */
  protected boolean isPostMethodValid(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    if (!"POST".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return false;
    }
    return true;
  }

  /**
   * Extrait et valide l'identifiant (ID) passé dans l'URL.
   *
   * @param request La requête HTTP.
   * @param nomEntite Le nom de l'entité pour formater le message d'erreur (ex : "client").
   * @return L'identifiant sous forme d'entier (int).
   * @throws InvalidParameterException Si l'ID est manquant ou contient des lettres.
   */
  protected int validerEtExtraireId(HttpServletRequest request, String nomEntite)
      throws InvalidParameterException {
    String idStr = request.getParameter("id");

    if (idStr == null || idStr.isBlank()) {
      throw new InvalidParameterException("L'identifiant du "
          + nomEntite + " est manquant dans l'URL.");
    }

    try {
      return Integer.parseInt(idStr);
    } catch (NumberFormatException e) {
      throw new InvalidParameterException("Le format de l'identifiant du "
          + nomEntite + " est invalide.");
    }
  }
}