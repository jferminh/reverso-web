<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Erreur — Reverso CRM</title>
</head>
<body>
<h1>⚠️ Une erreur est survenue</h1>

<c:choose>
    <c:when test="${not empty erreurMessage}">
        <p><strong>Détail :</strong> <c:out value="${erreurMessage}"/></p>
    </c:when>
    <c:otherwise>
        <p>Erreur inconnue. Veuillez contacter l'administrateur.</p>
    </c:otherwise>
</c:choose>

<a href="${pageContext.request.contextPath}/app">
    ← Retour à l'accueil
</a>
</body>
</html>
