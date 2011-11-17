package com.trifork.stamdata.importer.parsers;

import com.trifork.stamdata.importer.parsers.OutOfSequenceException;
import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.ParserException;
import com.trifork.stamdata.importer.parsers.annotations.ParserInformation;
import org.joda.time.Instant;

import java.io.File;
import java.sql.Connection;

@ParserInformation(id="foo", name="Foo")
public class MockParser implements Parser
{
    @Override
    public void process(File dataSet, Connection connection, Instant transactionTime) throws OutOfSequenceException, ParserException, Exception
    {

    }
}
