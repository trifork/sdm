CREATE TABLE YderLoebenummer (
	YderLoebenummerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY	,
	Loebenummer BIGINT(12) NOT NULL
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Yderregister (
	YderregisterPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Nummer VARCHAR(30) NOT NULL,
	Telefon VARCHAR(10),
	Navn VARCHAR(256),
	Vejnavn VARCHAR(100),
	Postnummer VARCHAR(10),
	Bynavn VARCHAR(30),
	AmtNummer BIGINT(12),
	Email VARCHAR(100),
	Www VARCHAR(100),
	HovedSpecialeKode VARCHAR(100),
	HovedSpecialeTekst VARCHAR(100),
	HistID VARCHAR(100),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE YderregisterPerson (
	YderregisterPersonPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Id VARCHAR(20) NOT NULL,
	Nummer VARCHAR(30) NOT NULL,
	CPR VARCHAR(10),
	personrolleKode BIGINT(20),
	personrolleTxt VARCHAR(200),
	HistIDPerson VARCHAR(100),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;
