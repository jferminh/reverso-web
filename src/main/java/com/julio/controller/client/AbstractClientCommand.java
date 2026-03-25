package com.julio.controller.client;

import com.julio.controller.common.AbstractSocieteCommand;
import com.julio.exception.InvalidParameterException;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe parente pour les commandes liées aux Clients.
 * Hérite de AbstractSocieteCommand pour réutiliser la logique d'extraction commune.
 *
 * @author Julio
 * @version 3.0
 */
@Slf4j
public abstract class AbstractClientCommand extends AbstractSocieteCommand {

  protected static final String VUE_FORM   = "/WEB-INF/views/client/form-client.jsp";

  /**
   * Construit un Client depuis les paramètres POST.
   *
   * @param request La requête HTTP contenant les données du formulaire
   * @return Un objet Client hydraté complet
   * @throws InvalidParameterException Si les paramètres numériques sont invalides
   */
  protected Client construireClient(HttpServletRequest request) throws InvalidParameterException {

    // 1. Instanciation d'un client vide
    Client client = new Client();

    // 2. MAGIE DU DRY : On laisse la classe mère remplir les 80% des champs communs !
    hydraterSociete(request, client);

    // 3. Traitement exclusif aux Clients (Sécurisation du parsing numérique)
    String caStr = request.getParameter("chiffreAffaires");
    String nbStr = request.getParameter("nbEmployes");

    try {
      long ca = (caStr != null && !caStr.isBlank())
          ? Long.parseLong(caStr.replaceAll("[^0-9]", "")) : 0L;
      int nb = (nbStr != null && !nbStr.isBlank())
          ? Integer.parseInt(nbStr.replaceAll("[^0-9]", "")) : 0;

      client.setChiffreAffaires(ca);
      client.setNbEmployes(nb);
    } catch (NumberFormatException e) {
      log.warn("Données numériques invalides reçues. CA: {}, Employés: {}", caStr, nbStr);
      throw new InvalidParameterException(
          "Les valeurs du chiffre d'affaires ou du nombre d'employés sont invalides.");
    }

    return client;
  }
}