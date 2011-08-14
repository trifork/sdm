CREATE TABLE Organisation (
	OrganisationPID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Nummer VARCHAR(30) NOT NULL,
	Navn VARCHAR(256),
	Organisationstype VARCHAR(30) NOT NULL,

	ModifiedDate DATETIME NOT NULL,
	ValidFrom DATETIME,
	ValidTo DATETIME,
	CreatedDate DATETIME NOT NULL,
	INDEX (ValidFrom, ValidTo)
) ENGINE=InnoDB COLLATE=utf8_danish_ci;
