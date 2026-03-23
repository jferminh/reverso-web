document.addEventListener("DOMContentLoaded", () => {

    // ── 1. Afficher/masquer mot de passe (UX & Accessibilité) ──────────
    const brancherToggleMdp = (inputId, btnId) => {
        const input = document.getElementById(inputId);
        const btn = document.getElementById(btnId);
        if (!input || !btn) return;

        btn.addEventListener('click', () => {
            const estVisible = input.type === 'text';
            input.type = estVisible ? 'password' : 'text';
            btn.setAttribute('aria-pressed', String(!estVisible));
            btn.setAttribute('aria-label', estVisible ? 'Masquer le mot de passe' : 'Afficher le mot de passe');
            // Logique améliorée : l'icône change dynamiquement
            btn.textContent = estVisible ? '👁' : '🙈';
            input.focus();
        });
    };

    brancherToggleMdp('hero-mdp', 'btn-toggle-hero-mdp');
    brancherToggleMdp('modal-mdp', 'btn-toggle-modal-mdp');

    // ── 2. Helper de validation côté client (Front-end) ─────────────────
    const validerChamp = (champ, erreurId) => {
        const zone = document.getElementById(erreurId);
        if (!champ) return false;

        // On vérifie si le champ respecte le "required" de HTML5
        if (champ.validity.valid && champ.value.trim() !== "") {
            champ.classList.remove("is-invalid");
            champ.classList.add("is-valid");
            champ.removeAttribute("aria-invalid");
            if (zone) {
                zone.textContent = "";
                zone.hidden = true;
            }
            return true;
        } else {
            champ.classList.remove("is-valid");
            champ.classList.add("is-invalid");
            champ.setAttribute("aria-invalid", "true");
            if (zone) {
                zone.textContent = champ.id.includes("mdp")
                    ? "Le mot de passe est obligatoire."
                    : "L'identifiant est obligatoire.";
                zone.hidden = false;
            }
            return false;
        }
    };

    // ── 3. Logique de soumission vers Tomcat ────────────────────────────
    const gererFormulaire = (formId, idChamp, idErrChamp, mdpChamp, mdpErrChamp) => {
        const form = document.getElementById(formId);
        if (!form) return;

        const champIdentifiant = document.getElementById(idChamp);
        const champMdp = document.getElementById(mdpChamp);

        // Validation en temps réel quand l'utilisateur quitte le champ (blur) ou tape (input)
        [champIdentifiant, champMdp].forEach(champ => {
            if (!champ) return;
            const errId = champ.id === idChamp ? idErrChamp : mdpErrChamp;

            champ.addEventListener('blur', () => validerChamp(champ, errId));
            champ.addEventListener('input', () => {
                if (champ.classList.contains('is-invalid')) validerChamp(champ, errId);
            });
        });

        // Interception du clic sur "Se connecter"
        form.addEventListener('submit', (e) => {
            const isIdValid = validerChamp(champIdentifiant, idErrChamp);
            const isMdpValid = validerChamp(champMdp, mdpErrChamp);

            // ⛔ Si le Front-end détecte des champs vides : on bloque l'envoi au serveur
            if (!isIdValid || !isMdpValid) {
                e.preventDefault(); // Annule la requête POST
                if (!isIdValid) champIdentifiant.focus();
                else champMdp.focus();
            }
            // ✅ Si tout est OK : on ne fait RIEN !
            // Le navigateur va envoyer naturellement le POST vers le FrontController Java.
        });
    };

    // Initialisation des deux formulaires (Desktop et Mobile)
    gererFormulaire('form-connexion-hero', 'hero-identifiant', 'hero-identifiant-erreur', 'hero-mdp', 'hero-mdp-erreur');
    gererFormulaire('form-connexion-modal', 'modal-identifiant', 'modal-id-erreur', 'modal-mdp', 'modal-mdp-erreur');
});