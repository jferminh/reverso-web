package com.julio.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe singleton pour gérer le pool de connexions à la base de données MySQL.
 *
 * <p>Utilise HikariCP pour fournir des connexions thread-safe, indispensable
 * pour l'environnement Web Jakarta EE sur Tomcat 11.
 *
 * @author Julio FERMIN
 * @version 3.0
 * @since 20/03/2026
 */
@Slf4j
public class DatabaseConnexion {
  // Instance unique (Singleton)
  private static DatabaseConnexion instance;
  private HikariDataSource dataSource;

  /**
   * Constructeur privé. Charge la configuration et initialise le pool HikariCP.
   *
   * @throws SQLException si la configuration échoue
   */
  private DatabaseConnexion() throws SQLException {
    try {
      Properties properties = new Properties();

      try (InputStream input = getClass().getClassLoader()
          .getResourceAsStream("database.properties")) {
        if (input == null) {
          throw new SQLException("Fichier database.properties introuvable");
        }
        properties.load(input);
      }

      // Configuration de HikariCP
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(properties.getProperty("db.url"));
      config.setUsername(properties.getProperty("db.username"));
      config.setPassword(properties.getProperty("db.password"));
      config.setDriverClassName(properties.getProperty("db.driver"));

      config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.maximumPoolSize")));
      config.setMinimumIdle(Integer.parseInt(properties.getProperty("db.minimumIdle")));
      config.setConnectionTimeout(Integer
          .parseInt(properties.getProperty("db.connectionTimeout")));

      this.dataSource = new HikariDataSource(config);
      log.info("Pool de connexions (DataSource) initialisé avec succès");

    } catch (Exception e) {
      log.error("Erreur lors de l'initialisation du DataSource", e);
      throw new SQLException("Impossible de configurer la base de données", e);
    }
  }

  /**
   * Retourne l'instance unique de DatabaseConnexion.
   *
   * @return l'instance Singleton
   * @throws SQLException en cas d'erreur d'initialisation
   */
  public static synchronized DatabaseConnexion getInstance() throws SQLException {
    if (instance == null) {
      instance = new DatabaseConnexion();
    }
    return instance;
  }

  /**
   * Fournit une connexion exclusive tirée du pool.
   *
   * @return un objet Connection prêt à l'emploi
   * @throws SQLException si aucune connexion n'est disponible
   */
  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  public void closePool() {
    if (dataSource != null && !dataSource.isClosed()) {
      dataSource.close();
      log.info("Pool de connexions (DataSource) fermé");
    }
  }
}
