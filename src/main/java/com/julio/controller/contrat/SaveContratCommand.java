package com.julio.controller.contrat;

import com.julio.controller.Icommand;
import com.julio.dao.ContratDao;
import com.julio.exception.InvalidParameterException;
import com.julio.model.Contrat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Commande (POST) pour enregistrer (Création ou Mise à jour) un contrat.
 *
 * <p>Implémente le pattern PRG (Post-Redirect-Get). Après l'opération,
 * l'utilisateur est redirigé vers la fiche détaillée du client.
 * </p>
 *
 * @author Julio
 * @version 1.0
 */
@Slf4j
public class SaveContratCommand implements Icommand {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // 1. Sécurité : Accepter uniquement POST
    if (!"POST".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée");
      return null;
    }

    // 2. Extraction du Client ID (Obligatoire pour savoir où rediriger à la fin)
    String clientIdStr = request.getParameter("clientId");
    if (clientIdStr == null || clientIdStr.isBlank()) {
      throw new InvalidParameterException("L'identifiant du client est manquant pour ce contrat.");
    }
    int clientId = Integer.parseInt(clientIdStr);

    // 3. Extraction des autres paramètres
    String idStr = request.getParameter("id");
    Integer id = (idStr != null && !idStr.isBlank()) ? Integer.parseInt(idStr) : null;
    String nomContrat = request.getParameter("nomContrat");
    String montantStr = request.getParameter("montant");

    double montant = 0.0;
    try {
      if (montantStr != null && !montantStr.isBlank()) {
        // Remplacement de la virgule par un point pour éviter les erreurs de parsing
        montant = Double.parseDouble(montantStr.replace(",", "."));
      }
    } catch (NumberFormatException e) {
      log.warn("Tentative de saisie d'un montant invalide : {}", montantStr);
      throw new InvalidParameterException("Le format du montant est invalide.");
    }

    // 4. Construction de l'objet via le Builder Lombok
    Contrat contrat = Contrat.builder()
        .id(id)
        .clientId(clientId)
        .nomContrat(nomContrat)
        .montant(montant)
        .build();

    // 5. Bean Validation (Vérification des règles métier)
    Validator validator = (Validator) request.getServletContext().getAttribute("validator");
    Set<ConstraintViolation<Contrat>> violations = validator.validate(contrat);

    HttpSession session = request.getSession();

    if (!violations.isEmpty()) {
      String messageErreur = violations.iterator().next().getMessage();
      log.warn("Échec validation contrat : {}", messageErreur);
      // On met l'erreur en session et on redirige (PRG)
      session.setAttribute("erreurMessage", messageErreur);
      response.sendRedirect(request.getContextPath() + "/app?cmd=viewClient&id=" + clientId);
      return null;
    }

    // 6. Sauvegarde en Base de Données
    ContratDao contratDao = new ContratDao();
    contratDao.save(contrat);

    // 7. Succès et Redirection
    session.setAttribute("successMessage", "Le contrat a été enregistré avec succès.");
    log.info("Contrat sauvegardé avec succès pour le client ID={}", clientId);

    response.sendRedirect(request.getContextPath() + "/app?cmd=viewClient&id=" + clientId);
    return null;
  }
}