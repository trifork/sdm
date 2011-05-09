ALTER TABLE ATC
 ADD COLUMN ATCNiveau1 VARCHAR(2),
 ADD COLUMN ATCNiveau2 VARCHAR(2),
 ADD COLUMN ATCNiveau3 VARCHAR(1),
 ADD COLUMN ATCNiveau4 VARCHAR(1),
 ADD COLUMN ATCNiveau5 VARCHAR(2);

 
ALTER TABLE Administrationsvej
 ADD COLUMN AdministrationsvejKortTekst VARCHAR(10);
 
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
);

ALTER TABLE Dosering
 ADD COLUMN DoseringKortTekst VARCHAR(10),
 ADD COLUMN DoseringstekstLinie1 VARCHAR(26),
 ADD COLUMN DoseringstekstLinie2 VARCHAR(26),
 ADD COLUMN DoseringstekstLinie3 VARCHAR(26);

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
);

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
);

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
);

ALTER TABLE Indikation
 ADD COLUMN IndikationstekstLinie1 VARCHAR(26),
 ADD COLUMN IndikationstekstLinie2 VARCHAR(26),
 ADD COLUMN IndikationstekstLinie3 VARCHAR(26),
 ADD COLUMN aktiv BOOLEAN;


ALTER TABLE IndikationATCRef
 ADD COLUMN DrugID BIGINT(12);
 
ALTER TABLE Laegemiddel
 ADD COLUMN Varetype VARCHAR(2),
 ADD COLUMN Varedeltype VARCHAR(2),
 ADD COLUMN AlfabetSekvensplads VARCHAR(9),
 ADD COLUMN SpecNummer BIGINT(12),
 ADD COLUMN LaegemiddelformTekst VARCHAR(20),
 ADD COLUMN KodeForYderligereFormOplysn VARCHAR(7),
 ADD COLUMN Trafikadvarsel BOOLEAN,
 ADD COLUMN Substitution VARCHAR(1),
 ADD COLUMN LaegemidletsSubstitutionsgruppe VARCHAR(4),
 ADD COLUMN DatoForAfregistrAfLaegemiddel VARCHAR(10),
 ADD COLUMN Karantaenedato VARCHAR(10),
 ADD COLUMN AdministrationsvejKode VARCHAR(8),
 ADD COLUMN MTIndehaverKode BIGINT(12),
 ADD COLUMN RepraesentantDistributoerKode BIGINT(12);

ALTER TABLE Formbetegnelse
 ADD COLUMN Aktiv BOOLEAN;

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
);

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
);

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
);

ALTER TABLE Pakning
 ADD COLUMN AlfabetSekvensnr BIGINT(12),
 ADD COLUMN AntalDelpakninger BIGINT(12),
 ADD COLUMN Udleveringsbestemmelse VARCHAR(5),
 ADD COLUMN UdleveringSpeciale VARCHAR(5),
 ADD COLUMN AntalDDDPrPakning DECIMAL,
 ADD COLUMN OpbevaringstidNumerisk BIGINT(12),
 ADD COLUMN Opbevaringstid BIGINT(12),
 ADD COLUMN Opbevaringsbetingelser VARCHAR(1),
 ADD COLUMN Oprettelsesdato VARCHAR(10),
 ADD COLUMN DatoForSenestePrisaendring VARCHAR(10),
 ADD COLUMN UdgaaetDato VARCHAR(10),
 ADD COLUMN BeregningskodeAIRegpris VARCHAR(1),
 ADD COLUMN PakningOptagetITilskudsgruppe BOOLEAN,
 ADD COLUMN Faerdigfremstillingsgebyr BOOLEAN,
 ADD COLUMN Pakningsdistributoer BIGINT(12);

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
);

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
);

ALTER TABLE Pakningsstoerrelsesenhed
 ADD COLUMN PakningsstoerrelsesenhedKortTekst VARCHAR(10);

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
);

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
);

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
);

ALTER TABLE Styrkeenhed
 ADD COLUMN StyrkeenhedKortTekst VARCHAR(10);

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
);

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
);

ALTER TABLE Tidsenhed
 ADD COLUMN TidsenhedKortTekst VARCHAR(10);

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
);

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
);

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
);

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
);

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
);