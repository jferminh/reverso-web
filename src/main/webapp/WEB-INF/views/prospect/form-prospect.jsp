<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/header.jsp" %>

<div class="d-flex align-items-center justify-content-between pb-3 mb-4 border-bottom">
    <a href="${pageContext.request.contextPath}/app?cmd=listProspects" class="btn btn-outline-secondary btn-sm">
        ← Retour à la liste
    </a>
    <div class="text-center">
        <p class="m-0 fw-bold fs-5">${modeEdit ? 'Modifier le Prospect' : 'Nouveau Prospect'}</p>
        <p class="m-0 small text-muted">Fiche de prospection</p>
    </div>
    <span class="badge bg-light text-secondary border" data-badge-brouillon>
        📄 Brouillon auto
    </span>
</div>

<%-- Affichage des erreurs Serveur --%>
<c:if test="${not empty erreurMessage}">
    <div class="alert alert-danger shadow-sm" role="alert" aria-live="assertive">
        ❌ ${erreurMessage}
    </div>
</c:if>

<%-- Formulaire aux normes RGAA et prêt pour le JS --%>
<form id="form-prospect" action="${pageContext.request.contextPath}/app" method="POST" aria-label="Formulaire de gestion prospect" novalidate>

    <input type="hidden" name="cmd" value="saveProspect">
    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
    <input type="hidden" name="id" value="${prospect.id}">
    <input type="hidden" name="idAdresse" value="${prospect.adresse.id}">

    <p class="text-muted mb-4" aria-hidden="true">
        Les champs marqués d'un <span class="text-danger fw-bold">*</span> sont obligatoires.
    </p>

    <%-- ================= SECTION 1 : SOCIÉTÉ ================= --%>
    <section aria-labelledby="titre-societe" class="card mb-4 shadow-sm border-0">
        <div class="card-body">
            <h2 class="card-title h5 mb-3" id="titre-societe">Information Société</h2>

            <div class="row g-3">
                <div class="col-12 col-md-12 mb-3">
                    <label for="raison-sociale" class="form-label">Raison sociale <span class="text-danger" aria-hidden="true">*</span></label>
                    <input type="text" class="form-control" id="raison-sociale" name="raisonSociale"
                           value="<c:out value='${prospect.raisonSociale}'/>" placeholder="Ex : Green Corp"
                           required aria-required="true" minlength="2" aria-describedby="raison-sociale-erreur">
                    <div id="raison-sociale-erreur" class="invalid-feedback" hidden>La raison sociale est obligatoire.</div>
                </div>

                <div class="col-12 col-md-6 mb-3">
                    <label for="email-prospect" class="form-label">Email <span class="text-danger" aria-hidden="true">*</span></label>
                    <input type="email" class="form-control" id="email-prospect" name="email"
                           value="<c:out value='${prospect.email}'/>" placeholder="contact@entreprise.fr"
                           required aria-required="true" aria-describedby="email-prospect-erreur">
                    <div id="email-prospect-erreur" class="invalid-feedback" hidden>Veuillez saisir un email valide.</div>
                </div>

                <div class="col-12 col-md-6 mb-3">
                    <label for="telephone" class="form-label">Téléphone <span class="text-danger" aria-hidden="true">*</span></label>
                    <input type="tel" id="telephone" name="telephone" class="form-control"
                           value="<c:out value='${prospect.telephone}'/>" placeholder="Ex : 0123456789"
                           required aria-required="true" pattern="[0-9]{10}" aria-describedby="telephone-erreur">
                    <div id="telephone-erreur" class="invalid-feedback" hidden>Doit contenir exactement 10 chiffres.</div>
                </div>
            </div>
        </div>
    </section>

    <%-- ================= SECTION 2 : PROSPECTION ================= --%>
    <section aria-labelledby="titre-prospection" class="card mb-4 shadow-sm border-0">
        <div class="card-body">
            <h2 class="card-title h5 mb-3" id="titre-prospection">Suivi de Prospection</h2>

            <div class="row g-3">
                <div class="col-12 col-md-6">
                    <label for="date-prospection" class="form-label">Date de prospection <span class="text-danger" aria-hidden="true">*</span></label>
                    <input type="date" id="date-prospection" name="dateProspection" class="form-control"
                           value="<c:out value='${prospect.dateProspection}'/>"
                           required aria-required="true" aria-describedby="date-prospection-erreur">
                    <div id="date-prospection-erreur" class="invalid-feedback" hidden>La date est requise et ne peut être dans le futur.</div>
                </div>

                <div class="col-12 col-md-6">
                    <label for="interesse" class="form-label">Niveau d'intérêt <span class="text-danger" aria-hidden="true">*</span></label>
                    <select class="form-select" id="interesse" name="interesse" required aria-required="true" aria-describedby="interesse-erreur">
                        <option value="" ${empty prospect.interesse ? 'selected' : ''}>-- Sélectionnez --</option>
                        <option value="OUI" ${prospect.interesse.name() == 'OUI' ? 'selected' : ''}>Fort (OUI)</option>
                        <option value="NON" ${prospect.interesse.name() == 'NON' ? 'selected' : ''}>Faible (NON)</option>
                    </select>
                    <div id="interesse-erreur" class="invalid-feedback" hidden>Veuillez sélectionner le niveau d'intérêt.</div>
                </div>
            </div>
        </div>
    </section>

    <%-- ================= SECTION 3 : ADRESSE ================= --%>
    <section aria-labelledby="titre-adresse" class="card mb-4 shadow-sm border-0">
        <div class="card-body">
            <h2 class="card-title h5 mb-3" id="titre-adresse">Adresse Postale</h2>

            <div class="row g-3">
                <div class="col-12 col-sm-3 mb-3">
                    <label for="numero-rue" class="form-label">Numéro <span class="text-danger" aria-hidden="true">*</span></label>
                    <input type="text" id="numero-rue" name="numeroRue" class="form-control"
                           value="<c:out value='${prospect.adresse.numeroRue}'/>" required aria-required="true" aria-describedby="numero-rue-erreur">
                    <div id="numero-rue-erreur" class="invalid-feedback" hidden>Le numéro est requis.</div>
                </div>

                <div class="col-12 col-sm-9 mb-3 position-relative">
                    <label for="rue" class="form-label">Voie <span class="text-danger" aria-hidden="true">*</span></label>
                    <div class="input-group">
                        <input type="text" id="rue" name="nomRue" class="form-control"
                               value="<c:out value='${prospect.adresse.nomRue}'/>" required aria-required="true" aria-describedby="rue-erreur">
                        <button type="button" class="btn btn-outline-secondary" id="btn-geo" aria-label="Rechercher l'adresse automatiquement">📍</button>
                    </div>
                    <div id="rue-erreur" class="invalid-feedback" hidden>La voie est requise.</div>
                    <ul id="suggestion-adresse" class="list-group position-absolute z-3 w-100 d-none" role="listbox"></ul>
                </div>
            </div>

            <div class="row g-3 mb-3">
                <div class="col-12 col-sm-4">
                    <label for="code-postal" class="form-label">Code postal <span class="text-danger" aria-hidden="true">*</span></label>
                    <input type="text" id="code-postal" name="codePostal" class="form-control"
                           value="<c:out value='${prospect.adresse.codePostal}'/>" required aria-required="true" pattern="[0-9]{5}" aria-describedby="code-postal-erreur">
                    <div id="code-postal-erreur" class="invalid-feedback" hidden>5 chiffres requis.</div>
                </div>
                <div class="col-12 col-sm-8">
                    <label for="ville" class="form-label">Ville <span class="text-danger" aria-hidden="true">*</span></label>
                    <input type="text" id="ville" name="ville" class="form-control"
                           value="<c:out value='${prospect.adresse.ville}'/>" required aria-required="true" aria-describedby="ville-erreur">
                    <div id="ville-erreur" class="invalid-feedback" hidden>La ville est requise.</div>
                </div>
            </div>
        </div>
    </section>

    <%-- ================= SECTION 4 : COMMENTAIRES ================= --%>
    <section aria-labelledby="titre-commentaires" class="card mb-4 shadow-sm border-0">
        <div class="card-body">
            <h2 class="card-title h5 mb-3" id="titre-commentaires">Notes additionnelles</h2>
            <div class="col-12">
                <label for="commentaires" class="form-label">Commentaires (Optionnel)</label>
                <textarea class="form-control" id="commentaires" name="commentaires" rows="3"
                          maxlength="500" aria-describedby="commentaires-aide"><c:out value='${prospect.commentaires}'/></textarea>
                <div id="commentaires-aide" class="form-text">Limité à 500 caractères.</div>
            </div>
        </div>
    </section>

    <%-- ================= SECTION 5 : RGPD ================= --%>
    <section aria-labelledby="titre-rgpd" class="alert alert-light border mt-4 small">
        <h2 class="h6 fw-bold mb-2" id="titre-rgpd">🔒 Protection des données (RGPD)</h2>
        <p class="mb-0">
            Les données recueillies sont traitées par <strong>Reverso CRM</strong> dans le but de gérer nos opportunités commerciales.
            Base légale : Intérêt légitime (Art. 6.1.f RGPD).
            Elles sont conservées pendant un maximum de 3 ans à compter du dernier contact.
        </p>
    </section>

    <div class="form-check mt-2 mb-4">
        <input class="form-check-input" type="checkbox" id="consentement-prospect" name="consentement" required aria-required="true" aria-describedby="consentement-erreur">
        <label class="form-check-label" for="consentement-prospect">
            Je confirme l'exactitude des informations et j'accepte leur traitement. <span class="text-danger" aria-hidden="true">*</span>
        </label>
        <div id="consentement-erreur" class="invalid-feedback" hidden>Vous devez accepter pour soumettre le formulaire.</div>
    </div>

    <%-- ================= ACTIONS ================= --%>
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mt-2 pt-3 border-top">
        <a href="${pageContext.request.contextPath}/app?cmd=listProspects" id="btn-annuler" class="btn btn-outline-secondary">❌ Annuler</a>
        <div class="d-flex gap-2">
            <button type="button" id="btn-brouillon" class="btn btn-outline-primary">💾 Brouillon</button>
            <button type="submit" id="btn-soumettre" class="btn btn-primary">✅ Enregistrer</button>
        </div>
    </div>
</form>

<script src="${pageContext.request.contextPath}/assets/js/brouillon.js" defer></script>
<script src="${pageContext.request.contextPath}/assets/js/utils-form.js" defer></script>
<script src="${pageContext.request.contextPath}/assets/js/geo-adresse.js" defer></script>
<script src="${pageContext.request.contextPath}/assets/js/form-prospect.js" defer></script> <%-- LE NOUVEAU SCRIPT --%>

<%@ include file="../common/footer.jsp" %>