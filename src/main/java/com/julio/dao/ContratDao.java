package com.julio.dao;

import static com.julio.util.JdbcUtil.closeResources;

import com.julio.exception.DaoException;
import com.julio.exception.ValidationException;
import com.julio.model.Contrat;
import com.julio.service.LoggerService;
import com.julio.util.SqlExceptionAnalyzer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO pour la gestion de la persistance des contrats.
 *
 * <p>* Gère les opérations CRUD sur la table {@code contrat}.</p>
 *
 * <h2>Opérations</h2>
 * <ul>
 *   <li>{@link #create(Contrat)} - Crée un contrat</li>
 *   <li>{@link #findById(Integer)} - Recherche par ID</li>
 *   <li>{@link #findByIdClient(Integer)} - Liste contrats d'un client</li>
 *   <li>{@link #findAll()} - Liste tous les contrats</li>
 *   <li>{@link #save(Contrat)} - Modifie un contrat</li>
 *   <li>{@link #delete(Integer)} - Supprime un contrat</li>
 * </ul>
 *
 * <h2>Contraintes</h2>
 * <ul>
 *   <li><b>FK</b> : client_id → client.id_client</li>
 *   <li><b>NOT NULL</b> : nom_contrat, montant, client_id</li>
 * </ul>
 *
 * @author Julio FERMIN
 * @version 2.0
 * @since 15/01/2026
 * @see Contrat
 * @see DaoException
 */
public class ContratDao {

  private static final Logger LOGGER = LoggerService.getLogger(ContratDao.class);
  private final DatabaseConnexion dbConnection;

  /**
   * Constructeur qui récupère l'instance Singleton de DatabaseConnection.
   *
   * @throws DaoException si la connexion à la base de données échoue
   */
  public ContratDao() throws DaoException {
    try {
      this.dbConnection = DatabaseConnexion.getInstance();

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Échec de l'initialisation de ContratDao", e);
      throw new DaoException(
          DaoException.ErrorCode.CONNECTION_ERROR,
          "init",
          null,
          "Impossible d'initialiser ContratDao : " + e.getMessage(),
          e
      );
    }
  }

  /**
   * Récupère tous les contrats de la base de données.
   *
   * <p>Cette méthode retourne tous les contrats sans filtrage, triés par ID.
   * Pour récupérer les contrats d'un client spécifique, utilisez findByIdClient().
   *
   * @return une liste de tous les contrats
   * @throws DaoException si une erreur survient lors de la requête
   */
  public List<Contrat> findAll() throws DaoException {
    List<Contrat> contrats = new ArrayList<>();

    String sql = """
        SELECT id_contrat, client_id, nom_contrat, montant 
        FROM contrat
        ORDER BY id_contrat
        """;

    Statement stmt = null;
    ResultSet rs = null;

    try {
      // ✅ CORRECTION : Ne pas utiliser try-with-resources sur connexion
      Connection connection = dbConnection.getConnection();
      stmt = connection.createStatement();
      rs = stmt.executeQuery(sql);

      while (rs.next()) {
        try {
          Contrat contrat = mapResultSetToContrat(rs);
          contrats.add(contrat);

        } catch (ValidationException e) {
          Integer contratId = rs.getInt("id_contrat");
          LOGGER.log(Level.WARNING,
              "Contrat ID={0} ignoré : données invalides - {1}",
              new Object[]{contratId, e.getMessage()});
        }
      }

      return contrats;

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Erreur SQL lors de findAll()", e);
      throw new DaoException(
          SqlExceptionAnalyzer.categorize(e),
          "findAll",
          null,
          "Erreur lors de la récupération de tous les contrats : "
              + SqlExceptionAnalyzer.analyze(e),
          e
      );
    } finally {
      // ✅ IMPORTANT : Fermer SEULEMENT ResultSet et Statement
      closeResources(rs, stmt, null);
      // ❌ NE PAS fermer connection (Singleton)
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
      LOGGER.log(Level.WARNING, "Tentative de findById avec un ID invalide : {0}", id);
      throw new DaoException(
          DaoException.ErrorCode.INVALID_PARAMETER,
          "findById",
          id,
          "L'ID doit être un entier positif non null"
      );
    }

    String sql = """
        SELECT id_contrat, client_id, nom_contrat, montant
        FROM contrat
        WHERE id_contrat = ?
        """;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      // ✅ CORRECTION : Ne pas utiliser try-with-resources sur connexion
      Connection connection = dbConnection.getConnection();
      pstmt = connection.prepareStatement(sql);
      pstmt.setInt(1, id);
      rs = pstmt.executeQuery();

      if (rs.next()) {
        try {
          Contrat contrat = mapResultSetToContrat(rs);

          return contrat;

        } catch (ValidationException e) {
          LOGGER.log(Level.SEVERE,
              "Erreur de validation lors du mapping du contrat ID={0}", id);
          throw new DaoException(
              DaoException.ErrorCode.INVALID_PARAMETER,
              "findById",
              id,
              "Données invalides pour le contrat : " + e.getMessage(),
              e
          );
        }
      } else {
        return null;
      }

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Erreur SQL lors de findById avec ID=" + id, e);
      throw new DaoException(
          SqlExceptionAnalyzer.categorize(e),
          "findById",
          id,
          "Erreur lors de la recherche du contrat : " + SqlExceptionAnalyzer.analyze(e),
          e
      );
    } finally {
      // ✅ IMPORTANT : Fermer SEULEMENT ResultSet et PreparedStatement
      closeResources(rs, pstmt, null);
      // ❌ NE PAS fermer connection (Singleton)
    }
  }

  /**
   * Récupère tous les contrats associés à un client spécifique.
   *
   * <p>Cette méthode est essentielle pour afficher les contrats d'un client
   * dans l'interface utilisateur. Les contrats sont triés par ID.
   *
   * @param clientId l'identifiant du client
   * @return une liste des contrats du client (peut être vide)
   * @throws DaoException si une erreur survient lors de la requête
   */
  public List<Contrat> findByIdClient(Integer clientId) throws DaoException {
    if (clientId == null || clientId <= 0) {
      throw new DaoException(
          DaoException.ErrorCode.INVALID_PARAMETER,
          "findByIdClient",
          clientId,
          "L'ID du client doit être un entier positif non null"
      );
    }

    List<Contrat> contrats = new ArrayList<>();

    String sql = """
        SELECT id_contrat, client_id, nom_contrat, montant
        FROM contrat
        WHERE client_id = ?
        ORDER BY id_contrat
        """;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      // ✅ CORRECTION : Ne pas utiliser try-with-resources sur connexion
      Connection connection = dbConnection.getConnection();
      pstmt = connection.prepareStatement(sql);
      pstmt.setInt(1, clientId);
      rs = pstmt.executeQuery();

      while (rs.next()) {
        try {
          Contrat contrat = mapResultSetToContrat(rs);
          contrats.add(contrat);

        } catch (ValidationException e) {
          Integer contratId = rs.getInt("id_contrat");
          LOGGER.log(Level.WARNING,
              "Contrat ID={0} ignoré pour client ID={1} : {2}",
              new Object[]{contratId, clientId, e.getMessage()});
        }
      }

      return contrats;

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Erreur SQL lors de findByIdClient avec clientId="
          + clientId, e);
      throw new DaoException(
          SqlExceptionAnalyzer.categorize(e),
          "findByIdClient",
          clientId,
          "Erreur lors de la recherche des contrats du client : "
              + SqlExceptionAnalyzer.analyze(e),
          e
      );
    } finally {
      // ✅ IMPORTANT : Fermer SEULEMENT ResultSet et PreparedStatement
      closeResources(rs, pstmt, null);
      // ❌ NE PAS fermer connection (Singleton)
    }
  }

  /**
   * Insère un nouveau contrat dans la base de données avec transaction.
   *
   * <p>Le client référencé par client_id doit exister dans la base de données,
   * sinon une exception de type FOREIGN_KEY_VIOLATION sera levée.
   *
   * @param contrat le contrat à insérer
   * @return le contrat avec son ID généré
   * @throws DaoException si une erreur survient lors de l'insertion
   */
  public Contrat create(Contrat contrat) throws DaoException {
    if (contrat == null) {
      throw new DaoException(
          DaoException.ErrorCode.INVALID_PARAMETER,
          "create",
          null,
          "Le contrat ne peut pas être null"
      );
    }

    String sql = """
        INSERT INTO contrat (client_id, nom_contrat, montant)
        "VALUES (?, ?, ?)
        """;

    Connection connection = null;
    PreparedStatement pstmt = null;
    ResultSet generatedKeys = null;

    try {
      // ✅ CORRECTION : Récupérer la connexion sans try-with-resources
      connection = dbConnection.getConnection();
      connection.setAutoCommit(false);

      pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pstmt.setInt(1, contrat.getClientId());
      pstmt.setString(2, contrat.getNomContrat());
      pstmt.setDouble(3, contrat.getMontant());

      int rowsAffected = pstmt.executeUpdate();

      if (rowsAffected == 0) {
        throw new SQLException("L'insertion du contrat a échoué, aucune ligne affectée");
      }

      generatedKeys = pstmt.getGeneratedKeys();

      if (generatedKeys.next()) {
        Integer contratId = generatedKeys.getInt(1);
        contrat.setId(contratId);

        // ✅ COMMIT : Transaction réussie
        connection.commit();

        return contrat;

      } else {
        throw new SQLException("L'insertion a échoué, aucun ID généré");
      }

    } catch (SQLException e) {
      // ✅ ROLLBACK en cas d'erreur SQL
      if (connection != null) {
        try {
          connection.rollback();
          LOGGER.log(Level.WARNING, "Rollback effectué suite à l'erreur SQL", e);
        } catch (SQLException rollbackEx) {
          LOGGER.log(Level.SEVERE, "Erreur lors du rollback", rollbackEx);
        }
      }

      LOGGER.log(Level.SEVERE, "Erreur SQL lors de la création du contrat", e);

      // Analyse spécifique pour les violations de clés étrangères
      if (SqlExceptionAnalyzer.isForeignKeyViolation(e)) {
        String constraintName = SqlExceptionAnalyzer.extractConstraintName(e);
        throw new DaoException(
            DaoException.ErrorCode.FOREIGN_KEY_VIOLATION,
            "create",
            contrat.getClientId(),
            "Le client ID=" + contrat.getClientId() + " n'existe pas dans la base de données"
                + (constraintName != null ? " (contrainte: " + constraintName + ")" : ""),
            e
        );
      }

      throw new DaoException(
          SqlExceptionAnalyzer.categorize(e),
          "create",
          contrat.getId(),
          "Erreur lors de la création du contrat : " + SqlExceptionAnalyzer.analyze(e),
          e
      );
    } finally {
      // ✅ IMPORTANT : Fermer toutes les ressources et réactiver autoCommit
      closeResources(generatedKeys, pstmt, connection);
    }
  }

  /**
   * Met à jour un contrat existant dans la base de données avec transaction.
   *
   * <p>Note : Le client_id ne peut pas être modifié. Pour réaffecter un contrat
   * à un autre client, il faut le supprimer et le recréer.
   *
   * @param contrat le contrat à mettre à jour (doit avoir un ID valide)
   * @return true si la mise à jour a réussi, false sinon
   * @throws DaoException si une erreur survient lors de la mise à jour
   */
  public boolean save(Contrat contrat) throws DaoException {
    if (contrat == null || contrat.getId() == null || contrat.getId() <= 0) {
      throw new DaoException(
          DaoException.ErrorCode.INVALID_PARAMETER,
          "save",
          contrat != null ? contrat.getId() : null,
          "Le contrat doit avoir un ID valide pour être mis à jour"
      );
    }

    String sql = """
        UPDATE contrat
        SET nom_contrat = ?, montant = ?
        WHERE id_contrat = ?
        """;

    Connection connection = null;
    PreparedStatement pstmt = null;

    try {
      // ✅ CORRECTION : Récupérer la connexion sans try-with-resources
      connection = dbConnection.getConnection();
      connection.setAutoCommit(false);

      pstmt = connection.prepareStatement(sql);
      pstmt.setString(1, contrat.getNomContrat());
      pstmt.setDouble(2, contrat.getMontant());
      pstmt.setInt(3, contrat.getId());

      int rowsAffected = pstmt.executeUpdate();

      if (rowsAffected > 0) {
        // ✅ COMMIT : Transaction réussie
        connection.commit();

        return true;
      } else {
        connection.rollback();
        return false;
      }

    } catch (SQLException e) {
      // ✅ ROLLBACK en cas d'erreur SQL
      if (connection != null) {
        try {
          connection.rollback();
          LOGGER.log(Level.WARNING, "Rollback effectué suite à l'erreur SQL", e);
        } catch (SQLException rollbackEx) {
          LOGGER.log(Level.SEVERE, "Erreur lors du rollback", rollbackEx);
        }
      }

      LOGGER.log(Level.SEVERE, "Erreur SQL lors de la mise à jour du contrat ID="
          + contrat.getId(), e);
      throw new DaoException(
          SqlExceptionAnalyzer.categorize(e),
          "save",
          contrat.getId(),
          "Erreur lors de la mise à jour du contrat : " + SqlExceptionAnalyzer.analyze(e),
          e
      );
    } finally {
      // ✅ IMPORTANT : Fermer toutes les ressources et réactiver autoCommit
      closeResources(null, pstmt, connection);
      // ❌ NE PAS fermer connection (Singleton)
    }
  }

  /**
   * Supprime un contrat de la base de données avec transaction.
   *
   * <p>Cette opération supprime définitivement le contrat. Le client associé
   * n'est pas affecté par cette suppression.
   *
   * @param id l'identifiant du contrat à supprimer
   * @return true si la suppression a réussi, false sinon
   * @throws DaoException si une erreur survient lors de la suppression
   */
  public boolean delete(Integer id) throws DaoException {
    if (id == null || id <= 0) {
      throw new DaoException(
          DaoException.ErrorCode.INVALID_PARAMETER,
          "delete",
          id,
          "L'ID doit être un entier positif non null pour supprimer un contrat"
      );
    }

    Connection connection = null;
    PreparedStatement pstmt = null;

    try {
      // ✅ CORRECTION : Récupérer la connexion sans try-with-resources
      connection = dbConnection.getConnection();
      connection.setAutoCommit(false);

      String sql = "DELETE FROM contrat WHERE id_contrat = ?";
      pstmt = connection.prepareStatement(sql);
      pstmt.setInt(1, id);

      int rowsAffected = pstmt.executeUpdate();

      if (rowsAffected > 0) {
        // ✅ COMMIT : Transaction réussie
        connection.commit();
        return true;
      } else {
        connection.rollback();
        return false;
      }

    } catch (SQLException e) {
      // ✅ ROLLBACK en cas d'erreur SQL
      if (connection != null) {
        try {
          connection.rollback();
          LOGGER.log(Level.WARNING, "Rollback effectué suite à l'erreur SQL", e);
        } catch (SQLException rollbackEx) {
          LOGGER.log(Level.SEVERE, "Erreur lors du rollback", rollbackEx);
        }
      }

      LOGGER.log(Level.SEVERE, "Erreur SQL lors de la suppression du contrat ID=" + id, e);
      throw new DaoException(
          SqlExceptionAnalyzer.categorize(e),
          "delete",
          id,
          "Erreur lors de la suppression du contrat : " + SqlExceptionAnalyzer.analyze(e),
          e
      );
    } finally {
      // ✅ IMPORTANT : Fermer toutes les ressources et réactiver autoCommit
      closeResources(null, pstmt, connection);
      // ❌ NE PAS fermer connection (Singleton)
    }
  }

  /**
   * Méthode utilitaire privée pour mapper un ResultSet vers un objet Contrat.
   *
   * <p>Cette méthode reconstruit un objet Contrat à partir des données
   * d'une requête SQL sur la table contrat.
   *
   * @param rs le ResultSet contenant les données du contrat
   * @return un objet Contrat reconstitué
   * @throws SQLException si une erreur survient lors de la lecture du ResultSet
   * @throws ValidationException si les données ne respectent pas les contraintes métier
   */
  private Contrat mapResultSetToContrat(ResultSet rs)
      throws SQLException, ValidationException {
    Contrat contrat = new Contrat(
        rs.getInt("client_id"),
        rs.getString("nom_contrat"),
        rs.getDouble("montant")
    );
    contrat.setId(rs.getInt("id_contrat"));
    return contrat;
  }
}
