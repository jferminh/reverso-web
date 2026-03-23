/**
 * utils-form.js
 * Fonctions génériques de validation partagées.
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

function afficherErreur(champ, messagesErreur) {
    const zoneErreur = document.getElementById(champ.id + "-erreur");
    if (champ.validity.valid) {
        champ.classList.remove("is-invalid");
        champ.classList.add("is-valid");
        champ.removeAttribute("aria-invalid");
        if (zoneErreur) { zoneErreur.textContent = ""; zoneErreur.hidden = true; }
    } else {
        champ.classList.remove("is-valid");
        champ.classList.add("is-invalid");
        champ.setAttribute("aria-invalid", "true");
        if (zoneErreur) { zoneErreur.textContent = getMessageErreur(champ, messagesErreur); zoneErreur.hidden = false; }
    }
}

function brancherValidation(messagesErreur) {
    Object.keys(messagesErreur).forEach(id => {
        const champ = document.getElementById(id);
        if (!champ) return;
        champ.addEventListener("blur", () => afficherErreur(champ, messagesErreur));
        champ.addEventListener("input", () => {
            if (champ.classList.contains("is-invalid")) afficherErreur(champ, messagesErreur);
        });
    });
}

// ── Gestion Visuelle Brouillon ──
function mettreAJourBadgeBrouillon(statut) {
    const badge = document.querySelector("[data-badge-brouillon]");
    if (!badge) return;
    if (statut === "en-cours") {
        badge.textContent = "📄 Sauvegarde…";
        badge.className = "badge bg-light text-secondary border";
    } else if (statut === "sauvegarde") {
        badge.textContent = "✅ Brouillon sauvegardé";
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

function brancherAutoSauvegarde(form, CLE_BROUILLON) {
    setInterval(() => {
        const donnees = lireFormulaire(form);
        const aucuneDonnee = Object.values(donnees).every(v => v === "" || v === false);
        if (!aucuneDonnee) {
            sauvegarderBrouillon(CLE_BROUILLON, donnees);
            mettreAJourBadgeBrouillon("sauvegarde");
        }
    }, 30000);

    form.addEventListener("input", () => {
        clearTimeout(form._debounceTimer);
        form._debounceTimer = setTimeout(() => sauvegarderBrouillon(CLE_BROUILLON, lireFormulaire(form)), 1000);
    });
}

function restaurerAvecEtatVisuel(form, messagesErreur, CLE_BROUILLON) {
    const brouillonSauvegarde = lireBrouillon(CLE_BROUILLON);
    if (!brouillonSauvegarde) return;

    restaurerFormulaire(form, brouillonSauvegarde);

    Object.keys(messagesErreur).forEach(id => {
        const champ = document.getElementById(id);
        if (champ && champ.value.trim() !== "") afficherErreur(champ, messagesErreur);
    });
    afficherBanniereBrouillon(form, brouillonSauvegarde._sauvegardeLe || "heure inconnue");
}

// ── OPTIMISATION : Redirection Dynamique ──
function brancherBoutonAnnuler(btnId, CLE_BROUILLON) {
    const btn = document.getElementById(btnId);
    if (!btn) return;

    btn.addEventListener("click", (evenement) => {
        if (lireBrouillon(CLE_BROUILLON)) {
            evenement.preventDefault(); // On bloque le clic temporairement
            if (window.confirm("Vous avez un brouillon non soumis.\nVoulez-vous vraiment quitter sans enregistrer ?")) {
                effacerBrouillon(CLE_BROUILLON);
                window.location.href = btn.href; // Redirige vers le lien natif du bouton JSP
            }
        }
    });
}

// ── 🚨 CORRECTION CRITIQUE : La Soumission ──
function brancherSoumission(form, messagesErreur, CLE_BROUILLON) {
    form.addEventListener("submit", (evenement) => {
        let formulaireValide = true;
        let premierChampInvalide = null;

        Object.keys(messagesErreur).forEach(id => {
            const champ = document.getElementById(id);
            if (!champ) return;
            afficherErreur(champ, messagesErreur);
            if (!champ.validity.valid) {
                formulaireValide = false;
                if (!premierChampInvalide) premierChampInvalide = champ;
            }
        });

        if (!formulaireValide) {
            evenement.preventDefault(); // ⛔ ON BLOQUE SEULEMENT SI ERREUR
            if (premierChampInvalide) premierChampInvalide.focus();
            return;
        }

        // ✅ TOUT EST VALIDE : On efface le brouillon et on laisse le navigateur envoyer le POST à Tomcat !
        effacerBrouillon(CLE_BROUILLON);
    });
}