CREATE TABLE DosageStructure (
	DosageStructurePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	releaseNumber BIGINT(15) NOT NULL,
	code VARCHAR(11) NOT NULL,
	type VARCHAR(100) NOT NULL,
	simpleString VARCHAR(100),
	supplementaryText VARCHAR(200),
	xml VARCHAR(10000) NOT NULL,
	shortTranslation VARCHAR(70),
	longTranslation VARCHAR(10000),
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

CREATE TABLE DrugDosageStructureRelation (
	DrugDosageStructurePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	
	id VARCHAR(200) NOT NULL,
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
	dosageUnitCode BIGINT(11) NOT NULL,
	drugName VARCHAR(200) NOT NULL,
	
	ModifiedBy VARCHAR(200),
	ModifiedDate DATETIME,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedBy VARCHAR(200),
	CreatedDate DATETIME,
	INDEX (releaseNumber)
);
