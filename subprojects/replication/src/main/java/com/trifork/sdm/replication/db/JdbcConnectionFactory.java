package com.trifork.sdm.replication.db;

import java.sql.Connection;

public interface JdbcConnectionFactory {

	public enum DB {
		
		/**
		 * This schema is the main stamdata schema that
		 * hold all the data from the respective sources.
		 */
		SdmDB,
		
		/**
		 * This is a legacy schema for logging and other
		 * non-stamdata information.
		 * This table will be removed once we no longer 
		 * use MySQL replication.
		 */
		SdmHousekeepingDB,
		
		/**
		 * This schema is used to store role/permission
		 * information as entered in the admin GUI.
		 */
		SdmAdminDB,
		
		/**
		 * This schema is solely used for comparison tests
		 * and should not be used for anything else.
		 */
		SdmDuplicateDB
	}
	
	Connection create(DB database);
}
