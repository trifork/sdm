CREATE TABLE TakstVersion (
	PID SERIAL,
	
	TakstUge VARCHAR(8) NOT NULL,
	
	ModifiedDate DATETIME NOT NULL,
	CreatedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS11
CREATE TABLE Administrationsvej (
	PID SERIAL,
	
	Kode CHAR(2) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50) NOT NULL,
	
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME NOT NULL,
	ValidTo DATETIME NOT NULL,
	CreatedDate DATETIME,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS12
CREATE TABLE ATC (
	PID SERIAL,
	
	Kode CHAR(8) NOT NULL,
	Tekst VARCHAR(72) NOT NULL,
	
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS13
CREATE TABLE Beregningsregler (
	PID SERIAL,
	
	Kode CHAR(1) NOT NULL,
	Tekst VARCHAR(50), 
	
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS28
CREATE TABLE Dosering (
	PID SERIAL,
	
	Kode BIGINT(7) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(78) NOT NULL,
	AntalEnhederPrDoegn BIGINT(9) NOT NULL,
	Aktiv CHAR(1),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS14
CREATE TABLE EmballagetypeKoder (
	PID SERIAL,
	
	Kode VARCHAR(4) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS31
CREATE TABLE Enhedspriser (
	PID SERIAL,
	
	DrugID BIGINT(11),
	Varenummer INT(6) NOT NULL,
	PrisPrEnhed BIGINT(9),
	PrisPrDDD BIGINT(9),
	BilligstePakning CHAR(1),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS09
CREATE TABLE Firma (
	PID SERIAL,
	
	Firmanummer INT(6) NOT NULL,
	FirmamaerkeKort VARCHAR(20),
	FirmamaerkeLangtNavn VARCHAR(32),
	ParallelimportoerKode VARCHAR(2),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS22
CREATE TABLE Formbetegnelse (
	PID SERIAL,
	
	Kode VARCHAR(7) NOT NULL,
	Tekst VARCHAR(100) NOT NULL,
	Aktiv BOOLEAN,

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS30
CREATE TABLE Indholdsstof (
	PID SERIAL,
	
	CID VARCHAR(364) NOT NULL,
	DrugID BIGINT(11),
	Varenummer INT(6),
	Stofklasse VARCHAR(100),
	Substansgruppe VARCHAR(100),
	Substans VARCHAR(150),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS26
CREATE TABLE Indikation (
	PID SERIAL,
	
	Kode INT(7),
	Tekst VARCHAR(78),
	Aktiv CHAR(1),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS25
CREATE TABLE Indikationskode (
	PID SERIAL,
	
	CID VARCHAR(22) NOT NULL,
	
	ATC VARCHAR(8) NOT NULL,
	Kode INT(7) NOT NULL,
	DrugID BIGINT(11),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS01
CREATE TABLE Laegemiddel (
	PID SERIAL,
	
	DrugID BIGINT(11) NOT NULL,
	Varetype VARCHAR(2),
	Varedeltype VARCHAR(2),
	AlfabetSekvensplads VARCHAR(9),
	SpecNummer INT(5),
	DrugName VARCHAR(30),
	LaegemiddelformTekst VARCHAR(20),
	FormKode VARCHAR(10),
	KodeForYderligereFormOplysn VARCHAR(7),
	StyrkeTekst VARCHAR(20),
	StyrkeNumerisk DECIMAL(10,3), -- TODO: Change this type!
	StyrkeEnhed VARCHAR(3),
	MTIndehaverKode INT(6),
	RepraesentantDistributoerKode INT(6),
	ATC VARCHAR(8),
	Administrationsvej VARCHAR(8),
	Trafikadvarsel CHAR(1),
	Substitution CHAR(1),	
	LaegemidletsSubstitutionsgruppe VARCHAR(4),
	Dosisdispenserbar CHAR(1),
	DatoForAfregistrAfLaegemiddel DATE,
	Karantaenedato DATE,

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS21
CREATE TABLE Laegemiddelnavn (
	PID SERIAL,
	
	DrugID BIGINT(11) NOT NULL,
	LaegemidletsUforkortedeNavn VARCHAR(60),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS27
CREATE TABLE Doseringskode (
	PID SERIAL,
	
	CID VARCHAR(22) NOT NULL,
	DrugID BIGINT(11) NOT NULL,
	Doseringkode BIGINT(7) NOT NULL,

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS17
CREATE TABLE Klausulering (
	PID SERIAL,
	
	Kode VARCHAR(10) NOT NULL,
	KortTekst VARCHAR(50),
	Tekst VARCHAR(500),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS16
CREATE TABLE Medicintilskud (
	PID SERIAL,
	
	Kode CHAR(2) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS20
CREATE TABLE Opbevaringsbetingelse (
	PID SERIAL,
	
	Kode CHAR(1) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS24
CREATE TABLE Dosisdispensering (
	PID SERIAL,
	
	DrugID BIGINT(11),
	Varenummer INT(6) NOT NULL,
	
	LaegemidletsSubstitutionsgruppe VARCHAR(4),
	MindsteAIPPrEnhed BIGINT(9),
	MindsteRegisterprisEnh BIGINT(9),
	TSPPrEnhed BIGINT(9),
	KodeForBilligsteDrugid CHAR(1),
	BilligsteDrugid BIGINT(11),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS02
CREATE TABLE Pakning (
	PID SERIAL,
	
	DrugID BIGINT(11) NOT NULL,
	Varenummer INT(6) NOT NULL,
	AlfabetSekvensnr BIGINT(3),
	VarenummerDelpakning INT(6),
	AntalDelpakninger INT(3),
	PakningsstoerrelseTekst VARCHAR(30),
	PakningsstoerrelseNumerisk BIGINT(8),
	Pakningsstoerrelsesenhed VARCHAR(2),
	EmballageTypeKode VARCHAR(4),
	Udleveringsbestemmelse VARCHAR(5),
	UdleveringSpeciale VARCHAR(5),
	MedicintilskudsKode VARCHAR(2),
	KlausuleringsKode VARCHAR(5),
	AntalDDDPrPakning BIGINT(9),
	OpbevaringstidNumerisk TINYINT(2),
	OpbevaringstidEnhed CHAR(1),
	Opbevaringsbetingelser CHAR(1),
	Oprettelsesdato DATE,
	DatoForSenestePrisaendring DATE,
	UdgaaetDato DATE,
	BeregningskodeFraAIPTilRegPris CHAR(1),
	PakningOptagetITilskudsgruppe CHAR(1),
	Faerdigfremstillingsgebyr CHAR(1),
	Pakningsdistributoer INT(6),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS32
CREATE TABLE Pakningskombination (
	PID SERIAL,
	
	CID VARCHAR(23) NOT NULL,
	VarenummerOrdineret INT(6),
	VarenummerSubstitueret INT(6),
	VarenummerAlternativt INT(6),
	AntalPakninger TINYINT(2),
	EkspeditionensSamledePris BIGINT(9),
	InformationspligtMarkering CHAR(1),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

 -- LMS33
CREATE TABLE PakningskombinationUdenPris (
	PID SERIAL,
	
	VarenummerOrdineret INT(6) NOT NULL, -- ID Column
	VarenummerSubstitueret INT(6),
	VarenummerAlternativt INT(6),
	AntalPakninger TINYINT(2),
	InformationspligtMarkering CHAR(1),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS15
CREATE TABLE Enhed (
	PID SERIAL,
	
	CID VARCHAR(11), -- Type + Kode
	
	Type TINYINT(1),
	Kode VARCHAR(10) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50) NOT NULL,
	
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME,
	
 	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS03
CREATE TABLE Priser (
	PID SERIAL,
	
	Varenummer INT(6) NOT NULL, 
	ApoteketsIndkoebspris BIGINT(9),
	Registerpris BIGINT(9),
	EkspeditionensSamledePris BIGINT(9),
	Tilskudspris BIGINT(12),
	LeveranceprisTilHospitaler BIGINT(9),
	IkkeTilskudsberettigetDel BIGINT(9),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS29
CREATE TABLE Rekommandation (
	PID SERIAL,
	
	Rekommandationsgruppe BIGINT(4),
	DrugID BIGINT(11),
	Varenummer BIGINT(6) NOT NULL,
	Rekommandationsniveau VARCHAR(25),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS19
CREATE TABLE SpecialeForNBS (
	PID SERIAL,
	
	Kode VARCHAR(5) NOT NULL,
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS04
CREATE TABLE Substitution (
	PID SERIAL,
	
	Substitutionsgruppenummer INT(4),
	ReceptensVarenummer INT(6) NOT NULL,
	NumeriskPakningsstoerrelse BIGINT(8),
	ProdAlfabetiskeSekvensplads VARCHAR(9),
	SubstitutionskodeForPakning CHAR(1),
	BilligsteVarenummer INT(6),
	
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,

	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS05
CREATE TABLE SubstitutionAfLegemiddelUdenFastPris (
	PID SERIAL,
	
	Substitutionsgruppenummer INT(4),
	Varenummer INT(6) NOT NULL,
	
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,

	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS23
CREATE TABLE Tilskudsinterval (
	PID SERIAL,
	
	CID VARCHAR(4) NOT NULL,
	
	Type TINYINT(2),
	Niveau TINYINT(1),
	NedreGraense BIGINT(8),
	OevreGraense BIGINT(8),
	Procent INT(5),
	
	ModifiedDate DATETIME NOT NULL,
	CreatedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,

	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS07
CREATE TABLE TilskudsprisgruppePaaPakningsniveau (
	PID SERIAL,
	
	Varenummer BIGINT(6) NOT NULL,
	TilskudsprisGruppe MEDIUMINT(4),
	
	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,

	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS10
CREATE TABLE UdgaaetNavn (
	PID SERIAL,
	
	CID VARCHAR(71) NOT NULL,
	
	DrugID BIGINT(11),
	DatoForAendringen DATE,
	TidligereNavn VARCHAR(50),
	
	ModifiedDate DATETIME NOT NULL,
	CreatedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,

	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- LMS18
CREATE TABLE Udleveringsbestemmelser (
	PID SERIAL,
	
	Kode VARCHAR(5) NOT NULL,
	Udleveringsgruppe CHAR(1),
	KortTekst VARCHAR(10),
	Tekst VARCHAR(50),
	
	ModifiedDate DATETIME NOT NULL,
	CreatedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,

	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;
