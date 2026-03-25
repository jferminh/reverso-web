package com.julio.controller.client;

import com.julio.dao.ClientDao;
import com.julio.dao.ProspectDao;
import com.julio.exception.BusinessException;
import com.julio.exception.DuplicateResourceException;
import com.julio.exception.InvalidParameterException;
import com.julio.model.Client;
import com.julio.service.UnicityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (POST) pour enregistrer (Création/Modification) un client.
 *
 * <p>Hérite de AbstractClientCommand pour la récupération des données (DRY).
 * Laisse remonter les exceptions techniques (DaoException) vers le ExceptionService.
 * </p>
 *
 * @author Julio
 * @version 2.0
 */
@Slf4j
public class SaveClientCommand extends AbstractClientCommand {

  private final ClientDao clientDao;
  private final UnicityService unicityService;

  public SaveClientCommand() {
    try {
      this.clientDao = new ClientDao();
      ProspectDao prospectDao = new ProspectDao();
      this.unicityService = new UnicityService(clientDao, prospectDao);
    } catch (Exception e) {
      log.error("Erreur d'initialisation des DAO dans SaveClientCommand", e);
      throw new RuntimeException("Erreur d'initialisation de SaveClientCommand", e);
    }
  }

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. Sécurité (Verbe HTTP)
    if (!"POST".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return null;
    }

    log.info("Traitement de la sauvegarde d'un client...");
    Client client = null;

    try {
      // 2. Construction de l'objet (Peut lever InvalidParameterException)
      client = construireClient(request);

      // 3. Validation Métier (Unicité)
      boolean isDoublon = unicityService.isRaisonSocialeDupliqueePourClient(
          client.getRaisonSociale(), client.getId());
      if (isDoublon) {
        log.warn("Doublon détecté pour la raison sociale : {}", client.getRaisonSociale());
        // Utilisation de la belle exception que tu as créée !
        throw new DuplicateResourceException("Raison sociale", client.getRaisonSociale());
      }

      // 4. Validation des contraintes (Bean Validation)
      Validator validator = (Validator) request.getServletContext().getAttribute("validator");
      Set<ConstraintViolation<Client>> violations = validator.validate(client);

      if (!violations.isEmpty()) {
        String messageErreur = violations.iterator().next().getMessage();
        log.warn("Échec Bean Validation pour le client {}: {}",
            client.getRaisonSociale(), messageErreur);
        throw new InvalidParameterException(messageErreur); // Classe abstraite parente !
      }

      // 5. Sauvegarde en Base de Données
      clientDao.save(client); // Si ça plante, DaoException remonte toute seule !

      // 6. Succès et Redirection (PRG)
      HttpSession session = request.getSession();
      session.setAttribute("successMessage", "Le client "
          + client.getRaisonSociale() + " a été enregistré avec succès.");

      log.info("Client {} enregistré avec succès (ID: {})",
          client.getRaisonSociale(), client.getId());

      response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
      return null;

    } catch (BusinessException e) {
      // On attrape SEULEMENT les erreurs liées à la saisie de l'utilisateur (BusinessException)
      // On recharge le formulaire pour lui laisser une chance de corriger.

      if (client == null) {
        // Si la construction a échoué dès la ligne 58,
        // on crée un objet vide pour ne pas crasher la JSP
        client = new Client();
        client.setAdresse(new com.julio.model.Adresse());
      }
      return rechargerFormulaire(request, client, e.getMessage());
    }
  }

  /**
   * Méthode utilitaire pour renvoyer l'utilisateur au formulaire avec ses données.
   */
  private String rechargerFormulaire(
      HttpServletRequest request, Client client, String messageErreur) {
    request.setAttribute("client", client);
    request.setAttribute("erreurMessage", messageErreur);

    boolean isEditMode = (client.getId() != null && client.getId() > 0);
    request.setAttribute("modeEdit", isEditMode);
    request.setAttribute("pageTitle", (isEditMode ?
        "Modifier" : "Nouveau") + " Client - Reverso CRM");

    return VUE_FORM;
  }
}