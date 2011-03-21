------------------------------------------------------------
-- Housekeeping Tables
------------------------------------------------------------


CREATE TABLE import ( 
	importtime  DATETIME,
	spoolername VARCHAR(100) 
);


CREATE TABLE adressebeskyttelse ( 
	cpr VARCHAR(10) NOT NULL, 
	fornavn (60), 
	mellemnavn VARCHAR(60), 
	efternavn VARCHAR(60), 
	conavn VARCHAR(50), 
	lokalitet VARCHAR(50), 
	vejnavn VARCHAR(30), 
	bygningsnummer VARCHAR(10), 
	husnummer VARCHAR(10), 
	etage VARCHAR(10), 
	sidedoernummer VARCHAR(10), 
	bynavn VARCHAR(30), 
	postnummer BIGINT(12), 
	postdistrikt VARCHAR(30), 
	navnebeskyttelsestartdato DATETIME, 
	navnebeskyttelseslettedato DATETIME, 
	vejkode BIGINT(12), 
	kommunekode BIGINT(12), 
	UNIQUE INDEX (cpr) 
);


------------------------------------------------------------
-- Administration Tables
------------------------------------------------------------


CREATE TABLE User (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(200) NOT NULL,
	cvr CHAR(8) NOT NULL,
	cpr CHAR(10) NOT NULL
);


CREATE TABLE Client (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(200) NOT NULL,
	cvr CHAR(200) NOT NULL
);


CREATE TABLE Client_Permissions (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	client_id BIGINT UNSIGNED NOT NULL,
	permissions VARCHAR(200) NOT NULL,
	FOREIGN KEY (client_id) REFERENCES Client(id) ON DELETE CASCADE
);

CREATE TABLE LogEntry (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	message VARCHAR(500),
	createdAt TIMESTAMP NOT NULL
);

CREATE TABLE Authorization (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	cvr CHAR(8) NOT NULL,
	entity VARCHAR(200) NOT NULL,
	token BLOB(512) NOT NULL,
	expiresAt TIMESTAMP NOT NULL,
	createdAt TIMESTAMP NOT NULL
);
	
	
CREATE TABLE DrugDosageStructure (
 DrugDosageStructurePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 id VARCHAR(22) NOT NULL,
 releaseNumber BIGINT(15) NOT NULL,
 drugId BIGINT(11),
 dosageStructureCode BIGINT(15) NOT NULL,
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME ,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200),
 CreatedDate DATETIME,
 INDEX (releaseNumber)
);

CREATE TABLE DosageUnit (
	DosageUnitPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	releaseNumber BIGINT(15) NOT NULL,
	code INT(4),
	textSingular VARCHAR(100) NOT NULL,
	textPlural VARCHAR(100) NOT NULL,
	
	ModifiedBy VARCHAR(200),
	ModifiedDate DATETIME,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200),
	CreatedDate DATETIME,
	INDEX (releaseNumber)
);

CREATE TABLE DosageVersion (
	DosageVersionPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	daDate DATE NOT NULL,
	lmsDate DATE NOT NULL,
	releaseDate DATE NOT NULL,
	releaseNumber BIGINT(15) NOT NULL,
	
	ModifiedBy VARCHAR(200),
	ModifiedDate DATETIME,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200),
	CreatedDate DATETIME,
	INDEX (releaseNumber)
);

CREATE TABLE DrugDosageUnitRelation (
	DosageDrugPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	drugId BIGINT(11) NOT NULL,
	dosageUnitCode BIGINT(11) NOT NULL,
	releaseNumber BIGINT(15) NOT NULL,
	
	ModifiedBy VARCHAR(200),
	ModifiedDate DATETIME,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200),
	CreatedDate DATETIME,
	INDEX (releaseNumber)
);

CREATE TABLE DrugDosageStructureRelation (
	DrugDosageStructurePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	drugId BIGINT(11) NOT NULL,
	dosageStructureCode BIGINT(11) NOT NULL,
	releaseNumber BIGINT(15) NOT NULL,
	
	ModifiedBy VARCHAR(200),
	ModifiedDate DATETIME,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200),
	CreatedDate DATETIME,
	INDEX (releaseNumber)
);

CREATE TABLE DosageDrug (
	DosageDrug BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	releaseNumber BIGINT(15) NOT NULL,
	drugId BIGINT(11) NOT NULL,
	dosageStructureCode BIGINT(11) NOT NULL,
	
	ModifiedBy VARCHAR(200),
	ModifiedDate DATETIME,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200),
	CreatedDate DATETIME,
	INDEX (releaseNumber)
);

-------------------------------------------------------------------------
-- Other Registry Tables
-------------------------------------------------------------------------

CREATE TABLE Administrationsvej (
 AdministrationsvejPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 AdministrationsvejKode CHAR(2) NOT NULL,
 AdministrationsvejTekst VARCHAR(50) NOT NULL,
 AdministrationsvejKortTekst VARCHAR(10),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME ,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200),
 CreatedDate DATETIME,
 INDEX (ValidFrom, ValidTo),
 CONSTRAINT UC_Administrationsvej_1 UNIQUE (AdministrationsvejKode, ValidTo)
);

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
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
);


CREATE TABLE ATC (
 ATCPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 ATC VARCHAR(10) NOT NULL,
 ATCTekst VARCHAR(72) NOT NULL,
 ATCNiveau1 VARCHAR(2),
 ATCNiveau2 VARCHAR(2),
 ATCNiveau3 VARCHAR(1),
 ATCNiveau4 VARCHAR(1),
 ATCNiveau5 VARCHAR(2),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Autorisation (
 AutorisationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Autorisationsnummer VARCHAR(10) NOT NULL,
 cpr VARCHAR(10) NOT NULL,
 Fornavn VARCHAR(100) NOT NULL,
 Efternavn VARCHAR(100) NOT NULL,
 UddannelsesKode INT(4),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

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
 INDEX (ValidFrom, ValidTo),
 CONSTRAINT UC_Person_1 UNIQUE (Id, ValidFrom)
)
;

CREATE TABLE Beregningsregler (
 BeregningsreglerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Kode VARCHAR(1) NOT NULL,
 Tekst VARCHAR(50), 
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

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
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL
, INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE EmballagetypeKoder (
 EmballagetypeKoderPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Kode VARCHAR(4) NOT NULL,
 KortTekst VARCHAR(10),
 Tekst VARCHAR(50),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Enhedspriser (
 EnhedspriserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Varenummer BIGINT(12) NOT NULL,
 DrugID BIGINT(12),
 PrisPrEnhed BIGINT(12),
 PrisPrDDD BIGINT(12),
 BilligstePakning VARCHAR(1),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Firma (
 FirmaPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Firmanummer BIGINT(12) NOT NULL,
 FirmamaerkeKort VARCHAR(20),
 FirmamaerkeLangtNavn VARCHAR(32),
 ParallelimportoerKode VARCHAR(2),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

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
 INDEX (ValidFrom, ValidTo),
 CONSTRAINT UC_Person_1 UNIQUE (Id, ValidFrom)
)
;

CREATE TABLE Formbetegnelse (
 FormbetegnelsePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Kode VARCHAR(10) NOT NULL,
 Tekst VARCHAR(150) NOT NULL,
 Aktiv BOOLEAN,
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Indholdsstoffer (
 IndholdsstofferPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY, 
 CID VARCHAR(364) NOT NULL,
 DrugID BIGINT(12),
 Varenummer BIGINT(12),
 Stofklasse VARCHAR(100),
 Substansgruppe VARCHAR(100),
 Substans VARCHAR(150),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Indikation (
 IndikationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 IndikationKode BIGINT(15),
 IndikationTekst VARCHAR(100),
 IndikationstekstLinie1 VARCHAR(26),
 IndikationstekstLinie2 VARCHAR(26),
 IndikationstekstLinie3 VARCHAR(26),
 aktiv BOOLEAN,
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE IndikationATCRef (
 IndikationATCRefPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 CID VARCHAR(22) NOT NULL,
 IndikationKode BIGINT(15) NOT NULL,
 ATC VARCHAR(10) NOT NULL,
 DrugID BIGINT(12),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo, IndikationKode, ATC)
)
;

CREATE TABLE Kommune (
 KommunePID BIGINT(15) NOT NULL PRIMARY KEY,
 Nummer VARCHAR(12) NOT NULL,
 Navn VARCHAR(100) NOT NULL,
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;


CREATE TABLE Laegemiddel (
 LaegemiddelPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 DrugID BIGINT(12) NOT NULL,
 DrugName VARCHAR(30) NOT NULL,
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
 DatoForAfregistrAfLaegemiddel VARCHAR(10),
 Karantaenedato VARCHAR(8),
 AdministrationsvejKode VARCHAR(8),
 MTIndehaverKode BIGINT(12),
 RepraesentantDistributoerKode BIGINT(12),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Laegemiddelnavn (
 LaegemiddelnavnPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 DrugID BIGINT(12) NOT NULL,
 LaegemidletsUforkortedeNavn VARCHAR(60),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;


CREATE TABLE LaegemiddelAdministrationsvejRef (
 LaegemiddelAdministrationsvejRefPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 CID VARCHAR(22) NOT NULL,
 DrugID BIGINT(12) NOT NULL,
 AdministrationsvejKode CHAR(2) NOT NULL,
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE LaegemiddelDoseringRef (
 LaegemiddelDoseringRefPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 CID VARCHAR(22) NOT NULL,
 DrugID BIGINT(12) NOT NULL,
 DoseringKode BIGINT(12) NOT NULL,
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Klausulering (
 KlausuleringPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Kode VARCHAR(10) NOT NULL,
 KortTekst  VARCHAR(60),
 Tekst VARCHAR(600),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Medicintilskud (
 MedicintilskudPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Kode VARCHAR(10) NOT NULL,
 KortTekst VARCHAR(20),
 Tekst VARCHAR(60),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Opbevaringsbetingelser (
 OpbevaringsbetingelserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Kode VARCHAR(1) NOT NULL,
 KortTekst VARCHAR(10),
 Tekst VARCHAR(50),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

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
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Organisation (
 organisationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Nummer VARCHAR(30) NOT NULL,
 Navn VARCHAR(256),
 Organisationstype VARCHAR(30) NOT NULL,
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Pakning (
 PakningPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 VarenummerDelpakning BIGINT(12),
 DrugID DECIMAL(12) NOT NULL,
 PakningsstoerrelseNumerisk DECIMAL(10,2),
 Pakningsstoerrelsesenhed VARCHAR(10),
 PakningsstoerrelseTekst VARCHAR(30),
 EmballageTypeKode VARCHAR(10),
 Dosisdispenserbar BOOL,
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
 Oprettelsesdato VARCHAR(10),
 DatoForSenestePrisaendring VARCHAR(10),
 UdgaaetDato VARCHAR(10),
 BeregningskodeAIRegpris VARCHAR(1),
 PakningOptagetITilskudsgruppe BOOLEAN,
 Faerdigfremstillingsgebyr BOOLEAN,
 Pakningsdistributoer BIGINT(12),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Pakningskombinationer (
 PakningskombinationerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 CID VARCHAR(23) NOT NULL,
 VarenummerOrdineret BIGINT(12),
 VarenummerSubstitueret BIGINT(12),
 VarenummerAlternativt BIGINT(12),
 AntalPakninger BIGINT(12),
 EkspeditionensSamledePris BIGINT(12),
 InformationspligtMarkering VARCHAR(1),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE PakningskombinationerUdenPriser (
 PakningskombinationerUdenPriserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 VarenummerOrdineret BIGINT(12) NOT NULL,
 VarenummerSubstitueret BIGINT(12),
 VarenummerAlternativt BIGINT(12),
 AntalPakninger BIGINT(12),
 InformationspligtMarkering VARCHAR(1),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Pakningsstoerrelsesenhed (
 PakningsstoerrelsesenhedPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 PakningsstoerrelsesenhedKode VARCHAR(10) NOT NULL,
 PakningsstoerrelsesenhedTekst VARCHAR(50) NOT NULL,
 PakningsstoerrelsesenhedKortTekst VARCHAR(10),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200),
 CreatedDate DATETIME
, INDEX (ValidFrom, ValidTo),
 CONSTRAINT UC_Pakningsstoerrelsesenhed_1 UNIQUE (PakningsstoerrelsesEnhedKode, ValidTo)
)
;

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
 INDEX (ValidFrom, ValidTo),
 CONSTRAINT UC_Person_1 UNIQUE (CPR, ValidFrom)
)
;


CREATE TABLE PersonIkraft (
 PersonIkraftPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY	,
 IkraftDato DATETIME NOT NULL
)
;

CREATE TABLE Praksis (
 praksisPID BIGINT(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 SorNummer BIGINT(20) NOT NULL,
 EanLokationsnummer BIGINT(20),
 RegionCode BIGINT(12),
 Navn VARCHAR(256),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Priser (
 PriserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Varenummer BIGINT(12) NOT NULL,
 apoteketsIndkoebspris BIGINT(12),
 Registerpris BIGINT(12),
 ekspeditionensSamledePris BIGINT(12),
 tilskudspris BIGINT(12),
 LeveranceprisTilHospitaler BIGINT(12),
 IkkeTilskudsberettigetDel BIGINT(12),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Rekommandationer (
 RekommandationerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Varenummer BIGINT(12) NOT NULL,
 Rekommandationsgruppe BIGINT(12),
 DrugID BIGINT(12),
 Rekommandationsniveau VARCHAR(25),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE SpecialeForNBS (
 SpecialeForNBSPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Kode VARCHAR(5) NOT NULL,
 KortTekst VARCHAR(10),
 Tekst VARCHAR(50),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Styrkeenhed (
 StyrkeenhedPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 StyrkeenhedKode VARCHAR(10) NOT NULL,
 StyrkeenhedTekst VARCHAR(50) NOT NULL,
 StyrkeenhedKortTekst VARCHAR(10),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200),
 CreatedDate DATETIME
, INDEX (ValidFrom, ValidTo),
 CONSTRAINT UC_Styrkeenhed_1 UNIQUE (StyrkeenhedKode, ValidTo)
)
;

CREATE TABLE Substitution (
 SubstitutionPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 ReceptensVarenummer BIGINT(12) NOT NULL,
 Substitutionsgruppenummer BIGINT(12),
 NumeriskPakningsstoerrelse BIGINT(12),
 ProdAlfabetiskeSekvensplads VARCHAR(9),
 SubstitutionskodeForPakning VARCHAR(1),
 BilligsteVarenummer BIGINT(12),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE SubstitutionAfLaegemidlerUdenFastPris (
 SubstitutionAfLaegemidlerUdenFastPrisPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Varenummer BIGINT(12) NOT NULL,
 Substitutionsgruppenummer BIGINT(12),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

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
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

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
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE TakstVersion (
 TakstVersionPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 TakstUge VARCHAR(8) NOT NULL,
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Tidsenhed (
 TidsenhedPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 TidsenhedKode VARCHAR(10) NOT NULL,
 TidsenhedTekst VARCHAR(50) NOT NULL,
 TidsenhedKortTekst VARCHAR(10),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200),
 CreatedDate DATETIME,
  INDEX (ValidFrom, ValidTo),
 CONSTRAINT UC_Tidsenhed_1 UNIQUE (TidsenhedKode, ValidTo)
)
;

CREATE TABLE Tilskudsintervaller (
 TilskudsintervallerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 CID VARCHAR(4) NOT NULL,
 Type BIGINT(12),
 Niveau BIGINT(12),
 NedreGraense BIGINT(12),
 OevreGraense BIGINT(12),
 Procent DECIMAL,
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE TilskudsprisgrupperPakningsniveau (
 TilskudsprisgrupperPakningsniveauPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Varenummer BIGINT(12) NOT NULL,
 TilskudsprisGruppe BIGINT(12),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE UdgaaedeNavne (
 UdgaaedeNavnePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 CID VARCHAR(71) NOT NULL,
 Drugid BIGINT(12),
 DatoForAendringen VARCHAR(10),
 TidligereNavn VARCHAR(50),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE Udleveringsbestemmelser (
 UdleveringsbestemmelserPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Kode VARCHAR(5) NOT NULL,
 Udleveringsgruppe VARCHAR(1),
 KortTekst VARCHAR(10),
 Tekst VARCHAR(50),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE UmyndiggoerelseVaergeRelation (
 ForaeldreMyndighedRelationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
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
 INDEX (ValidFrom, ValidTo),
 CONSTRAINT UC_Person_1 UNIQUE (Id, ValidFrom)
)
;

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
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE YderLoebenummer (
 YderLoebenummerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY	,
 Loebenummer BIGINT(12) NOT NULL
)
;

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
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;

CREATE TABLE YderregisterPerson (
 YderregisterPersonPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
 Id VARCHAR(20) NOT NULL,
 Nummer VARCHAR(30) NOT NULL,
 CPR VARCHAR(10),
 personrolleKode BIGINT(20),
 personrolleTxt VARCHAR(200),
 HistIDPerson VARCHAR(100),
 ModifiedBy VARCHAR(200) NOT NULL,
 ModifiedDate DATETIME NOT NULL,
 ValidFrom DATETIME,
 ValidTo DATETIME,
 CreatedBy VARCHAR(200) NOT NULL,
 CreatedDate DATETIME NOT NULL,
 INDEX (ValidFrom, ValidTo)
)
;
