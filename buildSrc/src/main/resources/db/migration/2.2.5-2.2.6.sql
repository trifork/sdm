CREATE TABLE YderLoebenummer (
 YderLoebenummerPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY	,
 Loebenummer BIGINT(12) NOT NULL
) ENGINE=InnoDB
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
) ENGINE=InnoDB
;

CREATE INDEX IX_YderregisterPerson_1 ON YderregisterPerson(Id);
CREATE INDEX IX_YderregisterPerson_2 ON YderregisterPerson(CPR);

USE sdm_housekeeping;

CREATE TABLE AdresseBeskyttelse (
 CPR VARCHAR(10) NOT NULL,
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
 NavneBeskyttelseStartDato DATETIME,
 NavneBeskyttelseSletteDato DATETIME,
 VejKode BIGINT(12), 
 KommuneKode BIGINT(12),
 UNIQUE INDEX (CPR)
) ENGINE=InnoDB
;
