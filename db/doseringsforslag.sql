CREATE TABLE DosageVersion (
	PID SERIAL,
	
	daDate DATE NOT NULL,
	lmsDate DATE NOT NULL,
	releaseDate DATE NOT NULL,
	releaseNumber BIGINT(15) NOT NULL
	
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE DosageStructure (
	PID SERIAL,
	
	releaseNumber BIGINT(15) NOT NULL,
	code VARCHAR(11) NOT NULL,
	type VARCHAR(100) NOT NULL,
	simpleString VARCHAR(100), -- OPTIONAL
	supplementaryText VARCHAR(200), -- OPTIONAL
	xml VARCHAR(10000) NOT NULL,
	shortTranslation VARCHAR(70),
	longTranslation VARCHAR(10000) -- OPTIONAL (The specs say it cannot be NULL. See comment in DosageStructure.java)

) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE DosageUnit (
	PID SERIAL,
	
	releaseNumber BIGINT(15) NOT NULL,
	code INT(4),
	textSingular VARCHAR(100) NOT NULL,
	textPlural VARCHAR(100) NOT NULL
	
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE DrugDosageStructureRelation (
	PID SERIAL,
	
	id VARCHAR(200) NOT NULL,
	drugId BIGINT(11) NOT NULL,
	dosageStructureCode BIGINT(11) NOT NULL,
	releaseNumber BIGINT(15) NOT NULL

) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE DosageDrug (
	PID SERIAL,
	
	releaseNumber BIGINT(15) NOT NULL,
	drugId BIGINT(11) NOT NULL,
	dosageUnitCode BIGINT(11) NOT NULL,
	drugName VARCHAR(200) NOT NULL

) ENGINE=InnoDB COLLATE=utf8_danish_ci;
