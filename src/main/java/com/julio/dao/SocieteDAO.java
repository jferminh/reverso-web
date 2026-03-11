package com.julio.dao;

import com.julio.exception.DAOException;
import com.julio.model.Adresse;
import com.julio.model.Societe;
import com.julio.service.LoggerService;
import com.julio.util.SQLExceptionAnalyzer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.julio.util.JdbcUtil.closeResources;

public abstract class SocieteDAO {

  private static final Logger LOGGER = LoggerService.getLogger(SocieteDAO.class);
  protected final DatabaseConnexion dbConnexion;
  protected final AdresseDao adresseDAO;

  public SocieteDAO() throws DAOException {
    try {
      this.dbConnexion = DatabaseConnexion.getInstance();
      this.adresseDAO = new AdresseDao();

    } catch (SQLException ex) {
      LOGGER.log(Level.SEVERE, "Échec de l'initialisation de SocieteDAO", ex);
      throw new DAOException(
              DAOException.ErrorCode.CONNECTION_ERROR,
              "init",
              null,
              "Impossible d'initialiser SocieteDAO : " + ex.getMessage()
      );
    }
  }

  protected Integer createSociete(Societe societe) throws DAOException {
    if (societe == null) {
      throw new DAOException(
              DAOException.ErrorCode.INVALID_PARAMETER,
              "createSociete",
              null,
              "La société ne peut pas être null"
      );
    }

    // ========== ÉTAPE 1 : Valider et créer/récupérer l'adresse ==========
    Adresse adresse = societe.getAdresse();
    if (adresse == null) {
      throw new DAOException(
              DAOException.ErrorCode.INVALID_PARAMETER,
              "createSociete",
              null,
              "L'adresse de la société ne peut pas être null"
      );
    }

    // Créer l'adresse si elle n'existe pas encore
    if (adresse.getId() == null) {
      try {
        // ⚠️ ATTENTION : adresseDAO.create() doit AUSSI ne pas gérer de transaction
        adresse = adresseDAO.create(adresse);

      } catch (DAOException ex) {
        LOGGER.log(Level.SEVERE, "Erreur lors de la création de l'adresse", ex);
        throw new DAOException(
                DAOException.ErrorCode.CREATE_ERROR,
                "createSociete",
                null,
                "Erreur lors de la création de l'adresse : " + ex.getMessage(),
                ex
        );
      }
    }

    // ========== ÉTAPE 2 : Insérer la société ==========
    String sql = "INSERT INTO societe (raison_sociale, adresse_id, telephone, email, commentaires) " +
            "VALUES (?, ?, ?, ?, ?)";

    PreparedStatement pstmt = null;
    ResultSet generatedKeys = null;

    try {
      // ✅ CORRECTION : Récupérer la connexion sans try-with-resources
      // La connexion est en mode transaction (autoCommit=false) depuis create()
      Connection connection = dbConnexion.getConnection();

      pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
      pstmt.setString(1, societe.getRaisonSociale());
      pstmt.setInt(2, adresse.getId());
      pstmt.setString(3, societe.getTelephone());
      pstmt.setString(4, societe.getEmail());
      pstmt.setString(5, societe.getCommentaires());

      int rowsAffected = pstmt.executeUpdate();

      if (rowsAffected == 0) {
        throw new SQLException("L'insertion de la société a échoué, aucune ligne affectée");
      }

      // ========== ÉTAPE 3 : Récupérer l'ID généré ==========
      generatedKeys = pstmt.getGeneratedKeys();

      if (generatedKeys.next()) {
        Integer societeId = generatedKeys.getInt(1);
        societe.setId(societeId);

        return societeId;

      } else {
        throw new SQLException("L'insertion a échoué, aucun ID généré");
      }

    } catch (SQLException sqlEx) {
      LOGGER.log(Level.SEVERE, "Erreur SQL lors de la création de la société", sqlEx);
      throw new DAOException(
              SQLExceptionAnalyzer.categorize(sqlEx),
              "createSociete",
              null,
              "Erreur lors de la création de la société : " + SQLExceptionAnalyzer.analyze(sqlEx),
              sqlEx
      );
    } finally {
      // ✅ IMPORTANT : Fermer SEULEMENT ResultSet et PreparedStatement
      // NE PAS :
      // ._ Fermer la connexion (Singleton)
      // ._ Faire commit/rollback (géré par l'appelant)
      // ._ Modifier autoCommit (géré par l'appelant)

      closeResources(generatedKeys, pstmt, null);

      // NE PAS fermer connection
      // NE PAS toucher à setAutoCommit
      // NE PAS faire commit/rollback
    }
  }

  protected void saveSociete(Societe societe, Integer societeId, Connection connection)
          throws DAOException {

    if (societe == null) {
      throw new DAOException(
              DAOException.ErrorCode.INVALID_PARAMETER,
              "saveSociete",
              societeId,
              "La société ne peut pas être null"
      );
    }

    if (societeId == null || societeId <= 0) {
      throw new DAOException(
              DAOException.ErrorCode.INVALID_PARAMETER,
              "saveSociete",
              societeId,
              "L'ID société doit être un entier positif non null"
      );
    }

    if (connection == null) {
      throw new DAOException(
              DAOException.ErrorCode.INVALID_PARAMETER,
              "saveSociete",
              societeId,
              "La connexion ne peut pas être null"
      );
    }

    String sql = "UPDATE societe " +
            "SET raison_sociale = ?, telephone = ?, email = ?, commentaires = ? " +
            "WHERE id_societe = ?";

    PreparedStatement pstmt = null;

    try {
      // ✅ CORRECTION : Ne pas utiliser try-with-resources
      // La connexion est gérée par l'appelant (transaction parent)
      pstmt = connection.prepareStatement(sql);
      pstmt.setString(1, societe.getRaisonSociale());
      pstmt.setString(2, societe.getTelephone());
      pstmt.setString(3, societe.getEmail());
      pstmt.setString(4, societe.getCommentaires());
      pstmt.setInt(5, societeId);

      int rowsAffected = pstmt.executeUpdate();

      if (rowsAffected == 0) {
        throw new SQLException(
                "La mise à jour de la société a échoué, aucune ligne affectée (ID=" + societeId + ")"
        );
      }

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE,
              "Erreur SQL lors de la mise à jour de la société ID=" + societeId, e);

      // Vérifier si c'est une violation de contrainte d'unicité
      if (SQLExceptionAnalyzer.isUniqueConstraintViolation(e)) {
        String constraintName = SQLExceptionAnalyzer.extractConstraintName(e);
        throw new DAOException(
                DAOException.ErrorCode.UNIQUE_CONSTRAINT_VIOLATION,
                "saveSociete",
                societeId,
                "La raison sociale '" + societe.getRaisonSociale() + "' existe déjà" +
                        (constraintName != null ? " (contrainte: " + constraintName + ")" : ""),
                e
        );
      }

      throw new DAOException(
              SQLExceptionAnalyzer.categorize(e),
              "saveSociete",
              societeId,
              "Erreur lors de la mise à jour de la société : " + SQLExceptionAnalyzer.analyze(e),
              e
      );
    } finally {
      // ✅ IMPORTANT : Fermer SEULEMENT le PreparedStatement
      // NE PAS :
      // ._ Fermer la connexion (gérée par l'appelant)
      // ._ Faire commit/rollback (géré par l'appelant)
      // ._ Modifier autoCommit (géré par l'appelant)

      closeResources(null, pstmt, null);
    }
  }

  protected void deleteSociete(Connection connection, Integer societeId) throws DAOException {
    if (connection == null) {
      throw new DAOException(
              DAOException.ErrorCode.INVALID_PARAMETER,
              "deleteSociete",
              societeId,
              "La connexion ne peut pas être null"
      );
    }

    if (societeId == null || societeId <= 0) {
      LOGGER.log(Level.WARNING, "Tentative de suppression avec ID société invalide : {0}", societeId);
      throw new DAOException(
              DAOException.ErrorCode.INVALID_PARAMETER,
              "deleteSociete",
              societeId,
              "L'ID société doit être un entier positif non null"
      );
    }

    String sql = "DELETE FROM societe WHERE id_societe = ?";
    PreparedStatement pstmt = null;

    try {
      pstmt = connection.prepareStatement(sql);
      pstmt.setInt(1, societeId);

      int rowsAffected = pstmt.executeUpdate();

      if (rowsAffected > 0) {
        LOGGER.log(Level.FINE, "Société supprimée : ID={0}", societeId);
      } else {
        LOGGER.log(Level.WARNING, "Aucune société trouvée avec l'ID {0}", societeId);
        throw new DAOException(
                DAOException.ErrorCode.ENTITY_NOT_FOUND,
                "deleteSociete",
                societeId,
                "Aucune société trouvée avec l'ID " + societeId
        );
      }

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Erreur SQL lors de la suppression de la société ID=" + societeId, e);

      if (SQLExceptionAnalyzer.isForeignKeyViolation(e)) {
        String constraintName = SQLExceptionAnalyzer.extractConstraintName(e);
        throw new DAOException(
                DAOException.ErrorCode.FOREIGN_KEY_VIOLATION,
                "deleteSociete",
                societeId,
                "Impossible de supprimer la société : elle est référencée par d'autres entités" +
                        (constraintName != null ? " (contrainte: " + constraintName + ")" : ""),
                e
        );
      }

      throw new DAOException(
              SQLExceptionAnalyzer.categorize(e),
              "deleteSociete",
              societeId,
              "Erreur lors de la suppression de la société : " + SQLExceptionAnalyzer.analyze(e),
              e
      );
    } finally {
      closeResources(null, pstmt, null);
    }
  }


}
