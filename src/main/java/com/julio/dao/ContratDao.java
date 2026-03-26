package com.julio.dao;

import com.julio.exception.DaoException;
import com.julio.model.Contrat;
import com.julio.util.SqlExceptionAnalyzer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * DAO pour la gestion de la persistance des contrats.
 *
 * <p>Gère les opérations CRUD sur la table {@code contrat} via transactions ACID.
 * Utilise une méthode unique {@link #save(Contrat)} pour la création et la mise à jour,
 * et s'appuie sur le try with resources pour la fermeture automatique des flux.</p>
 *
 * @author Julio
 * @version 2.0 (Optimisé avec SLF4J, try-with-resources et unification save)
 * @since 26/03/2026
 */
@Slf4j
public class ContratDao {

  private static ContratDao instance;
  private final DatabaseConnexion dbConnexion;

  /**
   * Constructeur privé (Singleton) récupérant l'instance de DatabaseConnexion.
   *
   * @throws DaoException si la connexion à la base de données échoue
   */
  public ContratDao() throws DaoException {
    try {
      this.dbConnexion = DatabaseConnexion.getInstance();
    } catch (SQLException e) {
      log.error("Échec de l'initialisation de ContratDao", e);
      throw new DaoException(DaoException.ErrorCode.CONNECTION_ERROR, "init", null,
          "Impossible d'initialiser ContratDao : " + e.getMessage(), e);
    }
  }

  /**
   * Récupère tous les contrats de la base de données.
   *
   * @return une liste de tous les contrats
   * @throws DaoException si une erreur survient lors de la requête
   */
  public List<Contrat> findAll() throws DaoException {
    List<Contrat> contrats = new ArrayList<>();
    String sql =
        """
            SELECT id_contrat, client_id, nom_contrat, montant
            FROM contrat ORDER BY id_contrat
        """;

    try (Connection conn = dbConnexion.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        contrats.add(mapResultSetToContrat(rs));
      }
      return contrats;

    } catch (SQLException e) {
      log.error("Erreur SQL dans findAll()", e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e), "findAll", null,
          "Erreur lors de la récupération des contrats : "
              + SqlExceptionAnalyzer.analyze(e), e);
    }
  }

  /**
   * Récupère un contrat par son identifiant.
   *
   * @param id l'identifiant du contrat
   * @return le contrat correspondant ou null si non trouvé
   * @throws DaoException si une erreur survient lors de la requête
   */
  public Contrat findById(Integer id) throws DaoException {
    if (id == null || id <= 0) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "findById", id, "ID invalide");
    }

    String sql =
        """
            SELECT id_contrat, client_id, nom_contrat, montant
            FROM contrat WHERE id_contrat = ?
        """;

    try (Connection conn = dbConnexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToContrat(rs);
        }
        return null;
      }

    } catch (SQLException e) {
      log.error("Erreur SQL lors de findById avec ID={}", id, e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e),
          "findById", id, "Erreur de recherche", e);
    }
  }

  /**
   * Récupère tous les contrats associés à un client spécifique.
   *
   * @param clientId l'identifiant du client
   * @return une liste des contrats du client
   * @throws DaoException si une erreur survient
   */
  public List<Contrat> findByIdClient(Integer clientId) throws DaoException {
    if (clientId == null || clientId <= 0) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "findByIdClient", clientId, "ID client invalide");
    }

    List<Contrat> contrats = new ArrayList<>();
    String sql =
        """
            SELECT id_contrat, client_id, nom_contrat, montant
            FROM contrat
            WHERE client_id = ?
            ORDER BY id_contrat
        """;

    try (Connection conn = dbConnexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, clientId);

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          contrats.add(mapResultSetToContrat(rs));
        }
      }
      return contrats;

    } catch (SQLException e) {
      log.error("Erreur SQL lors de findByIdClient avec clientId={}", clientId, e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e), "findByIdClient",
          clientId, "Erreur recherche contrats client", e);
    }
  }

  /**
   * Méthode UNIQUE pour insérer (create) ou mettre à jour (update) un contrat.
   *
   * <p>Gère manuellement la transaction SQL (Commit/Rollback).</p>
   *
   * @param contrat le contrat à sauvegarder
   * @return le contrat avec son ID à jour
   * @throws DaoException en cas d'erreur ou de violation de clé étrangère
   */
  public Contrat save(Contrat contrat) throws DaoException {
    if (contrat == null) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "save", null, "Contrat null");
    }

    boolean isNew = (contrat.getId() == null || contrat.getId() <= 0);

    try (Connection connection = dbConnexion.getConnection()) {
      connection.setAutoCommit(false); // Début de la transaction

      try {
        if (isNew) {
          // =================== LOGIQUE CREATE =====================
          String sql = "INSERT INTO contrat (client_id, nom_contrat, montant) VALUES (?, ?, ?)";
          try (PreparedStatement pstmt = connection.prepareStatement(
              sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, contrat.getClientId());
            pstmt.setString(2, contrat.getNomContrat());
            pstmt.setDouble(3, contrat.getMontant());

            if (pstmt.executeUpdate() == 0) {
              throw new SQLException("L'insertion a échoué");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
              if (rs.next()) {
                contrat.setId(rs.getInt(1));
              } else {
                throw new SQLException("Aucun ID généré");
              }
            }
          }
        } else {
          // ================== LOGIQUE UPDATE ==================
          String sql = "UPDATE contrat SET nom_contrat = ?, montant = ? WHERE id_contrat = ?";
          try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, contrat.getNomContrat());
            pstmt.setDouble(2, contrat.getMontant());
            pstmt.setInt(3, contrat.getId());

            if (pstmt.executeUpdate() == 0) {
              throw new DaoException(DaoException.ErrorCode.ENTITY_NOT_FOUND,
                  "save", contrat.getId(), "Contrat introuvable");
            }
          }
        }

        connection.commit();
        log.info("Contrat {} avec succès : ID={}", isNew ? "créé" : "mis à jour", contrat.getId());
        return contrat;

      } catch (Exception e) {
        connection.rollback();
        log.warn("Rollback effectué lors de la sauvegarde du contrat : {}", e.getMessage());
        throw e;
      }

    } catch (SQLException e) {
      log.error("Erreur SQL lors du save() du contrat", e);

      // Traitement spécifique de la clé étrangère (Le Client n'existe pas)
      if (SqlExceptionAnalyzer.isForeignKeyViolation(e)) {
        throw new DaoException(DaoException.ErrorCode.FOREIGN_KEY_VIOLATION,
            "save", contrat.getClientId(),
            "Impossible d'attacher le contrat : le client ID="
                + contrat.getClientId() + " n'existe pas.", e);
      }

      throw new DaoException(SqlExceptionAnalyzer.categorize(e),
          "save", contrat.getId(), "Erreur base de données", e);
    }
  }

  /**
   * Supprime un contrat de la base de données avec transaction.
   *
   * @param id l'identifiant du contrat à supprimer
   * @return true si la suppression a réussi, false sinon
   * @throws DaoException si une erreur survient lors de la suppression
   */
  public boolean delete(Integer id) throws DaoException {
    if (id == null || id <= 0) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "delete", id, "ID invalide");
    }

    String sql = "DELETE FROM contrat WHERE id_contrat = ?";

    try (Connection connection = dbConnexion.getConnection()) {
      connection.setAutoCommit(false);

      try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, id);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
          connection.commit();
          log.info("Contrat supprimé avec succès : ID={}", id);
          return true;
        } else {
          connection.rollback();
          log.warn("Aucun contrat trouvé pour la suppression : ID={}", id);
          return false;
        }
      } catch (Exception e) {
        connection.rollback();
        throw e;
      }
    } catch (SQLException e) {
      log.error("Erreur SQL lors de la suppression du contrat ID={}", id, e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e),
          "delete", id, "Erreur suppression contrat", e);
    }
  }

  /**
   * Méthode utilitaire privée pour mapper un ResultSet vers un objet Contrat.
   *
   * @param rs le ResultSet positionné
   * @return le contrat mappé
   * @throws SQLException en cas de problème de lecture du ResultSet
   */
  private Contrat mapResultSetToContrat(ResultSet rs) throws SQLException {
    return Contrat.builder()
        .id(rs.getInt("id_contrat"))
        .clientId(rs.getInt("client_id"))
        .nomContrat(rs.getString("nom_contrat"))
        .montant(rs.getDouble("montant"))
        .build();
  }
}