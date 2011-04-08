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