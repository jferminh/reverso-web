package com.julio.model;

import com.julio.exception.ValidationException;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class Prospect extends Societe implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private LocalDate dateProspection;

  private Interesse interesse;

  public Prospect(String raisonSociale, Adresse adresse, String telephone,
                  String email, String commentaires, LocalDate dateProspection,
                  Interesse interesse) throws ValidationException {
    super(raisonSociale, adresse, telephone, email, commentaires);
    setDateProspection(dateProspection);
    setInteresse(interesse);
  }

  public Prospect(String raisonSociale, Adresse adresse, String telephone, String email, String commentaires) throws ValidationException {
    super(raisonSociale, adresse, telephone, email, commentaires);
  }

  public LocalDate getDateProspection() {
    return dateProspection;
  }

  public void setDateProspection(LocalDate dateProspection) {
    this.dateProspection = dateProspection;
  }

  public Interesse getInteresse() {
    return interesse;
  }

  public void setInteresse(Interesse interesse) {
    this.interesse = interesse;
  }

  @Override
  public String getTypeSociete() {
    return "Prospect";
  }

  @Override
  public String toString() {
    return getRaisonSociale() + " (Prospect)";
  }

}
