package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.model.Adresse;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe parente pour les commandes liées aux Clients.
 * Centralise la récupération et le nettoyage des données du formulaire (Principe DRY).
 */
@Slf4j
public abstract class AbstractClientCommand implements Icommand {

  protected static final String VUE_FORM   = "/WEB-INF/views/client/form-client.jsp";
  protected static final String VUE_ERREUR = "/WEB-INF/views/common/erreur.jsp";

  /**
   * Construit un Client depuis les paramètres POST.
   *
   * @param request La requête HTTP contenant les données du formulaire
   * @return Un objet Client hydraté (avec son Adresse)
   */
  protected Client construireClient(HttpServletRequest request) {

    // 1. Récupération des IDs (Très important pour la modification)
    String idStr = request.getParameter("id");
    String idAdresseStr = request.getParameter("idAdresse");

    Integer idClient = (idStr != null && !idStr.isBlank()) ? Integer.parseInt(idStr) : null;
    Integer idAdresse = (idAdresseStr != null && !idAdresseStr.isBlank())
        ? Integer.parseInt(idAdresseStr) : null;

    // 2. Construction de l'Adresse
    Adresse adresse = Adresse.builder()
        .numeroRue(request.getParameter("numeroRue"))
        .nomRue(request.getParameter("nomRue"))
        .codePostal(request.getParameter("codePostal"))
        .ville(request.getParameter("ville"))
        .build();
    adresse.setId(idAdresse); // Injection de l'ID

    // 3. Construction et retour du Client
    Client client = Client.builder()
        .raisonSociale(request.getParameter("raisonSociale"))
        .adresse(adresse)
        .telephone(request.getParameter("telephone"))
        .email(request.getParameter("email"))
        .commentaires(request.getParameter("commentaires"))
        .build();

    String caStr = request.getParameter("chiffreAffaires");
    String nbStr = request.getParameter("nbEmployes");

    long ca = (caStr != null && !caStr.isBlank())
        ? Long.parseLong(caStr.replaceAll("[^0-9]", "")) : 0L;
    int nb = (nbStr != null && !nbStr.isBlank())
        ? Integer.parseInt(nbStr.replaceAll("[^0-9]", "")) : 0;

    client.setId(idClient); // Injection de l'ID
    client.setChiffreAffaires(ca);
    client.setNbEmployes(nb);

    return client;
  }
}