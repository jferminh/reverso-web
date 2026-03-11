package com.julio.model;

import com.julio.exception.ValidationException;

import java.io.Serial;
import java.io.Serializable;

public class Adresse implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  //    private int id;
  private Integer id;

  /**
   * Numéro dans la rue (peut inclure bis, ter, etc.). Obligatoire
   */
  private String numeroRue;

  /**
   * Nom de la voie (rue, avenue, boulevard, etc.). Obligatoire
   */
  private String nomRue;

  /**
   * Code postal français à 5 chiffres - obligatoire et validé
   */
  private String codePostal;

  /**
   * Nom de la ville - obligatoire
   */
  private String ville;

  public Adresse(String numeroRue, String nomRue, String codePostal, String ville) throws ValidationException {
    this.id = null; // ID null avant insertion en BDD
    setNumeroRue(numeroRue);
    setNomRue(nomRue);
    setCodePostal(codePostal);
    setVille(ville);
  }

  public Adresse() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getNumeroRue() {
    return numeroRue;
  }

  public void setNumeroRue(String numeroRue) {
    this.numeroRue = numeroRue;
  }

  public String getNomRue() {
    return nomRue;
  }

  public void setNomRue(String nomRue) {
    this.nomRue = nomRue;
  }

  public String getCodePostal() {
    return codePostal;
  }

  public void setCodePostal(String codePostal) {
    this.codePostal = codePostal;
  }

  public String getVille() {
    return ville;
  }

  public void setVille(String ville) {
    this.ville = ville;
  }

  @Override
  public String toString() {
    return numeroRue + " " + nomRue + " " + codePostal + " " + ville;
  }


}
