package com.julio.model;

import com.julio.util.RegexPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JavaBean représentant une adresse postale.
 *
 * @author Julio
 * @version 2.0
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Adresse implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private Integer id;

  @NotBlank(message = "Le numéro de voie est obligatoire")
  private String numeroRue;

  @NotBlank(message = "Le nom de la voie est obligatoire")
  private String nomRue;

  @NotBlank(message = "Le code postal est obligatoire")
  @Pattern(regexp = RegexPatterns.CODE_POSTAL, message = "Code postal invalide (5 chiffres)")
  private String codePostal;

  @NotBlank(message = "La ville est obligatoire")
  private String ville;

}

