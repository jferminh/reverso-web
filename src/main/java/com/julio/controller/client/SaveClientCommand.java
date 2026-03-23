package com.julio.controller.client;

import com.julio.dao.ClientDao;
import com.julio.dao.ProspectDao;
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
 * Hérite de AbstractClientCommand pour la récupération des données (DRY).
 * Applique la validation Bean Validation et la vérification des doublons.
 */
@Slf4j
public class SaveClientCommand extends AbstractClientCommand {

  private final ClientDao clientDao;
  private final ProspectDao prospectDao;
  private final UnicityService unicityService;

  /**
   * Constructeur.
   */
  public SaveClientCommand() {
    try {
      this.clientDao = new ClientDao();
      this.prospectDao = new ProspectDao();
      this.unicityService = new UnicityService(clientDao, prospectDao);
    } catch (Exception e) {
      log.error("Erreur d'initialisation des DAO dans SaveClientCommand", e);
      throw new RuntimeException("Erreur d'initialisation de SaveClientCommand", e);
    }
  }

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

    // 1. Sécurité : On s'assure que la requête est bien de type POST
    if (!"POST".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return null;
    }

    log.info("Traitement de la sauvegarde d'un client...");

    // 2. 🪄 MAGIE DU DRY : On utilise la méthode de la classe parente !
    // Elle lit tous les champs, nettoie les nombres (regex) et construit l'objet complet.
    Client client = construireClient(request);

    // ==========================================
    // 3. VALIDATION MÉTIER (Unicité)
    // ==========================================
    try {
      // Vérifie si la raison sociale existe déjà (en excluant l'ID actuel en cas de modification)
      boolean isDoublon = unicityService.isRaisonSocialeDupliqueePourClient(
          client.getRaisonSociale(),
          client.getId()
      );

      if (isDoublon) {
        log.warn("Doublon détecté pour la raison sociale : {}", client.getRaisonSociale());
        return rechargerFormulaire(request, client,
            "Cette raison sociale existe déjà dans la base de données.");
      }
    } catch (Exception e) {
      log.error("Erreur lors de la vérification de l'unicité", e);
      return rechargerFormulaire(request, client,
          "Erreur technique lors de la vérification des données.");
    }

    // ==========================================
    // 4. JAKARTA BEAN VALIDATION (@NotNull, @Min, etc.)
    // ==========================================
    Validator validator = (Validator) request.getServletContext().getAttribute("validator");
    Set<ConstraintViolation<Client>> violations = validator.validate(client);

    if (!violations.isEmpty()) {
      // S'il y a des erreurs, on prend le premier message pour l'afficher à l'utilisateur
      String messageErreur = violations.iterator().next().getMessage();
      log.warn("Échec Bean Validation pour le client {}: {}",
          client.getRaisonSociale(), messageErreur);
      return rechargerFormulaire(request, client, messageErreur);
    }

    // ==========================================
    // 5. SAUVEGARDE EN BASE DE DONNÉES
    // ==========================================
    try {
      clientDao.save(client);

      // On place un message de succès dans la Session (pour qu'il survive à la redirection PRG).
      HttpSession session = request.getSession();
      session.setAttribute("successMessage", "Le client "
          + client.getRaisonSociale() + " a été enregistré avec succès.");

      log.info("Client {} enregistré avec succès (ID: {})",
          client.getRaisonSociale(), client.getId());

      // 6. REDIRECTION (Pattern PRG : Post-Redirect-Get)
      // Redirige vers la liste des clients pour éviter la double soumission (F5)
      response.sendRedirect(request.getContextPath() + "/app?cmd=listClients");
      return null; // Important : retourner null après un sendRedirect

    } catch (Exception e) {
      log.error("Erreur critique lors de la sauvegarde du client en BDD", e);
      return rechargerFormulaire(request, client,
          "Une erreur technique a empêché l'enregistrement en base de données.");
    }
  }

  /**
   * Méthode utilitaire pour renvoyer l'utilisateur au formulaire
   * avec ses données pré-remplies en cas d'erreur.
   */
  private String rechargerFormulaire(
      HttpServletRequest request, Client client, String messageErreur) {
    // On replace l'objet client dans la requête pour le "Data-Binding"
    // du JSP (conserve la saisie).
    request.setAttribute("client", client);
    request.setAttribute("erreurMessage", messageErreur);

    // Si l'ID est présent, c'est une modification, sinon c'est une création
    boolean isEditMode = (client.getId() != null && client.getId() > 0);
    request.setAttribute("modeEdit", isEditMode);
    request.setAttribute("pageTitle", (isEditMode
        ? "Modifier" : "Nouveau") + " Client - Reverso CRM");

    // On utilise la constante VUE_FORM définie dans AbstractClientCommand
    return VUE_FORM;
  }
}