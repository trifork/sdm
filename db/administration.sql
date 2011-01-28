CREATE TABLE clients (
	id BIGINT UNSIGNED NOT NULL auto_increment PRIMARY KEY,
	name VARCHAR(200) NOT NULL,
	certificate_id CHAR(200) NOT NULL
) ENGINE=InnoDB;


CREATE TABLE clients_permissions (
	id BIGINT UNSIGNED NOT NULL auto_increment PRIMARY KEY,
	client_id BIGINT UNSIGNED NOT NULL,
	resource_id VARCHAR(200) NOT NULL,
	FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE administrators (
	id BIGINT UNSIGNED NOT NULL auto_increment PRIMARY KEY,
	name VARCHAR(200) NOT NULL,
	cvr CHAR(8) NOT NULL,
	cpr CHAR(10) NOT NULL
) ENGINE=InnoDB;


CREATE TABLE auditlog (
	id BIGINT UNSIGNED NOT NULL auto_increment PRIMARY KEY,
	message VARCHAR(500) NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT current_timestamp 
) ENGINE=InnoDB;
