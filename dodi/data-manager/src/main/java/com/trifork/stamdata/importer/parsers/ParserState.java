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

import org.joda.time.DateTime;

/**
 * A light-weight descriptor that describes the state and configuration of a parser.
 *
 * @author Thomas Børlum <thb@trifork.com>
 */
public interface ParserState
{
    /** A globally unique identifier for the parser. */
    String identifier();

    /** A human readable identifier for the parser. */
    String name();

    /** The maximum number of days between data sets arriving. */
    int minimumImportFrequency();

    /**
     * Indicated whether a file delivery is overdue.
     *
     * If no files have previously been imported, this method always returns
     * false.
     *
     * @return true if the parser expected files but has not received any.
     */
    boolean isOverdue();

    /**
     * The deadline for when the next files have to have been imported.
     *
     * The returned date will always be at midnight to avoid the day of time
     * slipping every time a new batch is imported.
     *
     * @return the timestamp with the deadline.
     */
    DateTime nextDeadline();

    DateTime latestRunTime();

    boolean hasBeenRun();

    boolean isLocked();

    /**
     * Indicates whether the parser is currently being executed.
     *
     * @return true if the parser is being executed.
     */
    boolean isInProgress();
}
