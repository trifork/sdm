CREATE TABLE BarnRelation (
	PID SERIAL,
	Id VARCHAR(21) NOT NULL,
	CPR VARCHAR(10) NOT NULL,
	BarnCPR VARCHAR(10) NOT NULL,

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo),
	CONSTRAINT UC_Person_1 UNIQUE (Id, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE ForaeldreMyndighedRelation (
	PID SERIAL,
	
	Id VARCHAR(21) NOT NULL,
	CPR VARCHAR(10) NOT NULL,
	TypeKode VARCHAR(4) NOT NULL,
	TypeTekst VARCHAR(50) NOT NULL,
	RelationCpr VARCHAR(10),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo),
	CONSTRAINT UC_Person_1 UNIQUE (Id, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE PersonIkraft (
	PID SERIAL,
	IkraftDato DATETIME NOT NULL
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE UmyndiggoerelseVaergeRelation (
	PID SERIAL,
	
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

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Person (
	PID SERIAL,
	
	CPR CHAR(10) NOT NULL,
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
	GaeldendeCPR CHAR(10),
	Foedselsdato DATE NOT NULL,
	Stilling VARCHAR(50),
	VejKode BIGINT(12), 
	KommuneKode BIGINT(12),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

