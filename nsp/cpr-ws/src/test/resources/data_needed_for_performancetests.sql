# ************************************************************
# Sequel Pro SQL dump
# Version 3408
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: localhost (MySQL 5.5.15)
# Database: sdm_warehouse
# Generation Time: 2011-10-06 10:57:51 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table Person
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Person`;

CREATE TABLE `Person` (
  `PersonPID` bigint(15) NOT NULL AUTO_INCREMENT,
  `CPR` char(10) COLLATE utf8_danish_ci NOT NULL,
  `Koen` char(1) COLLATE utf8_danish_ci NOT NULL,
  `Fornavn` varchar(50) COLLATE utf8_danish_ci DEFAULT NULL,
  `Mellemnavn` varchar(40) COLLATE utf8_danish_ci DEFAULT NULL,
  `Efternavn` varchar(40) COLLATE utf8_danish_ci DEFAULT NULL,
  `CoNavn` varchar(34) COLLATE utf8_danish_ci DEFAULT NULL,
  `Lokalitet` varchar(34) COLLATE utf8_danish_ci DEFAULT NULL,
  `Vejnavn` varchar(30) COLLATE utf8_danish_ci DEFAULT NULL,
  `Bygningsnummer` varchar(10) COLLATE utf8_danish_ci DEFAULT NULL,
  `Husnummer` varchar(4) COLLATE utf8_danish_ci DEFAULT NULL,
  `Etage` varchar(2) COLLATE utf8_danish_ci DEFAULT NULL,
  `SideDoerNummer` varchar(4) COLLATE utf8_danish_ci DEFAULT NULL,
  `Bynavn` varchar(34) COLLATE utf8_danish_ci DEFAULT NULL,
  `Postnummer` int(4) DEFAULT NULL,
  `PostDistrikt` varchar(20) COLLATE utf8_danish_ci DEFAULT NULL,
  `Status` char(2) COLLATE utf8_danish_ci DEFAULT NULL,
  `NavneBeskyttelseStartDato` datetime DEFAULT NULL,
  `NavneBeskyttelseSletteDato` datetime DEFAULT NULL,
  `GaeldendeCPR` char(10) COLLATE utf8_danish_ci DEFAULT NULL,
  `Foedselsdato` date NOT NULL,
  `Stilling` varchar(50) COLLATE utf8_danish_ci DEFAULT NULL,
  `VejKode` int(4) DEFAULT NULL,
  `KommuneKode` int(4) DEFAULT NULL,
  `NavnTilAdressering` varchar(34) COLLATE utf8_danish_ci DEFAULT NULL,
  `VejnavnTilAdressering` varchar(20) COLLATE utf8_danish_ci DEFAULT NULL,
  `FoedselsdatoMarkering` char(1) COLLATE utf8_danish_ci DEFAULT NULL,
  `StatusDato` datetime DEFAULT NULL,
  `ModifiedDate` datetime NOT NULL,
  `ValidFrom` datetime DEFAULT NULL,
  `ValidTo` datetime DEFAULT NULL,
  `CreatedDate` datetime NOT NULL,
  PRIMARY KEY (`PersonPID`),
  UNIQUE KEY `UC_Person_1` (`CPR`,`ValidFrom`),
  KEY `ValidFrom` (`ValidFrom`,`ValidTo`),
  KEY `ModifiedDate` (`ModifiedDate`,`PersonPID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;

LOCK TABLES `Person` WRITE;
/*!40000 ALTER TABLE `Person` DISABLE KEYS */;

INSERT INTO `Person` (`PersonPID`, `CPR`, `Koen`, `Fornavn`, `Mellemnavn`, `Efternavn`, `CoNavn`, `Lokalitet`, `Vejnavn`, `Bygningsnummer`, `Husnummer`, `Etage`, `SideDoerNummer`, `Bynavn`, `Postnummer`, `PostDistrikt`, `Status`, `NavneBeskyttelseStartDato`, `NavneBeskyttelseSletteDato`, `GaeldendeCPR`, `Foedselsdato`, `Stilling`, `VejKode`, `KommuneKode`, `NavnTilAdressering`, `VejnavnTilAdressering`, `FoedselsdatoMarkering`, `StatusDato`, `ModifiedDate`, `ValidFrom`, `ValidTo`, `CreatedDate`)
VALUES
	(1,'1111111111','M','Thomas','Greve','Kristensen','Søren Petersen','Birkely','Ørstedgade','A','10','12','tv',NULL,6666,'Überwald','01',NULL,NULL,'0102852469','1982-04-15',NULL,8464,461,'Peter,Andersen','Østergd.','0','2011-10-05 12:43:18','2011-10-06 12:43:30','2011-10-05 12:43:30','2011-10-07 12:43:30','2011-10-06 12:43:30'),
	(2,'0101821234','F','Margit','Greve','Kristensen','Søren Petersen','Birkely','Ørstedgade','A','10','12','tv',NULL,6666,'Überwald','01',NULL,NULL,'0102852469','1982-01-01',NULL,8000,461,'Peter,Andersen','Østergd.','0','2011-10-05 12:43:18','2011-10-06 12:43:30','2011-10-05 12:43:30','2011-10-07 12:43:30','2011-10-06 12:43:30'),
	(3,'0101821232','F','Margit','Greve','Kristensen','Søren Petersen','Birkely','Ørstedgade','A','10','12','tv',NULL,6666,'Überwald','01',NULL,NULL,'0102852469','1929-01-01',NULL,8100,461,'Peter,Andersen','Østergd.','0','2011-10-05 12:43:18','2011-10-06 12:43:30','2011-10-05 12:43:30','2011-10-07 12:43:30','2011-10-06 12:43:30');

/*!40000 ALTER TABLE `Person` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table Sikrede
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Sikrede`;

CREATE TABLE `Sikrede` (
  `SikredePID` bigint(15) NOT NULL AUTO_INCREMENT,
  `CPR` char(10) COLLATE utf8_danish_ci NOT NULL,
  `kommunekode` char(3) COLLATE utf8_danish_ci NOT NULL,
  `kommunekodeIkraftDato` date DEFAULT NULL,
  `foelgeskabsPersonCpr` char(10) COLLATE utf8_danish_ci DEFAULT NULL,
  `status` char(2) COLLATE utf8_danish_ci DEFAULT NULL,
  `bevisIkraftDato` date DEFAULT NULL,
  `forsikringsinstans` varchar(21) COLLATE utf8_danish_ci NOT NULL,
  `forsikringsinstansKode` varchar(10) COLLATE utf8_danish_ci NOT NULL,
  `forsikringsnummer` varchar(15) COLLATE utf8_danish_ci NOT NULL,
  `sslGyldigFra` date NOT NULL,
  `sslGyldigTil` date NOT NULL,
  `socialLand` varchar(47) COLLATE utf8_danish_ci NOT NULL,
  `socialLandKode` char(2) COLLATE utf8_danish_ci NOT NULL,
  `ModifiedDate` datetime NOT NULL,
  `ValidFrom` datetime NOT NULL,
  `ValidTo` datetime DEFAULT NULL,
  `CreatedDate` datetime NOT NULL,
  PRIMARY KEY (`SikredePID`),
  UNIQUE KEY `UC_Person_1` (`CPR`,`ValidFrom`),
  KEY `ValidFrom` (`ValidFrom`,`ValidTo`),
  KEY `ModifiedDate` (`ModifiedDate`,`SikredePID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_danish_ci;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
