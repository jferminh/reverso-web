/**
 * @file form-prospect.js
 * @description Orchestrateur principal de la vue Prospect.
 * Connecte les modules de validation, de brouillon et de géolocalisation.
 * @author Julio
 */

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("form-prospect");
    if (!form) return;

    // IMPORTANT : Clé unique pour que le brouillon Client n'écrase pas le brouillon Prospect !
    const CLE_BROUILLON = "crm-brouillon-prospect";

    // Dictionnaire des messages d'erreur spécifiques au modèle Prospect
    const messagesErreur = {
        "raison-sociale": {
            valueMissing: "La raison sociale est obligatoire pour identifier le prospect.",
            tooShort: "Veuillez saisir au moins 2 caractères."
        },
        "email-prospect": {
            valueMissing: "L'adresse email est requise pour contacter le prospect.",
            typeMismatch: "Format invalide (attendu : nom@domaine.com).",
        },
        "telephone": {
            valueMissing: "Le numéro de contact est obligatoire.",
            patternMismatch: "Le numéro doit comporter exactement 10 chiffres (sans espaces).",
        },
        "date-prospection": {
            valueMissing: "La date de prospection est obligatoire.",
            rangeOverflow: "La date ne peut pas être dans le futur." // Optionnel: dépend de ta config JS
        },
        "interesse": {
            valueMissing: "Veuillez sélectionner le niveau d'intérêt de ce prospect.",
        },
        "numero-rue": {
            valueMissing: "Le numéro de voie est obligatoire.",
        },
        "rue": {
            valueMissing: "Le nom de la rue/voie est obligatoire.",
        },
        "code-postal": {
            valueMissing: "Le code postal est requis.",
            patternMismatch: "Le code postal doit comporter exactement 5 chiffres.",
        },
        "ville": {
            valueMissing: "La ville est obligatoire.",
        },
        "consentement-prospect": {
            valueMissing: "Vous devez consentir au traitement des données pour valider.",
        },
    };

    // ── 1. Initialisation de la Validation RGAA ──
    if (typeof brancherValidation === "function") {
        brancherValidation(messagesErreur);
    }

    // ── 2. Initialisation du Brouillon (Éco-conception & UX) ──
    if (typeof brancherBoutonBrouillon === "function") {
        brancherBoutonBrouillon("btn-brouillon", form, CLE_BROUILLON);
    }
    if (typeof brancherAutoSauvegarde === "function") {
        brancherAutoSauvegarde(form, CLE_BROUILLON);
    }
    if (typeof restaurerAvecEtatVisuel === "function") {
        restaurerAvecEtatVisuel(form, messagesErreur, CLE_BROUILLON);
    }
    if (typeof brancherBoutonAnnuler === "function") {
        brancherBoutonAnnuler("btn-annuler", CLE_BROUILLON);
    }

    // ── 3. Initialisation de la Soumission Sécurisée ──
    if (typeof brancherSoumission === "function") {
        brancherSoumission(form, messagesErreur, CLE_BROUILLON);
    }

    // ── 4. Initialisation de l'API Adresse (Gouv.fr) ──
    if (typeof brancherGeoAdresse === "function") {
        brancherGeoAdresse({
            idNumero: "numero-rue",
            idRue: "rue",
            idCp: "code-postal",
            idVille: "ville",
            idBtnGeo: "btn-geo",
            idSuggestions: "suggestion-adresse",
            messagesErreur: messagesErreur,
        });
    }
});