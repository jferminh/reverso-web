package com.julio.controller.client;

import com.julio.controller.Icommand;
import com.julio.model.Adresse;
import com.julio.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe abstract client.
 */
@Slf4j
public abstract class AbstractClientCommand implements Icommand {
  protected static final String VUE_FORM    = "WEB-INF/views/client/form-client.jsp";
  protected static final String VUE_ERREUR  = "WEB-INF/views/common/erreur.jsp";

  /**
   * Construit un Client depuis les paramètres POST.
   * @param request request
   * @return objet client
   */
  protected Client construireClient(HttpServletRequest request) {
    String caStr = request.getParameter("chiffreAffaires");
    String nbStr = request.getParameter("nbEmployes");

    long ca = (caStr != null && !caStr.isBlank())
        ? Long.parseLong(caStr.replaceAll("[^0-9]", "")) : 0L;
    int  nb = (nbStr != null && !nbStr.isBlank())
        ? Integer.parseInt(nbStr.replaceAll("[^0-9]", "")) : 0;

    Adresse adresse = Adresse.builder()
        .numeroRue(request.getParameter("numeroRue"))
        .nomRue(request.getParameter("nomRue"))
        .codePostal(request.getParameter("codePostal"))
        .ville(request.getParameter("ville"))
        .build();

    return Client.builder()
        .raisonSociale(request.getParameter("raisonSociale"))
        .adresse(adresse)
        .telephone(request.getParameter("telephone"))
        .email(request.getParameter("email"))
        .commentaires(request.getParameter("commentaires"))
        .build();
  }

}
