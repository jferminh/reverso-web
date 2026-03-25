package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.exception.InvalidParameterException;
import com.julio.model.Adresse;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe parente pour les commandes liées aux Clients.
 * Centralise la récupération et le nettoyage des données du formulaire (Principe DRY).
 *
 * @author Julio
 * @version 2.0
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
   * @throws InvalidParameterException Si les paramètres numériques sont invalides
   */
  protected Client construireClient(HttpServletRequest request) throws InvalidParameterException {

    String idStr = request.getParameter("id");
    String idAdresseStr = request.getParameter("idAdresse");

    Integer idClient = (idStr != null && !idStr.isBlank()) ? Integer.parseInt(idStr) : null;
    Integer idAdresse = (idAdresseStr != null && !idAdresseStr.isBlank()) ? Integer.parseInt(idAdresseStr) : null;

    Adresse adresse = Adresse.builder()
        .numeroRue(request.getParameter("numeroRue"))
        .nomRue(request.getParameter("nomRue"))
        .codePostal(request.getParameter("codePostal"))
        .ville(request.getParameter("ville"))
        .build();
    adresse.setId(idAdresse);

    Client client = Client.builder()
        .raisonSociale(request.getParameter("raisonSociale"))
        .adresse(adresse)
        .telephone(request.getParameter("telephone"))
        .email(request.getParameter("email"))
        .commentaires(request.getParameter("commentaires"))
        .build();
    client.setId(idClient);

    // Sécurisation du parsing numérique
    String caStr = request.getParameter("chiffreAffaires");
    String nbStr = request.getParameter("nbEmployes");

    try {
      long ca = (caStr != null && !caStr.isBlank()) ? Long.parseLong(caStr.replaceAll("[^0-9]", "")) : 0L;
      int nb = (nbStr != null && !nbStr.isBlank()) ? Integer.parseInt(nbStr.replaceAll("[^0-9]", "")) : 0;

      client.setChiffreAffaires(ca);
      client.setNbEmployes(nb);
    } catch (NumberFormatException e) {
      log.warn("Données numériques invalides reçues. CA: {}, Employés: {}", caStr, nbStr);
      throw new InvalidParameterException("Les valeurs du chiffre d'affaires ou du nombre d'employés sont invalides.");
    }

    return client;
  }
}