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

import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.persistence.RecordPersister;
import org.joda.time.Instant;

import java.io.File;
import java.sql.Connection;

/**
 * A parser that imports files and stores the data in a database.
 *
 * Generally parsers should never log anything other than on DEBUG level.
 * If something goes wrong the parser must throw an exception and let the
 * caller do the logging.
 * 
 * @author Thomas Børlum <thb@trifork.com>
 */
public interface Parser
{
    /**
     * Processes a data set and persists the data.
     *
     * Processing consists of four steps:
     *
     * <ol>
     *     <li>Check that all required files are present.</li>
     *     <li>Check that the import sequence is in order.</li>
     *     <li>Parse the data set and persisting it accordingly.</li>
     *     <li>Update the version number, in the key value store.</li>
     * </ol>
     *
     * You should only log on DEBUG level. See {@linkplain Parser parser}.
     *
     * @param dataSet the root directory of the file set. Data files are contained within the directory.
     * @param connection A connection that can be used to store records, etc.
     * @param transactionTime The time to use in timestamps.
     * @throws OutOfSequenceException if the data set is out of sequence in the expected order.
     * @throws ParserException if anything parser specific error happens or unexpected happens.
     */
    void process(File dataSet, Connection connection, Instant transactionTime) throws OutOfSequenceException, ParserException, Exception;
}
