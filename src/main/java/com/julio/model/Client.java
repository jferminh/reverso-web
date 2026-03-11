package com.julio.model;

import com.julio.exception.ValidationException;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Client extends Societe implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Chiffre d'affaires du client en euros (minimum 200)
   */
  private long chiffreAffaires;

  /**
   * Nombre d'employés du client (minimum 1)
   */
  private int nbEmployes;

  /**
   * Liste des contrats associés au client
   */
  private List<Contrat> contrats;

  public Client(String raisonSociale, Adresse adresse, String telephone,
                String email, String commentaires, long chiffreAffaires,
                int nbEmployes) throws ValidationException {
    super(raisonSociale, adresse, telephone, email, commentaires);
    setChiffreAffaires(chiffreAffaires);
    setNbEmployes(nbEmployes);
    this.contrats = new ArrayList<>();
  }

  public Client(String raisonSociale, Adresse adresse, String telephone, String email, String commentaires)
          throws ValidationException {
    super(raisonSociale, adresse, telephone, email, commentaires);
  }

  public long getChiffreAffaires() {
    return chiffreAffaires;
  }

  public void setChiffreAffaires(long chiffreAffaires) {
    this.chiffreAffaires = chiffreAffaires;
  }

  public int getNbEmployes() {
    return nbEmployes;
  }

  public void setNbEmployes(int nbEmployes) {
    this.nbEmployes = nbEmployes;
  }

  public void setContrats(List<Contrat> contrats) {
    this.contrats = contrats;
  }

  public List<Contrat> getContrats() {
    return new ArrayList<>(contrats);
  }

  public void ajouterContrat(Contrat contrat) {
    if (contrat != null && !contrats.contains(contrat)) {
      contrats.add(contrat);
    }
  }

  @Override
  public String toString() {
    return getRaisonSociale() + " (Client)";
  }

  @Override
  public String getTypeSociete() {
    return "Client";
  }
}
