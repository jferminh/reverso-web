-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: ecf_dao
-- ------------------------------------------------------
-- Server version	8.4.7

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `adresse`
--

DROP TABLE IF EXISTS `adresse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `adresse` (
  `id_adresse` int NOT NULL AUTO_INCREMENT,
  `numero_rue` varchar(10) NOT NULL,
  `nom_rue` varchar(100) NOT NULL,
  `code_postal` char(5) NOT NULL,
  `ville` varchar(50) NOT NULL,
  PRIMARY KEY (`id_adresse`),
  KEY `idx_code_postal` (`code_postal`),
  KEY `idx_ville` (`ville`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adresse`
--

LOCK TABLES `adresse` WRITE;
/*!40000 ALTER TABLE `adresse` DISABLE KEYS */;
INSERT INTO `adresse` VALUES (1,'12','Rue de la Paix','75001','Paris'),(2,'45','Avenue des Champs','69002','Lille'),(4,'98','Rue Victor Hugo Rés Point du J','62800','Liévin'),(6,'10','La Resistence','75000','Paris'),(8,'45','Rue de la Resistence','54390','Frouard'),(9,'25','Rue de La Liberation','54000','Nancy'),(12,'40','Avenue General Fulano','54000','Nancy'),(13,'15','Rue Les Salines','97233','Schoelcher'),(14,'15','Rue Les Salines','97233','Schoelcher'),(15,'12','Rue Victor Hugo','54000','Nancy'),(27,'32','Avenue Crampel','31400','Toulouse'),(29,'32','Rue de la Résistance','42000','Saint-Étienne'),(30,'45','Rue Marcadet','75018','Paris');
/*!40000 ALTER TABLE `adresse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client` (
  `id_client` int NOT NULL AUTO_INCREMENT,
  `id_societe` int NOT NULL,
  `chiffre_affaires` int NOT NULL,
  `nb_employes` smallint NOT NULL,
  PRIMARY KEY (`id_client`),
  KEY `fk_client_societe` (`id_societe`),
  CONSTRAINT `fk_client_societe` FOREIGN KEY (`id_societe`) REFERENCES `societe` (`id_societe`),
  CONSTRAINT `chk_chiffre_affaires` CHECK ((`chiffre_affaires` >= 200)),
  CONSTRAINT `chk_nb_employes` CHECK ((`nb_employes` >= 1))
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES (1,1,500000,25),(2,3,52000,25),(14,23,520000,14);
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contrat`
--

DROP TABLE IF EXISTS `contrat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contrat` (
  `id_contrat` int NOT NULL AUTO_INCREMENT,
  `client_id` int NOT NULL,
  `nom_contrat` varchar(100) NOT NULL,
  `montant` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_contrat`),
  KEY `idx_client_id` (`client_id`),
  KEY `idx_nom_contrat` (`nom_contrat`),
  CONSTRAINT `fk_contrat_client` FOREIGN KEY (`client_id`) REFERENCES `client` (`id_client`),
  CONSTRAINT `chk_montant` CHECK ((`montant` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contrat`
--

LOCK TABLES `contrat` WRITE;
/*!40000 ALTER TABLE `contrat` DISABLE KEYS */;
INSERT INTO `contrat` VALUES (1,1,'Contrat Maintenance 2026',15000.00),(10,2,'Service Maintenance',1500.50),(11,1,'Support Technique 2026',15000.00),(12,2,'Support Utilisateurs 2026',10000.00);
/*!40000 ALTER TABLE `contrat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prospect`
--

DROP TABLE IF EXISTS `prospect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prospect` (
  `id_prospect` int NOT NULL AUTO_INCREMENT,
  `id_societe` int NOT NULL,
  `date_prospection` date NOT NULL,
  `interesse` tinyint(1) NOT NULL,
  PRIMARY KEY (`id_prospect`),
  KEY `fk_prospect_societe` (`id_societe`),
  KEY `idx_date_prospection` (`date_prospection`),
  CONSTRAINT `fk_prospect_societe` FOREIGN KEY (`id_societe`) REFERENCES `societe` (`id_societe`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prospect`
--

LOCK TABLES `prospect` WRITE;
/*!40000 ALTER TABLE `prospect` DISABLE KEYS */;
INSERT INTO `prospect` VALUES (1,2,'2026-01-10',1),(8,21,'2026-03-10',1),(9,24,'2026-03-12',1);
/*!40000 ALTER TABLE `prospect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `societe`
--

DROP TABLE IF EXISTS `societe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `societe` (
  `id_societe` int NOT NULL AUTO_INCREMENT,
  `raison_sociale` varchar(100) NOT NULL,
  `adresse_id` int NOT NULL,
  `telephone` varchar(15) NOT NULL,
  `email` varchar(100) NOT NULL,
  `commentaires` text,
  PRIMARY KEY (`id_societe`),
  UNIQUE KEY `raison_sociale` (`raison_sociale`),
  KEY `fk_societe_adresse` (`adresse_id`),
  KEY `idx_raison_sociale` (`raison_sociale`),
  KEY `idx_email` (`email`),
  CONSTRAINT `fk_societe_adresse` FOREIGN KEY (`adresse_id`) REFERENCES `adresse` (`id_adresse`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `societe`
--

LOCK TABLES `societe` WRITE;
/*!40000 ALTER TABLE `societe` DISABLE KEYS */;
INSERT INTO `societe` VALUES (1,'Entreprise Test SARL',1,'0123456789','contact@test.fr','Client de test'),(2,'Prospect Test SA',2,'0456789125','contact@prospect.fr','Prospect intéressé'),(3,'Bioenergy SA',4,'0123456780','email@email.com',NULL),(21,'Query',27,'0123456789','juliofermin1606@gmail.com',''),(23,'Microsoft Corp',29,'0123456780','email@email.com',NULL),(24,'Carib Innov',30,'0234567890','carib@carib.fr','');
/*!40000 ALTER TABLE `societe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utilisateur` (
  `id_utilisateur` int NOT NULL AUTO_INCREMENT,
  `identifiant` varchar(50) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `sel` varchar(100) NOT NULL,
  PRIMARY KEY (`id_utilisateur`),
  UNIQUE KEY `identifiant` (`identifiant`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utilisateur`
--

LOCK TABLES `utilisateur` WRITE;
/*!40000 ALTER TABLE `utilisateur` DISABLE KEYS */;
INSERT INTO `utilisateur` VALUES (1,'admin','onpMisCsU6RmhoF1mU8pTUW0a7yXCKaEVivOy0odeuE=','LKLHynhJYK83/6fmlrg46g==');
/*!40000 ALTER TABLE `utilisateur` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-26 16:24:39
