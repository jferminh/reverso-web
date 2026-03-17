<%-- src/main/webapp/WEB-INF/views/client/list-clients.jsp --%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des clients — Reverso CRM</title>
</head>
<body>

<nav>
    <a href="${pageContext.request.contextPath}/app?cmd=accueil">Accueil</a> |
    <a href="${pageContext.request.contextPath}/app?cmd=listClients">Clients</a> |
    <a href="${pageContext.request.contextPath}/app?cmd=listProspects">Prospects</a>
</nav>

<h1>Liste des clients</h1>

<%-- Message de succès après création / modification / suppression --%>
<c:if test="${param.success eq 'created'}">
    <p style="color:green;">✅ Client créé avec succès.</p>
</c:if>
<c:if test="${param.success eq 'updated'}">
    <p style="color:green;">✅ Client modifié avec succès.</p>
</c:if>
<c:if test="${param.success eq 'deleted'}">
    <p style="color:darkorange;">🗑️ Client supprimé.</p>
</c:if>

<p>Nombre de clients : <strong>${nbClients}</strong></p>

<a href="${pageContext.request.contextPath}/app?cmd=createClient">+ Nouveau client</a>

<c:choose>

    <c:when test="${empty clients}">
        <p>Aucun client enregistré pour le moment.</p>
    </c:when>

    <c:otherwise>
        <table border="1" cellpadding="6" cellspacing="0">
            <thead>
            <tr>
                <th>Raison sociale</th>
                <th>Ville</th>
                <th>Téléphone</th>
                <th>Email</th>
                <th>Chiffre d'affaires (€)</th>
                <th>Nb employés</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="client" items="${clients}">
                <tr>
                    <td>${client.raisonSociale}</td>
                    <td>${client.adresse.ville}</td>
                    <td>${client.telephone}</td>
                    <td>${client.email}</td>
                    <td>${client.chiffreAffaires}</td>
                    <td>${client.nbEmployes}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/app?cmd=editClient&id=${client.id}">
                            Modifier
                        </a>
                        &nbsp;|&nbsp;
                        <a href="${pageContext.request.contextPath}/app?cmd=confirmDeleteClient&id=${client.id}">
                            Supprimer
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>

</c:choose>

</body>
</html>
