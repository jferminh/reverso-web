package com.julio.controller.prospect;

import com.julio.dao.ClientDao;
import com.julio.dao.ProspectDao;
import com.julio.exception.BusinessException;
import com.julio.exception.DuplicateResourceException;
import com.julio.exception.InvalidParameterException;
import com.julio.model.Prospect;
import com.julio.service.UnicityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (POST) pour enregistrer (Création ou Modification) un prospect.
 *
 * <p>Hérite de {@link AbstractProspectCommand} pour le traitement des paramètres (DRY).
 * Laisse délibérément remonter les exceptions techniques vers le contrôleur frontal.
 * Utilise les exceptions métier concrètes pour réafficher le formulaire en cas d'erreur de saisie.
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
@Slf4j
public class SaveProspectCommand extends AbstractProspectCommand {

  private final ProspectDao prospectDao;
  private final UnicityService unicityService;

  /**
   * Constructeur par défaut.
   * Initialise les accès aux données et le service de vérification d'unicité.
   */
  public SaveProspectCommand() {
    try {
      this.prospectDao = new ProspectDao();
      ClientDao clientDao = new ClientDao();
      this.unicityService = new UnicityService(clientDao, prospectDao);
    } catch (Exception e) {
      log.error("Erreur d'initialisation des dépendances dans SaveProspectCommand", e);
      throw new RuntimeException("Échec de l'initialisation de la commande Prospect", e);
    }
  }

  /**
   * Exécute le flux complet de sauvegarde d'un prospect (Validation, Règle Métier, Persistance).
   *
   * @param request  La requête HTTP entrante.
   * @param response La réponse HTTP sortante.
   * @return Le chemin JSP en cas d'erreur de saisie, ou null en cas de succès (Redirection).
   * @throws Exception Si une erreur d'accès aux données survient (non attrapée ici).
   */
  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. Sécurité : Vérification du verbe HTTP
    if (!"POST".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return null;
    }

    log.info("Début du traitement de sauvegarde d'un prospect...");
    Prospect prospect = null;

    try {
      // 2. Data-Binding : Construction de l'objet (Peut lever InvalidParameterException)
      prospect = construireProspect(request);

      // 3. Validation Métier : Unicité de la Raison Sociale
      boolean isDoublon = unicityService.isRaisonSocialeDupliqueePourProspect(
          prospect.getRaisonSociale(),
          prospect.getId()
      );

      if (isDoublon) {
        log.warn("Rejet de la sauvegarde : La raison sociale '{}' est déjà utilisée.",
            prospect.getRaisonSociale());
        throw new DuplicateResourceException("Raison sociale", prospect.getRaisonSociale());
      }

      // 4. Bean Validation : Vérification des contraintes du Modèle (@NotNull, etc.)
      Validator validator = (Validator) request.getServletContext().getAttribute("validator");
      Set<ConstraintViolation<Prospect>> violations = validator.validate(prospect);

      if (!violations.isEmpty()) {
        String messageErreur = violations.iterator().next().getMessage();
        log.warn("Échec de la validation du prospect '{}' : {}",
            prospect.getRaisonSociale(), messageErreur);
        throw new InvalidParameterException(messageErreur);
      }

      // 5. Base de Données : Insertion ou Mise à jour
      prospectDao.save(prospect); // Si ça plante, DaoException remonte toute seule !

      // 6. Succès et Redirection (Pattern PRG : Post-Redirect-Get)
      HttpSession session = request.getSession();
      session.setAttribute("successMessage", "Le prospect "
          + prospect.getRaisonSociale() + " a été enregistré avec succès.");

      log.info("Prospect '{}' sauvegardé avec succès (ID: {})",
          prospect.getRaisonSociale(), prospect.getId());

      response.sendRedirect(request.getContextPath() + "/app?cmd=listProspects");
      return null;

    } catch (BusinessException e) {
      // Interception polymorphique : Attrape DuplicateResourceException
      // ET InvalidParameterException. L'utilisateur a fait une erreur,
      // on recharge la vue avec ses données.
      log.debug("Interruption du flux métier (BusinessException) : {}", e.getMessage());

      if (prospect == null) {
        // Sécurité anti-NullPointerException si construireProspect()
        prospect = new Prospect();
        prospect.setAdresse(new com.julio.model.Adresse());
      }
      return rechargerFormulaire(request, prospect, e.getMessage());
    }
  }

  /**
   * Recharge la vue du formulaire avec les données saisies par l'utilisateur
   * et le message d'erreur.
   *
   * @param request       La requête HTTP courante.
   * @param prospect      Le prospect partiellement hydraté.
   * @param messageErreur L'explication de l'erreur métier ou de validation.
   * @return Le chemin vers la JSP du formulaire.
   */
  private String rechargerFormulaire(
      HttpServletRequest request, Prospect prospect, String messageErreur) {
    request.setAttribute("prospect", prospect);
    request.setAttribute("erreurMessage", messageErreur);

    boolean isEditMode = (prospect.getId() != null && prospect.getId() > 0);
    request.setAttribute("modeEdit", isEditMode);
    request.setAttribute("pageTitle", (isEditMode
        ? "Modifier" : "Nouveau") + " Prospect - Reverso CRM");

    return VUE_FORM; // Variable statique héritée de AbstractProspectCommand
  }
}