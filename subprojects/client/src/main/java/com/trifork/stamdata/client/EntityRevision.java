// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

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
