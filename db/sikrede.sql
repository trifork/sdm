/* "Sikrede" type 10  */
CREATE TABLE Sikrede (
	SikredePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR CHAR(10) NOT NULL,
    kommunekode CHAR(3) NOT NULL, /* Person.KommuneKode  */
    kommunekodeIkraftDato DATE, /* KommuneKode validFrom for person */
    foelgeskabsPersonCpr CHAR(10), /* CPR paa ledsager */
    status CHAR(2),
    bevisIkraftDato DATE, /* Sygesikringsbevis validFrom dato */
    /* Personens civilstand findes i AktuelCivilstand.CivilStandskode, Personens adresseinfo findes i Person.* */

    /* SSL elementer */
    forsikringsinstans VARCHAR(21) NOT NUll,
    forsikringsinstansKode VARCHAR(10) NOT NULL,
    forsikringsnummer VARCHAR(15) NOT NULL,
    sslGyldigFra DATE NOT NULL,
    sslGyldigTil DATE NOT NULL,
    socialLand VARCHAR(47) NOT NULL,
    socialLandKode CHAR(2) NOT NULL,

    ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	CONSTRAINT UC_Person_1 UNIQUE (CPR, ValidFrom),
	INDEX (ModifiedDate, SikredePID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

/* "Sikrede" type 10  YderRelation (For each Sikrede, there are three sikredeyderrelation-records - 'current', 'old', 'future') */
CREATE TABLE SikredeYderRelation (
	SikredeYderRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Id CHAR(12) NOT NULL,
	CPR CHAR(10) NOT NULL,
	
	type CHAR(1) NOT NULL, /* C = Current, P = Past, F = Future */

    /* Nuvaerende valg af yder */
	ydernummer MEDIUMINT(6) NOT NULL, 
	ydernummerIkraftDato DATE NOT NULL,
	sikringsgruppeKode CHAR(1) NOT NULL,
	gruppeKodeIkraftDato DATE NOT NULL,
	gruppekodeRegistreringDato DATE NOT NULL,
    ydernummerRegistreringDato DATE NOT NULL,

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	CONSTRAINT UC_Person_1 UNIQUE (id, ValidFrom),
	INDEX (ModifiedDate, SikredeYderRelationPID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

/* "Sikrede" type 10  SSK */
CREATE TABLE SaerligSundhedskort (
	SaerligSundhedskortPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR CHAR(10) NOT NULL,

    adresseLinje1 VARCHAR(40),
    adresseLinje2 VARCHAR(40),
    bopelsLand VARCHAR(40),
    bopelsLandKode VARCHAR(2),
    emailAdresse VARCHAR(50),
    familieRelationCpr CHAR(10), /* CPR paa familierelation */
    foedselsDato DATE,
    sskGyldigFra DATE,
    sskGyldigTil DATE,
    mobilNummer VARCHAR(20),
    postnummerBy VARCHAR(40),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	CONSTRAINT UC_Person_1 UNIQUE (CPR, ValidFrom),
	INDEX (ModifiedDate, SaerligSundhedskortPID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;
