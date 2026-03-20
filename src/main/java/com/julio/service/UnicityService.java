package com.julio.service;

import com.julio.dao.ClientDao;
import com.julio.dao.ProspectDao;
import com.julio.exception.DaoException;
import com.julio.model.Client;
import com.julio.model.Prospect;
import lombok.extern.slf4j.Slf4j;

/**
 * Service de vérification de l'unicité des données métier.
 *
 * <p>Ce service vérifie l'unicité de la raison sociale à travers
 * les tables Client et Prospect pour éviter les doublons dans le système.
 *
 * @author Julio FERMIN
 * @version 3.0 (Optimisé avec SLF4J et correction de collision d'ID)
 */
@Slf4j
public class UnicityService {

  private final ClientDao clientDao;
  private final ProspectDao prospectDao;

  /**
   * Constructeur avec injection des DAO.
   */
  public UnicityService(ClientDao clientDao, ProspectDao prospectDao) {
    if (clientDao == null || prospectDao == null) {
      throw new IllegalArgumentException("Les DAO ne peuvent pas être null");
    }
    this.clientDao = clientDao;
    this.prospectDao = prospectDao;
  }

  /**
   * Vérifie si une raison sociale existe déjà (Idéal pour la CRÉATION).
   *
   * @param raisonSociale la raison sociale à vérifier
   * @return true si un doublon existe, false sinon
   * @throws DaoException si une erreur survient
   */
  public boolean isRaisonSocialeDupliquee(String raisonSociale) throws DaoException {
    if (raisonSociale == null || raisonSociale.trim().isEmpty()) {
      throw new IllegalArgumentException("La raison sociale ne peut pas être null ou vide");
    }

    try {
      return (prospectDao.findByRaisonSociale(raisonSociale) != null) ||
          (clientDao.findByRaisonSociale(raisonSociale) != null);
    } catch (DaoException e) {
      log.error("Erreur lors de la vérification unicité raison sociale '{}'", raisonSociale, e);
      throw e;
    }
  }

  /**
   * Vérifie l'unicité lors de la MODIFICATION d'un CLIENT.
   *
   * @param raisonSociale la raison sociale à vérifier
   * @param clientIdAExclure l'ID du client en cours de modification
   * @return true si doublon, false sinon
   */
  public boolean isRaisonSocialeDupliqueePourClient(String raisonSociale, Integer clientIdAExclure) throws DaoException {
    if (isRaisonSocialeDupliquee(raisonSociale)) {
      // Si on trouve un doublon, on vérifie si c'est EXACTEMENT le client qu'on modifie
      Client clientExistant = clientDao.findByRaisonSociale(raisonSociale);

      // S'il existe un client avec ce nom, et que ce n'est PAS le nôtre -> Doublon !
      if (clientExistant != null && !clientExistant.getId().equals(clientIdAExclure)) {
        return true;
      }

      // S'il existe un prospect avec ce nom -> Doublon direct ! (Un prospect ne peut pas avoir l'ID d'un client)
      Prospect prospectExistant = prospectDao.findByRaisonSociale(raisonSociale);
      if (prospectExistant != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * Vérifie l'unicité lors de la MODIFICATION d'un PROSPECT.
   *
   * @param raisonSociale la raison sociale à vérifier
   * @param prospectIdAExclure l'ID du prospect en cours de modification
   * @return true si doublon, false sinon
   */
  public boolean isRaisonSocialeDupliqueePourProspect(String raisonSociale, Integer prospectIdAExclure) throws DaoException {
    if (isRaisonSocialeDupliquee(raisonSociale)) {

      Prospect prospectExistant = prospectDao.findByRaisonSociale(raisonSociale);
      if (prospectExistant != null && !prospectExistant.getId().equals(prospectIdAExclure)) {
        return true;
      }

      Client clientExistant = clientDao.findByRaisonSociale(raisonSociale);
      if (clientExistant != null) {
        return true;
      }
    }
    return false;
  }
}