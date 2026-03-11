package com.julio.model;

import com.julio.exception.ValidationException;

import java.io.Serial;
import java.io.Serializable;

public abstract class Societe implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private Integer id;
  private String raisonSociale;
  private Adresse adresse;
  private String telephone;
  private String email;
  private String commentaires;

  public Societe(String raisonSociale, Adresse adresse, String telephone,
                 String email, String commentaires) throws ValidationException {
    setRaisonSociale(raisonSociale);
    setAdresse(adresse);
    setTelephone(telephone);
    setEmail(email);
    this.commentaires = commentaires;
  }

  public Societe() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getRaisonSociale() {
    return raisonSociale;
  }

  public void setRaisonSociale(String raisonSociale) {
    this.raisonSociale = raisonSociale;
  }

  public Adresse getAdresse() {
    return adresse;
  }

  public void setAdresse(Adresse adresse) {
    this.adresse = adresse;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCommentaires() {
    return commentaires;
  }

  public void setCommentaires(String commentaires) {
    this.commentaires = commentaires;
  }

  public abstract String getTypeSociete();

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Societe{");
    sb.append("id=").append(id);
    sb.append(", raisonSociale='").append(raisonSociale).append('\'');
    sb.append(", adresse=").append(adresse);
    sb.append(", telephone='").append(telephone).append('\'');
    sb.append(", email='").append(email).append('\'');
    sb.append(", commentaires='").append(commentaires).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
