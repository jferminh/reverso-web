package com.julio.config;

import com.julio.dao.DatabaseConnexion;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

/**
 * Écouteur du cycle de vie de l'application (Jakarta EE).
 * S'exécute au démarrage (déploiement) et à l'arrêt (undeploy) du serveur Tomcat.
 */
@Slf4j
@WebListener // Tomcat détecte automatiquement cette classe grâce à cette annotation !
public class AppLifecycleListener implements ServletContextListener {
  private ValidatorFactory validatorFactory;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    log.info("🚀 Démarrage de Reverso CRM... Initialisation des ressources globales.");

    // 1. Initialisation du Validator
    try {
      validatorFactory = Validation.buildDefaultValidatorFactory();
      Validator validator = validatorFactory.getValidator();

      // On le place dans le "coffre-fort" de l'application (ServletContext)
      sce.getServletContext().setAttribute("validator", validator);
      log.info("✅ Bean Validator global initialisé et injecté dans le contexte.");

    } catch (Exception e) {
      log.error("❌ Erreur critique lors de l'initialisation du Validator", e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    log.info("🛑 Arrêt de Reverso CRM... Nettoyage de la mémoire.");

    // 1. Fermeture propre de la fabrique de validation
    if (validatorFactory != null) {
      validatorFactory.close();
      log.info("🧹 ValidatorFactory fermée.");
    }

    // 2. Fermeture propre du pool de connexions à la base de données !
    // Cela évite le fameux message rouge de Tomcat : "Memory Leak / Fuite de mémoire détectée."
    try {
      DatabaseConnexion.getInstance().closePool();
    } catch (SQLException e) {
      log.error("Erreur lors de la fermeture du pool HikariCP", e);
    }
  }
}
