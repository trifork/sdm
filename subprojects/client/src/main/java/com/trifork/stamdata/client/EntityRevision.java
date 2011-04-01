// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

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
