USE sdm_warehouse;

-- ADMINISTRATION TABLES (USERS ETC.)

CREATE TABLE Client (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(200) NOT NULL,
	subjectSerialNumber CHAR(200) NOT NULL
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Client_permissions (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	client_id BIGINT NOT NULL,
	permissions TEXT NOT NULL,
	FOREIGN KEY (client_id) REFERENCES Client(id) ON DELETE CASCADE
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE LogEntry (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	message VARCHAR(500),
	createdAt DATETIME NOT NULL
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Authorization (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	cvr CHAR(8) NOT NULL,
	viewName VARCHAR(200) NOT NULL,
	token BLOB(512) NOT NULL,
	expiresAt DATETIME NOT NULL
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- STAMDATA TABLES (ACTUAL RAW DATA)

CREATE TABLE Import (
	importtime DATETIME,
	spoolername VARCHAR(200)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE DosageStructure (
	DosageStructurePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	releaseNumber BIGINT(15) NOT NULL,
	code VARCHAR(11) NOT NULL,
	type VARCHAR(100) NOT NULL,
	simpleString VARCHAR(100), -- OPTIONAL
	supplementaryText VARCHAR(200), -- OPTIONAL
	xml VARCHAR(10000) NOT NULL,
	shortTranslation VARCHAR(70),
	longTranslation VARCHAR(10000), -- OPTIONAL (The specs say it cannot be NULL. See comment in DosageStructure.java)
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME,
	
	INDEX (DosageStructurePID, ModifiedDate),
	INDEX (code, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE DosageUnit (
	DosageUnitPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	releaseNumber BIGINT(15) NOT NULL,
	code INT(4),
	textSingular VARCHAR(100) NOT NULL,
	textPlural VARCHAR(100) NOT NULL,
	ModifiedDate DATETIME,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME,
	
	INDEX (DosageUnitPID, ModifiedDate),
	INDEX (code, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE DosageVersion (
	DosageVersionPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	daDate DATE NOT NULL,
	lmsDate DATE NOT NULL,
	releaseDate DATE NOT NULL,
	releaseNumber BIGINT(15) NOT NULL,
	ModifiedDate DATETIME,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME,
	
	INDEX (DosageVersionPID, ModifiedDate),
	INDEX (releaseDate, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE DrugDosageStructureRelation (
	DrugDosageStructureRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	id VARCHAR(200) NOT NULL,
	drugId BIGINT(11) NOT NULL,
	dosageStructureCode BIGINT(11) NOT NULL,
	releaseNumber BIGINT(15) NOT NULL,
	ModifiedDate DATETIME,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME,
	
	INDEX (DrugDosageStructureRelationPID, ModifiedDate),
	INDEX (id, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE DosageDrug (
	DosageDrugPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	releaseNumber BIGINT(15) NOT NULL,
	drugId BIGINT(11) NOT NULL,
	dosageUnitCode BIGINT(11) NOT NULL,
	drugName VARCHAR(200) NOT NULL,
	ModifiedDate DATETIME,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME,
	
	INDEX (DosageDrugPID, ModifiedDate),
	INDEX (drugId, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Administrationsvej (
	AdministrationsvejPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	AdministrationsvejKode CHAR(2) NOT NULL,
	AdministrationsvejTekst VARCHAR(50) NOT NULL,
	AdministrationsvejKortTekst VARCHAR(10),
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	CreatedDate DATETIME,
	
	INDEX (AdministrationsvejPID, ModifiedDate),
	INDEX (AdministrationsvejKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

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
	
	INDEX (ApotekPID, ModifiedDate),
	INDEX (SorNummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- See LMS12 for documentation of this table.

CREATE TABLE ATC (
	ATCPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	ATC CHAR(8) NOT NULL,
	ATCTekst VARCHAR(72) NOT NULL,
	
	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (ATCPID, ModifiedDate),
	INDEX (ATC, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Autorisation (
	AutorisationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Autorisationsnummer VARCHAR(10) NOT NULL, -- TODO: This should be CHAR(5)
	cpr VARCHAR(10) NOT NULL,
	Fornavn VARCHAR(100) NOT NULL,
	Efternavn VARCHAR(100) NOT NULL,
	UddannelsesKode INT(4) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (AutorisationPID, ModifiedDate),
	INDEX (Autorisationsnummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- This table is used to hold the set of currently valid
-- autorisations.

CREATE TABLE autreg (
  id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  cpr CHAR(10) NOT NULL,
  given_name VARCHAR(50) NOT NULL,
  surname VARCHAR(100) NOT NULL,
  aut_id CHAR(5) NOT NULL,
  edu_id CHAR(4) NOT NULL,
  KEY cpr_aut_id (cpr, aut_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE BarnRelation (
	BarnRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Id VARCHAR(21) NOT NULL,
	CPR VARCHAR(10) NOT NULL,
	BarnCPR VARCHAR(10) NOT NULL,

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (BarnRelationPID, ModifiedDate),
	INDEX (Id, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Beregningsregler (
	BeregningsreglerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Kode VARCHAR(1) NOT NULL,
	Tekst VARCHAR(50), 
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (BeregningsreglerPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Dosering (
	DoseringPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	DoseringKode BIGINT(12) NOT NULL,
	DoseringTekst VARCHAR(100) NOT NULL,
	AntalEnhederPrDoegn FLOAT(10) NOT NULL,
	Aktiv BOOLEAN,
	DoseringKortTekst VARCHAR(10),
	DoseringstekstLinie1 VARCHAR(26),
	DoseringstekstLinie2 VARCHAR(26),
	DoseringstekstLinie3 VARCHAR(26),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (DoseringPID, ModifiedDate),
	INDEX (DoseringKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE EmballagetypeKoder (
	EmballagetypeKoderPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Kode VARCHAR(4) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (EmballagetypeKoderPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Enhedspriser (
	EnhedspriserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Varenummer BIGINT(12) NOT NULL,
	DrugID BIGINT(12),
	PrisPrEnhed BIGINT(12),
	PrisPrDDD BIGINT(12),
	BilligstePakning VARCHAR(1),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (EnhedspriserPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Firma (
	FirmaPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Firmanummer BIGINT(12) NOT NULL,
	FirmamaerkeKort VARCHAR(20),
	FirmamaerkeLangtNavn VARCHAR(32),
	ParallelimportoerKode VARCHAR(2),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (FirmaPID, ModifiedDate),
	INDEX (Firmanummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE ForaeldreMyndighedRelation (
	ForaeldreMyndighedRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Id VARCHAR(21) NOT NULL,
	CPR VARCHAR(10) NOT NULL,
	TypeKode VARCHAR(4) NOT NULL,
	TypeTekst VARCHAR(50) NOT NULL,
	RelationCpr VARCHAR(10),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ForaeldreMyndighedRelationPID, ModifiedDate),
	INDEX (Id, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Formbetegnelse (
	FormbetegnelsePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Kode VARCHAR(10) NOT NULL,
	Tekst VARCHAR(150) NOT NULL,
	Aktiv BOOLEAN,

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,

	INDEX (FormbetegnelsePID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Indholdsstoffer (
	IndholdsstofferPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY, 
	CID VARCHAR(364) NOT NULL,
	DrugID BIGINT(12),
	Varenummer BIGINT(12),
	Stofklasse VARCHAR(100),
	Substansgruppe VARCHAR(100),
	Substans VARCHAR(150),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (IndholdsstofferPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Indikation (
	IndikationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	IndikationKode BIGINT(15),
	IndikationTekst VARCHAR(100),
	IndikationstekstLinie1 VARCHAR(26),
	IndikationstekstLinie2 VARCHAR(26),
	IndikationstekstLinie3 VARCHAR(26),
	aktiv BOOLEAN,
	
	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (IndikationPID, ModifiedDate),
	INDEX (IndikationKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE IndikationATCRef (
	IndikationATCRefPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CID VARCHAR(22) NOT NULL,
	IndikationKode BIGINT(15) NOT NULL,
	ATC VARCHAR(10) NOT NULL,
	DrugID BIGINT(12),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (IndikationATCRefPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Kommune (
	KommunePID BIGINT(15) NOT NULL PRIMARY KEY,
	Nummer VARCHAR(12) NOT NULL,
	Navn VARCHAR(100) NOT NULL,

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (KommunePID, ModifiedDate),
	INDEX (Nummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Laegemiddel (
	LaegemiddelPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	DrugID BIGINT(12) NOT NULL,
	DrugName VARCHAR(30), /* Some drugs are not named. */
	FormKode VARCHAR(10),
	FormTekst VARCHAR(150),
	ATCKode VARCHAR(10),
	ATCTekst VARCHAR(100),
	StyrkeNumerisk DECIMAL(10,3),
	StyrkeEnhed VARCHAR(100),
	StyrkeTekst VARCHAR(30),
	Dosisdispenserbar BOOLEAN,
	Varetype VARCHAR(2),
	Varedeltype VARCHAR(2),
	AlfabetSekvensplads VARCHAR(9),
	SpecNummer BIGINT(12),
	LaegemiddelformTekst VARCHAR(20),
	KodeForYderligereFormOplysn VARCHAR(7),
	Trafikadvarsel BOOLEAN,
	Substitution VARCHAR(1),
	LaegemidletsSubstitutionsgruppe VARCHAR(4),
	DatoForAfregistrAfLaegemiddel DATE,
	Karantaenedato DATE,
	AdministrationsvejKode VARCHAR(8),
	MTIndehaverKode BIGINT(12),
	RepraesentantDistributoerKode BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (LaegemiddelPID, ModifiedDate),
	INDEX (DrugID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Laegemiddelnavn (
	LaegemiddelnavnPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	DrugID BIGINT(12) NOT NULL,
	LaegemidletsUforkortedeNavn VARCHAR(60),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (LaegemiddelnavnPID, ModifiedDate),
	INDEX (DrugID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE LaegemiddelAdministrationsvejRef (
	LaegemiddelAdministrationsvejRefPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CID VARCHAR(22) NOT NULL,
	DrugID BIGINT(12) NOT NULL,
	AdministrationsvejKode CHAR(2) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (LaegemiddelAdministrationsvejRefPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE LaegemiddelDoseringRef (
	LaegemiddelDoseringRefPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CID VARCHAR(22) NOT NULL,
	DrugID BIGINT(12) NOT NULL,
	DoseringKode BIGINT(12) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (LaegemiddelDoseringRefPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Klausulering (
	KlausuleringPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Kode VARCHAR(10) NOT NULL,
	KortTekst VARCHAR(60),
	Tekst VARCHAR(600),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (KlausuleringPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Medicintilskud (
	MedicintilskudPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Kode CHAR(2) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (MedicintilskudPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Opbevaringsbetingelser (
	OpbevaringsbetingelserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Kode VARCHAR(1) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (OpbevaringsbetingelserPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE OplysningerOmDosisdispensering (
	OplysningerOmDosisdispenseringPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Varenummer BIGINT(12) NOT NULL,
	DrugID BIGINT(12),
	LaegemidletsSubstitutionsgruppe VARCHAR(4),
	MindsteAIPPrEnhed BIGINT(12),
	MindsteRegisterprisEnh BIGINT(12),
	TSPPrEnhed BIGINT(12),
	KodeForBilligsteDrugid VARCHAR(1),
	BilligsteDrugid BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (OplysningerOmDosisdispenseringPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Organisation (
	OrganisationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Nummer VARCHAR(30) NOT NULL,
	Navn VARCHAR(256),
	Organisationstype VARCHAR(30) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (OrganisationPID, ModifiedDate),
	INDEX (Nummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Pakning (
	PakningPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Varenummer BIGINT(12) NOT NULL,
	VarenummerDelpakning BIGINT(12),
	DrugID DECIMAL(12) NOT NULL,
	PakningsstoerrelseNumerisk DECIMAL(10,2),
	Pakningsstoerrelsesenhed VARCHAR(10),
	PakningsstoerrelseTekst VARCHAR(30),
	EmballageTypeKode VARCHAR(10),
	Dosisdispenserbar BOOLEAN,
	MedicintilskudsKode VARCHAR(10),
	KlausuleringsKode VARCHAR(10),
	AlfabetSekvensnr BIGINT(12),
	AntalDelpakninger BIGINT(12),
	Udleveringsbestemmelse VARCHAR(5),
	UdleveringSpeciale VARCHAR(5),
	AntalDDDPrPakning DECIMAL,
	OpbevaringstidNumerisk BIGINT(12),
	Opbevaringstid BIGINT(12),
	Opbevaringsbetingelser VARCHAR(1),
	Oprettelsesdato DATE,
	DatoForSenestePrisaendring DATE,
	UdgaaetDato DATE,
	BeregningskodeAIRegpris CHAR(1),
	PakningOptagetITilskudsgruppe BOOLEAN,
	Faerdigfremstillingsgebyr BOOLEAN,
	Pakningsdistributoer BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (PakningPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Pakningskombinationer (
	PakningskombinationerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CID VARCHAR(23) NOT NULL,
	VarenummerOrdineret BIGINT(12),
	VarenummerSubstitueret BIGINT(12),
	VarenummerAlternativt BIGINT(12),
	AntalPakninger BIGINT(12),
	EkspeditionensSamledePris BIGINT(12),
	InformationspligtMarkering VARCHAR(1),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (PakningskombinationerPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE PakningskombinationerUdenPriser (
	PakningskombinationerUdenPriserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	VarenummerOrdineret BIGINT(12) NOT NULL, -- ID Column
	VarenummerSubstitueret BIGINT(12),
	VarenummerAlternativt BIGINT(12),
	AntalPakninger BIGINT(12),
	InformationspligtMarkering VARCHAR(1),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (PakningskombinationerUdenPriserPID, ModifiedDate),
	INDEX (VarenummerOrdineret, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Pakningsstoerrelsesenhed (
	PakningsstoerrelsesenhedPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	PakningsstoerrelsesenhedKode VARCHAR(10) NOT NULL,
	PakningsstoerrelsesenhedTekst VARCHAR(50) NOT NULL,
	PakningsstoerrelsesenhedKortTekst VARCHAR(10),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (PakningsstoerrelsesenhedPID, ModifiedDate),
	INDEX (PakningsstoerrelsesenhedKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

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
	Foedselsdato DATE NOT NULL,
	Stilling VARCHAR(50),
	VejKode BIGINT(12), 
	KommuneKode BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (PersonPID, ModifiedDate),
	INDEX (CPR, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE PersonIkraft (
	PersonIkraftPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY	,
	IkraftDato DATETIME NOT NULL
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Praksis (
	praksisPID BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	SorNummer BIGINT(20) NOT NULL,
	EanLokationsnummer BIGINT(20),
	RegionCode BIGINT(12),
	Navn VARCHAR(256),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (PraksisPID, ModifiedDate),
	INDEX (SorNummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Priser (
	PriserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Varenummer BIGINT(12) NOT NULL,
	apoteketsIndkoebspris BIGINT(12),
	Registerpris BIGINT(12),
	ekspeditionensSamledePris BIGINT(12),
	tilskudspris BIGINT(12),
	LeveranceprisTilHospitaler BIGINT(12),
	IkkeTilskudsberettigetDel BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (PriserPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Rekommandationer (
	RekommandationerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Varenummer BIGINT(12) NOT NULL,
	Rekommandationsgruppe BIGINT(12),
	DrugID BIGINT(12),
	Rekommandationsniveau VARCHAR(25),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (RekommandationerPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE SpecialeForNBS (
	SpecialeForNBSPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Kode VARCHAR(5) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (SpecialeForNBSPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Styrkeenhed (
	StyrkeenhedPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	StyrkeenhedKode VARCHAR(10) NOT NULL,
	StyrkeenhedTekst VARCHAR(50) NOT NULL,
	StyrkeenhedKortTekst VARCHAR(10),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (StyrkeenhedPID, ModifiedDate),
	INDEX (StyrkeenhedKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Substitution (
	SubstitutionPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	ReceptensVarenummer BIGINT(12) NOT NULL,
	Substitutionsgruppenummer BIGINT(12),
	NumeriskPakningsstoerrelse BIGINT(12),
	ProdAlfabetiskeSekvensplads VARCHAR(9),
	SubstitutionskodeForPakning VARCHAR(1),
	BilligsteVarenummer BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (SubstitutionPID, ModifiedDate),
	INDEX (ReceptensVarenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE SubstitutionAfLaegemidlerUdenFastPris (
	SubstitutionAfLaegemidlerUdenFastPrisPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Varenummer BIGINT(12) NOT NULL,
	Substitutionsgruppenummer BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (SubstitutionAfLaegemidlerUdenFastPrisPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
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

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (SygeHusPID, ModifiedDate),
	INDEX (SorNummer, ValidTo, ValidFrom)
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

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (SygeHusAfdelingPID, ModifiedDate),
	INDEX (SorNummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE TakstVersion (
	TakstVersionPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	TakstUge VARCHAR(8) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (TakstVersionPID, ModifiedDate),
	INDEX (TakstUge, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Tidsenhed (
	TidsenhedPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	TidsenhedKode VARCHAR(10) NOT NULL,
	TidsenhedTekst VARCHAR(50) NOT NULL,
	TidsenhedKortTekst VARCHAR(10),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (TidsenhedPID, ModifiedDate),
	INDEX (TidsenhedKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Tilskudsintervaller (
	TilskudsintervallerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CID VARCHAR(4) NOT NULL,
	Type BIGINT(12),
	Niveau BIGINT(12),
	NedreGraense BIGINT(12),
	OevreGraense BIGINT(12),
	Procent DECIMAL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (TilskudsintervallerPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE TilskudsprisgrupperPakningsniveau (
	TilskudsprisgrupperPakningsniveauPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Varenummer BIGINT(12) NOT NULL,
	TilskudsprisGruppe BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (TilskudsprisgrupperPakningsniveauPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE UdgaaedeNavne (
	UdgaaedeNavnePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CID VARCHAR(71) NOT NULL,
	Drugid BIGINT(12),
	DatoForAendringen DATE,
	TidligereNavn VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (UdgaaedeNavnePID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Udleveringsbestemmelser (
	UdleveringsbestemmelserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Kode VARCHAR(5) NOT NULL,
	Udleveringsgruppe VARCHAR(1),
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (UdleveringsbestemmelserPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
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

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (UmyndiggoerelseVaergeRelationPID, ModifiedDate),
	INDEX (Id, ValidTo, ValidFrom)
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

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (YderPID, ModifiedDate),
	INDEX (SorNummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

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

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (YderregisterPID, ModifiedDate),
	INDEX (Nummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE YderregisterPerson (
	YderregisterPersonPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Id VARCHAR(20) NOT NULL,
	Nummer VARCHAR(30) NOT NULL,
	CPR VARCHAR(10),
	personrolleKode BIGINT(20),
	personrolleTxt VARCHAR(200),
	HistIDPerson VARCHAR(100),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (YderregisterPersonPID, ModifiedDate),
	INDEX (Id, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Folkekirkeoplysninger (
	FolkekirkeoplysningerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR CHAR(10) NOT NULL,
	Forholdskode CHAR(1) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, FolkekirkeoplysningerPID),
	CONSTRAINT FKO_Person_1 UNIQUE (CPR, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Udrejseoplysninger (
	UdrejseoplysningerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	UdrejseLandekode VARCHAR(4) NOT NULL,
	Udrejsedato DATETIME NOT NULL,
	UdrejsedatoUsikkerhedsmarkering CHAR(1) NOT NULL,
	Udlandsadresse1 VARCHAR(34) NOT NULL,
	Udlandsadresse2 VARCHAR(34) NOT NULL,
	Udlandsadresse3 VARCHAR(34) NOT NULL,
	Udlandsadresse4 VARCHAR(34) NOT NULL,
	Udlandsadresse5 VARCHAR(34) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, UdrejseoplysningerPID),
	CONSTRAINT Udrejse_Person_1 UNIQUE (CPR, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Foedselsregistreringsoplysninger(
	FoedselsregistreringsoplysningerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Foedselsregistreringsstedkode VARCHAR(4) NOT NULL,
	foedselsregistreringstekst VARCHAR(20) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, FoedselsregistreringsoplysningerPID),
	CONSTRAINT Foedsel_Person_1 UNIQUE (CPR, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Statsborgerskab(
	StatsborgerskabPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	landekode VARCHAR(4) NOT NULL,
	StatsborgerskabstartdatoUsikkerhedsmarkering CHAR(1) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, StatsborgerskabPID),
	CONSTRAINT Statsborgerskab_Person_1 UNIQUE (CPR, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Valgoplysninger (
	ValgoplysningerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Valgkode VARCHAR(1) NOT NULL,
	Valgretsdato DATETIME,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, ValgoplysningerPID),
	CONSTRAINT VO_Person_1 UNIQUE (CPR, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE KommunaleForhold (
	KommunaleForholdPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR VARCHAR(10) NOT NULL,
	Kommunalforholdstypekode VARCHAR(1) NOT NULL,
	Kommunalforholdskode VARCHAR(5) NOT NULL,
	Bemaerkninger VARCHAR(30) NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, KommunaleForholdPID),
	CONSTRAINT KommunaleForhold_Person_1 UNIQUE (CPR, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE AktuelCivilstand (
	CivilstandPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPR CHAR(10) NOT NULL,
	Civilstandskode VARCHAR(1),
	Aegtefaellepersonnummer CHAR(10),
	Aegtefaellefoedselsdato DATETIME,
	Aegtefaellenavn VARCHAR(34),
	Separation DATETIME,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, CivilstandPID),
	CONSTRAINT AktuelCivilstand_Person_1 UNIQUE (CPR, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Haendelse (
	HaendelsePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Uuid VARCHAR(100) NOT NULL,
	CPR CHAR(10) NOT NULL,
	Ajourfoeringsdato DATETIME,
	Haendelseskode VARCHAR(3),
	AfledtMarkering VARCHAR(2),
	Noeglekonstant VARCHAR(15),
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo),
	INDEX(modifiedDate, HaendelsePID)	
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE UsageLogEntry (
	UsageLogEntryPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	ClientId VARCHAR(100) NOT NULL,
	Date DATETIME NOT NULL,
	Type VARCHAR(200) NOT NULL,
	Amount INTEGER NOT NULL,
	INDEX (ClientId, UsageLogEntryPID)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;


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

	CreatedDate DATETIME NOT NULL,
    ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	
	INDEX (SikredePID, ModifiedDate),
	INDEX (CPR, ValidTo, ValidFrom)
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

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME,
	
	INDEX (SikredeYderRelationPID, ModifiedDate),
	INDEX (Id, ValidTo, ValidFrom)
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

	INDEX (SaerligSundhedskortPID, ModifiedDate),
	INDEX (CPR, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;
