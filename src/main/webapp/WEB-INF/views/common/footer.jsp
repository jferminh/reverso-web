<%@ page contentType="text/html;charset=UTF-8" language="java" %>

</main> <footer class="border-top py-3 mt-5 bg-white">
    <div class="container d-flex flex-column flex-md-row justify-content-between gap-2 align-items-center">
        <p class="mb-0 small text-muted">
            © 2026 Reverso CRM — AFPA CDA
        </p>
        <p class="mb-0 small">
            <a href="#" class="link-secondary text-decoration-none me-2">Accessibilité</a>
            <a href="${pageContext.request.contextPath}/app?cmd=mentionsLegales" class="link-secondary text-decoration-none">Mentions légales</a>
        </p>
    </div>
</footer>

<div id="rgpd-banner"
     class="position-fixed bottom-0 start-0 end-0 z-3 bg-dark text-white p-3 shadow-lg"
     role="dialog" aria-modal="true" aria-label="Bandeau de consentement" hidden>

    <div class="container d-flex flex-wrap align-items-center justify-content-between gap-3">
        <p class="mb-0 small">
            🍪 <strong>Reverso CRM</strong> utilise le <strong>LocalStorage</strong> pour mémoriser temporairement vos saisies (brouillons de formulaires). Ces données restent sur votre appareil et sont supprimées automatiquement après <strong>30 jours sans activité</strong>.
            <a href="${pageContext.request.contextPath}/app?cmd=mentionsLegales" class="text-white-50 text-decoration-underline ms-1">
                En savoir plus
            </a>
        </p>

        <div class="d-flex gap-2 flex-shrink-0">
            <button type="button" id="btn-rgpd-refuser" class="btn btn-outline-light btn-sm">
                ❌ Refuser
            </button>
            <button type="button" id="btn-rgpd-accepter" class="btn btn-success btn-sm">
                ✅ Accepter
            </button>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/bootstrap/js/bootstrap.bundle.min.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/rgpd-consent.js"></script>

<%--<c:if test="${pageTitle == 'Tableau de bord - Reverso CRM'}">--%>
<%--    <script src="${pageContext.request.contextPath}/assets/js/dashboard.js"></script>--%>
<%--</c:if>--%>

</body>
</html>