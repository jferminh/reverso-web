package com.julio.dao;

import com.julio.exception.DaoException;
import com.julio.exception.ValidationException;
import com.julio.model.Adresse;
import com.julio.model.Client;
import com.julio.model.Contrat;
import com.julio.util.SqlExceptionAnalyzer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * DAO pour la gestion de la persistance des clients.
 *
 * <p>Gère les opérations CRUD sur la table {@code client} et les tables
 * associées ({@code societe}, {@code adresse}) via transactions ACID.
 * Utilise une méthode unique {@link #save(Client)} pour la création et la mise à jour.
 *
 * @author Julio FERMIN
 * @version 3.0 (Optimisé avec SLF4J et try-with-resources)
 * @since 20/03/2026
 */
@Slf4j
public class ClientDao extends SocieteDao {

  private final ContratDao contratDao;
  private static ClientDao instance;

  /**
   * Constructeur qui récupère l'instance de DatabaseConnection.
   * Initialise également le ContratDao pour gérer les contrats associés.
   *
   * @throws DaoException si la connexion à la base de données échoue
   */
  public ClientDao() throws DaoException {
    super();
    this.contratDao = new ContratDao();

  }

  /**
   * Méthode synchronisée de l'instance.
   *
   * @return intance
   * @throws DaoException DAO exception
   */
  public static synchronized ClientDao getInstance() throws DaoException {
    if (instance == null) {
      instance = new ClientDao();
    }
    return instance;
  }

  /**
   * Récupère tous les clients de la base de données avec leurs adresses et contrats.
   *
   * <p>Effectue une jointure entre les tables societe, client, adresse et contrat
   * pour récupérer toutes les informations en une seule requête.
   *
   * @return une liste de tous les clients
   * @throws DaoException si une erreur survient lors de la requête
   */
  public List<Client> findAll() throws DaoException {
    Map<Integer, Client> clientsMap = new LinkedHashMap<>();
    String sql =
        """
        SELECT s.id_societe, s.raison_sociale, a.id_adresse, s.telephone, s.email,
        s.commentaires, c.id_client, c.chiffre_affaires, c.nb_employes,
        a.numero_rue, a.nom_rue, a.code_postal, a.ville, ct.id_contrat, 
        ct.nom_contrat, ct.montant 
        FROM societe s 
        INNER JOIN client c ON s.id_societe = c.id_societe 
        INNER JOIN adresse a ON s.adresse_id = a.id_adresse
        LEFT JOIN contrat ct ON c.id_client = ct.client_id
        ORDER BY s.raison_sociale ASC
        """;

    try (Connection conn = dbConnexion.getConnection();
    PreparedStatement stmt = conn.prepareStatement(sql);
    ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        Integer clientId = rs.getInt("id_client");
        Client client = clientsMap.computeIfAbsent(clientId, id -> {
          try {
            return mapResultSetToClient(rs);
          } catch (Exception ex) {
            log.error("Erreur mapping client ID={}", id, ex);
            return null;
          }
        });

        if (client == null) {
          Integer contratId = rs.getInt("id_contrat");
          if (!rs.wasNull() && contratId > 0) {
            try {
              Contrat contrat = new Contrat(clientId, rs.getString("nom_contrat")
                  , rs.getDouble("montant"));
              contrat.setId(contratId);
              client.ajouterContrat(contrat);
            } catch (ValidationException ex) {
              log.warn("Contrat invalide ignoré pour client ID={}", clientId);
            }
          }
        }
      }
      return new ArrayList<>(clientsMap.values());
    } catch (SQLException ex) {
      log.error("Erreur SQL dans findAll", ex);
      throw new DaoException(SqlExceptionAnalyzer.categorize(ex), "findAll"
      , null, "Erreur récupération", ex);
    }
  }

  /**
   * Méthode UNIQUE pour insérer (create) ou mettre à jour (update) un client.
   *
   * @param client le client à sauvegarder
   * @return le client avec son ID à jour
   * @throws DaoException en cas d'erreur
   */
  public Client save(Client client) throws DaoException {
    if (client == null) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER
          , "save", null, "Le client ne peut pas être null");
    }

    // Déterminer si c'est une création (ID null ou <= 0)
    boolean isNew = (client.getId() == null || client.getId() <= 0);

    // La connexion est obtenue et fermée automatiquement (retournée au pool).
    try (Connection connection = dbConnexion.getConnection()) {
      connection.setAutoCommit(false); // Début de la transaction

      try {
        if (isNew) {
          // =================== LOGIQUE CREATE =====================
          Integer societeId = createSociete(client, connection);

          String sql =
          """
          INSERT INTO client (id_societe, chiffre_affaires, nb_employes) 
          VALUES (?, ?, ?)
          """;
          try (PreparedStatement pstmt = connection.prepareStatement(sql
              , Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, societeId);
            pstmt.setLong(2, client.getChiffreAffaires());
            pstmt.setInt(3, client.getNbEmployes());

            if (pstmt.executeUpdate() == 0)
              throw new SQLException("L'insertion du client a échoué");

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
              if (rs.next()) client.setId(rs.getInt(1));
              else throw new SQLException("Aucun ID généré pour le client");
            }
          }
        } else {
          // ================== LOGIQUE UPDATE ==================
          Integer societeId = null;
          String getSocieteSql = "SELECT id_societe FROM client WHERE id_client = ?";

          try (PreparedStatement pstmt = connection.prepareStatement(getSocieteSql)) {
            pstmt.setInt(1, client.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
              if (rs.next()) societeId = rs.getInt("id_societe");
              else throw new DaoException(DaoException.ErrorCode.ENTITY_NOT_FOUND
                  , "save", client.getId(), "Client introuvable");
            }
          }

          // Mise à jour Adresse (si nécessaire et si AdresseDao supporte la transaction)
          if (client.getAdresse() != null && client.getAdresse().getId() != null) {
            adresseDao.save(client.getAdresse(), connection);
          }

          // Mise à jour Société
          saveSociete(client, societeId, connection);

          // Mise à jour Client
          String sql =
          """
          UPDATE client SET chiffre_affaires = ?, nb_employes = ? 
          WHERE id_client = ?
          """;
          try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, client.getChiffreAffaires());
            pstmt.setInt(2, client.getNbEmployes());
            pstmt.setInt(3, client.getId());
            if (pstmt.executeUpdate() == 0) throw new SQLException("Mise à jour client échouée");
          }
        }

        connection.commit(); // ✅ Succès total
        log.info("Client {} avec succès : ID={}", isNew ? "créé" : "mis à jour", client.getId());
        return client;

      } catch (Exception e) {
        connection.rollback(); // ❌ Annulation en cas d'erreur
        log.warn("Rollback effectué suite à une erreur : {}", e.getMessage());
        throw e;
      }
    } catch (SQLException e) {
      log.error("Erreur SQL lors du save() du client", e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e), "save", client.getId(), "Erreur base de données", e);
    }
  }

  /**
   * Supprime un client de la base de données.
   *
   * <p>Cette méthode effectue une transaction qui :</p>
   * <ol>
   *   <li>Vérifie que le client existe</li>
   *   <li>Vérifie qu'il n'a pas de contrats (sinon erreur)</li>
   *   <li>Supprime le client</li>
   *   <li>Supprime la société associée</li>
   *   <li>Supprime l'adresse si elle n'est plus référencée</li>
   * </ol>
   *
   * @param id l'ID du client à supprimer
   * @throws DaoException si une erreur survient ou si le client a des contrats
   */
  public void delete(Integer id) throws DaoException {
    if (id == null || id <= 0) {
      throw new DaoException(DaoException.ErrorCode.INVALID_PARAMETER
          , "delete", id, "ID invalide");
    }

    try (Connection connection = dbConnexion.getConnection()) {
      connection.setAutoCommit(false);
      try {
        Integer societeId = null;
        Integer adresseId = null;

        String getIdsSql =
        """
        SELECT c.id_societe, s.adresse_id FROM client c 
        INNER JOIN societe s ON c.id_societe = s.id_societe 
        WHERE c.id_client = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(getIdsSql)) {
          pstmt.setInt(1, id);
          try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
              societeId = rs.getInt("id_societe");
              adresseId = rs.getInt("adresse_id");
            } else {
              throw new DaoException(DaoException.ErrorCode.ENTITY_NOT_FOUND
                  , "delete", id, "Client introuvable");
            }
          }
        }

        int nbContrats = countContratsByClientId(connection, id);
        if (nbContrats > 0) {
          throw new DaoException(DaoException.ErrorCode.FOREIGN_KEY_VIOLATION
              , "delete", id, "Impossible: le client a des contrats");
        }

        try (PreparedStatement pstmt =
                 connection.prepareStatement("DELETE FROM client WHERE id_client = ?")) {
          pstmt.setInt(1, id);
          if (pstmt.executeUpdate() == 0) throw new SQLException("Aucune ligne supprimée");
        }

        deleteSociete(connection, societeId);

        if (!isAdresseReferencee(connection, adresseId)) {
          adresseDao.deleteAdresse(connection, adresseId);
        }

        connection.commit();
        log.info("Client supprimé avec succès : ID={}", id);

      } catch (Exception e) {
        connection.rollback();
        log.warn("Rollback lors de la suppression du client ID={}", id);
        throw e;
      }
    } catch (SQLException e) {
      log.error("Erreur SQL lors de la suppression", e);
      throw new DaoException(SqlExceptionAnalyzer.categorize(e), "delete", id, "Erreur suppression", e);
    }
  }

  // ========== MÉTHODES PRIVÉES UTILITAIRES ==========

  /**
   * Compte le nombre de contrats associés à un client.
   *
   * <p>Cette méthode est utilisée pour vérifier si un client peut être supprimé.</p>
   *
   * @param connection la connexion à utiliser (transaction en cours)
   * @param clientId   l'ID du client
   * @return le nombre de contrats associés au client
   * @throws SQLException si une erreur survient
   */
  private int countContratsByClientId(Connection connection, Integer clientId)
      throws SQLException {
    try (PreparedStatement pstmt = connection.prepareStatement(
        "SELECT COUNT(*) AS nb FROM contrat WHERE client_id = ?")) {
      pstmt.setInt(1, clientId);
      try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next() ? rs.getInt("nb") : 0;
      }
    }
  }

  /**
   * Vérifie si une adresse est encore référencée par d'autres sociétés.
   *
   * @param connection la connexion à utiliser (transaction en cours)
   * @param adresseId  l'ID de l'adresse
   * @return true si l'adresse est encore référencée, false sinon
   * @throws SQLException si une erreur survient
   */
  private boolean isAdresseReferencee(Connection connection, Integer adresseId)
      throws SQLException {
    if (adresseId == null) return false;

    try (PreparedStatement pstmt = connection.prepareStatement(
        "SELECT COUNT(*) AS nb FROM societe WHERE adresse_id = ?")) {
      pstmt.setInt(1, adresseId);
      try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next() && rs.getInt("nb") > 0;
      }
    }
  }

  /**
   * Mappe un ResultSet vers un objet Client.
   *
   * @param rs le ResultSet positionné sur une ligne client
   * @return le client mappé
   * @throws SQLException si erreur d'accès aux données du ResultSet
   * @throws DaoException si les données sont invalides (ValidationException encapsulée)
   */
  private Client mapResultSetToClient(ResultSet rs) throws SQLException, DaoException, ValidationException {
    Adresse adresse = Adresse.builder()
        .numeroRue(rs.getString("numero_rue"))
        .nomRue(rs.getString("nom_rue"))
        .codePostal(rs.getString("code_postal"))
        .ville(rs.getString("ville"))
        .build();
    adresse.setId(rs.getInt("id_adresse"));

    Client client = Client.builder()
        .raisonSociale(rs.getString("raison_sociale"))
        .adresse(adresse)
        .telephone(rs.getString("telephone"))
        .email(rs.getString("email"))
        .commentaires(rs.getString("commentaires"))
        .chiffreAffaires(rs.getLong("chiffre_affaires"))
        .nbEmployes(rs.getInt("nb_employes"))
        .build();
    client.setId(rs.getInt("id_client"));
    return client;
  }

}
