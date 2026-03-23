package com.julio.dao;

import com.julio.exception.DaoException;
import com.julio.model.Adresse;
import com.julio.model.Societe;
import com.julio.util.SqlExceptionAnalyzer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

/**
 * DAO pour les opérations sur la table {@code societe}.
 *
 * <p><b>Pattern Méthodes Participantes :</b> Les méthodes de cette classe
 * participent à des transactions gérées par {@link ClientDao} et {@link ProspectDao}.
 * Toutes les méthodes reçoivent l'objet Connection du parent pour respecter l'ACID.
 * </p>
 *
 * @author Julio FERMIN
 * @version 3.0 (Optimisé avec SLF4J et try-with-resources)
 */
@Slf4j // ✅ Remplace tout le boilerplate du Logger
public abstract class SocieteDao {

  protected final DatabaseConnexion dbConnexion;
  protected final AdresseDao adresseDao;

  /**
   * Constructeur qui récupère l'instance de DatabaseConnection.
   *
   * @throws DaoException si la connexion à la base de données échoue
   */
  public SocieteDao() throws DaoException {
    try {
      this.dbConnexion = DatabaseConnexion.getInstance();
      this.adresseDao = new AdresseDao();

    } catch (SQLException ex) {
      log.error("Échec de l'initialisation de SocieteDao", ex);
      throw new DaoException(
          DaoException.ErrorCode.CONNECTION_ERROR,
          "init",
          null,
          "Impossible d'initialiser SocieteDao : " + ex.getMessage()
      );
    }
  }

  /**
   * Crée une société dans la base de données.
   *
   * @param societe    la société à créer
   * @param connection la connexion transactionnelle fournie par l'appelant
   * @return l'ID de la société créée
   * @throws DaoException si une erreur survient
   */
  protected Integer createSociete(Societe societe, Connection connection) throws DaoException {
    if (societe == null || societe.getAdresse() == null) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER, "createSociete", null,
          "La société et son adresse ne peuvent pas être null");
    }

    Adresse adresse = societe.getAdresse();

    // Note : Si adresseDao.create n'utilise pas la même 'connection',
    // il faudrait aussi lui passer en paramètre pour être 100% transactionnel.
    if (adresse.getId() == null) {
      try {
        adresse = adresseDao.save(adresse, connection);
      } catch (DaoException ex) {
        log.error("Erreur lors de la création de l'adresse", ex);
        throw new DaoException(DaoException.ErrorCode.CREATE_ERROR, "createSociete", null,
            "Erreur lors de la création de l'adresse", ex);
      }
    }

    String sql =
        """
            INSERT INTO societe (raison_sociale, adresse_id, telephone, email, commentaires) 
            VALUES (?, ?, ?, ?, ?)
        """;

    // OPTIMISATION : try-with-resources. Le PreparedStatement se ferme tout seul !
    try (PreparedStatement pstmt =
             connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

      pstmt.setString(1, societe.getRaisonSociale());
      pstmt.setInt(2, adresse.getId());
      pstmt.setString(3, societe.getTelephone());
      pstmt.setString(4, societe.getEmail());
      pstmt.setString(5, societe.getCommentaires());

      if (pstmt.executeUpdate() == 0) {
        throw new SQLException("L'insertion de la société a échoué, aucune ligne affectée");
      }

      // Un autre try-with-resources imbriqué pour le ResultSet
      try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          Integer societeId = generatedKeys.getInt(1);
          societe.setId(societeId);
          return societeId;
        } else {
          throw new SQLException("L'insertion a échoué, aucun ID généré");
        }
      }

    } catch (SQLException sqlEx) {
      log.error("Erreur SQL lors de la création de la société", sqlEx);
      throw new DaoException(
          SqlExceptionAnalyzer.categorize(sqlEx),
          "createSociete",
          null,
          "Erreur création société", sqlEx);
    }
  }

  /**
   * Met à jour une société existante.
   */
  protected void saveSociete(Societe societe, Integer societeId, Connection connection)
      throws DaoException {
    if (societe == null || societeId == null || connection == null) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "saveSociete", societeId, "Paramètres invalides");
    }

    String sql =
        """
            UPDATE societe
            SET raison_sociale = ?, telephone = ?, email = ?, commentaires = ?
            WHERE id_societe = ?
        """;

    // OPTIMISATION : try-with-resources
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, societe.getRaisonSociale());
      pstmt.setString(2, societe.getTelephone());
      pstmt.setString(3, societe.getEmail());
      pstmt.setString(4, societe.getCommentaires());
      pstmt.setInt(5, societeId);

      if (pstmt.executeUpdate() == 0) {
        throw new SQLException("La mise à jour a échoué, aucune ligne affectée");
      }

    } catch (SQLException e) {
      log.error("Erreur SQL lors de la mise à jour de la société ID={}", societeId, e);

      if (SqlExceptionAnalyzer.isUniqueConstraintViolation(e)) {
        throw new DaoException(
            DaoException.ErrorCode.UNIQUE_CONSTRAINT_VIOLATION,
            "saveSociete", societeId, "La raison sociale existe déjà", e);
      }
      throw new DaoException(
          SqlExceptionAnalyzer.categorize(e), "saveSociete", societeId, "Erreur mise à jour", e);
    }
  }

  /**
   * Supprime une société.
   */
  protected void deleteSociete(Connection connection, Integer societeId) throws DaoException {
    if (connection == null || societeId == null || societeId <= 0) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "deleteSociete", societeId, "Paramètres invalides");
    }

    String sql = "DELETE FROM societe WHERE id_societe = ?";

    // ✅ OPTIMISATION : try-with-resources
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setInt(1, societeId);

      if (pstmt.executeUpdate() > 0) {
        log.debug("Société supprimée : ID={}", societeId);
      } else {
        log.warn("Aucune société trouvée avec l'ID {}", societeId);
        throw new DaoException(DaoException.ErrorCode.ENTITY_NOT_FOUND,
            "deleteSociete", societeId, "Introuvable");
      }

    } catch (SQLException e) {
      log.error("Erreur SQL lors de la suppression de la société ID={}", societeId, e);

      if (SqlExceptionAnalyzer.isForeignKeyViolation(e)) {
        throw new DaoException(DaoException.ErrorCode.FOREIGN_KEY_VIOLATION,
            "deleteSociete", societeId,
            "Impossible de supprimer, entité référencée", e);
      }
      throw new DaoException(SqlExceptionAnalyzer.categorize(e),
          "deleteSociete", societeId, "Erreur suppression", e);
    }
  }
}