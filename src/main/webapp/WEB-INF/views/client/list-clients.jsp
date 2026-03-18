<%@ include file="../common/taglibs.jsp" %>
<%@ include file="../common/header.jsp" %>

<%-- ===== BARRE D'ACTIONS ===== --%>
<div class="d-flex flex-column flex-md-row justify-content-between
            align-items-md-center gap-2 mb-4">

    <%-- Champ recherche (côté client, JS optionnel) --%>
    <div class="flex-grow-1">
        <label for="search-clients" class="form-label visually-hidden">
            Rechercher un client
        </label>
        <div class="input-group">
            <span class="input-group-text bg-white" id="search-addon">🔍</span>
            <input type="search"
                   id="search-clients"
                   class="form-control border-end-0"
                   placeholder="Rechercher par raison sociale, email ou ville..."
                   aria-describedby="search-addon">
            <span class="input-group-text bg-white border-start-0">
        <button class="btn btn-link p-0 text-decoration-none"
                type="button"
                onclick="document.getElementById('search-clients').value=''">
          ✕
        </button>
      </span>
        </div>
    </div>

    <%-- Bouton créer --%>
    <div class="text-md-end mt-2 mt-md-0">
        <a href="${pageContext.request.contextPath}/app?cmd=createClient"
           class="btn btn-primary">
            <span class="d-none d-sm-inline">➕ Créer un client</span>
            <span class="d-inline d-sm-none">➕ Créer</span>
        </a>
    </div>
</div>

<%-- ===== MESSAGE FLASH ===== --%>
<c:if test="${not empty flashMessage}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${flashMessage}
        <button type="button" class="btn-close"
                data-bs-dismiss="alert" aria-label="Fermer"></button>
    </div>
</c:if>

<%-- ===== TABLEAU OU ÉTAT VIDE ===== --%>
<c:choose>

    <c:when test="${empty clients}">
        <div class="card border-0 shadow-sm">
            <div class="card-body text-center py-5 text-muted">
                <span style="font-size:3rem">📭</span>
                <p class="mt-3 mb-3">Aucun client enregistré pour le moment.</p>
                <a href="${pageContext.request.contextPath}/app?cmd=createClient"
                   class="btn btn-primary">➕ Créer le premier client</a>
            </div>
        </div>
    </c:when>

    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-hover align-middle">

                    <%-- Caption sémantique (RGAA / accessibilité) --%>
                <caption class="text-muted">
                    Liste des clients — ${clients.size()} enregistré(s)
                </caption>

                <thead class="table-light">
                <tr>
                    <th scope="col">Raison sociale</th>
                    <th scope="col" class="d-none d-md-table-cell">Email</th>
                    <th scope="col" class="d-none d-md-table-cell">Téléphone</th>
                    <th scope="col" class="d-none d-lg-table-cell">Ville</th>
                    <th scope="col" class="d-none d-lg-table-cell text-end">CA (€)</th>
                    <th scope="col" class="text-end">Actions</th>
                </tr>
                </thead>

                <tbody id="table-clients">
                <c:forEach var="client" items="${clients}">
                    <tr>
                        <th scope="row">${client.raisonSociale}</th>

                        <td class="d-none d-md-table-cell">
                            <a href="mailto:${client.email}">${client.email}</a>
                        </td>

                        <td class="d-none d-md-table-cell">
                            <a href="tel:${client.telephone}">${client.telephone}</a>
                        </td>

                        <td class="d-none d-lg-table-cell">
                                ${client.adresse.ville}
                        </td>

                        <td class="d-none d-lg-table-cell text-end">
                            <c:if test="${not empty client.chiffreAffaires}">
                                <fmt:formatNumber value="${client.chiffreAffaires}"
                                                  type="currency"
                                                  currencySymbol="€"
                                                  maxFractionDigits="0"/>
                            </c:if>
                        </td>

                        <td class="text-end">
                                <%-- Modifier --%>
                            <a href="${pageContext.request.contextPath}/app?cmd=editClient&id=${client.id}"
                               class="btn btn-sm btn-outline-secondary me-1"
                               title="Modifier ${client.raisonSociale}">
                                ⚙️
                            </a>
                                <%-- Voir détail --%>
                            <a href="${pageContext.request.contextPath}/app?cmd=detailClient&id=${client.id}"
                               class="btn btn-sm btn-outline-primary me-1"
                               title="Voir ${client.raisonSociale}">
                                📍
                            </a>
                                <%-- Supprimer (modale de confirmation) --%>
                            <button type="button"
                                    class="btn btn-sm btn-outline-danger"
                                    title="Supprimer ${client.raisonSociale}"
                                    data-bs-toggle="modal"
                                    data-bs-target="#modal-suppression"
                                    data-id="${client.id}"
                                    data-nom="${client.raisonSociale}">
                                🗑️
                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>

            </table>
        </div>

        <%-- ===== PAGINATION ===== --%>
        <nav aria-label="Pagination des clients" class="mt-3">
            <ul class="pagination pagination-sm justify-content-center mb-0">
                <li class="page-item disabled" aria-disabled="true">
                    <span class="page-link">Précédent</span>
                </li>
                <li class="page-item active" aria-current="page">
                    <span class="page-link">1</span>
                </li>
                <li class="page-item">
                    <a class="page-link" href="#" aria-label="Aller à la page 2">2</a>
                </li>
                <li class="page-item">
                    <a class="page-link" href="#" aria-label="Aller à la page suivante">Suivant</a>
                </li>
            </ul>
        </nav>
    </c:otherwise>

</c:choose>

<%-- ===== MODALE SUPPRESSION ===== --%>
<div class="modal fade" id="modal-suppression" tabindex="-1"
     aria-labelledby="titre-suppression" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="titre-suppression">
                    Confirmer la suppression
                </h5>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Fermer"></button>
            </div>
            <div class="modal-body">
                Êtes-vous sûr de vouloir supprimer
                <strong id="modal-nom-client"></strong> ?
                <br><small class="text-muted">Cette action est irréversible.</small>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        data-bs-dismiss="modal">Annuler</button>
                <a id="btn-confirm-suppr"
                   href="#"
                   class="btn btn-danger">
                    🗑️ Supprimer
                </a>
            </div>
        </div>
    </div>
</div>

<%-- ===== JS : recherche live + modale suppression ===== --%>
<script>
    // Recherche live côté client
    document.getElementById('search-clients').addEventListener('input', function () {
        const val = this.value.toLowerCase();
        document.querySelectorAll('#table-clients tr').forEach(function (row) {
            row.style.display = row.textContent.toLowerCase().includes(val) ? '' : 'none';
        });
    });

    // Modale suppression : injecter id + nom du client
    document.getElementById('modal-suppression')
        .addEventListener('show.bs.modal', function (e) {
            const btn = e.relatedTarget;
            const id  = btn.getAttribute('data-id');
            const nom = btn.getAttribute('data-nom');
            document.getElementById('modal-nom-client').textContent = nom;
            document.getElementById('btn-confirm-suppr').href =
                '${pageContext.request.contextPath}/app?cmd=deleteClient&id=' + id;
        });
</script>

<%@ include file="../common/footer.jsp" %>
