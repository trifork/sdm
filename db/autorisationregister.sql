CREATE TABLE AutorisationVersion (
	PID SERIAL,
	
	ReleaseDate DATE NOT NULL,
	
	UNIQUE KEY (ReleaseDate)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;

CREATE TABLE Autorisation (
	PID SERIAL,
	
	Autorisationsnummer CHAR(5) NOT NULL,
	CPR CHAR(10) NOT NULL,
	Fornavn VARCHAR(100) NOT NULL,
	Efternavn VARCHAR(100) NOT NULL,
	UddannelsesKode INT(4) NOT NULL,
	
	KEY (Autorisationsnummer)
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

