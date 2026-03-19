<%@ include file="../common/taglibs.jsp" %>
<%@ include file="../common/header.jsp" %>

<%-- ===== EN-TÊTE ===== --%>
<div class="d-flex align-items-center justify-content-between mb-4">
    <div>
        <h2 class="fw-bold mb-1">
            <c:choose>
                <c:when test="${modeEdit}">✏️ Modifier le client</c:when>
                <c:otherwise>➕ Nouveau client</c:otherwise>
            </c:choose>
        </h2>
        <p class="text-muted mb-0">
            Les champs marqués d'un
            <span class="text-danger fw-bold">*</span> sont obligatoires.
        </p>
    </div>
    <a href="${pageContext.request.contextPath}/app?cmd=listClients"
       class="btn btn-outline-secondary btn-sm">
        ← Retour à la liste
    </a>
</div>

<%-- ===== MESSAGE D'ERREUR ===== --%>
<c:if test="${not empty erreur}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ⚠️ ${erreur}
        <button type="button" class="btn-close"
                data-bs-dismiss="alert" aria-label="Fermer"></button>
    </div>
</c:if>

<%-- ===== FORMULAIRE ===== --%>
<form method="post"
      action="${pageContext.request.contextPath}/app?cmd=${modeEdit ? 'updateClient' : 'saveClient'}${modeEdit ? '&id='.concat(client.id) : ''}"
      novalidate
      aria-label="Formulaire ${modeEdit ? 'modification' : 'création'} client">

    <%-- ===== SECTION 1 : SOCIÉTÉ ===== --%>
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-header bg-light fw-bold">
            🏢 Informations société
        </div>
        <div class="card-body">

            <div class="row g-3">

                <%-- Raison sociale --%>
                <div class="col-12">
                    <label for="raisonSociale" class="form-label">
                        Raison sociale <span class="text-danger" aria-hidden="true">*</span>
                    </label>
                    <input type="text"
                           id="raisonSociale"
                           name="raisonSociale"
                           class="form-control"
                           value="${not empty client.raisonSociale ? client.raisonSociale : ''}"
                           placeholder="Ex : ACME Corporation"
                           required
                           autocomplete="organization"
                           aria-required="true"/>
                </div>

                <%-- Email --%>
                <div class="col-md-6">
                    <label for="email" class="form-label">Email</label>
                    <input type="email"
                           id="email"
                           name="email"
                           class="form-control"
                           value="${not empty client.email ? client.email : ''}"
                           placeholder="contact@societe.fr"
                           autocomplete="email"/>
                </div>

                <%-- Téléphone --%>
                <div class="col-md-6">
                    <label for="telephone" class="form-label">Téléphone</label>
                    <input type="tel"
                           id="telephone"
                           name="telephone"
                           class="form-control"
                           value="${not empty client.telephone ? client.telephone : ''}"
                           placeholder="01 23 45 67 89"
                           autocomplete="tel"/>
                </div>

                <%-- CA annuel --%>
                <div class="col-md-6">
                    <label for="chiffreAffaires" class="form-label">
                        Chiffre d'affaires (€)
                    </label>
                    <input type="number"
                           id="chiffreAffaires"
                           name="chiffreAffaires"
                           class="form-control"
                           value="${not empty client.chiffreAffaires ? client.chiffreAffaires : ''}"
                           placeholder="150000"
                           min="0"/>
                </div>

                <%-- Nb employés --%>
                <div class="col-md-6">
                    <label for="nbEmployes" class="form-label">Nombre d'employés</label>
                    <input type="number"
                           id="nbEmployes"
                           name="nbEmployes"
                           class="form-control"
                           value="${not empty client.nbEmployes ? client.nbEmployes : ''}"
                           placeholder="45"
                           min="0"/>
                </div>

                <%-- Commentaires --%>
                <div class="col-12">
                    <label for="commentaires" class="form-label">Commentaires</label>
                    <textarea id="commentaires"
                              name="commentaires"
                              class="form-control"
                              rows="3"
                              placeholder="Notes internes...">${not empty client.commentaires ? client.commentaires : ''}</textarea>
                </div>

            </div>
        </div>
    </div>

    <%-- ===== SECTION 2 : ADRESSE ===== --%>
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-header bg-light fw-bold">
            📍 Adresse postale
        </div>
        <div class="card-body">

            <div class="row g-3">

                <%-- Numéro de rue --%>
                <div class="col-md-3">
                    <label for="numeroRue" class="form-label">N°</label>
                    <input type="text"
                           id="numeroRue"
                           name="numeroRue"
                           class="form-control"
                           value="${not empty client.adresse.numeroRue ? client.adresse.numeroRue : ''}"
                           placeholder="15"/>
                </div>

                <%-- Nom de rue --%>
                <div class="col-md-9">
                    <label for="nomRue" class="form-label">Rue</label>
                    <input type="text"
                           id="nomRue"
                           name="nomRue"
                           class="form-control"
                           value="${not empty client.adresse.nomRue ? client.adresse.nomRue : ''}"
                           placeholder="Avenue des Champs-Élysées"
                           autocomplete="street-address"/>
                </div>

                <%-- Code postal --%>
                <div class="col-md-4">
                    <label for="codePostal" class="form-label">Code postal</label>
                    <input type="text"
                           id="codePostal"
                           name="codePostal"
                           class="form-control"
                           value="${not empty client.adresse.codePostal ? client.adresse.codePostal : ''}"
                           placeholder="75008"
                           pattern="[0-9]{5}"
                           maxlength="5"
                           autocomplete="postal-code"/>
                </div>

                <%-- Ville --%>
                <div class="col-md-8">
                    <label for="ville" class="form-label">Ville</label>
                    <input type="text"
                           id="ville"
                           name="ville"
                           class="form-control"
                           value="${not empty client.adresse.ville ? client.adresse.ville : ''}"
                           placeholder="Paris"
                           autocomplete="address-level2"/>
                </div>

            </div>
        </div>
    </div>

    <%-- ===== BOUTONS ===== --%>
    <div class="d-flex justify-content-between gap-2">
        <a href="${pageContext.request.contextPath}/app?cmd=listClients"
           class="btn btn-outline-secondary">
            Annuler
        </a>
        <button type="submit" class="btn btn-primary px-4">
            <c:choose>
                <c:when test="${modeEdit}">💾 Enregistrer les modifications</c:when>
                <c:otherwise>✅ Créer le client</c:otherwise>
            </c:choose>
        </button>
    </div>

</form>

<%@ include file="../common/footer.jsp" %>
