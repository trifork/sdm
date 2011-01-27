CREATE TABLE clients (
	id bigint not null auto_increment,
	name VARCHAR(200) NOT NULL,
	certificate_id CHAR(200) NOT NULL,
	PRIMARY KEY (certificate_id)
) ENGINE=InnoDB;


CREATE TABLE clients_permissions (
	id SERIAL,
	client_id BIGINT UNSIGNED NOT NULL,
	resource_id VARCHAR(200) NOT NULL,
	FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE administrators (
	id SERIAL,
	name VARCHAR(200) NOT NULL,
	cvr CHAR(8) NOT NULL,
	cpr CHAR(10) NOT NULL
) ENGINE=InnoDB;


CREATE TABLE auditlog (
	id SERIAL,
	message VARCHAR(500) NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT current_timestamp 
) ENGINE=InnoDB;
