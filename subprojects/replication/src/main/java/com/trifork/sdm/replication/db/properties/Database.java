package com.trifork.sdm.replication.db.properties;


public enum Database
{
	/**
	 * The main data store database of Stamdata, where all entities are stored.
	 */
	WAREHOUSE,

	/**
	 * A maintenance database used for keeping track of versions and e.g. name and address
	 * protection.
	 */
	HOUSEKEEPING,

	/**
	 * A database used for storing authorizations and other managment data. This is primarily for
	 * use this the GUI.
	 */
	ADMINISTRATION
}
