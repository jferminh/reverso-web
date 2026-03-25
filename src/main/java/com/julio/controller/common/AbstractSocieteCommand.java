package com.julio.controller.common;

import com.julio.controller.Icommand;
import com.julio.model.Adresse;
import com.julio.model.Societe;
import jakarta.servlet.http.HttpServletRequest;

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
}