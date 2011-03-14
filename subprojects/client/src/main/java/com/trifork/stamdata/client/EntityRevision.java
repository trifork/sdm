package com.trifork.stamdata.client;

/**
 * Represents a version of a given entity, at a certain point in time.
 * 
 * @param <T> The type of entity described.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class EntityRevision<T> {

	private final T entity;
	private final String id;

	EntityRevision(String id, T entity) {

		this.id = id;
		this.entity = entity;
	}

	/**
	 * The id of the entity revision.
	 * 
	 * Records will always be transfered in-order, and if something goes wrong
	 * you can use the highest revision id (the revision of the record last
	 * handled) to resume the transfer where you left off.
	 * 
	 * @return the revision id.
	 */
	public String getId() {

		return id;
	}

	/**
	 * The entity associated with this revision.
	 * 
	 * @return the entity itself.
	 */
	public T getEntity() {

		return entity;
	}
}
