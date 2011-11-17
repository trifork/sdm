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
package com.trifork.stamdata.importer.jobs.sks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.trifork.stamdata.importer.util.Files;

public class SksParserTest
{
    private static File completeTxt = new File("data/sks/SHAKCOMPLETE.TXT");
    private static File completeXml = new File("data/sks/SHAKCOMPLETE.XML");
    private static File delta = new File("data/sks/SHAKDELTA.TXT");

    SKSParser importer = new SKSParser();
    
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
