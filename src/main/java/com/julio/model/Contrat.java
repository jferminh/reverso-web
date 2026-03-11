package com.julio.model;

import com.julio.exception.ValidationException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Contrat implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Identifiant unique du contrat
   */
  private Integer id;

  /**
   * Identifiant du client auquel le contrat est associé (doit être > 0)
   */
  private Integer clientId;

  /**
   * Nom ou désignation du contrat (obligatoire)
   */
  private String nomContrat;

  /**
   * Montant financier du contrat en euros (doit être > 0)
   */
  private double montant;

  public Contrat(Integer clientId, String nomContrat, double montant) throws ValidationException {
    this.id = null;
    setClientId(clientId);
    setNomContrat(nomContrat);
    setMontant(montant);
  }

  public Contrat() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getClientId() {
    return clientId;
  }

  public void setClientId(Integer clientId) {
    this.clientId = clientId;
  }

  public String getNomContrat() {
    return nomContrat;
  }

  public void setNomContrat(String nomContrat) {
    this.nomContrat = nomContrat;
  }

  public double getMontant() {
    return montant;
  }

  public void setMontant(double montant) {
    this.montant = montant;
  }

  public String toString() {
    return nomContrat + " (" + montant + "€)";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Contrat)) return false;
    Contrat contrat = (Contrat) o;
    return Objects.equals(id, contrat.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
