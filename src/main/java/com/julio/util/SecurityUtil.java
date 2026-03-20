package com.julio.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilitaire de sécurité pour la gestion des mots de passe et des tokens.
 * Implémente le hachage avec Sel (Salt) et Poivre (Pepper).
 *
 * @author Julio
 * @version 1.0
 */
@Slf4j
public class SecurityUtil {

  // ✅ LE POIVRE (Pepper) : Secret global du serveur.
  // Le poivre ne doit JAMAIS être stocké dans la base de données.
  // En production, on le lirait depuis un fichier de configuration ou variable d'environnement.
  private static final String PEPPER = "A_S3cr3t_P3pp3r_Afpa_2026_!#";

  /**
   * Génère un Sel (Salt) aléatoire unique pour un nouvel utilisateur.
   * Ce sel doit être sauvegardé dans la base de données avec l'utilisateur.
   *
   * @return le sel encodé en Base64
   */
  public static String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16]; // 16 octets est la norme recommandée
    random.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  /**
   * Hache un mot de passe en appliquant la formule : HASH(Poivre + MotDePasse + Sel).
   *
   * @param rawPassword le mot de passe en clair saisi par l'utilisateur
   * @param salt le sel unique de l'utilisateur (récupéré en BDD)
   * @return le hash final encodé en Base64
   */
  public static String hashPassword(String rawPassword, String salt) {
    if (rawPassword == null || salt == null) {
      throw new IllegalArgumentException("Le mot de passe et le sel ne peuvent pas être null");
    }

    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");

      // Concaténation des 3 éléments de sécurité
      String combined = PEPPER + rawPassword + salt;

      byte[] hashedBytes = md.digest(combined.getBytes());
      return Base64.getEncoder().encodeToString(hashedBytes);

    } catch (NoSuchAlgorithmException e) {
      log.error("Erreur critique : algorithme SHA-256 introuvable", e);
      throw new RuntimeException("Erreur de configuration cryptographique", e);
    }
  }

  /**
   * Vérifie si le mot de passe saisi correspond au hash stocké en base.
   *
   * @param rawPassword le mot de passe saisi lors du login
   * @param dbSalt le sel de l'utilisateur stocké en BDD
   * @param dbHash le hash du mot de passe stocké en BDD
   * @return true si le mot de passe est correct, false sinon
   */
  public static boolean verifyPassword(String rawPassword, String dbSalt, String dbHash) {
    if (rawPassword == null || dbSalt == null || dbHash == null) {
      return false;
    }
    // On recalcule le hash avec le mot de passe saisi, et on compare
    String newHash = hashPassword(rawPassword, dbSalt);
    return newHash.equals(dbHash);
  }

  /**
   * Génère un token aléatoire pour la protection CSRF.
   *
   * @return un token CSRF encodé en Base64
   */
  public static String generateCsrfToken() {
    SecureRandom random = new SecureRandom();
    byte[] token = new byte[32]; // 32 octets pour une haute sécurité
    random.nextBytes(token);
    return Base64.getEncoder().encodeToString(token);
  }

  // =========================================================================
  // ⚠️ UTILITAIRE TEMPORAIRE POUR GÉNÉRER LE PREMIER UTILISATEUR (ADMIN)
  // À exécuter une seule fois via ton IDE (Run as Java Application)
  // =========================================================================
  static void main(String[] args) {
    String username = "admin";
    String rawPassword = "password123"; // Le mot de passe en clair que tu taperas sur le site

    // 1. Générer un nouveau sel
    String sel = generateSalt();

    // 2. Calculer le hash
    String hash = hashPassword(rawPassword, sel);

    System.out.println("=== COPIE ET COLLE CE SCRIPT SQL DANS MYSQL ===");
    System.out.println("INSERT INTO utilisateur (identifiant, mot_de_passe, sel) VALUES ");
    System.out.println("('" + username + "', '" + hash + "', '" + sel + "');");
    System.out.println("================================================");
  }
}