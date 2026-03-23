<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/header.jsp" %>

<div class="d-flex align-items-center justify-content-between pb-3 mb-4 border-bottom">
    <a href="${pageContext.request.contextPath}/app?cmd=listClients" class="btn btn-outline-secondary btn-sm">
        ← Retour à la liste
    </a>
    <div class="text-center">
        <p class="m-0 fw-bold fs-5">${modeEdit ? 'Modifier le Client' : 'Nouveau Client'}</p>
        <p class="m-0 small text-muted">Fiche d'information</p>
    </div>
    <span class="badge bg-light text-secondary border" data-badge-brouillon>
        📄 Brouillon auto
    </span>
</div>

<%-- Affichage des erreurs de validation Serveur (Bean Validation) --%>
<c:if test="${not empty erreurMessage}">
    <div class="alert alert-danger" role="alert">
        ❌ ${erreurMessage}
    </div>
</c:if>

<form id="form-client" action="${pageContext.request.contextPath}/app" method="POST" aria-label="Formulaire client">

    <input type="hidden" name="cmd" value="saveClient">
    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
    <input type="hidden" name="id" value="${client.id}">
    <input type="hidden" name="idAdresse" value="${client.adresse.id}">

    <p class="text-muted mb-4">
        Les champs marqués d'un <span class="text-danger fw-bold">*</span> sont obligatoires.
    </p>

    <section aria-labelledby="titre-societe" class="card mb-4 shadow-sm border-0">
        <div class="card-body">
            <h2 class="card-title h5 mb-3" id="titre-societe">Information Société</h2>

            <div class="mb-3">
                <label for="raison-sociale" class="form-label">Raison sociale <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="raison-sociale" name="raisonSociale"
                       value="${client.raisonSociale}" placeholder="Ex : ACME Corporation" required>
                <div id="raison-sociale-erreur" class="invalid-feedback" hidden>La raison sociale est obligatoire.</div>
            </div>

            <div class="mb-3">
                <label for="email-client" class="form-label">Email <span class="text-danger">*</span></label>
                <input type="email" class="form-control" id="email-client" name="email"
                       value="${client.email}" placeholder="Ex : contact@societe.fr" required>
                <div id="email-client-erreur" class="invalid-feedback" hidden>Veuillez saisir un email valide.</div>
            </div>
        </div>
    </section>

    <section aria-labelledby="titre-coordonnees" class="card mb-4 shadow-sm border-0">
        <div class="card-body">
            <h2 class="card-title h5 mb-3" id="titre-coordonnees">Coordonnées &amp; Chiffres clés</h2>
            <div class="row g-3">
                <div class="col-12 col-md-6">
                    <label for="telephone" class="form-label">Téléphone <span class="text-danger">*</span></label>
                    <input type="tel" id="telephone" name="telephone" class="form-control"
                           value="${client.telephone}" placeholder="Ex : 0123456789" required pattern="[0-9]{10}">
                    <div id="telephone-erreur" class="invalid-feedback" hidden>Doit contenir exactement 10 chiffres.</div>
                </div>

                <div class="col-12 col-md-6">
                    <label for="ca-annuel" class="form-label">CA annuel (€) <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <input type="number" id="ca-annuel" name="chiffreAffaires" class="form-control"
                               value="${client.chiffreAffaires}" required min="200">
                        <span class="input-group-text">€</span>
                    </div>
                    <div id="ca-annuel-erreur" class="invalid-feedback" hidden>Minimum 200€.</div>
                </div>

                <div class="col-12 col-md-6 mb-3">
                    <label for="nb-employes" class="form-label">Nombre d'employés <span class="text-danger">*</span></label>
                    <input type="number" id="nb-employes" name="nbEmployes" class="form-control"
                           value="${client.nbEmployes}" required min="1">
                    <div id="nb-employes-erreur" class="invalid-feedback" hidden>Minimum 1 employé.</div>
                </div>
            </div>
        </div>
    </section>

    <section aria-labelledby="titre-adresse" class="card mb-4 shadow-sm border-0">
        <div class="card-body">
            <h2 class="card-title h5 mb-3" id="titre-adresse">Adresse Postale</h2>

            <div class="row g-3">
                <div class="col-12 col-sm-3 mb-3">
                    <label for="numero-rue" class="form-label">Numéro <span class="text-danger">*</span></label>
                    <input type="text" id="numero-rue" name="numeroRue" class="form-control"
                           value="${client.adresse.numeroRue}" required>
                </div>

                <div class="col-12 col-sm-9 mb-3">
                    <label for="rue" class="form-label">Rue <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <input type="text" id="rue" name="nomRue" class="form-control"
                               value="${client.adresse.nomRue}" required>
                        <button type="button" class="btn btn-outline-secondary" id="btn-geo" title="Rechercher">📍</button>
                    </div>
                    <ul id="suggestion-adresse" class="list-group position-absolute z-3 w-100 d-none"></ul>
                </div>
            </div>

            <div class="row g-3 mb-3">
                <div class="col-12 col-sm-4">
                    <label for="code-postal" class="form-label">Code postal <span class="text-danger">*</span></label>
                    <input type="text" id="code-postal" name="codePostal" class="form-control"
                           value="${client.adresse.codePostal}" required pattern="[0-9]{5}">
                </div>
                <div class="col-12 col-sm-8">
                    <label for="ville" class="form-label">Ville <span class="text-danger">*</span></label>
                    <input type="text" id="ville" name="ville" class="form-control"
                           value="${client.adresse.ville}" required>
                </div>
            </div>
        </div>
    </section>

    <div class="alert alert-light border mt-4 small">
        <h2 class="h6 fw-bold mb-2">🔒 Protection des données (RGPD)</h2>
        <p class="mb-0">Traitement par AFPA. Base légale : intérêt légitime (Art. 6.1.f RGPD). Conservation : 30 jours.</p>
    </div>

    <div class="form-check mt-2 mb-4">
        <input class="form-check-input" type="checkbox" id="consentement-client" name="consentement" required>
        <label class="form-check-label" for="consentement-client">
            J'accepte le traitement de mes données. <span class="text-danger">*</span>
        </label>
    </div>

    <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mt-2 pt-3 border-top">
        <a href="${pageContext.request.contextPath}/app?cmd=listClients" class="btn btn-outline-secondary">❌ Annuler</a>
        <div class="d-flex gap-2">
            <button type="button" id="btn-brouillon" class="btn btn-outline-primary">💾 Brouillon</button>
            <button type="submit" id="btn-soumettre" class="btn btn-primary">✅ Enregistrer</button>
        </div>
    </div>
</form>

<script src="${pageContext.request.contextPath}/assets/js/brouillon.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/utils-form.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/geo-adresse.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/form-client.js"></script>

<%@ include file="../common/footer.jsp" %>