package com.julio.exception;

/**
 * Exception technique de base pour toutes les erreurs liées aux opérations DAO (Base de données).
 *
 * <p>Permet d'encapsuler les {@link java.sql.SQLException} et d'y ajouter un contexte métier riche
 * (Code d'erreur précis, type d'opération en échec, identifiant de l'entité concernée).
 * Cette exception est destinée à être interceptée par l'ExceptionService global.
 * </p>
 *
 * @author Julio FERMIN
 * @version 2.1
 * @since 15/01/2026
 */
public class DaoException extends Exception {

  private final ErrorCode errorCode;
  private final String operation;
  private final Object entityId;

  /**
   * Constructeur principal et complet (Master Constructor).
   * Tous les autres constructeurs délèguent à celui-ci (Principe DRY).
   */
  public DaoException(
      ErrorCode errorCode, String operation, Object entityId, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode != null ? errorCode : ErrorCode.GENERAL_ERROR;
    this.operation = operation;
    this.entityId = entityId;
  }

  /**
   * Constructeur complet sans cause (quand l'erreur est purement logique au niveau DAO).
   */
  public DaoException(ErrorCode errorCode, String operation, Object entityId, String message) {
    this(errorCode, operation, entityId, message, null);
  }

  /**
   * Constructeur avec message et cause technique (ex : SQLException).
   */
  public DaoException(String message, Throwable cause) {
    this(ErrorCode.GENERAL_ERROR, null, null, message, cause);
  }

  /**
   * Constructeur simple pour une erreur générique.
   */
  public DaoException(String message) {
    this(ErrorCode.GENERAL_ERROR, null, null, message, null);
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public String getOperation() {
    return operation;
  }

  public Object getEntityId() {
    return entityId;
  }

  @Override
  public String toString() {
    return String.format("DaoException{errorCode=%s, operation='%s', entityId=%s, message='%s'}",
        errorCode, operation, entityId, getMessage());
  }

  /**
   * Énumération des codes d'erreur pour une catégorisation précise des pannes DAO.
   */
  public enum ErrorCode {
    // Erreurs de connexion / Infrastructure
    CONNECTION_ERROR("Impossible d'établir une connexion au serveur de base de données"),

    // Erreurs CRUD
    CREATE_ERROR("Échec de la transaction d'insertion en base de données"),
    READ_ERROR("Échec de la récupération ou du mappage des données"),
    UPDATE_ERROR("Échec de la mise à jour des données (aucune ligne affectée)"),
    DELETE_ERROR("Échec de la suppression de l'enregistrement"),

    // Erreurs d'intégrité (Très utile pour le ExceptionService)
    FOREIGN_KEY_VIOLATION(
        "Action bloquée : cette donnée est liée à une autre entité via une clé étrangère"),
    UNIQUE_CONSTRAINT_VIOLATION(
        "Doublon détecté : une contrainte d'unicité (ex: email, raison sociale) "
            + "n'est pas respectée"),
    NOT_NULL_VIOLATION("Donnée manquante : une colonne obligatoire a reçu une valeur NULL"),
    CHECK_CONSTRAINT_VIOLATION("Valeur rejetée : la donnée ne respecte pas "
        + "les règles de contrôle (CHECK) de la table"),

    // Erreurs métier gérées au niveau DAO
    ENTITY_NOT_FOUND("L'entité demandée n'existe pas ou a été supprimée"),
    INVALID_PARAMETER("Les paramètres fournis au DAO sont invalides (ex: ID null)"),

    // Erreurs générales
    TRANSACTION_ERROR("La transaction SQL a échoué et un Rollback a été forcé"),
    INTERNAL_ERROR("Une erreur interne inattendue s'est produite dans la couche "
        + "d'accès aux données"),
    GENERAL_ERROR("Erreur SQL non catégorisée");

    private final String description;

    ErrorCode(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }
}