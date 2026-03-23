package com.julio.dao;

import com.julio.exception.DaoException;
import com.julio.model.Utilisateur;
import com.julio.util.SqlExceptionAnalyzer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

/**
 * DAO pour la gestion des utilisateurs (Authentification).
 */
@Slf4j
public class UtilisateurDao {

  private final DatabaseConnexion dbConnexion;

  /**
   * Constructeur.
   *
   * @throws DaoException Exception
   */
  public UtilisateurDao() throws DaoException {
    try {
      this.dbConnexion = DatabaseConnexion.getInstance();
    } catch (SQLException ex) {
      log.error("Erreur init UtilisateurDao", ex);
      throw new DaoException(DaoException.ErrorCode.CONNECTION_ERROR,
          "init", null, "Erreur BDD", ex);
    }
  }

  /**
   * Recherche un utilisateur par son identifiant (username ou email).
   */
  public Utilisateur findByIdentifiant(String identifiant) throws DaoException {
    String sql =
        """
            SELECT id_utilisateur, identifiant, mot_de_passe, sel 
            FROM utilisateur 
            WHERE identifiant = ?
        """;

    try (Connection conn = dbConnexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, identifiant);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return Utilisateur.builder()
              .id(rs.getInt("id_utilisateur"))
              .identifiant(rs.getString("identifiant"))
              .motDePasse(rs.getString("mot_de_passe"))
              .sel(rs.getString("sel"))
              .build();
        }
        return null;
      }
    } catch (SQLException e) {
      log.error("Erreur lors de la recherche de l'utilisateur '{}'", identifiant, e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e),
          "findByIdentifiant", null, "Erreur lecture", e);
    }
  }
}