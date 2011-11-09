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
package com.trifork.stamdata.importer.util;

import java.io.File;
import java.io.FileNotFoundException;

import com.google.common.base.Preconditions;
import com.trifork.stamdata.importer.parsers.dkma.ParserException;

public class Files
{
    public static File getFile(File root, String path, boolean isRequired)
    {
        File file = new File(root, path);
        
        if (isRequired && !file.exists())
        {
            FileNotFoundException e = new FileNotFoundException(file.getAbsolutePath());
            throw new ParserException("A required file was not present.", e);
        }
        
        return file;
    }
    
    public static File getFile(File[] files, String filename, boolean isRequired)
    {
        File file = null;
        
        for (File f : files)
        {
            if (f.getName().equals(filename))
            {
                file = f;
                break;
            }
        }
        
        if (isRequired && (file == null || !file.exists()))
        {
            FileNotFoundException e = new FileNotFoundException(file.getAbsolutePath());
            throw new ParserException("A required file was not present.", e);
        }
        
        return file;
    }
    
    public static File[] toArray(File file)
    {
        Preconditions.checkNotNull(file);
        
        return new File[] { file };
    }
}
