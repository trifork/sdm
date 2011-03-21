ALTER TABLE Pakning 
 ADD COLUMN MedicintilskudsKode VARCHAR(10),
 ADD COLUMN KlausuleringsKode VARCHAR(10);

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
) ENGINE=InnoDB
;

CREATE INDEX IX_Klausulering_1 ON Klausulering (Kode);

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
) ENGINE=InnoDB
;

CREATE INDEX IX_Medicintilskud_1 ON Medicintilskud (Kode);


