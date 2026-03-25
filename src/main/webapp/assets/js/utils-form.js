/**
 * @file utils-form.js
 * @description Boîte à outils JS pour la validation Front-End et la gestion du cycle de vie des formulaires.
 * @author Julio
 */

/**
 * Traduit l'état de validité natif d'un champ en un message personnalisé.
 */
function getMessageErreur(champ, messagesErreur) {
    const validite = champ.validity;
    const messages = messagesErreur[champ.id];
    if (!messages) return champ.validationMessage;

    const etats = ["valueMissing", "typeMismatch", "patternMismatch", "rangeUnderflow", "tooShort", "badInput"];
    for (const etat of etats) {
        if (validite[etat] && messages[etat]) return messages[etat];
    }
    return champ.validationMessage;
}

/**
 * Gère l'affichage visuel et l'accessibilité (RGAA) d'un champ en erreur.
 */
function afficherErreur(champ, messagesErreur) {
    const zoneErreur = document.getElementById(champ.id + "-erreur");

    if (champ.validity.valid) {
        champ.classList.remove("is-invalid");
        champ.classList.add("is-valid");
        // RGAA : On retire l'état invalide pour le lecteur d'écran
        champ.removeAttribute("aria-invalid");
        if (zoneErreur) {
            zoneErreur.textContent = "";
            zoneErreur.hidden = true;
        }
    } else {
        champ.classList.remove("is-valid");
        champ.classList.add("is-invalid");
        // RGAA : On signale l'erreur au lecteur d'écran
        champ.setAttribute("aria-invalid", "true");
        if (zoneErreur) {
            zoneErreur.textContent = getMessageErreur(champ, messagesErreur);
            zoneErreur.hidden = false;
        }
    }
}

/**
 * Connecte les écouteurs d'événements pour la validation en temps réel (Blur & Input).
 */
function brancherValidation(messagesErreur) {
    Object.keys(messagesErreur).forEach(id => {
        const champ = document.getElementById(id);
        if (!champ) return;

        // On valide quand l'utilisateur quitte le champ
        champ.addEventListener("blur", () => afficherErreur(champ, messagesErreur));

        // On valide en direct uniquement si le champ était déjà en erreur (pour un retour rapide)
        champ.addEventListener("input", () => {
            if (champ.classList.contains("is-invalid")) afficherErreur(champ, messagesErreur);
        });
    });
}

// =========================================================================
// GESTION DU CYCLE DE VIE (Brouillons & Soumission)
// =========================================================================

function mettreAJourBadgeBrouillon(statut) {
    const badge = document.querySelector("[data-badge-brouillon]");
    if (!badge) return;

    if (statut === "en-cours") {
        badge.textContent = "⏳ Sauvegarde…";
        badge.className = "badge bg-warning text-dark border";
    } else if (statut === "sauvegarde") {
        badge.textContent = "✅ Sauvegardé";
        badge.className = "badge bg-success text-white border";
        setTimeout(() => {
            badge.textContent = "📄 Brouillon auto";
            badge.className = "badge bg-light text-secondary border";
        }, 2000);
    }
}

function brancherBoutonBrouillon(btnId, form, CLE_BROUILLON) {
    const btn = document.getElementById(btnId);
    if (!btn) return;

    btn.addEventListener("click", () => {
        sauvegarderBrouillon(CLE_BROUILLON, lireFormulaire(form));
        afficherConfirmationBrouillon(btn);
    });
}

/**
 * Active la sauvegarde automatique du formulaire.
 * Implémente le pattern "Debouncing" pour l'éco-conception.
 */
function brancherAutoSauvegarde(form, CLE_BROUILLON) {
    // 1. Sauvegarde régulière de sécurité toutes les 30s
    setInterval(() => {
        const donnees = lireFormulaire(form);
        const aucuneDonnee = Object.values(donnees).every(v => v === "" || v === false);
        if (!aucuneDonnee) {
            sauvegarderBrouillon(CLE_BROUILLON, donnees);
            mettreAJourBadgeBrouillon("sauvegarde");
        }
    }, 30000);

    // 2. ÉCO-CONCEPTION (Debouncing) : On attend 1 seconde d'inactivité avant de sauvegarder.
    // Cela évite de lancer 50 sauvegardes si l'utilisateur tape 50 lettres rapidement.
    form.addEventListener("input", () => {
        clearTimeout(form._debounceTimer);
        mettreAJourBadgeBrouillon("en-cours");

        form._debounceTimer = setTimeout(() => {
            sauvegarderBrouillon(CLE_BROUILLON, lireFormulaire(form));
            mettreAJourBadgeBrouillon("sauvegarde");
        }, 1000);
    });
}

function restaurerAvecEtatVisuel(form, messagesErreur, CLE_BROUILLON) {
    const brouillonSauvegarde = lireBrouillon(CLE_BROUILLON);
    if (!brouillonSauvegarde) return;

    restaurerFormulaire(form, brouillonSauvegarde);

    // Revalide visuellement les champs restaurés
    Object.keys(messagesErreur).forEach(id => {
        const champ = document.getElementById(id);
        if (champ && champ.value.trim() !== "") afficherErreur(champ, messagesErreur);
    });

    afficherBanniereBrouillon(form, brouillonSauvegarde._sauvegardeLe || "inconnue");
}

function brancherBoutonAnnuler(btnId, CLE_BROUILLON) {
    const btn = document.getElementById(btnId);
    if (!btn) return;

    btn.addEventListener("click", (evenement) => {
        if (lireBrouillon(CLE_BROUILLON)) {
            evenement.preventDefault();
            if (window.confirm("Vous avez un brouillon en cours.\nVoulez-vous vraiment quitter sans enregistrer et perdre vos saisies ?")) {
                effacerBrouillon(CLE_BROUILLON);
                window.location.href = btn.href;
            }
        }
    });
}

/**
 * Intercepte la soumission, valide tous les champs, et gère le focus RGAA.
 */
function brancherSoumission(form, messagesErreur, CLE_BROUILLON) {
    form.addEventListener("submit", (evenement) => {
        let formulaireValide = true;
        let premierChampInvalide = null;

        // Validation intégrale de tous les champs suivis
        Object.keys(messagesErreur).forEach(id => {
            const champ = document.getElementById(id);
            if (!champ) return;

            afficherErreur(champ, messagesErreur);

            if (!champ.validity.valid) {
                formulaireValide = false;
                if (!premierChampInvalide) premierChampInvalide = champ; // On garde le 1er en mémoire
            }
        });

        // Comportement en cas d'échec
        if (!formulaireValide) {
            evenement.preventDefault(); // ⛔ On bloque l'envoi au serveur

            // RGAA : On force le curseur sur la première erreur pour assister l'utilisateur
            if (premierChampInvalide) {
                premierChampInvalide.focus();
            }
            return;
        }

        // ✅ TOUT EST VALIDE : On efface le brouillon (puisqu'on envoie en base)
        effacerBrouillon(CLE_BROUILLON);
        // Le navigateur prend le relai et exécute la requête HTTP POST !
    });
}

// =========================================================================
// ORCHESTRATEUR GÉNÉRIQUE (DRY)
// =========================================================================

/**
 * Initialise n'importe quel formulaire CRM en branchant tous les modules
 * (Validation, Brouillon, Soumission, Geo-Adresse).
 * * @param {Object} config - Configuration spécifique du formulaire.
 * @param {string} config.idForm - L'ID HTML de la balise <form>.
 * @param {string} config.cleBrouillon - La clé LocalStorage unique (ex: "brouillon-client").
 * @param {Object} config.messagesErreur - Le dictionnaire des messages de validation.
 */
function initialiserFormulaire(config) {
    const form = document.getElementById(config.idForm);
    if (!form) return;

    // 1. Validation visuelle
    if (typeof brancherValidation === "function") {
        brancherValidation(config.messagesErreur);
    }

    // 2. Gestion du Brouillon (LocalStorage)
    if (typeof brancherBoutonBrouillon === "function") {
        brancherBoutonBrouillon("btn-brouillon", form, config.cleBrouillon);
    }
    if (typeof brancherAutoSauvegarde === "function") {
        brancherAutoSauvegarde(form, config.cleBrouillon);
    }
    if (typeof restaurerAvecEtatVisuel === "function") {
        restaurerAvecEtatVisuel(form, config.messagesErreur, config.cleBrouillon);
    }
    if (typeof brancherBoutonAnnuler === "function") {
        brancherBoutonAnnuler("btn-annuler", config.cleBrouillon);
    }

    // 3. Soumission
    if (typeof brancherSoumission === "function") {
        brancherSoumission(form, config.messagesErreur, config.cleBrouillon);
    }

    // 4. API Adresse (Les IDs d'adresse sont identiques entre Client et Prospect)
    if (typeof brancherGeoAdresse === "function") {
        brancherGeoAdresse({
            idNumero: "numero-rue",
            idRue: "rue",
            idCp: "code-postal",
            idVille: "ville",
            idBtnGeo: "btn-geo",
            idSuggestions: "suggestion-adresse",
            messagesErreur: config.messagesErreur,
        });
    }
}