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

import java.io.File;
import java.util.Iterator;

import com.trifork.stamdata.importer.parsers.exceptions.ParserException;
import com.trifork.stamdata.importer.persistence.Persister;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.jobs.sks.Institution.InstitutionType;
import com.trifork.stamdata.importer.persistence.Dataset;

/**
 * Parser for the SKS register.
 * 
 * SKS is an acronym for 'Sundhedsvæsenets Klassifikationssystem'.
 * The input file is documented in the sks.pdf file in the doc directory.
 * 
 * Ændringer til SKS-registeret (sgh/afd) indlæses via deltafiler.
 * Ved etablering af registeret anvendes en deltafil der indeholder
 * samtlige sgh/afd dvs. indlæsningen foretages på præcis samme måde
 * hvadenten der indlæses/opdateres et fuld register eller blot ændringer
 * siden sidst (delta)
 * 
 * Eksempel på deltafil-indhold:
 * 
 * afd1301011             197901011979010119821231ANÆSTHESIAFD. AN                                                                                                        084                                            
 * afd1301011             198301011983010119941231ANÆSTESIAFD. AN,ANÆSTESIAFSNIT                                                                                          084                                            
 * afd1301011             199501011999112419991231ANÆSTESIAFD. AN,ANÆSTESIAFSNIT                                                                                          084         SKS     3                          
 * afd1301011             200001012004102920031231Anæstesiologisk klinik AN, anæstesiafsnit                                                                               084         SKS     3                          
 * afd1301011             200401012004102925000101Anæstesi-/operationsklinik, ABD                                                                                         084         SKS     1                          
 *
 * Hver række angiver en sgh/afd med nummer, gyldighedperiode, navn samt
 * operationskode (3=opdatering, 1=ny eller 2=sletning) 
 * Der anvendes fastpositionering dvs. værdierne er altid placeret på
 * samme position og der anvendes whitespaces til at "fylde" ud med 
 * 
 * Der er intet krav om at rækkefølgen for hvert nummer skal være
 * kronologisk dvs. der tages højde for at der efter at være indlæst en
 * sgh/afd med gyldighedsperiode
 * 
 * 01.01.2008 - 01.01.2500
 * 
 * kan optræde en anden record for samme nummer med gyldighedsperiode
 *
 * 01.01.2000 - 31.13.2007
 * 
 * Det garanteres dog at der ikke optræder overlap på gyldighedsperioden
 * for samme nummer.
 * 
 * Operationskoden (action) (position 187-188) angiver om recorden skal
 * betragtes som ny, opdatering eller sletning.
 * Med den måde hvorpå SKS-registeret anvendes i PEM gælder det at alle
 * entries/versioner af hvert nummer skal være placeret i
 * Organisationshistorik-tabellen (og altså ikke kun gamle versioner i
 * denne tabel) dvs. det er altid muligt heri at finde den gyldige/aktive
 * sgh/afd for en bestemt dato. I Organisations-tabellen derimod placeres
 * kun den nyeste record for en given sgh/afd dvs. recorden med nyeste
 * gyldighedsdato. 
 * For at sikre denne versionering skal enhver entry (med operationskode 1
 * eller 3) altid skal indsættes/opdateres i Organisationshistorik-tabellen,
 * mens kun nyeste entry (med operationskode 1 eller 3) skal
 * indsættes/opdateres i Organisations-tabellen.
 * Det gælder at operationskode/action (1,2,3) kun er angivet for entries
 * nyere end 1995. Da vi kun ønsker at indlæse records nyere end 1995
 * ignoreres alle records hvor operationskode ikke er angivet.
 */
public class SKSParser implements FileParser
{
    private static final int SKS_CODE_START_INDEX = 3;
    private static final int SKS_CODE_END_INDEX = 23;

    private static final int CODE_TEXT_START_INDEX = 47;
    
    /**
     * The field is actually 120 characters long. But the
     * specification says only to use the first 60.
     */
    private static final int CODE_TEXT_END_INDEX = 107;
    
    private static final int ENTRY_TYPE_START_INDEX = 0;
    private static final int ENTRY_TYPE_END_INDEX = 3;

    private static final char OPERATION_CODE_NONE = ' ';
    private static final char OPERATION_CODE_CREATE = '1';
    private static final char OPERATION_CODE_UPDATE = '3';

    private static final String RECORD_TYPE_HOSPITAL = "sgh";
    private static final String RECORD_TYPE_DEPARTMENT = "afd";

    private static final int OPERATION_CODE_INDEX = 187;

    private static final String FILE_ENCODING = "ISO8859-15";

    private static final DateTimeFormatter dateFormat = ISODateTimeFormat.basicDate();

    @Override
    public String getIdentifier()
    {
        return "sks";
    }

    @Override
    public String getHumanName()
    {
        return "SKS Parser";
    }

    @Override
    public boolean validateInputStructure(File[] input)
    {
        Preconditions.checkNotNull(input, "input");
        Preconditions.checkArgument(input.length > 0, "At least one file should be present at this point.");
        
        return input.length == 1 && isValidFilePresent(input);
    }
    
    private boolean isValidFilePresent(File[] input)
    {
        final String filename = input[0].getName();
        
        return filename.equalsIgnoreCase("SHAKCOMPLETE.TXT")
            || filename.equalsIgnoreCase("SHAKDELTA.TXT");
    }

    @Override
    public void parse(File[] files, Persister persister, KeyValueStore keyValueStore) throws Exception
    {
        Preconditions.checkArgument(files.length == 1, "Only one file should be present at this point.");
        
        // TODO: Check sequence.
        
        LineIterator lines = null;
        
        try
        {
            lines = FileUtils.lineIterator(files[0], FILE_ENCODING);
            
            Dataset<Institution> dataset = innerParse(lines);
            persister.persistDeltaDataset(dataset);
        }
        catch (Exception e)
        {
            LineIterator.closeQuietly(lines);
        }
    }
    
    private Dataset<Institution> innerParse(Iterator<String> lines)
    {
        Dataset<Institution> dataset = new Dataset<Institution>(Institution.class);
        
        while (lines.hasNext())
        {
            Institution institution = parseLine(lines.next());

            if (institution != null)
            {
                dataset.add(institution);
            }
        }
        
        return dataset;
    }

    private Institution parseLine(String line)
    {
        // Determine the record type.
        //
        String recordType = line.substring(ENTRY_TYPE_START_INDEX, ENTRY_TYPE_END_INDEX);

        if (!recordType.equals(RECORD_TYPE_DEPARTMENT) && !recordType.equals(RECORD_TYPE_HOSPITAL))
        {
            throw new ParserException("Unknown record type. line=" + line);
        }
        
        // Since the old record types do not have a operation code (and we are not
        // interested in those records) we can ignore the line.
        //
        if (line.length() < OPERATION_CODE_INDEX + 1)
        {
            return null;
        }

        // Determine the operation code.
        //
        char code = line.charAt(OPERATION_CODE_INDEX);

        if (code == OPERATION_CODE_CREATE || code == OPERATION_CODE_UPDATE)
        {
            // Create and update are handled the same way.

            InstitutionType type = recordType.equals(RECORD_TYPE_DEPARTMENT) ? InstitutionType.HOSPITAL_DEPARTMENT : InstitutionType.HOSPITAL;

            Institution institution = new Institution(type);
            
            // TODO: Rename to 'SKSkode' so it matches the input. This is generally what we should
            // do for all parsers to get consistency and transparency.
            //
            institution.setNummer(line.substring(SKS_CODE_START_INDEX, SKS_CODE_END_INDEX).trim());
            
            // FIXME: This does not make sense for validFrom and validTo. Use other fields.
            //
            institution.setValidFrom(dateFormat.parseDateTime(line.substring(23, 31)).toDate());
            institution.setValidTo(dateFormat.parseDateTime(line.substring(39, 47)).toDate());

            // TODO: Rename to 'Kodetekst' so it matches the input.
            //
            institution.setNavn(line.substring(CODE_TEXT_START_INDEX, CODE_TEXT_END_INDEX).trim());

            return institution;
        }
        else if (code == OPERATION_CODE_NONE)
        {
            return null;
        }
        else
        {
            throw new ParserException("SKS parser encountered an unknown operation code. code=" + code);
        }
    }
}
