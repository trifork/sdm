DROP TABLE BarnRelation;
DROP TABLE ForaeldreMyndighedRelation;
DROP TABLE Person;
DROP TABLE PersonIkraft;
DROP TABLE UmyndiggoerelseVaergeRelation;
DROP TABLE Folkekirkeoplysninger;
DROP TABLE Udrejseoplysninger;
DROP TABLE Foedselsregistreringsoplysninger;
DROP TABLE Statsborgerskab;
DROP TABLE Valgoplysninger;
DROP TABLE KommunaleForhold;
DROP TABLE AktuelCivilstand;
DROP TABLE Haendelse;
DROP TABLE MorOgFaroplysninger;
DROP TABLE Beskyttelse;

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
	CONSTRAINT UC_Person_1 UNIQUE (Id, ValidFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX (modifiedDate, BarnRelationPID),
	INDEX (CPR),
	INDEX (BarnCPR)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

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
	CONSTRAINT UC_Person_1 UNIQUE (Id, ValidFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX (modifiedDate, ForaeldreMyndighedRelationPID),
	INDEX (CPR),
	INDEX (RelationCpr)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Person (
	PersonPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Koen VARCHAR(1) NOT NULL,
	Fornavn VARCHAR(60),
	Mellemnavn VARCHAR(60),
	Efternavn VARCHAR(60),
	CoNavn VARCHAR(50),
	adresseringsNavn VARCHAR(60),
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
	CONSTRAINT UC_Person_1 UNIQUE (CPR, ValidFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, PersonPID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE PersonIkraft (
	PersonIkraftPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY	,
	IkraftDato DATETIME NOT NULL
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE UmyndiggoerelseVaergeRelation (
	UmyndiggoerelseVaergeRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
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
	CONSTRAINT UC_Person_1 UNIQUE (Id, ValidFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, UmyndiggoerelseVaergeRelationPID),
	INDEX(CPR),
	INDEX(RelationCpr)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Folkekirkeoplysninger (
	FolkekirkeoplysningerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Forholdskode VARCHAR(1) NOT NULL,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	CONSTRAINT FKO_Person_1 UNIQUE (CPR, ValidFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, FolkekirkeoplysningerPID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Udrejseoplysninger (
	UdrejseoplysningerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	UdrejseLandekode VARCHAR(4) NOT NULL,
	Udrejsedato DATETIME NOT NULL,
	UdrejsedatoUsikkerhedsmarkering VARCHAR(1) NOT NULL,
	Udlandsadresse1 VARCHAR(34) NOT NULL,
	Udlandsadresse2 VARCHAR(34) NOT NULL,
	Udlandsadresse3 VARCHAR(34) NOT NULL,
	Udlandsadresse4 VARCHAR(34) NOT NULL,
	Udlandsadresse5 VARCHAR(34) NOT NULL,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	CONSTRAINT Udrejse_Person_1 UNIQUE (CPR, ValidFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, UdrejseoplysningerPID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Foedselsregistreringsoplysninger(
	FoedselsregistreringsoplysningerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Foedselsregistreringsstedkode VARCHAR(4) NOT NULL,
	foedselsregistreringstekst VARCHAR(20) NOT NULL,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	CONSTRAINT Foedsel_Person_1 UNIQUE (CPR, ValidFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, FoedselsregistreringsoplysningerPID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Statsborgerskab(
	StatsborgerskabPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	landekode VARCHAR(4) NOT NULL,
	StatsborgerskabstartdatoUsikkerhedsmarkering VARCHAR(1) NOT NULL,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	CONSTRAINT Statsborgerskab_Person_1 UNIQUE (CPR, ValidFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, StatsborgerskabPID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Valgoplysninger (
	ValgoplysningerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Valgkode VARCHAR(1) NOT NULL,
	Valgretsdato DATETIME,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	CONSTRAINT VO_Person_1 UNIQUE (CPR, ValidFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, ValgoplysningerPID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE KommunaleForhold (
	KommunaleForholdPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Kommunalforholdstypekode VARCHAR(1) NOT NULL,
	Kommunalforholdskode VARCHAR(5) NOT NULL,
	Bemaerkninger VARCHAR(30) NOT NULL,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	CONSTRAINT KommunaleForhold_Person_1 UNIQUE (CPR, ValidFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, KommunaleForholdPID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE AktuelCivilstand (
	CivilstandPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Civilstandskode VARCHAR(1),
	Aegtefaellepersonnummer VARCHAR(10),
	Aegtefaellefoedselsdato DATETIME,
	Aegtefaellenavn VARCHAR(34),
	Separation DATETIME,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	CONSTRAINT AktuelCivilstand_Person_1 UNIQUE (CPR, ValidFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, CivilstandPID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Haendelse (
	HaendelsePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Uuid VARCHAR(100) NOT NULL,
	CPR VARCHAR(10) NOT NULL,
	Ajourfoeringsdato DATETIME,
	Haendelseskode VARCHAR(3),
	AfledtMarkering VARCHAR(2),
	Noeglekonstant VARCHAR(15),
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, HaendelsePID)	,
	INDEX(CPR)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE MorOgFaroplysninger (
	MorOgFarPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Id VARCHAR(12) NOT NULL,
	CPR VARCHAR(10) NOT NULL,
	foraelderCpr VARCHAR(10),
	Foraelderkode VARCHAR(1) NOT NULL,
	Dato DATETIME,
	Foedselsdato DATETIME,
	Navn VARCHAR(34) NULL,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	CONSTRAINT MorOgFaroplysninger_Person_1 UNIQUE (CPR, Foraelderkode, ValidFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, MorOgFarPID),
	INDEX(foraelderCpr)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Beskyttelse (
	BeskyttelsePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Id VARCHAR(15) NOT NULL,
	CPR VARCHAR(10) NOT NULL,
	beskyttelsestype VARCHAR(4) NOT NULL,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	CONSTRAINT Beskyttelse_1 UNIQUE(CPR, beskyttelsestype, validFrom),
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, BeskyttelsePID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;