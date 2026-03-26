/**
 * Gestion des modales pour les contrats (Ajout, Modification, Suppression)
 * Fichier : contrats.js
 */

function ouvrirModalContrat(id = '', nom = '', montant = '') {
    // Change le titre selon Création ou Modification
    document.getElementById('modal-contrat-titre').textContent = id ? 'Modifier le Contrat' : 'Nouveau Contrat';

    // Remplit les champs du formulaire
    document.getElementById('input-contrat-id').value = id;
    document.getElementById('input-contrat-nom').value = nom;
    document.getElementById('input-contrat-montant').value = montant;

    // Affiche la modale (Optimisation Bootstrap 5 : getOrCreateInstance)
    const modalElement = document.getElementById('modal-contrat');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
    modal.show();
}

function preparerSuppressionContrat(id) {
    // Injecte l'ID dans le formulaire de suppression
    document.getElementById('input-delete-contrat-id').value = id;

    // Affiche la modale de suppression
    const modalElement = document.getElementById('modal-suppression-contrat');
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
    modal.show();
}