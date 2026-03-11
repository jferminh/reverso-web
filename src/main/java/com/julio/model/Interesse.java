package com.julio.model;

public enum Interesse {
  OUI,
  NON;

  public String getLibelle() {
    return this == OUI ? "Oui" : "Non";
  }

  public static Interesse fromInt(int value) {
    if (value == 1) {
      return OUI;
    } else if (value == 0) {
      return NON;
    } else {
      throw new IllegalArgumentException(
              "Valeur 'interesse' invalide : " + value + ". Valeurs attendues : 0 (NON) ou 1 (OUI)"
      );
    }
  }

  public int toInt() {
    return this == OUI ? 1 : 0;
  }

  public static Interesse fromString(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("La valeur 'interesse' ne peut pas être null ou vide");
    }

    String normalized = value.trim().toUpperCase();

    // Gestion des valeurs textuelles
    if ("OUI".equals(normalized) || "YES".equals(normalized)) {
      return OUI;
    }
    if ("NON".equals(normalized) || "NO".equals(normalized)) {
      return NON;
    }

    // Gestion des valeurs numériques
    if ("1".equals(normalized)) {
      return OUI;
    }
    if ("0".equals(normalized)) {
      return NON;
    }

    throw new IllegalArgumentException(
            "Valeur 'interesse' invalide : '" + value + "'. Valeurs attendues : OUI, NON, 1, 0"
    );
  }
}
