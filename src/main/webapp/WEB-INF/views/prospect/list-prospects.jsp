<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/header.jsp" %>

<%-- En-tête de la page --%>
<div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2 mb-4">
    <h2 class="h3 mb-0 text-dark">🎯 Liste des Prospects</h2>
    <a href="${pageContext.request.contextPath}/app?cmd=createProspect" class="btn btn-primary shadow-sm">
        <span class="d-none d-sm-inline">+ Créer un prospect</span>
        <span class="d-inline d-sm-none">+ Créer</span>
    </a>
</div>

<%-- Messages de succès --%>
<c:if test="${not empty sessionScope.successMessage}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
        ✅ ${sessionScope.successMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fermer"></button>
    </div>
    <c:remove var="successMessage" scope="session"/>
</c:if>

<c:if test="${not empty erreurMessage}">
    <div class="alert alert-danger shadow-sm" role="alert">❌ ${erreurMessage}</div>
</c:if>

<%-- Tableau de données --%>
<div class="card shadow-sm border-0 mb-5">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
            <tr>
                <th scope="col">Raison sociale</th>
                <th scope="col">Email</th>
                <th scope="col" class="d-none d-md-table-cell">Téléphone</th>
                <th scope="col">Ville</th>
                <th scope="col" class="d-none d-lg-table-cell">Niveau d'intérêt</th>
                <th scope="col" class="text-end">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty prospects}">
                    <tr>
                        <td colspan="6" class="text-center py-5 text-muted">
                            Aucun prospect trouvé. C'est le moment de démarcher !
                        </td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="prospect" items="${prospects}">
                        <tr>
                            <th scope="row">${prospect.raisonSociale}</th>
                            <td><a href="mailto:${prospect.email}" class="text-decoration-none">${prospect.email}</a></td>
                            <td class="d-none d-md-table-cell">${prospect.telephone}</td>
                            <td>${prospect.adresse.ville}</td>
                            <td class="d-none d-lg-table-cell">
                                <c:choose>
                                    <c:when test="${prospect.interesse.name() == 'OUI'}">
                                        <span class="badge bg-success">OUI</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">NON</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-end">
                                <a href="${pageContext.request.contextPath}/app?cmd=editProspect&id=${prospect.id}" class="btn btn-sm btn-outline-secondary me-1" title="Modifier">⚙️</a>
                                <a href="${pageContext.request.contextPath}/app?cmd=viewProspect&id=${prospect.id}" class="btn btn-sm btn-outline-primary me-1" title="Voir détails">📍</a>
                                <button type="button" class="btn btn-sm btn-outline-danger" title="Supprimer" data-bs-toggle="modal" data-bs-target="#modal-suppression" data-nom="${prospect.raisonSociale}" data-id="${prospect.id}">🗑️</button>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</div>

<%-- MODALE DE SUPPRESSION SPÉCIFIQUE AU PROSPECT --%>
<div class="modal fade" id="modal-suppression" tabindex="-1" aria-labelledby="modal-suppression-titre" aria-modal="true" role="dialog">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/app" method="POST">
                <input type="hidden" name="cmd" value="deleteProspect"> <%-- 👈 Pointe vers la commande Prospect --%>
                <input type="hidden" name="id" id="input-id-suppression">

                <div class="modal-header">
                    <h2 class="modal-title h5" id="modal-suppression-titre">⚠️ Confirmation de suppression</h2>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fermer"></button>
                </div>
                <div class="modal-body">
                    <p>Êtes-vous sûr de vouloir supprimer le prospect <strong id="nom-entite-suppression">"…"</strong> ?</p>
                    <p class="text-danger mb-0"><strong>⚠️ Cette action est irréversible.</strong></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Annuler</button>
                    <button type="submit" class="btn btn-danger">Supprimer définitivement</button>
                </div>
            </form>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/js/modal.js"></script>

<%@ include file="../common/footer.jsp" %>