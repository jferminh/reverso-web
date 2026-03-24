<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="h3 mb-0 text-dark">
        <c:choose>
            <c:when test="${modeEdit}">⚙️ Modifier le prospect</c:when>
            <c:otherwise>✨ Nouveau prospect</c:otherwise>
        </c:choose>
    </h2>
    <a href="${pageContext.request.contextPath}/app?cmd=listProspects" class="btn btn-outline-secondary">
        ← Retour à la liste
    </a>
</div>

<%-- Affichage des erreurs de validation (Bean Validation / BusinessException) --%>
<c:if test="${not empty erreurMessage}">
    <div class="alert alert-danger shadow-sm" role="alert">
        <strong>⚠️ Attention :</strong> ${erreurMessage}
    </div>
</c:if>

<div class="card shadow-sm border-0 mb-5">
    <div class="card-body p-4">

        <%-- Le formulaire envoie toujours les données vers saveProspect (en POST) --%>
        <form action="${pageContext.request.contextPath}/app" method="POST" novalidate>

            <%-- Champs cachés vitaux pour le routage et la sécurité --%>
            <input type="hidden" name="cmd" value="saveProspect">
            <input type="hidden" name="id" value="${prospect.id}">
            <%-- Si tu as mis en place un token CSRF dans ta session, décommente la ligne suivante : --%>
            <%-- <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"> --%>

            <div class="row g-4">

                <%-- ================= SECTION 1 : SOCIÉTÉ ================= --%>
                <div class="col-12">
                    <h5 class="border-bottom pb-2 mb-3 text-primary">Informations de l'entreprise</h5>
                </div>

                <div class="col-md-6">
                    <label for="raisonSociale" class="form-label fw-bold">Raison sociale <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="raisonSociale" name="raisonSociale"
                           value="<c:out value='${prospect.raisonSociale}'/>" required maxlength="100"
                           placeholder="Ex: Green Corp">
                </div>

                <div class="col-md-6">
                    <label for="email" class="form-label fw-bold">Email de contact <span class="text-danger">*</span></label>
                    <input type="email" class="form-control" id="email" name="email"
                           value="<c:out value='${prospect.email}'/>" required maxlength="100"
                           placeholder="contact@entreprise.fr">
                </div>

                <div class="col-md-6">
                    <label for="telephone" class="form-label fw-bold">Téléphone <span class="text-danger">*</span></label>
                    <input type="tel" class="form-control" id="telephone" name="telephone"
                           value="<c:out value='${prospect.telephone}'/>" required minlength="10" maxlength="20"
                           placeholder="01 23 45 67 89">
                </div>

                <%-- ================= SECTION 2 : SPÉCIFIQUE PROSPECT ================= --%>
                <div class="col-12 mt-5">
                    <h5 class="border-bottom pb-2 mb-3 text-primary">Suivi de Prospection</h5>
                </div>

                <div class="col-md-6">
                    <label for="dateProspection" class="form-label fw-bold">Date de prospection <span class="text-danger">*</span></label>
                    <input type="date" class="form-control" id="dateProspection" name="dateProspection"
                           value="<c:out value='${prospect.dateProspection}'/>" required>
                    <div class="form-text">La date ne peut pas être dans le futur.</div>
                </div>

                <div class="col-md-6">
                    <label for="interesse" class="form-label fw-bold">Niveau d'intérêt <span class="text-danger">*</span></label>
                    <select class="form-select" id="interesse" name="interesse" required>
                        <option value="" ${empty prospect.interesse ? 'selected' : ''}>-- Sélectionnez --</option>
                        <option value="OUI" ${prospect.interesse.name() == 'OUI' ? 'selected' : ''}>Fort (OUI)</option>
                        <option value="NON" ${prospect.interesse.name() == 'NON' ? 'selected' : ''}>Faible (NON)</option>
                    </select>
                </div>

                <%-- ================= SECTION 3 : ADRESSE ================= --%>
                <div class="col-12 mt-5">
                    <h5 class="border-bottom pb-2 mb-3 text-primary">Adresse</h5>
                    <input type="hidden" name="adresseId" value="${prospect.adresse.id}">
                </div>

                <div class="col-md-2">
                    <label for="numeroRue" class="form-label fw-bold">N° <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="numeroRue" name="numeroRue"
                           value="<c:out value='${prospect.adresse.numeroRue}'/>" required maxlength="10">
                </div>

                <div class="col-md-10">
                    <label for="nomRue" class="form-label fw-bold">Voie (rue, avenue...) <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="nomRue" name="nomRue"
                           value="<c:out value='${prospect.adresse.nomRue}'/>" required maxlength="100">
                </div>

                <div class="col-md-4">
                    <label for="codePostal" class="form-label fw-bold">Code Postal <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="codePostal" name="codePostal"
                           value="<c:out value='${prospect.adresse.codePostal}'/>" required maxlength="10">
                </div>

                <div class="col-md-8">
                    <label for="ville" class="form-label fw-bold">Ville <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="ville" name="ville"
                           value="<c:out value='${prospect.adresse.ville}'/>" required maxlength="100">
                </div>

                <%-- ================= SECTION 4 : COMMENTAIRES ================= --%>
                <div class="col-12 mt-5">
                    <h5 class="border-bottom pb-2 mb-3 text-primary">Notes additionnelles</h5>
                </div>

                <div class="col-12">
                    <label for="commentaires" class="form-label fw-bold">Commentaires (Optionnel)</label>
                    <textarea class="form-control" id="commentaires" name="commentaires" rows="3"
                              maxlength="500"><c:out value='${prospect.commentaires}'/></textarea>
                </div>

            </div>

            <hr class="my-4">

            <div class="d-flex justify-content-end gap-2">
                <a href="${pageContext.request.contextPath}/app?cmd=listProspects" class="btn btn-outline-secondary">Annuler</a>
                <button type="submit" class="btn btn-primary px-4">
                    <c:choose>
                        <c:when test="${modeEdit}">Enregistrer les modifications</c:when>
                        <c:otherwise>Créer le prospect</c:otherwise>
                    </c:choose>
                </button>
            </div>
        </form>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>