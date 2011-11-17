/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata.importer.config;


/**
 * A key value store for keeping state information.
 * 
 * A parser can use this for keeping sequence and versioning information.
 *
 * @author Thomas Børlum <thb@trifork.com>
 */
public interface KeyValueStore
{
    /**
     * Gets the value for a key.
     * 
     * @param key
     *            the key of the value to look up.
     * @return the value for the key, possibly {@code null}.
     */
    String get(String key);

    /**
     * Sets the value for the given key.
     * 
     * @precondition The the value is non-empty.
     * 
     * @param key
     *            The key for which the value is stored.
     * @param value
     *            A non-empty string. If null any existing recorded value is
     *            deleted.
     */
    void put(String key, String value);
}
