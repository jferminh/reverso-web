/**
 * Module Brouillon - LocalStorage
 */

function lireFormulaire(formulaire) {
    const donnees = {};
    // On exclut les champs RGPD et les champs cachés techniques du Backend
    const CHAMPS_EXCLUS = ["consentement", "consentement-client", "cmd", "csrfToken", "id", "idAdresse"];

    const elements = formulaire.elements;
    for (let i = 0; i < elements.length; i++) {
        const champ = elements[i];

        // OPTIMISATION : Utilisation du 'name' au lieu de l'ID
        if (!champ.name || CHAMPS_EXCLUS.includes(champ.name) || CHAMPS_EXCLUS.includes(champ.id)) continue;
        if (champ.type === "submit" || champ.type === "button") continue;

        if (champ.type === "checkbox") {
            donnees[champ.name] = champ.checked;
        } else {
            donnees[champ.name] = champ.value || "";
        }
    }
    return donnees;
}

function sauvegarderBrouillon(cle, donnees) {
    donnees._sauvegardeLe = new Date().toLocaleTimeString("fr-FR");
    localStorage.setItem(cle, JSON.stringify(donnees));
}

function afficherConfirmationBrouillon(bouton) {
    let zone = document.getElementById('msg-brouillon');
    if (!zone) {
        zone = document.createElement('small');
        zone.id = 'msg-brouillon';
        zone.className = 'text-success ms-2';
        zone.setAttribute("role", 'status');
        zone.setAttribute('aria-live', 'polite');
        bouton.parentNode.appendChild(zone);
    }
    zone.textContent = 'Brouillon sauvegardé à ' + new Date().toLocaleTimeString("fr-FR");
    setTimeout(() => zone.textContent = '', 3000);
}

function lireBrouillon(cle) {
    const json = localStorage.getItem(cle);
    if (!json) return null;
    try {
        return JSON.parse(json);
    } catch (erreur) {
        localStorage.removeItem(cle);
        return null;
    }
}

function restaurerFormulaire(formulaire, donnees) {
    Object.keys(donnees).forEach(cle => {
        if (cle.startsWith("_")) return;
        // Restaure via le 'name'
        const champ = formulaire.querySelector(`[name='${cle}']`);
        if (!champ) return;

        if (champ.type === "checkbox") {
            champ.checked = !!donnees[cle];
        } else {
            champ.value = donnees[cle];
        }
    });
}

function afficherBanniereBrouillon(formulaire, heureSauvegarde) {
    const banniere = document.createElement("div");
    banniere.className = "alert alert-info alert-dismissible d-flex align-items-center gap-2 mb-4";
    banniere.setAttribute("role", "alert");
    banniere.innerHTML = `<span>📄</span><span>Brouillon restauré (sauvegardé à ${heureSauvegarde}). Vous pouvez continuer.</span>
                          <button type='button' class='btn-close' data-bs-dismiss='alert'></button>`;
    formulaire.insertBefore(banniere, formulaire.firstChild);
}

function effacerBrouillon(cle) {
    localStorage.removeItem(cle);
}