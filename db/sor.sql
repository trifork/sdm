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
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Praksis (
	praksisPID BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	SorNummer BIGINT(20) NOT NULL,
	EanLokationsnummer BIGINT(20),
	RegionCode BIGINT(12),
	Navn VARCHAR(256),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

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
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,

	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

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
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,

	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

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

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;
