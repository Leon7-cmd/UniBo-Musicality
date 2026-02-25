CREATE DATABASE  IF NOT EXISTS `project_musicality` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `project_musicality`;
-- MySQL dump 10.13  Distrib 8.0.44, for macos15 (arm64)
--
-- Host: 127.0.0.1    Database: project_musicality
-- ------------------------------------------------------
-- Server version	9.2.0

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
-- Table structure for table `abbonamento`
--

DROP TABLE IF EXISTS `abbonamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `abbonamento` (
  `dataAcquisto` datetime NOT NULL,
  `fk_tipo_abbonamento` int NOT NULL,
  `id_abbonamento` int NOT NULL AUTO_INCREMENT,
  `fk_utente` varchar(255) NOT NULL,
  PRIMARY KEY (`id_abbonamento`),
  UNIQUE KEY `fk_utente_UNIQUE` (`fk_utente`),
  KEY `fk_tipo_abbonamento_idx` (`fk_tipo_abbonamento`),
  KEY `fk_utente_idx` (`fk_utente`),
  CONSTRAINT `fk_tipo_abbonamento` FOREIGN KEY (`fk_tipo_abbonamento`) REFERENCES `tipo_abbonamento` (`id_tipo_abbonamento`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_utente` FOREIGN KEY (`fk_utente`) REFERENCES `utente` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `abbonamento`
--

LOCK TABLES `abbonamento` WRITE;
/*!40000 ALTER TABLE `abbonamento` DISABLE KEYS */;
INSERT INTO `abbonamento` VALUES ('2025-12-21 00:00:00',1,4,'user0@gmail.com'),('2025-12-21 00:00:00',1,7,'dummy1@gmail.com'),('2025-12-21 00:00:00',1,8,'dummy2@gmail.com'),('2025-12-21 00:00:00',1,9,'dummy3@gmail.com');
/*!40000 ALTER TABLE `abbonamento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contenuto_multimediale`
--

DROP TABLE IF EXISTS `contenuto_multimediale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contenuto_multimediale` (
  `id_contenuto` int NOT NULL AUTO_INCREMENT,
  `nome_contenuto` varchar(45) NOT NULL,
  `descrizione` varchar(255) NOT NULL,
  `tipo_contenuto` enum('canzone','podcast') NOT NULL,
  `fk_autore` varchar(255) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  PRIMARY KEY (`id_contenuto`),
  UNIQUE KEY `nome_contenuto` (`nome_contenuto`,`tipo_contenuto`,`fk_autore`,`file_path`),
  KEY `fk_utente_idx` (`fk_autore`),
  CONSTRAINT `fk_autore_contenuto` FOREIGN KEY (`fk_autore`) REFERENCES `utente` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contenuto_multimediale`
--

LOCK TABLES `contenuto_multimediale` WRITE;
/*!40000 ALTER TABLE `contenuto_multimediale` DISABLE KEYS */;
/*!40000 ALTER TABLE `contenuto_multimediale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist`
--

DROP TABLE IF EXISTS `playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlist` (
  `id_playlist` int NOT NULL AUTO_INCREMENT,
  `nome_playlist` varchar(32) NOT NULL,
  `descrizione` varchar(255) NOT NULL,
  `visibilita` enum('pubblica','privata') NOT NULL,
  `fk_utente` varchar(255) NOT NULL,
  PRIMARY KEY (`id_playlist`),
  UNIQUE KEY `nome_playlist` (`nome_playlist`,`fk_utente`),
  KEY `fk_utente_idx` (`fk_utente`),
  CONSTRAINT `fk_creatore` FOREIGN KEY (`fk_utente`) REFERENCES `utente` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist`
--

LOCK TABLES `playlist` WRITE;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist_contenuto`
--

DROP TABLE IF EXISTS `playlist_contenuto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlist_contenuto` (
  `fk_playlist` int NOT NULL,
  `fk_contenuto` int NOT NULL,
  PRIMARY KEY (`fk_playlist`,`fk_contenuto`),
  KEY `fk_contenuto_idx` (`fk_contenuto`),
  CONSTRAINT `fk_contenuto` FOREIGN KEY (`fk_contenuto`) REFERENCES `contenuto_multimediale` (`id_contenuto`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_playlist` FOREIGN KEY (`fk_playlist`) REFERENCES `playlist` (`id_playlist`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist_contenuto`
--

LOCK TABLES `playlist_contenuto` WRITE;
/*!40000 ALTER TABLE `playlist_contenuto` DISABLE KEYS */;
/*!40000 ALTER TABLE `playlist_contenuto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `nome_tag` varchar(255) NOT NULL,
  PRIMARY KEY (`nome_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES ('adventure'),('ambient'),('animals'),('art'),('blues'),('career'),('chillout'),('cinema'),('classical'),('comedy'),('country'),('culture'),('dance'),('design'),('documentary'),('educational'),('electronic'),('energetic'),('environment'),('fantasy'),('fashion'),('folk'),('food'),('friendship'),('funk'),('gaming'),('happy'),('health'),('hip-hop'),('history'),('horror'),('indie'),('interview'),('jazz'),('kids'),('language'),('literature'),('love'),('meditation'),('metal'),('motivation'),('motivational'),('mystery'),('news'),('party'),('photography'),('poetry'),('politics'),('pop'),('punk'),('rap'),('reggae'),('relaxing'),('rock'),('romantic'),('sad'),('science'),('sleep'),('soul'),('spiritual'),('sports'),('storytelling'),('study'),('technology'),('theatre'),('thriller'),('trance'),('travel'),('wellness'),('workout');
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag_contenuto`
--

DROP TABLE IF EXISTS `tag_contenuto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag_contenuto` (
  `fk_contenuto` int NOT NULL,
  `fk_tag` varchar(255) NOT NULL,
  PRIMARY KEY (`fk_contenuto`,`fk_tag`),
  KEY `fk_tag_idx` (`fk_tag`),
  CONSTRAINT `fk_multimedia` FOREIGN KEY (`fk_contenuto`) REFERENCES `contenuto_multimediale` (`id_contenuto`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_tag` FOREIGN KEY (`fk_tag`) REFERENCES `tag` (`nome_tag`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag_contenuto`
--

LOCK TABLES `tag_contenuto` WRITE;
/*!40000 ALTER TABLE `tag_contenuto` DISABLE KEYS */;
INSERT INTO `tag_contenuto` VALUES (61,'adventure'),(66,'motivational'),(64,'romantic'),(65,'romantic'),(63,'sad'),(64,'sad'),(65,'sad'),(66,'sad'),(63,'soul'),(64,'soul'),(65,'soul'),(65,'spiritual');
/*!40000 ALTER TABLE `tag_contenuto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `testo`
--

DROP TABLE IF EXISTS `testo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `testo` (
  `id_testo` int NOT NULL AUTO_INCREMENT,
  `testo` longtext NOT NULL,
  `fk_contenuto` int NOT NULL,
  `fk_revisione` int DEFAULT NULL,
  PRIMARY KEY (`id_testo`,`fk_contenuto`),
  KEY `fk_revisione_idx` (`fk_revisione`),
  KEY `fk_contenuto_associato_idx` (`fk_contenuto`),
  CONSTRAINT `fk_contenuto_associato` FOREIGN KEY (`fk_contenuto`) REFERENCES `contenuto_multimediale` (`id_contenuto`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_revisione` FOREIGN KEY (`fk_revisione`) REFERENCES `utente` (`codice_amministratore`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `testo`
--

LOCK TABLES `testo` WRITE;
/*!40000 ALTER TABLE `testo` DISABLE KEYS */;
/*!40000 ALTER TABLE `testo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_abbonamento`
--

DROP TABLE IF EXISTS `tipo_abbonamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_abbonamento` (
  `durata` int NOT NULL,
  `prezzo` decimal(10,2) NOT NULL,
  `id_tipo_abbonamento` int NOT NULL AUTO_INCREMENT,
  `sconto` decimal(5,2) NOT NULL,
  PRIMARY KEY (`id_tipo_abbonamento`),
  UNIQUE KEY `durata_UNIQUE` (`durata`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_abbonamento`
--

LOCK TABLES `tipo_abbonamento` WRITE;
/*!40000 ALTER TABLE `tipo_abbonamento` DISABLE KEYS */;
INSERT INTO `tipo_abbonamento` VALUES (0,0.00,1,0.00),(1,4.99,2,0.00),(3,13.99,3,5.00),(6,25.99,4,10.00),(12,49.99,5,15.00);
/*!40000 ALTER TABLE `tipo_abbonamento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_valutazione`
--

DROP TABLE IF EXISTS `tipo_valutazione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_valutazione` (
  `nome` varchar(45) NOT NULL,
  `tipo_contenuto_multimediale` enum('canzone','podcast') NOT NULL,
  PRIMARY KEY (`nome`,`tipo_contenuto_multimediale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_valutazione`
--

LOCK TABLES `tipo_valutazione` WRITE;
/*!40000 ALTER TABLE `tipo_valutazione` DISABLE KEYS */;
INSERT INTO `tipo_valutazione` VALUES ('arrangiamento','canzone'),('atmosfera','canzone'),('chiarezza','podcast'),('coinvolgimento','canzone'),('coinvolgimento','podcast'),('contenuto','podcast'),('durata','podcast'),('informativita','podcast'),('intrattenimento','podcast'),('melodia','canzone'),('originalita','canzone'),('originalita','podcast'),('produzione','canzone'),('profondita','podcast'),('ritmo','canzone'),('strumentazione','canzone'),('struttura','podcast'),('testo','canzone'),('voce','canzone'),('voce','podcast');
/*!40000 ALTER TABLE `tipo_valutazione` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utente`
--

DROP TABLE IF EXISTS `utente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utente` (
  `nome_utente` varchar(32) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nome` varchar(45) NOT NULL,
  `cognome` varchar(45) NOT NULL,
  `tipo_utente` enum('ascoltatore','autore') NOT NULL,
  `codice_amministratore` int DEFAULT NULL,
  `blocco_utente` int DEFAULT NULL,
  PRIMARY KEY (`email`),
  UNIQUE KEY `codice_amministratore_UNIQUE` (`codice_amministratore`),
  KEY `userBlock_idx` (`blocco_utente`),
  CONSTRAINT `userBlock` FOREIGN KEY (`blocco_utente`) REFERENCES `utente` (`codice_amministratore`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utente`
--

LOCK TABLES `utente` WRITE;
/*!40000 ALTER TABLE `utente` DISABLE KEYS */;
INSERT INTO `utente` VALUES ('Dummy1','dummy1@gmail.com','$2a$10$VXEu07zsOmGq.C53.ElKquDa.wSAlmmUSURhmRuja1j2pDVqsBL0W','Dummy','Dummy','ascoltatore',NULL,NULL),('Dummy2','dummy2@gmail.com','$2a$10$x0mYDOajyCeYnO5iThocoO29q4T0zSyr7srZcHFu66sMojXSKq/gu','Dummy','Dummy','ascoltatore',NULL,NULL),('Dummy3','dummy3@gmail.com','$2a$10$j6byJVgbscDNJWvTWy4EKO4SSAq7BfJomZ3uVPuQm6hW59cPIw8FO','Dummy','Dummy','ascoltatore',NULL,NULL),('User0','user0@gmail.com','$2a$10$VZ31aLKresemIIw/PPd7H.SApkd/fRZ0A0tF5ktCoRtu1dIKjf6tm','Mario','Rossi','autore',1234,NULL);
/*!40000 ALTER TABLE `utente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `valutazione`
--

DROP TABLE IF EXISTS `valutazione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `valutazione` (
  `id_valutazione` int NOT NULL AUTO_INCREMENT,
  `fk_nome_valutazione` varchar(45) NOT NULL,
  `fk_tipo_valutazione` enum('canzone','podcast') NOT NULL,
  `voto` tinyint NOT NULL,
  `fk_utente_valutazione` varchar(255) NOT NULL,
  `data` date NOT NULL,
  `fk_contenuto` int NOT NULL,
  PRIMARY KEY (`id_valutazione`),
  UNIQUE KEY `fk_nome_valutazione` (`fk_nome_valutazione`,`fk_tipo_valutazione`,`fk_utente_valutazione`,`fk_contenuto`),
  KEY `fk_utente_valutazione_idx` (`fk_utente_valutazione`),
  KEY `fk_contenuto_valutato` (`fk_contenuto`),
  CONSTRAINT `fk_contenuto_valutato` FOREIGN KEY (`fk_contenuto`) REFERENCES `contenuto_multimediale` (`id_contenuto`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_tipo_valutazione` FOREIGN KEY (`fk_nome_valutazione`, `fk_tipo_valutazione`) REFERENCES `tipo_valutazione` (`nome`, `tipo_contenuto_multimediale`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_utente_valutazione` FOREIGN KEY (`fk_utente_valutazione`) REFERENCES `utente` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `valutazione`
--

LOCK TABLES `valutazione` WRITE;
/*!40000 ALTER TABLE `valutazione` DISABLE KEYS */;
/*!40000 ALTER TABLE `valutazione` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-21 21:22:11
