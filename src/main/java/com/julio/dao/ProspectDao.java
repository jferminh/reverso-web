package com.julio.dao;

import com.julio.exception.DaoException;
import com.julio.model.Adresse;
import com.julio.model.Interesse;
import com.julio.model.Prospect;
import com.julio.util.SqlExceptionAnalyzer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * DAO (Data Access Object) dédié à la persistance de l'entité {@link Prospect}.
 *
 * <p>Hérite de {@link SocieteDao} pour réutiliser la logique d'enregistrement
 * des données communes. Implémente le pattern "try-with-resources",
 * garantit l'intégrité ACID grâce aux transactions manuelles, et utilise l'API JDBC 4.2
 * pour une gestion native des objets java.time (LocalDate).
 * </p>
 *
 * @author Julio FERMIN
 * @version 4.0
 */
@Slf4j
public class ProspectDao extends SocieteDao {

  /**
   * Constructeur ProspectDao.
   */
  public ProspectDao() throws DaoException {
    super();
  }

  /**
   * Sauvegarde un prospect (Création ou Mise à jour) dans une transaction globale.
   */
  public Prospect save(Prospect prospect) throws DaoException {
    if (prospect == null) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "save", null, "Le prospect fourni est null");
    }

    boolean isNew = (prospect.getId() == null || prospect.getId() <= 0);

    try (Connection connection = dbConnexion.getConnection()) {
      connection.setAutoCommit(false);

      try {
        if (isNew) {
          Integer societeId = createSociete(prospect, connection);

          String sql =
              """
                  INSERT INTO prospect (id_societe, date_prospection, interesse)
                  VALUES (?, ?, ?)
              """;
          try (PreparedStatement pstmt = connection.prepareStatement(
              sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, societeId);
            pstmt.setObject(2, prospect.getDateProspection()); // JDBC 4.2
            pstmt.setInt(3, prospect.getInteresse() != null ? prospect.getInteresse().toInt() : 0);

            if (pstmt.executeUpdate() == 0) {
              throw new SQLException("L'insertion du prospect a échoué");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
              if (rs.next()) {
                prospect.setId(rs.getInt(1));
              } else {
                throw new SQLException("Échec de la récupération de l'ID généré");
              }
            }
          }
        } else {
          Integer societeId;
          String getSocieteSql = "SELECT id_societe FROM prospect WHERE id_prospect = ?";
          try (PreparedStatement pstmt = connection.prepareStatement(getSocieteSql)) {
            pstmt.setInt(1, prospect.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
              if (rs.next()) {
                societeId = rs.getInt("id_societe");
              } else {
                throw new DaoException(DaoException.ErrorCode.ENTITY_NOT_FOUND,
                    "save", prospect.getId(), "Prospect introuvable");
              }
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
            pstmt.setObject(1, prospect.getDateProspection());
            pstmt.setInt(2, prospect.getInteresse() != null ? prospect.getInteresse().toInt() : 0);
            pstmt.setInt(3, prospect.getId());
            if (pstmt.executeUpdate() == 0) {
              throw new SQLException("Mise à jour du prospect échouée");
            }
          }
        }

        connection.commit();
        log.info("Prospect {} avec succès : ID={}", isNew ? "créé" : "mis à jour",
            prospect.getId());
        return prospect;

      } catch (Exception e) {
        log.warn("Erreur transactionnelle (save). Rollback en cours... Cause : {}", e.getMessage());
        try {
          connection.rollback();
        } catch (SQLException ex) {
          log.error("Échec critique du Rollback !", ex);
        }
        if (e instanceof DaoException) {
          throw (DaoException) e;
        }
        throw new DaoException(DaoException.ErrorCode.TRANSACTION_ERROR,
            "save", prospect.getId(), "Erreur lors de la transaction", e);
      }
    } catch (SQLException e) {
      log.error("Erreur d'accès BDD lors du save()", e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e),
          "save", prospect.getId(), "Erreur SQL BDD", e);
    }
  }

  /**
   * Récupère la liste complète des prospects.
   */
  public List<Prospect> findAll() throws DaoException {
    List<Prospect> prospects = new ArrayList<>();
    String sql =
        """
            SELECT p.id_prospect, p.id_societe, p.date_prospection, p.interesse,
                   s.raison_sociale, s.telephone, s.email, s.commentaires,
                   a.id_adresse, a.numero_rue, a.nom_rue, a.code_postal, a.ville
            FROM prospect p
            INNER JOIN societe s ON p.id_societe = s.id_societe
            INNER JOIN adresse a ON s.adresse_id = a.id_adresse
            ORDER BY s.raison_sociale ASC
        """;

    try (Connection conn = dbConnexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

      while (rs.next()) {
        prospects.add(mapResultSetToProspect(rs));
      }
      return prospects;
    } catch (SQLException e) {
      log.error("Erreur SQL lors de findAll()", e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e),
          "findAll", null, "Erreur lecture des prospects", e);
    }
  }

  /**
   * Trouve un prospect par son identifiant.
   */
  public Prospect findById(Integer id) throws DaoException {
    if (id == null || id <= 0) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "findById", id, "ID invalide");
    }

    String sql =
        """
            SELECT p.id_prospect, p.id_societe, p.date_prospection, p.interesse,
                   s.raison_sociale, s.telephone, s.email, s.commentaires,
                   a.id_adresse, a.numero_rue, a.nom_rue, a.code_postal, a.ville
            FROM prospect p
            INNER JOIN societe s ON p.id_societe = s.id_societe
            INNER JOIN adresse a ON s.adresse_id = a.id_adresse
            WHERE p.id_prospect = ?
        """;

    try (Connection conn = dbConnexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToProspect(rs);
        }
        return null;
      }
    } catch (SQLException e) {
      log.error("Erreur lors de findById", e);
      throw new DaoException(DaoException.ErrorCode.READ_ERROR,
          "findById", id, "Erreur lecture unitaire", e);
    }
  }

  /**
   * Supprime un prospect et ses entités liées (si non référencées ailleurs).
   */
  public void delete(Integer id) throws DaoException {
    if (id == null || id <= 0) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "delete", id, "ID invalide");
    }

    try (Connection connection = dbConnexion.getConnection()) {
      connection.setAutoCommit(false); // Début transaction

      try {
        Integer societeId;
        Integer adresseId;

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
              throw new DaoException(DaoException.ErrorCode.ENTITY_NOT_FOUND,
                  "delete", id, "Prospect introuvable");
            }
          }
        }

        try (PreparedStatement pstmt = connection.prepareStatement(
            "DELETE FROM prospect WHERE id_prospect = ?")) {
          pstmt.setInt(1, id);
          if (pstmt.executeUpdate() == 0) {
            throw new SQLException("Aucune ligne supprimée dans la table prospect");
          }
        }

        this.deleteSociete(connection, societeId);

        if (!isAdresseReferencee(connection, adresseId)) {
          adresseDao.deleteAdresse(connection, adresseId);
        }

        connection.commit();
        log.info("Prospect supprimé avec succès : ID={}", id);

      } catch (Exception e) {
        log.warn("Erreur transactionnelle (delete). Rollback en cours...");
        try {
          connection.rollback();
        } catch (SQLException ex) {
          log.error("Échec critique du Rollback !", ex);
        }
        if (e instanceof DaoException) {
          throw (DaoException) e;
        }
        throw new DaoException(DaoException.ErrorCode.TRANSACTION_ERROR,
            "delete", id, "Erreur lors de la suppression", e);
      }
    } catch (SQLException e) {
      log.error("Erreur SQL lors de la suppression", e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e), "delete", id, "Erreur BDD", e);
    }
  }

  /**
   * Recherche un prospect par sa raison sociale (Exact match).
   */
  public Prospect findByRaisonSociale(String raisonSociale) throws DaoException {
    if (raisonSociale == null || raisonSociale.trim().isEmpty()) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER,
          "findByRaisonSociale", null, "La raison sociale est vide");
    }

    String sql =
        """
            SELECT p.id_prospect, p.id_societe, p.date_prospection, p.interesse,
                   s.raison_sociale, s.telephone, s.email, s.commentaires,
                   a.id_adresse, a.numero_rue, a.nom_rue, a.code_postal, a.ville
            FROM prospect p
            INNER JOIN societe s ON p.id_societe = s.id_societe
            INNER JOIN adresse a ON s.adresse_id = a.id_adresse
            WHERE s.raison_sociale = ?
        """;

    try (Connection connection = dbConnexion.getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, raisonSociale);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToProspect(rs);
        }
        return null;
      }
    } catch (SQLException e) {
      log.error("Erreur SQL lors de findByRaisonSociale avec '{}'", raisonSociale, e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e),
          "findByRaisonSociale", null, "Erreur SQL", e);
    }
  }

  /**
   * Vérifie si une adresse est partagée par d'autres sociétés avant suppression.
   */
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

  /**
   * Mappe la ligne actuelle du ResultSet vers une entité Prospect.
   */
  private Prospect mapResultSetToProspect(ResultSet rs)
      throws SQLException {
    Adresse adresse = Adresse.builder()
        .id(rs.getInt("id_adresse"))
        .numeroRue(rs.getString("numero_rue"))
        .nomRue(rs.getString("nom_rue"))
        .codePostal(rs.getString("code_postal"))
        .ville(rs.getString("ville"))
        .build();

    // JDBC 4.2 : Lecture directe en LocalDate !
    LocalDate dateProspection = rs.getObject("date_prospection", LocalDate.class);

    return Prospect.builder()
        .id(rs.getInt("id_prospect"))
        .raisonSociale(rs.getString("raison_sociale"))
        .adresse(adresse)
        .telephone(rs.getString("telephone"))
        .email(rs.getString("email"))
        .commentaires(rs.getString("commentaires"))
        .dateProspection(dateProspection)
        .interesse(Interesse.fromInt(rs.getInt("interesse")))
        .build();
  }
}