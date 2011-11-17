package com.trifork.stamdata.importer.jobs.yderregister;

import com.trifork.stamdata.importer.parsers.OutOfSequenceException;
import com.trifork.stamdata.importer.parsers.ParserException;
import org.junit.Test;

public class YderregisterParserTest
{
    @Test(expected = OutOfSequenceException.class)
    public void testImportsOutOfSequenceWillResultInAnOutOfSequenceException()
    {

    }

    @Test(expected = ParserException.class)
    public void testMissingFilesResultsInAParserException()
    {

    }

    @Test
    public void testParsesTheVersionFromFileNamesCorrectly()
    {

    }

    @Test
    public void test()
    {
        
    }
}
