package com.trifork.stamdata.importer.jobs.sks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.trifork.stamdata.importer.util.Files;

public class SksImporterTest2
{
    public static File completeTxt = new File("data/sks/SHAKCOMPLETE.TXT");
    public static File completeXml = new File("data/sks/SHAKCOMPLETE.XML");
    public static File delta = new File("data/sks/SHAKDELTA.TXT");

    SKSParser importer = new SKSParser();
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptEmptyInputFileSet()
    {
        importer.ensureRequiredFileArePresent(new File[] {});
    }
    
    @Test
    public void shouldAcceptSKSCompleteTxtFile()
    {
        File[] input = Files.toArray(completeTxt);
        assertTrue(importer.ensureRequiredFileArePresent(input));
    }
    
    @Test
    public void shouldNotAcceptSKSCompleteXmlFile()
    {
        File[] input = Files.toArray(completeXml);
        assertFalse(importer.ensureRequiredFileArePresent(input));
    }
    
    @Test
    public void shouldAcceptSKSDeltaFile()
    {
        File[] input = Files.toArray(delta);
        assertTrue(importer.ensureRequiredFileArePresent(input));
    }
}
