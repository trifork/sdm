package com.trifork.stamdata.importer.jobs.bemyndigelse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.util.Files;

public class BemyndigelseParserTest
{
    private static File completeTxt = new File("data/sks/SHAKCOMPLETE.TXT");
    private static File completeXml = new File("data/sks/SHAKCOMPLETE.XML");
    private static File delta = new File("data/sks/SHAKDELTA.TXT");

    BemyndigelseParser importer = new BemyndigelseParser(new KeyValueStore() {
        
        @Override
        public void put(String key, String value) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public String get(String key) {
            // TODO Auto-generated method stub
            return null;
        }
    });
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptEmptyInputFileSet()
    {
        importer.validateInputStructure(new File[]{});
    }
    
    @Test
    public void shouldAcceptSKSCompleteTxtFile()
    {
        File[] input = Files.toArray(completeTxt);
        assertTrue(importer.validateInputStructure(input));
    }
    
    @Test
    public void shouldNotAcceptSKSCompleteXmlFile()
    {
        File[] input = Files.toArray(completeXml);
        assertFalse(importer.validateInputStructure(input));
    }
    
    @Test
    public void shouldAcceptSKSDeltaFile()
    {
        File[] input = Files.toArray(delta);
        assertTrue(importer.validateInputStructure(input));
    }
}
