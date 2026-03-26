# 💼 CRM - Gestion de Clients et Prospects (Projet ECF)

![Java](https://img.shields.io/badge/Java-25%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Jakarta EE](https://img.shields.io/badge/Jakarta_EE-11-2396E8?style=for-the-badge&logo=eclipse&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)

Application Web de gestion de la relation client (CRM) développée dans le cadre de l'Évaluation en Cours de Formation (ECF) Concepteur Développeur d'Applications.
Ce projet démontre la maîtrise du développement backend Java/Jakarta EE, de la conception de bases de données relationnelles, et de l'intégration d'interfaces utilisateur modernes et accessibles.

## 🌟 Fonctionnalités Principales

* **Gestion des Clients & Prospects :** Création, lecture, modification et suppression (CRUD complet).
* **Gestion des Contrats (Option Bonus) :** Rattachement de multiples contrats commerciaux à un client avec calcul dynamique des totaux.
* **Intégration d'API Tierces :** * Cartographie interactive avec **Leaflet.js**.
    * Affichage des données météorologiques en temps réel.
* **Sécurité Renforcée :** Protection systématique contre les failles CSRF via un jeton dynamique et filtre de sécurité.
* **Éco-conception & Accessibilité :** Respect des normes RGAA et optimisation des appels réseau.

## 🏗️ Architecture et Choix Techniques

Le projet repose sur une architecture **MVC (Modèle-Vue-Contrôleur)** stricte pour garantir la séparation des préoccupations (SoC).

### Backend (Java)
* **Pattern Command :** Le contrôleur frontal (Front Controller) délègue le traitement des requêtes HTTP à des classes `Command` spécifiques (ex: `SaveClientCommand`, `DeleteContratCommand`).
* **Pattern DAO (Data Access Object) :** Isolation complète de la logique d'accès aux données.
* **Transactions ACID Manuelles :** Utilisation du pattern des "Méthodes Participantes" pour garantir l'intégrité des données multi-tables (ex: insertion simultanée d'une Adresse, d'une Société et d'un Client) via `connection.setAutoCommit(false)`.
* **Pool de Connexions :** Implémentation de **HikariCP** couplé à un pattern **Singleton (Double-Checked Locking avec `volatile`)** pour des performances optimales en environnement multithread (Tomcat).
* **Code Propre (Clean Code) :** * Utilisation de **Lombok** (`@Builder`, `@Getter`, `@Setter`, `@Slf4j`) pour réduire le code boilerplate.
    * Gestion stricte de la mémoire avec le `try-with-resources`.
    * Hiérarchie d'exceptions personnalisées (`DaoException`, `BusinessException`).

### Frontend (JSP & UI)
* **Pattern PRG (Post-Redirect-Get) :** Empêche la double soumission des formulaires lors du rafraîchissement de la page.
* **Validation des Données :** Double validation (Backend via **Jakarta Bean Validation** et Frontend via HTML5/JS).
* **UI/UX :** Interface responsive construite avec Bootstrap 5 (Modales, Toasts de notification, typographie soignée).

## 🛠️ Stack Technique

* **Langage :** Java 25
* **Serveur d'application :** Apache Tomcat 11
* **Framework Backend :** Jakarta EE 11 (Servlets, JSP)
* **Base de données :** MySQL 8
* **Gestionnaire de dépendances :** Maven
* **Librairies clés :** HikariCP, Lombok, Jakarta Validation, SLF4J / Logback
* **Tests :** JUnit 5
* **Frontend :** HTML5, CSS3, JS Vanilla, Bootstrap 5

## 🚀 Prérequis et Installation

### 1. Base de données
Créer une base de données MySQL nommée `ecf_dao` et exécuter le script SQL de création des tables fourni à la racine du projet (`schema.sql` ou équivalent).

### 2. Configuration
* Renommer le fichier `src/main/resources/database.properties.example` à `src/main/resources/database.properties`
* Modifier le fichier `src/main/resources/database.properties` avec vos identifiants MySQL :
```properties
db.url=jdbc:mysql://localhost:3306/ecf_dao?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
db.username=root
db.password=votre_mot_de_passe
db.driver=com.mysql.cj.jdbc.Driver
db.maximumPoolSize=10
```
### 3. Compilation et Déploiement
   Cloner le dépôt.

Exécuter la commande Maven pour nettoyer et compiler le projet :

Bash
mvn clean install
Déployer le fichier .war généré dans le dossier target/ sur votre serveur Apache Tomcat.

Accéder à l'application via http://localhost:8080/nom-du-projet/app.

🧪 Tests Unitaires
Le projet inclut des tests unitaires validant la logique métier et les règles de validation.

Pour lancer les tests :
```bash
mvn test
```

Projet réalisé par Julio FERMIN dans le cadre de la formation Concepteur Développeur d'Applications.


---
