Drop table Partnerskab;

Drop table Postdistrikt;

Drop table Vej;

Drop table YderLoebenummer;

Drop table Apotek;

CREATE TABLE Apotek (
 ApotekPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 SorNummer BIGINT(20) NOT NULL,
 ApotekNummer BIGINT(15),
 FilialNummer BIGINT(15),
 EanLokationsnummer BIGINT(20),
 cvr BIGINT(15),
 pcvr BIGINT(15),
 Navn VARCHAR(256),
 Telefon VARCHAR(20),
 Vejnavn VARCHAR(100),
 Postnummer VARCHAR(10),
 Bynavn VARCHAR(30),
 Email VARCHAR(100),
 Www VARCHAR(100),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB
;

CREATE INDEX IX_Apotek_1 ON Apotek (SorNummer);

Drop table Person;

CREATE TABLE Person (
 PersonPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 CPR VARCHAR(10) NOT NULL,
 Koen VARCHAR(1) NOT NULL,
 Fornavn VARCHAR(60),
 Mellemnavn VARCHAR(60),
 Efternavn VARCHAR(60),
 CoNavn VARCHAR(50),
 Lokalitet VARCHAR(50),
 Vejnavn VARCHAR(30),
 Bygningsnummer VARCHAR(10),
 Husnummer VARCHAR(10),
 Etage VARCHAR(10),
 SideDoerNummer VARCHAR(10),
 Bynavn VARCHAR(30),
 Postnummer BIGINT(12),
 PostDistrikt VARCHAR(30),
 Status VARCHAR(2),
 NavneBeskyttelseStartDato DATETIME,
 NavneBeskyttelseSletteDato DATETIME,
 GaeldendeCPR VARCHAR(10),
 Foedselsdato DATETIME NOT NULL,
 Stilling VARCHAR(50),
 VejKode BIGINT(12),
 KommuneKode BIGINT(12),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo),
 CONSTRAINT UC_Person_1 UNIQUE (CPR, ValidFrom)
) ENGINE=InnoDB
;

CREATE INDEX IX_Person_1 ON Person (CPR);

Drop table Yder;

CREATE TABLE Yder (
 YderPID BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Nummer VARCHAR(30),
 SorNummer BIGINT(20) NOT NULL,
 PraksisSorNummer BIGINT(20) NOT NULL,
 EanLokationsnummer BIGINT(20),
 Telefon VARCHAR(20),
 Navn VARCHAR(256),
 Vejnavn VARCHAR(100),
 Postnummer VARCHAR(10),
 Bynavn VARCHAR(30),
 Email VARCHAR(100),
 Www VARCHAR(100),
 HovedSpecialeKode VARCHAR(20),
 HovedSpecialeTekst VARCHAR(40),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB
;

CREATE INDEX IX_Yder_1 ON Yder(SorNummer);

CREATE TABLE BarnRelation (
 BarnRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Id VARCHAR(21) NOT NULL,
 CPR VARCHAR(10) NOT NULL,
 BarnCPR VARCHAR(10) NOT NULL,
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo),
 CONSTRAINT UC_Person_1 UNIQUE (Id, ValidFrom)
) ENGINE=InnoDB
;

CREATE TABLE ForaeldreMyndighedRelation (
 ForaeldreMyndighedRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Id VARCHAR(21) NOT NULL,
 CPR VARCHAR(10) NOT NULL,
 TypeKode VARCHAR(4) NOT NULL,
 TypeTekst VARCHAR(50) NOT NULL,
 RelationCpr VARCHAR(10),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo),
 CONSTRAINT UC_Person_1 UNIQUE (Id, ValidFrom)
) ENGINE=InnoDB
;

CREATE TABLE Praksis (
 praksisPID BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 SorNummer BIGINT(20) NOT NULL,
 EanLokationsnummer BIGINT(20),
 RegionCode BIGINT(12),
 Navn VARCHAR(256),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB
;

CREATE TABLE Sygehus (
 SygeHusPID BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 SorNummer BIGINT(20) NOT NULL,
 EanLokationsnummer BIGINT(20),
 Nummer VARCHAR(30),
 Telefon VARCHAR(20),
 Navn VARCHAR(256),
 Vejnavn VARCHAR(100),
 Postnummer VARCHAR(10),
 Bynavn VARCHAR(30),
 Email VARCHAR(100),
 Www VARCHAR(100),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB
;

CREATE TABLE SygehusAfdeling (
 SygeHusAfdelingPID BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 SorNummer BIGINT(20) NOT NULL,
 EanLokationsnummer BIGINT(20),
 Nummer VARCHAR(30),
 Navn VARCHAR(256),
 SygehusSorNummer BIGINT(20),
 OverAfdelingSorNummer BIGINT(20),
 UnderlagtSygehusSorNummer BIGINT(20),
 AfdelingTypeKode BIGINT(20),
 AfdelingTypeTekst VARCHAR(50),
 HovedSpecialeKode VARCHAR(20),
 HovedSpecialeTekst VARCHAR(40),
 Telefon VARCHAR(20),
 Vejnavn VARCHAR(100),
 Postnummer VARCHAR(10),
 Bynavn VARCHAR(30),
 Email VARCHAR(100),
 Www VARCHAR(100),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB
;

CREATE TABLE UmyndiggoerelseVaergeRelation (
 ForaeldreMyndighedRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Id VARCHAR(21) NOT NULL,
 CPR VARCHAR(10) NOT NULL,
 TypeKode VARCHAR(4) NOT NULL,
 TypeTekst VARCHAR(50) NOT NULL,
 RelationCpr VARCHAR(10),
 RelationCprStartDato DATETIME,
 VaergesNavn VARCHAR(50),
 VaergesNavnStartDato DATETIME,
 RelationsTekst1 VARCHAR(50),
 RelationsTekst2 VARCHAR(50),
 RelationsTekst3 VARCHAR(50),
 RelationsTekst4 VARCHAR(50),
 RelationsTekst5 VARCHAR(50),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo),
 CONSTRAINT UC_Person_1 UNIQUE (Id, ValidFrom)
) ENGINE=InnoDB
;
