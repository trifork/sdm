CREATE TABLE Folkekirkeoplysninger (
	FolkekirkeoplysningerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Forholdskode VARCHAR(1) NOT NULL,
	Startdato DATETIME,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	CONSTRAINT FKO_Person_1 UNIQUE (CPR, ValidFrom)
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
	INDEX (ValidFrom, ValidTo),
	CONSTRAINT Udrejse_Person_1 UNIQUE (CPR, ValidFrom)
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
	INDEX (ValidFrom, ValidTo),
	CONSTRAINT Foedsel_Person_1 UNIQUE (CPR, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Valgoplysninger (
	ValgoplysningerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Valgkode VARCHAR(1) NOT NULL,
	Valgretsdato DATETIME,
	Startdato DATETIME NOT NULL,
	Slettedato DATETIME,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	CONSTRAINT VO_Person_1 UNIQUE (CPR, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE KommunaleForhold (
	KommunaleForholdPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Kommunalforholdstypekode VARCHAR(1) NOT NULL,
	Kommunalforholdskode VARCHAR(5) NOT NULL,
	Startdato DATETIME NOT NULL,
	Bemaerkninger VARCHAR(30) NOT NULL,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	CONSTRAINT KommunaleForhold_Person_1 UNIQUE (CPR, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE AktuelCivilstand (
	CivilstandPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Civilstandskode VARCHAR(1),
	Aegtefaellepersonnummer VARCHAR(10),
	Aegtefaellefoedselsdato DATETIME,
	Aegtefaellenavn VARCHAR(34),
	Startdato DATETIME,
	Separation DATETIME,
	ModifiedBy VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200) NOT NULL,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	CONSTRAINT KommunaleForhold_Person_1 UNIQUE (CPR, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

