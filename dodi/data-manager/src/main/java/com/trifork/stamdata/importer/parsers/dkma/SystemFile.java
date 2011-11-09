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
package com.trifork.stamdata.importer.parsers.dkma;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.trifork.stamdata.importer.util.Dates;

public class SystemFile
{
    public static final DateTimeFormatter CREATION_DATE_FORMATTER = DateTimeFormat.forPattern("yyyyMMdd").withZone(Dates.DANISH_TIMEZONE);
    
    private static final Pattern HEADER_LINE = Pattern.compile("00(.{5})LMS-TAKST(\\d{8}).{16}01(\\d{2})LMS.ZIP(\\d{6})");
    private static final int HEADER_LINE_INTERFACE_VERSION_INDEX = 1;
    private static final int HEADER_LINE_CRATION_DATE = 2;
    private static final int HEADER_LINE_NUM_FILES = 3;
    private static final int HEADER_LINE_VALIDITY_WEEK = 4;
    
    private static final Pattern FILE_LINE = Pattern.compile("10.{30}(.{12}).{16}01(\\d{5})");
    private static final int FILE_LINE_FILE_NAME = 1;
    private static final int FILE_LINE_RECORD_COUNT = 2;
    
    private String interfaceVersion;
    private String creationDate;
    private String validityWeek;
    private int numberOfFiles;
    private Set<FileDescriptor> files = Sets.newHashSet();
    
    public class FileDescriptor
    {
        private String filename;
        private int numRecords;
        
        public String getFilename()
        {
            return filename;
        }

        public int getNumRecords()
        {
            return numRecords;
        }
    }
    
    public SystemFile(File file)
    {
        parse(file);
    }
    
    private void parse(File file)
    {
        LineIterator lines = null;
        
        try
        {
            lines = IOUtils.lineIterator(new FileReader(file));
            innerParse(lines);
        }
        catch (Exception e)
        {
            throw new ParserException("Could not parse system.txt.", e);
        }
        finally
        {
            LineIterator.closeQuietly(lines);
        }
    }
    
    private void innerParse(Iterator<String> lines) throws Exception
    {
        Matcher matcher = HEADER_LINE.matcher(lines.next());
        
        if (matcher.find())
        {
            interfaceVersion = matcher.group(HEADER_LINE_INTERFACE_VERSION_INDEX).trim();
            creationDate = parseCreationDate(matcher.group(HEADER_LINE_CRATION_DATE));
            validityWeek = parseValidityWeek(matcher.group(HEADER_LINE_VALIDITY_WEEK));
            numberOfFiles = parseNumberOfFiles(matcher.group(HEADER_LINE_NUM_FILES));
            
            lines.next(); // TODO: Parse currency info line.
            
            files = parseFileList(lines, numberOfFiles);
        }
        else
        {
            throw new ParserException("Could not parse system.txt. File header is invalid.");
        }
    }

    private int parseNumberOfFiles(String numOfFiles)
    {
        final int SYSTEM_FILE = 1;
        
        return Integer.parseInt(numOfFiles.trim()) - SYSTEM_FILE;
    }

    private String parseCreationDate(String creationDate)
    {
        return CREATION_DATE_FORMATTER.parseDateTime(creationDate).toString(CREATION_DATE_FORMATTER);
    }
    
    private String parseValidityWeek(String week)
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyww").withZone(Dates.DANISH_TIMEZONE);
        return formatter.parseDateTime(week).toString(formatter);
    }
    
    private Set<FileDescriptor> parseFileList(Iterator<String> lines, int numberOfFiles)
    {
        Set<FileDescriptor> files = Sets.newHashSet();
        
        for (int i = 0; i < numberOfFiles; i++)
        {
            Matcher matcher = FILE_LINE.matcher(lines.next());
            
            if (matcher.find())
            {
                FileDescriptor file = new FileDescriptor();
                file.filename = matcher.group(FILE_LINE_FILE_NAME).trim();
                file.numRecords = Integer.parseInt(matcher.group(FILE_LINE_RECORD_COUNT).trim());
                files.add(file);
            }
            else
            {
                throw new ParserException("File line in system.txt could not be parsed.");
            }
        }
        
        return files;
    }

    public String getInterfaceVersion()
    {
        return interfaceVersion;
    }

    public String getCreationDate()
    {
        return creationDate;
    }

    public String getValidityWeek()
    {
        return validityWeek;
    }

    public Set<FileDescriptor> getFiles()
    {
        return ImmutableSet.copyOf(files);
    }
}
