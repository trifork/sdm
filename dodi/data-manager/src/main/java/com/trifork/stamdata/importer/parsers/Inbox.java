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
package com.trifork.stamdata.importer.parsers;

import java.io.File;
import java.io.IOException;

/**
 * An inbox is a store where parser input is placed.
 *
 * An inbox represents a snapshot of the inbox's state.
 * You have to call the {@code update()} method before new
 * elements are considered.
 *
 * An inbox is essentially a priority queue with slightly
 * different semantics.
 */
public interface Inbox
{
    /**
     * Gets the top-most data set in the inbox.
     *
     * The top-most (next) must always be handled before
     * any others to ensure data integrity.
     *
     * It is on the other hand not the inbox's
     * responsibility to check the import sequence.
     * This is up to the respective parsers.
     *
     * @return the top-most data set from the inbox, or null if non are ready.
     */
    File top();

    /**
     * Disposes of the top-most element and moves the next.
     *
     * This should only be called when an element has been
     * successfully imported.
     *
     * This has no effect if the inbox is empty.
     *
     * @throws IllegalStateException thrown if {@link #advance()} called while the inbox is empty.
     */
    void advance() throws IOException;

    /**
     * The number of data sets that are ready for handling.
     *
     * This count only shows the number of data sets that
     * are ready for import from the top down.
     *
     * @return the number of elements in the inbox.
     */
    int readyCount();

    /**
     * Updates the inbox's state.
     *
     * This should be called before the initial call to
     * {@code top()}. Subsequent calls will refresh the
     * state including {@link #readyCount}, top-most data set, etc.
     */
    void update() throws IOException;

    /**
     * Locks the inbox.
     */
    void lock();

    /**
     * Checks if the inbox is locked.
     */
    boolean isLocked();
}
