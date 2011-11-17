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
package com.trifork.stamdata.persistence;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA.
 * User: borlum
 * Date: 11/17/11
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecordMetadata
{
    private final Instant validFrom;
    private final Instant validTo;
    private final Instant modifiedDate;
    private final Long pid;
    private final Record record;

    public RecordMetadata(Instant validFrom, Instant validTo, Instant modifiedDate, Long pid, Record record)
    {
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.modifiedDate = modifiedDate;
        this.pid = pid;
        this.record = record;
    }

    public Instant getValidFrom()
    {
        return validFrom;
    }

    public Instant getValidTo()
    {
        return validTo;
    }

    public Instant getModifiedDate()
    {
        return modifiedDate;
    }

    public Long getPid()
    {
        return pid;
    }

    public Record getRecord()
    {
        return record;
    }
}
