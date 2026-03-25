package com.julio.model;

import static org.junit.jupiter.api.Assertions.*;

import com.julio.util.DateUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Suite de tests unitaires validant les contraintes du Modèle (ECF).
 */
class ValidationMetierTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  // --- Utilitaires de création (GIVEN) ---
  private Adresse fabriquerAdresseValide() {
    return Adresse.builder()
        .numeroRue("10")
        .nomRue("Rue de la Paix")
        .codePostal("75000")
        .ville("Paris")
        .build();
  }

  private Client fabriquerClientValide() {
    return Client.builder()
        .raisonSociale("ACME Corp")
        .adresse(fabriquerAdresseValide())
        .telephone("0123456789")
        .email("contact@acme.fr")
        .chiffreAffaires(5000L)
        .nbEmployes(10)
        .build();
  }

  private Prospect fabriquerProspectValide() {
    return Prospect.builder()
        .raisonSociale("Stark Industries")
        .adresse(fabriquerAdresseValide())
        .telephone("0612345678")
        .email("tony@stark.com")
        .dateProspection(LocalDate.now())
        .interesse(Interesse.OUI) // Assumant que Interesse est un Enum
        .build();
  }

  @Nested
  @DisplayName("Tests des règles de l'Adresse")
  class AdresseTests {

    @Test
    @DisplayName("Adresse valide ne remonte aucune erreur")
    void adresseValide_Ok() {
      Adresse adresse = fabriquerAdresseValide();
      Set<ConstraintViolation<Adresse>> violations = validator.validate(adresse);
      assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Code postal doit comporter exactement 5 chiffres (Regex)")
    void codePostal_Invalide_Erreur() {
      Adresse adresse = fabriquerAdresseValide();
      adresse.setCodePostal("7500A"); // Contient une lettre
      Set<ConstraintViolation<Adresse>> violations = validator.validate(adresse);

      assertFalse(violations.isEmpty());
      assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("codePostal")));

      adresse.setCodePostal("7500"); // 4 chiffres
      assertFalse(validator.validate(adresse).isEmpty());
    }
  }

  @Nested
  @DisplayName("Tests des règles communes (Société)")
  class SocieteTests {

    @Test
    @DisplayName("Le téléphone doit respecter le format Regex")
    void telephone_Invalide_Erreur() {
      Client client = fabriquerClientValide();
      client.setTelephone("12345"); // Trop court
      assertFalse(validator.validate(client).isEmpty());

      client.setTelephone("0A23456789"); // Contient une lettre
      assertFalse(validator.validate(client).isEmpty());
    }

    @Test
    @DisplayName("L'email doit respecter le format Regex")
    void email_Invalide_Erreur() {
      Client client = fabriquerClientValide();
      client.setEmail("contact@domaine"); // Pas de TLD (.com, .fr)
      assertFalse(validator.validate(client).isEmpty());
    }

    @Test
    @DisplayName("Les commentaires ne sont pas obligatoires")
    void commentaires_Vides_Ok() {
      Client client = fabriquerClientValide();
      client.setCommentaires(null); // Optionnel
      assertTrue(validator.validate(client).isEmpty());
    }
  }

  @Nested
  @DisplayName("Tests spécifiques au Client")
  class ClientTests {

    @Test
    @DisplayName("Le chiffre d'affaires doit être > 200")
    void chiffreAffaires_InferieurA200_Erreur() {
      Client client = fabriquerClientValide();

      client.setChiffreAffaires(200L); // Strictement supérieur à 200 requis
      assertFalse(validator.validate(client).isEmpty());

      client.setChiffreAffaires(201L); // OK
      assertTrue(validator.validate(client).isEmpty());
    }

    @Test
    @DisplayName("Le nombre d'employés doit être strictement > 0")
    void nbEmployes_Zero_Erreur() {
      Client client = fabriquerClientValide();
      client.setNbEmployes(0);
      assertFalse(validator.validate(client).isEmpty());
    }
  }

  @Nested
  @DisplayName("Tests spécifiques au Prospect")
  class ProspectTests {

    @Test
    @DisplayName("La date de prospection ne peut pas être dans le futur")
    void dateProspection_DansLeFutur_Erreur() {
      Prospect prospect = fabriquerProspectValide();
      prospect.setDateProspection(LocalDate.now().plusDays(1)); // Demain

      Set<ConstraintViolation<Prospect>> violations = validator.validate(prospect);
      assertFalse(violations.isEmpty());
      assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dateProspection")));
    }

    @Test
    @DisplayName("La date peut être formatée et parsée selon le pattern dd/MM/yyyy")
    void dateProspection_Formatage_Ok() {
      // ECF : Vérification du DateTimeFormatter
      String dateSaisie = "25/12/2026";

      // On vérifie que le parser ne lance pas d'erreur
      LocalDate dateParse = DateUtils.parseDate(dateSaisie);
      assertNotNull(dateParse);
      assertEquals(2026, dateParse.getYear());
      assertEquals(12, dateParse.getMonthValue());
      assertEquals(25, dateParse.getDayOfMonth());

      // Test du formatage inverse
      String dateFormatee = dateParse.format(DateUtils.FORMATTER);
      assertEquals("25/12/2026", dateFormatee);
    }

    @Test
    @DisplayName("Une erreur de format lève une DateTimeParseException")
    void dateProspection_MauvaisFormat_Erreur() {
      String dateMalSaisie = "2026-12-25"; // Format ISO au lieu de FR

      assertThrows(DateTimeParseException.class, () -> DateUtils.parseDate(dateMalSaisie));
    }
  }
}