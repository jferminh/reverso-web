package com.julio.dao;

import com.julio.exception.DaoException;
import com.julio.model.Adresse;
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
 * DAO pour les opérations sur la table {@code adresse}.
 *
 * <p><b>Pattern Méthodes Participantes :</b> Les méthodes de cette classe
 * participent à des transactions gérées par {@link ClientDao} et {@link ProspectDao}.
 * Elles reçoivent une connexion en paramètre et ne gèrent pas commit/rollback.
 * </p>
 *
 * @author Julio FERMIN
 * @version 3.0 (Optimisé avec SLF4J, try-with-resources et méthode save unique)
 */
@Slf4j
public class AdresseDao {

  private final DatabaseConnexion dbConnexion;

  /**
   * Constructeur.
   *
   * @throws DaoException Dao Exception
   */
  public AdresseDao() throws DaoException {
    try {
      this.dbConnexion = DatabaseConnexion.getInstance();
    } catch (SQLException ex) {
      log.error("Echec de l'initialisation de AdresseDao", ex);
      throw new DaoException(DaoException.ErrorCode.CONNECTION_ERROR, "init", null,
          "Impossible d'initialiser AdresseDao : " + ex.getMessage(), ex);
    }
  }

  /**
   * Récupère toutes les adresses de la base de données.
   */
  public List<Adresse> findAll() throws DaoException {
    List<Adresse> adresses = new ArrayList<>();
    String sql = "SELECT id_adresse, numero_rue, nom_rue, code_postal, ville FROM adresse";

    // ✅ try-with-resources gère la fermeture de la connexion, du statement et du resultset
    try (Connection conn = dbConnexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

      while (rs.next()) {
        adresses.add(mapResultSetToAdresse(rs));
      }
      return adresses;

    } catch (SQLException e) {
      log.error("Erreur lors de la récupération de toutes les adresses", e);
      throw new DaoException(DaoException.ErrorCode.READ_ERROR,
          "findAll", null, "Erreur de lecture", e);
    }
  }

  /**
   * Récupère une adresse par son identifiant.
   */
  public Adresse findById(Integer id) throws DaoException {
    if (id == null || id <= 0) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "findById", id, "ID invalide");
    }

    String sql =
        """
            SELECT id_adresse, numero_rue, nom_rue, code_postal, ville FROM adresse  
            WHERE id_adresse = ?
        """;

    try (Connection conn = dbConnexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToAdresse(rs);
        }
        return null;
      }
    } catch (SQLException e) {
      log.error("Erreur lors de la recherche de l'adresse ID={}", id, e);
      throw new DaoException(DaoException.ErrorCode.READ_ERROR,
          "findById", id, "Erreur de lecture", e);
    }
  }

  /**
   * Méthode UNIQUE pour insérer (create) ou mettre à jour (update) une adresse.
   *
   * <p><strong>IMPORTANT :</strong> Cette méthode reçoit une connexion externe
   * et participe à une transaction parente. Elle NE DOIT PAS gérer commit/rollback.</p>
   */
  public Adresse save(Adresse adresse, Connection connection) throws DaoException {
    if (adresse == null) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "save", null, "L'adresse est null");
    }
    if (connection == null) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "save", adresse.getId(), "Connexion null");
    }

    boolean isNew = (adresse.getId() == null || adresse.getId() <= 0);

    try {
      if (isNew) {
        // ================== LOGIQUE CREATE ==================
        String sql =
            """
                INSERT INTO adresse (numero_rue, nom_rue, code_postal, ville) 
                VALUES (?, ?, ?, ?)
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql,
            Statement.RETURN_GENERATED_KEYS)) {
          pstmt.setString(1, adresse.getNumeroRue());
          pstmt.setString(2, adresse.getNomRue());
          pstmt.setString(3, adresse.getCodePostal());
          pstmt.setString(4, adresse.getVille());

          if (pstmt.executeUpdate() == 0) {
            throw new SQLException("L'insertion de l'adresse a échoué");
          }

          try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
              adresse.setId(rs.getInt(1));
            } else {
              throw new SQLException("Aucun ID généré pour l'adresse");
            }
          }
        }
      } else {
        // ================== LOGIQUE UPDATE ==================
        String sql =
            """
                UPDATE adresse SET numero_rue = ?, nom_rue = ?, code_postal = ?, ville = ? 
                WHERE id_adresse = ?
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
          pstmt.setString(1, adresse.getNumeroRue());
          pstmt.setString(2, adresse.getNomRue());
          pstmt.setString(3, adresse.getCodePostal());
          pstmt.setString(4, adresse.getVille());
          pstmt.setInt(5, adresse.getId());

          if (pstmt.executeUpdate() == 0) {
            log.warn("Aucune adresse trouvée avec l'ID {} pour la mise à jour", adresse.getId());
          } else {
            log.debug("Adresse mise à jour : ID={}", adresse.getId());
          }
        }
      }
      return adresse;

    } catch (SQLException e) {
      log.error("Erreur SQL lors du save() de l'adresse", e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e),
          "save", adresse.getId(), "Erreur BDD", e);
    }
  }

  /**
   * Supprime une adresse dans le contexte d'une transaction parent.
   */
  protected void deleteAdresse(Connection connection, Integer adresseId)
      throws DaoException {
    if (connection == null || adresseId == null || adresseId <= 0) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "deleteAdresse", adresseId, "Paramètres invalides");
    }

    String sql = "DELETE FROM adresse WHERE id_adresse = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setInt(1, adresseId);
      if (pstmt.executeUpdate() <= 0) {
        throw new DaoException(DaoException.ErrorCode.ENTITY_NOT_FOUND,
            "deleteAdresse", adresseId, "Introuvable");
      }
    } catch (SQLException e) {
      log.error("Erreur SQL lors de la suppression de l'adresse ID={}", adresseId, e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e),
          "deleteAdresse", adresseId, "Erreur BDD", e);
    }
  }

  /**
   * Méthode utilitaire pour mapper un ResultSet vers un objet Adresse.
   */
  private Adresse mapResultSetToAdresse(ResultSet rs)
      throws SQLException {

    return Adresse.builder()
        .id(rs.getInt("id_adresse"))
        .numeroRue(rs.getString("numero_rue"))
        .nomRue(rs.getString("nom_rue"))
        .codePostal(rs.getString("code_postal"))
        .ville(rs.getString("ville"))
        .build();
  }
}