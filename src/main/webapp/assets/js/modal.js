/**
 * modal.js
 * Gère l'injection des données (ID et Nom) dans la modale de suppression
 * avant de laisser le navigateur envoyer le POST au serveur.
 */
document.addEventListener('DOMContentLoaded', () => {

    const modalSuppression = document.getElementById('modal-suppression');

    if (modalSuppression) {
        // Événement déclenché juste avant l'ouverture de la modale Bootstrap
        modalSuppression.addEventListener('show.bs.modal', (e) => {

            // 1. Récupérer le bouton 🗑️ qui a été cliqué
            const btnDeclencheur = e.relatedTarget;

            // 2. Extraire les données (data-nom et data-id)
            const nomEntite = btnDeclencheur.getAttribute('data-nom');
            const idEntite = btnDeclencheur.getAttribute('data-id');

            // 3. Injecter le nom dans le texte de la modale
            const spanNom = document.getElementById('nom-entite-suppression');
            if (spanNom) spanNom.textContent = nomEntite || 'cet élément';

            // 4. Injecter l'ID dans l'<input type="hidden"> du formulaire
            const inputId = document.getElementById('input-id-suppression');
            if (inputId) inputId.value = idEntite;
        });
    }
});