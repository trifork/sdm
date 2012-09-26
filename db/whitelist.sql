USE sdm_warehouse;

CREATE TABLE IF NOT EXISTS whitelist_config (
  component_name VARCHAR(50) NOT NULL,
  cvr CHAR(8) NOT NULL,
  PRIMARY KEY (component_name, cvr)
) ENGINE=InnoDB COLLATE=utf8_bin;

-- ADMINISTRATION TABLES (USERS ETC.)

CREATE TABLE IF NOT EXISTS Client (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(200) NOT NULL,
	subjectSerialNumber CHAR(200) NOT NULL
) ENGINE=InnoDB COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS Client_permissions (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	client_id BIGINT NOT NULL,
	permissions TEXT NOT NULL,
	FOREIGN KEY (client_id) REFERENCES Client(id) ON DELETE CASCADE
) ENGINE=InnoDB COLLATE=utf8_bin;