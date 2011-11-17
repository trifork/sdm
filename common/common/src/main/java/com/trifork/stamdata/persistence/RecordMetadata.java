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
