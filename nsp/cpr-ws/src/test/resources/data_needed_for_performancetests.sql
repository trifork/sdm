USE sdm_warehouse

# Dump of table Person
# ------------------------------------------------------------

TRUNCATE TABLE person;

LOCK TABLES `Person` WRITE;

INSERT INTO `Person` (`PersonPID`, `CPR`, `Koen`, `Fornavn`, `Mellemnavn`, `Efternavn`, `CoNavn`, `Lokalitet`, `Vejnavn`, `Bygningsnummer`, `Husnummer`, `Etage`, `SideDoerNummer`, `Bynavn`, `Postnummer`, `PostDistrikt`, `Status`, `NavneBeskyttelseStartDato`, `NavneBeskyttelseSletteDato`, `GaeldendeCPR`, `Foedselsdato`, `Stilling`, `VejKode`, `KommuneKode`, `NavnTilAdressering`, `VejnavnTilAdressering`, `FoedselsdatoMarkering`, `StatusDato`, `ModifiedDate`, `ValidFrom`, `ValidTo`, `CreatedDate`)
VALUES
	(1,'1111111111','M','Thomas','Greve','Kristensen','Søren Petersen','Birkely','Ørstedgade','A','10','12','tv',NULL,6666,'Überwald','01',NULL,NULL,'0102852469','1982-04-15',NULL,8464,461,'Peter,Andersen','Østergd.','0','2011-10-05 12:43:18','2011-10-06 12:43:30','2011-10-05 12:43:30','2011-10-07 12:43:30','2011-10-06 12:43:30'),
	(2,'0101821234','F','Margit','Greve','Kristensen','Søren Petersen','Birkely','Ørstedgade','A','10','12','tv',NULL,6666,'Überwald','01',NULL,NULL,'0102852469','1982-01-01',NULL,8000,461,'Peter,Andersen','Østergd.','0','2011-10-05 12:43:18','2011-10-06 12:43:30','2011-10-05 12:43:30','2011-10-07 12:43:30','2011-10-06 12:43:30'),
	(3,'0101821232','F','Margit','Greve','Kristensen','Søren Petersen','Birkely','Ørstedgade','A','10','12','tv',NULL,6666,'Überwald','01',NULL,NULL,'0102852469','1929-01-01',NULL,8100,461,'Peter,Andersen','Østergd.','0','2011-10-05 12:43:18','2011-10-06 12:43:30','2011-10-05 12:43:30','2011-10-07 12:43:30','2011-10-06 12:43:30');

UNLOCK TABLES;

TRUNCATE TABLE ChangesToCPR;

LOCK TABLES `ChangesToCPR` WRITE;

INSERT INTO `ChangesToCPR` (`CPR`, `ModifiedDate`)
VALUES
	('1212121234','2010-10-06 14:48:50');

UNLOCK TABLES;