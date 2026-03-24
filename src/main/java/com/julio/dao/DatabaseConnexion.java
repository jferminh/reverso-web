package com.julio.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe Singleton pour gérer le pool de connexions à la base de données MySQL.
 * <p>
 * Utilise le pattern "Double-Checked Locking" pour des performances maximales
 * en environnement multithread (Tomcat).
 *
 * @author Julio FERMIN
 * @version 3.1
 */
@Slf4j
public class DatabaseConnexion {

  // 1. Le mot-clé 'volatile' est OBLIGATOIRE pour le Double-Checked Locking.
  // Il garantit que la mémoire est synchronisée instantanément entre tous les threads.
  private static volatile DatabaseConnexion instance;

  private final HikariDataSource dataSource; // 'final' car on ne le modifie plus après création

  /**
   * Constructeur privé. Charge la configuration et initialise le pool HikariCP.
   */
  private DatabaseConnexion() throws SQLException {
    try {
      Properties properties = new Properties();

      try (InputStream input = getClass().getClassLoader().getResourceAsStream(
          "database.properties")) {
        if (input == null) {
          throw new SQLException("Fichier database.properties introuvable dans le classpath.");
        }
        properties.load(input);
      }

      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(properties.getProperty("db.url"));
      config.setUsername(properties.getProperty("db.username"));
      config.setPassword(properties.getProperty("db.password"));
      config.setDriverClassName(properties.getProperty("db.driver"));

      config.setMaximumPoolSize(Integer.parseInt(properties.getProperty(
          "db.maximumPoolSize", "10")));
      config.setMinimumIdle(Integer.parseInt(properties.getProperty(
          "db.minimumIdle", "2")));
      config.setConnectionTimeout(Integer.parseInt(properties.getProperty(
          "db.connectionTimeout", "30000")));

      // OPTIMISATION ACID : On s'assure que les connexions ne se ferment pas brutalement
      // Par défaut. Les DAO feront setAutoCommit(false) pour les transactions manuelles.
      config.setAutoCommit(true);

      this.dataSource = new HikariDataSource(config);
      log.info("🚀 Pool HikariCP initialisé avec succès !");

    } catch (Exception e) {
      log.error("❌ Erreur critique lors de l'initialisation du DataSource", e);
      throw new SQLException("Impossible de configurer la base de données", e);
    }
  }

  /**
   * Retourne l'instance unique (Pattern Singleton avec Double-Checked Locking).
   *
   * @return l'instance Singleton
   */
  public static DatabaseConnexion getInstance() throws SQLException {
    // 1ère vérification : pas de verrou (très rapide pour 99% des appels)
    if (instance == null) {
      // Si c'est null, on met un verrou synchronisé juste pour la création
      synchronized (DatabaseConnexion.class) {
        // 2ème vérification : au cas où un autre thread l'aurait créé
        // pendant qu'on attendait le verrou
        if (instance == null) {
          instance = new DatabaseConnexion();
        }
      }
    }
    return instance;
  }

  /**
   * Fournit une connexion exclusive tirée du pool.
   *
   * @return un objet Connection prêt à l'emploi
   */
  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  /**
   * Ferme proprement le pool HikariCP à l'arrêt du serveur Tomcat.
   */
  public void closePool() {
    if (dataSource != null && !dataSource.isClosed()) {
      dataSource.close();
      log.info("🛑 Pool de connexions HikariCP fermé avec succès.");
    }
  }
}