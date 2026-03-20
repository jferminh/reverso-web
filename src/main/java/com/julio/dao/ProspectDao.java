package com.julio.dao;

import com.julio.exception.DaoException;
import com.julio.exception.ValidationException;
import com.julio.model.Adresse;
import com.julio.model.Interesse;
import com.julio.model.Prospect;
import com.julio.util.SqlExceptionAnalyzer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * DAO pour la gestion de la persistance des prospects.
 * Optimisé avec une méthode unique save() et try-with-resources.
 *
 * @author Julio FERMIN
 * @version 3.0
 */
@Slf4j
public class ProspectDao extends SocieteDao {

  public ProspectDao() throws DaoException {
    super();
  }

  /**
   * Méthode UNIQUE pour insérer (create) ou mettre à jour (update) un prospect.
   */
  public Prospect save(Prospect prospect) throws DaoException {
    if (prospect == null) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER
          , "save", null, "Le prospect est null");
    }

    boolean isNew = (prospect.getId() == null || prospect.getId() <= 0);

    try (Connection connection = dbConnexion.getConnection()) {
      connection.setAutoCommit(false);

      try {
        if (isNew) {
          // ================== LOGIQUE CREATE ==================
          Integer societeId = createSociete(prospect, connection);

          String sql =
          """
          INSERT INTO prospect (id_societe, date_prospection, interesse) 
          VALUES (?, ?, ?)
          """;
          try (PreparedStatement pstmt = connection.prepareStatement(sql
              , Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, societeId);
            pstmt.setDate(2, prospect.getDateProspection() != null
                ? Date.valueOf(prospect.getDateProspection()) : null);
            pstmt.setInt(3, prospect.getInteresse() != null
                ? prospect.getInteresse().toInt() : 0);

            if (pstmt.executeUpdate() == 0)
              throw new SQLException("L'insertion du prospect a échoué");

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
              if (rs.next()) prospect.setId(rs.getInt(1));
              else throw new SQLException("Aucun ID généré pour le prospect");
            }
          }
        } else {
          // ================== LOGIQUE UPDATE ==================
          Integer societeId = null;
          String getSocieteSql = "SELECT id_societe FROM prospect WHERE id_prospect = ?";
          try (PreparedStatement pstmt = connection.prepareStatement(getSocieteSql)) {
            pstmt.setInt(1, prospect.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
              if (rs.next()) societeId = rs.getInt("id_societe");
              else throw new DaoException(DaoException.ErrorCode.ENTITY_NOT_FOUND
                  , "save", prospect.getId(), "Introuvable");
            }
          }

          if (prospect.getAdresse() != null && prospect.getAdresse().getId() != null) {
            adresseDao.save(prospect.getAdresse(), connection);
          }

          saveSociete(prospect, societeId, connection);

          String sql =
          """
          UPDATE prospect SET date_prospection = ?, interesse = ? 
          WHERE id_prospect = ?
          """;
          try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, prospect.getDateProspection() != null
                ? Date.valueOf(prospect.getDateProspection()) : null);
            pstmt.setInt(2, prospect.getInteresse() != null
                ? prospect.getInteresse().toInt() : 0);
            pstmt.setInt(3, prospect.getId());
            if (pstmt.executeUpdate() == 0) throw new SQLException("Mise à jour échouée");
          }
        }

        connection.commit();
        log.info("Prospect {} avec succès : ID={}", isNew ? "créé" : "mis à jour"
            , prospect.getId());
        return prospect;

      } catch (Exception e) {
        connection.rollback();
        log.warn("Rollback lors de la sauvegarde du prospect : {}", e.getMessage());
        throw e;
      }
    } catch (SQLException e) {
      log.error("Erreur SQL lors du save()", e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e)
          , "save", prospect.getId(), "Erreur BDD", e);
    }
  }

  public List<Prospect> findAll() throws DaoException {
    List<Prospect> prospects = new ArrayList<>();
    String sql = """
        SELECT p.id_prospect, p.id_societe, p.date_prospection, p.interesse, 
        s.raison_sociale, a.id_adresse, s.telephone, s.email, s.commentaires,
        a.numero_rue, a.nom_rue, a.code_postal, a.ville
        FROM prospect p
        INNER JOIN societe s ON p.id_societe = s.id_societe
        INNER JOIN adresse a ON s.adresse_id = a.id_adresse
        ORDER BY s.raison_sociale ASC
        """;

    try (Connection conn = dbConnexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

      while (rs.next()) {
        try {
          prospects.add(mapResultSetToProspect(rs));
        } catch (ValidationException e) {
          log.warn("Prospect ignoré car invalide (ID={})", rs.getInt("id_prospect"));
        }
      }
      return prospects;
    } catch (SQLException e) {
      log.error("Erreur SQL lors de findAll()", e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e)
          , "findAll", null, "Erreur lecture", e);
    }
  }

  public Prospect findById(Integer id) throws DaoException {
    if (id == null || id <= 0) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER
          , "findById", id, "ID invalide");
    }

    String sql = """
        SELECT p.id_prospect, p.id_societe, p.date_prospection, p.interesse,
        s.raison_sociale, a.id_adresse, s.telephone, s.email, s.commentaires,
        a.numero_rue, a.nom_rue, a.code_postal, a.ville
        FROM prospect p
        INNER JOIN societe s ON p.id_societe = s.id_societe
        INNER JOIN adresse a ON s.adresse_id = a.id_adresse
        WHERE p.id_prospect = ?
        """;

    try (Connection conn = dbConnexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, id);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) return mapResultSetToProspect(rs);
        return null;
      }
    } catch (SQLException | ValidationException e) {
      log.error("Erreur lors de findById", e);
      throw new DaoException(DaoException.ErrorCode.READ_ERROR, "findById", id, "Erreur", e);
    }
  }

  public void delete(Integer id) throws DaoException {
    if (id == null || id <= 0) throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER
        , "delete", id, "ID invalide");

    try (Connection connection = dbConnexion.getConnection()) {
      connection.setAutoCommit(false);
      try {
        Integer societeId = null;
        Integer adresseId = null;

        String getIdsSql =
        """
        SELECT p.id_societe, s.adresse_id FROM prospect p 
        INNER JOIN societe s ON p.id_societe = s.id_societe 
        WHERE p.id_prospect = ?
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(getIdsSql)) {
          pstmt.setInt(1, id);
          try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
              societeId = rs.getInt("id_societe");
              adresseId = rs.getInt("adresse_id");
            } else {
              throw new DaoException(DaoException.ErrorCode.ENTITY_NOT_FOUND
                  , "delete", id, "Introuvable");
            }
          }
        }

        try (PreparedStatement pstmt = connection.prepareStatement(
            "DELETE FROM prospect WHERE id_prospect = ?")) {
          pstmt.setInt(1, id);
          if (pstmt.executeUpdate() == 0) throw new SQLException("Aucune ligne supprimée");
        }

        this.deleteSociete(connection, societeId);

        if (adresseId != null && !isAdresseReferencee(connection, adresseId)) {
          adresseDao.deleteAdresse(connection, adresseId);
        }

        connection.commit();
        log.info("Prospect supprimé avec succès : ID={}", id);

      } catch (Exception e) {
        connection.rollback();
        log.warn("Rollback lors de la suppression du prospect", e);
        throw e;
      }
    } catch (SQLException e) {
      log.error("Erreur SQL lors de la suppression", e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e), "delete", id, "Erreur", e);
    }
  }

  /**
   * Recherche un prospect par sa raison sociale (exact match, sensible à la casse).
   *
   * @param raisonSociale la raison sociale à rechercher
   * @return le prospect trouvé ou null si non trouvé
   * @throws DaoException si une erreur survient lors de la recherche
   */
  public Prospect findByRaisonSociale(String raisonSociale) throws DaoException {
    if (raisonSociale == null || raisonSociale.trim().isEmpty()) {
      throw new DaoException(
          DaoException.ErrorCode.INVALID_PARAMETER,
          "findByRaisonSociale",
          null,
          "La raison sociale ne peut pas être null ou vide"
      );
    }

    String sql =
        """
        SELECT p.id_prospect, s.raison_sociale,
        a.id_adresse, a.numero_rue, a.nom_rue, a.code_postal, a.ville, 
        s.telephone, s.email, s.commentaires,
        p.date_prospection, p.interesse 
        FROM prospect p 
        INNER JOIN societe s ON p.id_societe = s.id_societe
        INNER JOIN adresse a ON s.adresse_id = a.id_adresse
        WHERE s.raison_sociale = ?
        """;

    // ✅ OPTIMISATION : try-with-resources (fermeture auto de Connection et PreparedStatement)
    try (Connection connection = dbConnexion.getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

      pstmt.setString(1, raisonSociale);

      // ✅ OPTIMISATION : try-with-resources imbriqué pour le ResultSet
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          try {
            return mapResultSetToProspect(rs);

          } catch (ValidationException e) {
            log.error("Erreur validation données prospect avec raison sociale '{}'"
                , raisonSociale, e);
            throw new DaoException(
                DaoException.ErrorCode.INVALID_PARAMETER,
                "findByRaisonSociale",
                null,
                "Données invalides pour le prospect : " + e.getMessage(),
                e
            );
          }
        }
        return null; // Aucun prospect trouvé
      }

    } catch (SQLException e) {
      log.error("Erreur SQL lors de findByRaisonSociale avec '{}'", raisonSociale, e);
      throw new DaoException(
          SqlExceptionAnalyzer.categorize(e),
          "findByRaisonSociale",
          null,
          "Erreur lors de la recherche par raison sociale : " + SqlExceptionAnalyzer.analyze(e),
          e
      );
    }
  }

  private boolean isAdresseReferencee(Connection connection, Integer adresseId)
      throws SQLException {
    try (PreparedStatement pstmt = connection.prepareStatement(
        "SELECT COUNT(*) AS nb FROM societe WHERE adresse_id = ?")) {
      pstmt.setInt(1, adresseId);
      try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next() && rs.getInt("nb") > 0;
      }
    }
  }

  // J'ai omis findByRaisonSociale par concision, mais il s'adapte exactement comme findAll()

  private Prospect mapResultSetToProspect(ResultSet rs)
      throws SQLException, DaoException, ValidationException {
    Adresse adresse = Adresse.builder()
        .numeroRue(rs.getString("numero_rue"))
        .nomRue(rs.getString("nom_rue"))
        .codePostal(rs.getString("code_postal"))
        .ville(rs.getString("ville"))
        .build();
    adresse.setId(rs.getInt("id_adresse"));

    java.sql.Date sqlDate = rs.getDate("date_prospection");
    LocalDate dateProspection = sqlDate != null ? sqlDate.toLocalDate() : null;

    Prospect prospect = Prospect.builder()
        .raisonSociale(rs.getString("raison_sociale"))
        .adresse(adresse)
        .telephone(rs.getString("telephone"))
        .email(rs.getString("email"))
        .commentaires(rs.getString("commentaires"))
        .dateProspection(dateProspection)
        .interesse(Interesse.fromInt(rs.getInt("interesse")))
        .build();
    prospect.setId(rs.getInt("id_prospect"));

    return prospect;
  }
}