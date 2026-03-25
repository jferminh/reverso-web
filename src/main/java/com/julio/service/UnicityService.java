package com.julio.service;

import com.julio.dao.ClientDao;
import com.julio.dao.ProspectDao;
import com.julio.exception.DaoException;
import com.julio.model.Client;
import com.julio.model.Prospect;
import lombok.extern.slf4j.Slf4j;

/**
 * Service transverse (Métier) dédié à la vérification de l'unicité des données.
 *
 * <p>Implémente le principe de Responsabilité Unique (SRP). Il garantit qu'aucune
 * société (Client ou Prospect) ne puisse être créée avec une raison sociale
 * déjà existante dans le système, évitant ainsi les collisions.
 * </p>
 *
 * @author Julio
 * @version 3.1
 */
@Slf4j
public class UnicityService {

  private final ClientDao clientDao;
  private final ProspectDao prospectDao;

  /**
   * Constructeur avec injection des dépendances (Pattern Dependency Injection).
   */
  public UnicityService(ClientDao clientDao, ProspectDao prospectDao) {
    if (clientDao == null || prospectDao == null) {
      throw new IllegalArgumentException("Les DAO injectés ne peuvent pas être null");
    }
    this.clientDao = clientDao;
    this.prospectDao = prospectDao;
  }

  /**
   * Vérifie l'unicité lors de la CRÉATION d'une nouvelle entité.
   *
   * @param raisonSociale La raison sociale à vérifier.
   * @return true si un doublon existe (chez les clients ou les prospects), false sinon.
   * @throws DaoException En cas de problème d'accès à la base de données.
   */
  public boolean isRaisonSocialeDupliquee(String raisonSociale) throws DaoException {
    if (raisonSociale == null || raisonSociale.trim().isEmpty()) {
      throw new IllegalArgumentException("La raison sociale ne peut pas être null ou vide");
    }
    return (clientDao.findByRaisonSociale(raisonSociale) != null)
        || (prospectDao.findByRaisonSociale(raisonSociale) != null);
  }

  /**
   * Vérifie l'unicité lors de la MODIFICATION d'un CLIENT.
   *
   * <p>OPTIMISATION : Seulement 2 requêtes SQL maximum exécutées.</p>
   *
   * @param raisonSociale La raison sociale soumise dans le formulaire.
   * @param clientIdAExclure L'ID du client en cours de modification.
   * @return true si un doublon est détecté, false si le nom est disponible.
   * @throws DaoException En cas d'erreur de base de données.
   */
  public boolean isRaisonSocialeDupliqueePourClient(
      String raisonSociale, Integer clientIdAExclure) throws DaoException {

    // 1. Vérification côté Client
    Client clientExistant = clientDao.findByRaisonSociale(raisonSociale);
    if (clientExistant != null && !clientExistant.getId().equals(clientIdAExclure)) {
      return true; // Un AUTRE client porte déjà ce nom
    }

    // 2. Vérification côté Prospect (Un client ne peut jamais écraser le nom d'un prospect)
    Prospect prospectExistant = prospectDao.findByRaisonSociale(raisonSociale);
    return prospectExistant != null;
  }

  /**
   * Vérifie l'unicité lors de la MODIFICATION d'un PROSPECT.
   */
  public boolean isRaisonSocialeDupliqueePourProspect(String raisonSociale, Integer prospectIdAExclure) throws DaoException {

    // 1. Vérification côté Prospect
    Prospect prospectExistant = prospectDao.findByRaisonSociale(raisonSociale);
    if (prospectExistant != null && !prospectExistant.getId().equals(prospectIdAExclure)) {
      return true; // Un AUTRE prospect porte déjà ce nom
    }

    // 2. Vérification côté Client
    Client clientExistant = clientDao.findByRaisonSociale(raisonSociale);
    return clientExistant != null;
  }
}