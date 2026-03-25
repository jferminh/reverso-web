package com.julio.controller.prospect;

import com.julio.controller.common.AbstractSocieteCommand;
import com.julio.exception.InvalidParameterException;
import com.julio.model.Adresse;
import com.julio.model.Interesse;
import com.julio.model.Prospect;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe parente (abstraite) pour les commandes liées à l'entité Prospect.
 * Hérite de AbstractSocieteCommand pour l'extraction des données communes.
 *
 * @author Julio
 * @version 3.0
 */
@Slf4j
public abstract class AbstractProspectCommand extends AbstractSocieteCommand {

  protected static final String VUE_FORM = "/WEB-INF/views/prospect/form-prospect.jsp";

  /**
   * Extrait les données de la requête HTTP et construit une instance de Prospect hydratée.
   *
   * @param request La requête HTTP contenant les données soumises par l'utilisateur.
   * @return Une instance de {@link Prospect}son objet {@link Adresse} imbriqué.
   * @throws InvalidParameterException Si (Date, Enumération) sont corrompues.
   */
  protected Prospect construireProspect(HttpServletRequest request)
      throws InvalidParameterException {

    // 1. Instanciation
    Prospect prospect = new Prospect();

    // 2. Remplissage des champs communs (Societe + Adresse)
    hydraterSociete(request, prospect);

    // 3. Conversions spécifiques au Prospect
    String dateStr = request.getParameter("dateProspection");
    if (dateStr != null && !dateStr.isBlank()) {
      try {
        prospect.setDateProspection(LocalDate.parse(dateStr));
      } catch (DateTimeParseException e) {
        log.warn("Date de prospection invalide fournie : {}", dateStr);
        throw new InvalidParameterException("Le format de la date de prospection est invalide.");
      }
    }

    String interesseStr = request.getParameter("interesse");
    if (interesseStr != null && !interesseStr.isBlank()) {
      try {
        prospect.setInteresse(Interesse.valueOf(interesseStr));
      } catch (IllegalArgumentException e) {
        log.warn("Niveau d'intérêt invalide fourni : {}", interesseStr);
        throw new InvalidParameterException("Le niveau d'intérêt sélectionné est invalide.");
      }
    }

    return prospect;
  }
}