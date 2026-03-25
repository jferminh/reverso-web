package com.julio.controller.prospect;

import com.julio.controller.Icommand;
import com.julio.exception.InvalidParameterException;
import com.julio.model.Adresse;
import com.julio.model.Interesse;
import com.julio.model.Prospect;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe parente (abstraite) pour les commandes liées à l'entité {@link Prospect}.
 *
 * <p>Applique le principe DRY (Don't Repeat Yourself) en centralisant la logique
 * de récupération, de nettoyage et de conversion des paramètres HTTP.
 * </p>
 *
 * @author Julio
 * @version 2.0
 */
@Slf4j
public abstract class AbstractProspectCommand implements Icommand {

  protected static final String VUE_FORM = "/WEB-INF/views/prospect/form-prospect.jsp";

  /**
   * Extrait les données de la requête HTTP et construit une instance de Prospect hydratée.
   *
   * @param request La requête HTTP contenant les données soumises par l'utilisateur.
   * @return Une instance de {@link Prospect} contenant les données saisies et
   * son objet {@link Adresse} imbriqué.
   * @throws InvalidParameterException Si les données complexes (Date, Enumération)
   * sont corrompues.
   */
  protected Prospect construireProspect(HttpServletRequest request)
      throws InvalidParameterException {

    // 1. Récupération des clés primaires
    String idStr = request.getParameter("id");
    String idAdresseStr = request.getParameter("idAdresse");

    Integer idProspect = (idStr != null && !idStr.isBlank()) ?
        Integer.parseInt(idStr) : null;
    Integer idAdresse = (idAdresseStr != null && !idAdresseStr.isBlank()) ?
        Integer.parseInt(idAdresseStr) : null;

    // 2. Construction de l'Adresse
    Adresse adresse = Adresse.builder()
        .numeroRue(request.getParameter("numeroRue"))
        .nomRue(request.getParameter("nomRue"))
        .codePostal(request.getParameter("codePostal"))
        .ville(request.getParameter("ville"))
        .build();
    adresse.setId(idAdresse);

    // 3. Construction de base du Prospect
    Prospect prospect = Prospect.builder()
        .raisonSociale(request.getParameter("raisonSociale"))
        .adresse(adresse)
        .telephone(request.getParameter("telephone"))
        .email(request.getParameter("email"))
        .commentaires(request.getParameter("commentaires"))
        .build();
    prospect.setId(idProspect);

    // 4. Conversions spécifiques avec la bonne exception concrète
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