This directory contains SQL schemas that are used with Stamdata.
Each file represents a database, logical or physical, depending on
the deployment.

- warehouse.sql

	This is where the central data tables are stored.

- housekeeping.sql

	These tables are used when we need to store management
	information that is not actual stam data, e.g. versions
	or information about address and name censoring.

- administration.sql
	
	Contains the tables used with the administration GUI.
