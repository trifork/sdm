USE sdm_warehouse;

CREATE TABLE KeyValueStore (
	ownerId VARCHAR(200) NOT NULL,
	id VARCHAR(200) NOT NULL,
	value VARCHAR(200) NOT NULL,
	PRIMARY KEY (ownerId, id),
	INDEX (id)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- ADMINISTRATION TABLES (USERS ETC.)

CREATE TABLE Client (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(200) NOT NULL,
	subjectSerialNumber CHAR(200) NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Client_permissions (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	client_id BIGINT NOT NULL,
	permissions TEXT NOT NULL,
	FOREIGN KEY (client_id) REFERENCES Client(id) ON DELETE CASCADE
) ENGINE=InnoDB COLLATE=utf8_bin;

-- STAMDATA TABLES (ACTUAL RAW DATA)

CREATE TABLE Import (
	importtime DATETIME,
	spoolername VARCHAR(200)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE DosageStructure (
	DosageStructurePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	releaseNumber BIGINT(15) NOT NULL,
	
	code VARCHAR(11) NOT NULL, -- ID
	type VARCHAR(100) NOT NULL,
	simpleString VARCHAR(100), -- OPTIONAL
	supplementaryText VARCHAR(200), -- OPTIONAL
	xml VARCHAR(10000) NOT NULL,
	shortTranslation VARCHAR(70),
	longTranslation VARCHAR(10000), -- OPTIONAL (The specs say it cannot be NULL. See comment in DosageStructure.java)
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (DosageStructurePID, ModifiedDate),
	INDEX (code, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE DosageUnit (
	DosageUnitPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	code INT(4) NOT NULL, -- ID
	
	releaseNumber BIGINT(15) NOT NULL,
	textSingular VARCHAR(100) NOT NULL,
	textPlural VARCHAR(100) NOT NULL,
	
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (DosageUnitPID, ModifiedDate),
	INDEX (code, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE DosageVersion (
	DosageVersionPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	daDate DATE NOT NULL, -- @ID // TODO: Should the id not be the releaseNumber? :S
	
	lmsDate DATE NOT NULL,
	releaseDate DATE NOT NULL,
	releaseNumber BIGINT(15) NOT NULL,
	
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (DosageVersionPID, ModifiedDate),
	INDEX (releaseDate, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE DrugDosageStructureRelation (
	DrugDosageStructureRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	id VARCHAR(200) NOT NULL,
	
	drugId BIGINT(11) NOT NULL,
	dosageStructureCode BIGINT(11) NOT NULL,
	releaseNumber BIGINT(15) NOT NULL,
	
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (DrugDosageStructureRelationPID, ModifiedDate),
	INDEX (id, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE DosageDrug (
	DosageDrugPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	releaseNumber BIGINT(15) NOT NULL,
	
	drugId BIGINT(11) NOT NULL,
	dosageUnitCode BIGINT(11) NOT NULL,
	drugName VARCHAR(200) NOT NULL,
	
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (DosageDrugPID, ModifiedDate),
	INDEX (drugId, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Takst
--
CREATE TABLE Administrationsvej (
	AdministrationsvejPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	AdministrationsvejKode CHAR(2) NOT NULL,
	AdministrationsvejTekst VARCHAR(50) NOT NULL,
	AdministrationsvejKortTekst VARCHAR(10),
	
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (AdministrationsvejPID, ModifiedDate),
	INDEX (AdministrationsvejKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;


-- SOR
--
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
) ENGINE=InnoDB COLLATE=utf8_bin;


-- See LMS12 for documentation of this table.
--
CREATE TABLE ATC (
	ATCPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	ATC CHAR(8) NOT NULL,
	ATCTekst VARCHAR(72) NOT NULL,
	
	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (ATCPID, ModifiedDate),
	INDEX (ATC, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Autorisation (
	AutorisationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Autorisationsnummer CHAR(5) NOT NULL,
	
	CPR CHAR(10) NOT NULL,
	Fornavn VARCHAR(100) NOT NULL,
	Efternavn VARCHAR(100) NOT NULL,
	UddannelsesKode INT(4) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (AutorisationPID, ModifiedDate),
	INDEX (Autorisationsnummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- This table is used to hold the set of currently valid
-- autorisations. (Used by the STS)
--
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
	
	Id CHAR(21) NOT NULL,
	
	CPR CHAR(10) NOT NULL,
	BarnCPR CHAR(10) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (BarnRelationPID, ModifiedDate),
	INDEX (Id, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- LMS13
--
CREATE TABLE Beregningsregler (
	BeregningsreglerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Kode CHAR(1) NOT NULL,
	Tekst VARCHAR(50),
	
	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (BeregningsreglerPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- LMS??
--
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

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (DoseringPID, ModifiedDate),
	INDEX (DoseringKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE EmballagetypeKoder (
	EmballagetypeKoderPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Kode VARCHAR(4) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (EmballagetypeKoderPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Takst
--
CREATE TABLE Enhedspriser (
	EnhedspriserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Varenummer BIGINT(12) NOT NULL,
	
	DrugID BIGINT(12),
	PrisPrEnhed BIGINT(12),
	PrisPrDDD BIGINT(12),
	BilligstePakning VARCHAR(1),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (EnhedspriserPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Takst
--
CREATE TABLE Firma (
	FirmaPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Firmanummer BIGINT(12) NOT NULL,
	FirmamaerkeKort VARCHAR(20),
	FirmamaerkeLangtNavn VARCHAR(32),
	ParallelimportoerKode VARCHAR(2),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (FirmaPID, ModifiedDate),
	INDEX (Firmanummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- CPR
--
CREATE TABLE ForaeldreMyndighedRelation (
	ForaeldreMyndighedRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Id VARCHAR(21) NOT NULL,
	
	CPR VARCHAR(10) NOT NULL,
	TypeKode VARCHAR(4) NOT NULL,
	TypeTekst VARCHAR(50) NOT NULL,
	RelationCpr VARCHAR(10),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (ForaeldreMyndighedRelationPID, ModifiedDate),
	INDEX (Id, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Takst
--
CREATE TABLE Formbetegnelse (
	FormbetegnelsePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Kode VARCHAR(10) NOT NULL,
	
	Tekst VARCHAR(150) NOT NULL,
	Aktiv BOOLEAN,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (FormbetegnelsePID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

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
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (IndholdsstofferPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Indikation (
	IndikationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	IndikationKode BIGINT(15) NOT NULL,
	
	IndikationTekst VARCHAR(100),
	IndikationstekstLinie1 VARCHAR(26),
	IndikationstekstLinie2 VARCHAR(26),
	IndikationstekstLinie3 VARCHAR(26),
	aktiv BOOLEAN,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (IndikationPID, ModifiedDate),
	INDEX (IndikationKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IndikationATCRef (
	IndikationATCRefPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	CID VARCHAR(22) NOT NULL,
	
	IndikationKode BIGINT(15) NOT NULL,
	ATC VARCHAR(10) NOT NULL,
	DrugID BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (IndikationATCRefPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Kommune (
	KommunePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Nummer VARCHAR(12) NOT NULL,
	Navn VARCHAR(100) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (KommunePID, ModifiedDate),
	INDEX (Nummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

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
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (LaegemiddelPID, ModifiedDate),
	INDEX (DrugID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Laegemiddelnavn (
	LaegemiddelnavnPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	DrugID BIGINT(12) NOT NULL,
	LaegemidletsUforkortedeNavn VARCHAR(60),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (LaegemiddelnavnPID, ModifiedDate),
	INDEX (DrugID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE LaegemiddelAdministrationsvejRef (
	LaegemiddelAdministrationsvejRefPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	CID VARCHAR(22) NOT NULL,
	
	DrugID BIGINT(12) NOT NULL,
	AdministrationsvejKode CHAR(2) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (LaegemiddelAdministrationsvejRefPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE LaegemiddelDoseringRef (
	LaegemiddelDoseringRefPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	CID VARCHAR(22) NOT NULL,
	
	DrugID BIGINT(12) NOT NULL,
	DoseringKode BIGINT(12) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (LaegemiddelDoseringRefPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Klausulering (
	KlausuleringPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Kode VARCHAR(10) NOT NULL,
	KortTekst VARCHAR(60),
	Tekst VARCHAR(600),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (KlausuleringPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Medicintilskud (
	MedicintilskudPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Kode CHAR(2) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (MedicintilskudPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Opbevaringsbetingelser (
	OpbevaringsbetingelserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Kode CHAR(1) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (OpbevaringsbetingelserPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Takst
--
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
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (OplysningerOmDosisdispenseringPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;


-- SKS (for some reason called institution in code)
--
CREATE TABLE Organisation (
	OrganisationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Nummer VARCHAR(20) NOT NULL,
	Navn VARCHAR(60),
	Organisationstype VARCHAR(30) NOT NULL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (OrganisationPID, ModifiedDate),
	INDEX (Nummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

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
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (PakningPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

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
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (PakningskombinationerPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Takst
--
CREATE TABLE PakningskombinationerUdenPriser (
	PakningskombinationerUdenPriserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	VarenummerOrdineret BIGINT(12) NOT NULL, -- ID Column
	VarenummerSubstitueret BIGINT(12),
	VarenummerAlternativt BIGINT(12),
	AntalPakninger BIGINT(12),
	InformationspligtMarkering VARCHAR(1),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (PakningskombinationerUdenPriserPID, ModifiedDate),
	INDEX (VarenummerOrdineret, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Takst
--
CREATE TABLE Pakningsstoerrelsesenhed (
	PakningsstoerrelsesenhedPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	PakningsstoerrelsesenhedKode VARCHAR(10) NOT NULL,
	PakningsstoerrelsesenhedTekst VARCHAR(50) NOT NULL,
	PakningsstoerrelsesenhedKortTekst VARCHAR(10),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (PakningsstoerrelsesenhedPID, ModifiedDate),
	INDEX (PakningsstoerrelsesenhedKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- CPR
--
CREATE TABLE Person (
	PersonPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	CPR CHAR(10) NOT NULL,
	
	Koen CHAR(1) NOT NULL,
	Fornavn VARCHAR(50),
	Mellemnavn VARCHAR(40),
	Efternavn VARCHAR(40),
	CoNavn VARCHAR(34),
	Lokalitet VARCHAR(34),
	Vejnavn VARCHAR(30),
	Bygningsnummer VARCHAR(10),
	Husnummer VARCHAR(4),
	Etage VARCHAR(2),
	SideDoerNummer VARCHAR(4),
	Bynavn VARCHAR(34),
	Postnummer INT(4),
	PostDistrikt VARCHAR(20),
	Status CHAR(2),
	NavneBeskyttelseStartDato DATETIME,
	NavneBeskyttelseSletteDato DATETIME,
	GaeldendeCPR CHAR(10),
	Foedselsdato DATE NOT NULL,
	Stilling VARCHAR(50),
	VejKode INT(4), 
	KommuneKode INT(4),
	
	# Additions for CPR Service
	
	NavnTilAdressering VARCHAR(34),
	VejnavnTilAdressering VARCHAR(20),
	FoedselsdatoMarkering CHAR,
	StatusDato DATETIME,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (PersonPID, ModifiedDate),
	INDEX (CPR, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;


# This table is used by the GOS/CPR subscription service
# to know when there are changes to the CPR registry.
#
# NB. The modified date might not be identical to the modified
# date in the original table. (This is not important for our use.)

CREATE TABLE ChangesToCPR (
	CPR CHAR(10) PRIMARY KEY,
	ModifiedDate TIMESTAMP NOT NULL
);


CREATE TABLE PersonIkraft (
	PersonIkraftPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY	,
	
	IkraftDato DATETIME NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;

-- SOR
--
CREATE TABLE Praksis (
	PraksisPID BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	SorNummer BIGINT(20) NOT NULL,
	
	EanLokationsnummer BIGINT(20),
	RegionCode BIGINT(12),
	Navn VARCHAR(256),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (PraksisPID, ModifiedDate),
	INDEX (SorNummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;


-- Takst
--
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
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (PriserPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;


-- Takst
--
CREATE TABLE Rekommandationer (
	RekommandationerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Varenummer BIGINT(12) NOT NULL,
	
	Rekommandationsgruppe BIGINT(12),
	DrugID BIGINT(12),
	Rekommandationsniveau VARCHAR(25),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (RekommandationerPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;


-- Takst
--
CREATE TABLE SpecialeForNBS (
	SpecialeForNBSPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Kode VARCHAR(5) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (SpecialeForNBSPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;


-- Takst
--
CREATE TABLE Styrkeenhed (
	StyrkeenhedPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	StyrkeenhedKode VARCHAR(10) NOT NULL,
	StyrkeenhedTekst VARCHAR(50) NOT NULL,
	StyrkeenhedKortTekst VARCHAR(10),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (StyrkeenhedPID, ModifiedDate),
	INDEX (StyrkeenhedKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Takst
--
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
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (SubstitutionPID, ModifiedDate),
	INDEX (ReceptensVarenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Takst
--
CREATE TABLE SubstitutionAfLaegemidlerUdenFastPris (
	SubstitutionAfLaegemidlerUdenFastPrisPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Varenummer BIGINT(12) NOT NULL,
	Substitutionsgruppenummer BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (SubstitutionAfLaegemidlerUdenFastPrisPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;


-- SOR
--
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
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (SygeHusPID, ModifiedDate),
	INDEX (SorNummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- SOR
--
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
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (SygeHusAfdelingPID, ModifiedDate),
	INDEX (SorNummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Takst
--
CREATE TABLE TakstVersion (
	TakstVersionPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	TakstUge CHAR(7) NOT NULL,
	
	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (TakstVersionPID, ModifiedDate),
	INDEX (TakstUge, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;


CREATE TABLE Tidsenhed (
	TidsenhedPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	TidsenhedKode VARCHAR(10) NOT NULL,
	TidsenhedTekst VARCHAR(50) NOT NULL,
	TidsenhedKortTekst VARCHAR(10),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (TidsenhedPID, ModifiedDate),
	INDEX (TidsenhedKode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;


CREATE TABLE Tilskudsintervaller (
	TilskudsintervallerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	CID CHAR(4) NOT NULL,
	Type INT(2) NOT NULL,
	Niveau INT(1) NOT NULL,
	NedreGraense BIGINT(12),
	OevreGraense BIGINT(12),
	Procent DECIMAL,

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (TilskudsintervallerPID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE TilskudsprisgrupperPakningsniveau (
	TilskudsprisgrupperPakningsniveauPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Varenummer BIGINT(12) NOT NULL,
	
	TilskudsprisGruppe BIGINT(12),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (TilskudsprisgrupperPakningsniveauPID, ModifiedDate),
	INDEX (Varenummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE UdgaaedeNavne (
	UdgaaedeNavnePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	CID VARCHAR(71) NOT NULL,
	Drugid BIGINT(12),
	DatoForAendringen DATE,
	TidligereNavn VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (UdgaaedeNavnePID, ModifiedDate),
	INDEX (CID, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE Udleveringsbestemmelser (
	UdleveringsbestemmelserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	Kode VARCHAR(5) NOT NULL,
	
	Udleveringsgruppe VARCHAR(1),
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	CreatedDate DATETIME NOT NULL,
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (UdleveringsbestemmelserPID, ModifiedDate),
	INDEX (Kode, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

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
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (UmyndiggoerelseVaergeRelationPID, ModifiedDate),
	INDEX (Id, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- SOR/Yder
--
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
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	
	INDEX (YderPID, ModifiedDate),
	INDEX (SorNummer, ValidTo, ValidFrom)
) ENGINE=InnoDB COLLATE=utf8_bin;


-- "Sikrede"
--
CREATE TABLE Sikrede (
	PID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CPRnr VARCHAR(10),
	SYdernr VARCHAR(6),
	SIkraftDatoYder VARCHAR(8),
	SRegDatoYder VARCHAR(8),
	SSikrGrpKode VARCHAR(1),
	SIkraftDatoGrp VARCHAR(8),
	SRegDatoGrp VARCHAR(8),
	SSikrKomKode VARCHAR(3),
	SIkraftDatoKomKode VARCHAR(8),
	SYdernrGl VARCHAR(6),
	SIkraftDatoYderGl VARCHAR(8),
	SRegDatoYderGl VARCHAR(8),
	SSikrGrpKodeGl VARCHAR(1),
	SIkraftDatoGrpGl VARCHAR(8),
	SRegDatoGrpGl VARCHAR(8),
	SYdernrFrem VARCHAR(6),
	SIkraftDatoYderFrem VARCHAR(8),
	SRegDatoYderFrem VARCHAR(8),
	SSikrGrpKodeFrem VARCHAR(1),
	SIkraftDatoGrpFrem VARCHAR(8),
	SRegDatoGrpFrem VARCHAR(8),
	SKon VARCHAR(1),
	SAlder VARCHAR(3),
	SFolgerskabsPerson VARCHAR(10),
	SStatus VARCHAR(2),
	SBevisDato VARCHAR(8),
	PNavn VARCHAR(34),
	SBSStatsborgerskabKode VARCHAR(2),
	SBSStatsborgerskab VARCHAR(47),
	SSKAdrLinie1 VARCHAR(40),
	SSKAdrLinie2 VARCHAR(40),
	SSKBopelsLand VARCHAR(40),
	SSKBopelsLAndKode VARCHAR(2),
	SSKEmailAdr VARCHAR(50),
	SSKFamilieRelation VARCHAR(10),
	SSKFodselsdato VARCHAR(10),
	SSKGyldigFra VARCHAR(10),
	SSKGyldigTil VARCHAR(10),
	SSKMobilNr VARCHAR(20),
	SSKPostNrBy VARCHAR(40),
	SSLForsikringsinstans VARCHAR(21),
	SSLForsikringsinstansKode VARCHAR(10),
	SSLForsikringsnr VARCHAR(15),
	SSLGyldigFra VARCHAR(10),
	SSLGyldigTil VARCHAR(10),
	SSLSocSikretLand VARCHAR(47),
	SSLSocSikretLandKode VARCHAR(2),
	ValidFrom DateTime NOT NULL,
	ValidTo DateTime,
	ModifiedDate DateTime NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Yderregister - yder
--
CREATE TABLE Yderregister (
	PID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	HistIdYder VARCHAR(16),
	AmtKodeYder VARCHAR(2),
	AmtTxtYder VARCHAR(60),
	YdernrYder VARCHAR(6),
	PrakBetegn VARCHAR(50),
	AdrYder VARCHAR(50),
	PostnrYder VARCHAR(4),
	PostdistYder VARCHAR(20),
	TilgDatoYder VARCHAR(8),
	AfgDatoYder VARCHAR(8),
	HvdSpecKode VARCHAR(2),
	HvdSpecTxt VARCHAR(60),
	HvdTlf VARCHAR(8),
	EmailYder VARCHAR(50),
	WWW VARCHAR(78),
	ValidFrom DateTime NOT NULL,
	ValidTo DateTime,
	ModifiedDate DateTime NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;

-- Yderregister - person
--
CREATE TABLE YderregisterPerson (
	PID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	HistIdPerson VARCHAR(16),
	YdernrPerson VARCHAR(6),
	TilgDatoPerson VARCHAR(8),
	AfgDatoPerson VARCHAR(8),
	CprNr VARCHAR(10),
	PersonrolleKode VARCHAR(2),
	PersonrolleTxt VARCHAR(60),
	ValidFrom DateTime NOT NULL,
	ValidTo DateTime,
	ModifiedDate DateTime NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;


-- This table is to be replicated to the BRS db schema.
-- It is populated by stamdata, but not read by stamdata.
--
CREATE TABLE AssignedDoctor ( -- Sikrede
  pk bigint AUTO_INCREMENT NOT NULL,

    -- cpr numre er base64 af hashede numre
  patientCpr varchar(80) NOT NULL,

  doctorOrganisationIdentifier varchar(6) NOT NULL, -- ydernummer

  assignedFrom datetime NOT NULL,
  assignedTo datetime,

  reference varchar(40) NOT NULL,

  PRIMARY KEY (pk)
) ENGINE=InnoDB COLLATE=utf8_bin;