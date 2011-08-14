CREATE TABLE Autorisation (
	PID SERIAL,
	Autorisationsnummer VARCHAR(10) NOT NULL, -- TODO: This should be CHAR(5)
	cpr VARCHAR(10) NOT NULL,
	Fornavn VARCHAR(100) NOT NULL,
	Efternavn VARCHAR(100) NOT NULL,
	UddannelsesKode INT(4) NOT NULL,

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

-- WARNING: This table is shared with the STS.
-- Be careful about changing anything.
CREATE TABLE autreg (
  id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  cpr CHAR(10) NOT NULL,
  given_name VARCHAR(50) NOT NULL,
  surname VARCHAR(100) NOT NULL,
  aut_id CHAR(5) NOT NULL,
  edu_id CHAR(4) NOT NULL,
  KEY cpr_aut_id (cpr, aut_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

