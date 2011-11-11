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

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.trifork.stamdata.Preconditions;

public class RecordSpecification
{

    public static enum SikredeType { ALFANUMERICAL, NUMERICAL }
    
    public static final RecordSpecification SIKREDE_FIELDS_SINGLETON;

    static 
    {
        SIKREDE_FIELDS_SINGLETON = RecordSpecification.newSikredeFields(
                // Page 1 (6 of 11)
                "PostType", SikredeType.NUMERICAL, 2,
                "CPRnr", SikredeType.ALFANUMERICAL, 10,
                "SYdernr", SikredeType.ALFANUMERICAL, 6,
                "SIkraftDatoYder", SikredeType.ALFANUMERICAL, 8,
                "SRegDatoYder", SikredeType.ALFANUMERICAL, 8,
                "SSikrGrpKode", SikredeType.ALFANUMERICAL, 1,
                "SIkraftDatoGrp", SikredeType.ALFANUMERICAL, 8,
                "SRegDatoGrp", SikredeType.ALFANUMERICAL, 8,
                "SSikrKomKode", SikredeType.ALFANUMERICAL, 3,
                "SIkraftDatoKomKode", SikredeType.ALFANUMERICAL, 3,
                "SYdernrGI", SikredeType.ALFANUMERICAL, 6,
                "SIkraftDatoYderGI", SikredeType.ALFANUMERICAL, 8,
                "SRegDatoYderGI", SikredeType.ALFANUMERICAL, 8,
                "SSikrGrpKodeGI", SikredeType.ALFANUMERICAL, 1,
                "SIkraftDatoGrpGI", SikredeType.ALFANUMERICAL, 8,
                "SRegDatoGrpGI", SikredeType.ALFANUMERICAL, 8,
                "SYdernrFrem", SikredeType.ALFANUMERICAL, 6,
                "SIkraftDatoYderFrem", SikredeType.ALFANUMERICAL, 8,
                "SRegDatoYderFrem", SikredeType.ALFANUMERICAL, 8,
                "SSikrGrpKodeFrem", SikredeType.ALFANUMERICAL, 1,

                // Page 2 (7 of 11)
                "SIkraftDatoGrpFrem", SikredeType.ALFANUMERICAL, 8,
                "SRegDatoGrpFrem", SikredeType.ALFANUMERICAL, 8,
                "SKon", SikredeType.ALFANUMERICAL, 1,
                "SAlder", SikredeType.ALFANUMERICAL, 3,
                "SFolgerskabsPerson", SikredeType.ALFANUMERICAL, 10,
                "SStatus", SikredeType.ALFANUMERICAL, 2,
                "SBevisDato", SikredeType.ALFANUMERICAL, 8,
                // ...
                "PNavn", SikredeType.ALFANUMERICAL, 34,
                // ...
                "SBSStatsborgerskabKode", SikredeType.ALFANUMERICAL, 2,
                "SBSStatsborgerskab", SikredeType.ALFANUMERICAL, 47,
                "SSKAdrLinie1", SikredeType.ALFANUMERICAL, 40,
                "SSKAdrLinie2", SikredeType.ALFANUMERICAL, 40,

                // Page 3 (8 of 11)
                "SSKBopelsLand", SikredeType.ALFANUMERICAL, 40,
                "SSKBopelsLAndKode", SikredeType.ALFANUMERICAL, 2,
                "SSKEmailAdr", SikredeType.ALFANUMERICAL, 50,
                "SSKFamilieRelation", SikredeType.ALFANUMERICAL, 10,
                "SSKFodselsdato", SikredeType.ALFANUMERICAL, 10,
                "SSKGyldigFra", SikredeType.ALFANUMERICAL, 10,
                "SSKGyldigTil", SikredeType.ALFANUMERICAL, 10,
                "SSKMobilNr", SikredeType.ALFANUMERICAL, 20,
                "SSKPostNrBy", SikredeType.ALFANUMERICAL, 40,
                "SSLForsikringsinstans", SikredeType.ALFANUMERICAL, 21,
                "SSLForsikringsinstansKode", SikredeType.ALFANUMERICAL, 10,
                "SSLForsikringsnr", SikredeType.ALFANUMERICAL, 15,
                "SSLGyldigFra", SikredeType.ALFANUMERICAL, 10,
                "SSLGyldigTil", SikredeType.ALFANUMERICAL, 10,
                "SSLSocSikretLand", SikredeType.ALFANUMERICAL, 47,
                "SSLSocSikretLandKode", SikredeType.ALFANUMERICAL, 2);
    }
    
    public static class FieldSpecification
    {
        public final String name;
        public final SikredeType type;
        public final int length;
        
        public FieldSpecification(String name, SikredeType type, int length)
        {
            this.name = name;
            this.type = type;
            this.length = length;
        }
    }
    
    private List<FieldSpecification> fieldSpecifications;
    
    private RecordSpecification()
    {
        fieldSpecifications = new ArrayList<FieldSpecification>();
    }
    
    public static RecordSpecification newSikredeFields(Object... fieldDefinitions)
    {
        assert(fieldDefinitions.length % 3 == 0);

        RecordSpecification recordSpecification = new RecordSpecification();
        
        for(int i = 0; i < fieldDefinitions.length; i += 3)
        {
            String name = (String) fieldDefinitions[i + 0];
            SikredeType type = (SikredeType) fieldDefinitions[i + 1];
            int length = (Integer) fieldDefinitions[i + 2];
            
            FieldSpecification fieldSpecification = new FieldSpecification(name, type, length);
            
            recordSpecification.fieldSpecifications.add(fieldSpecification);
        }
        
        return recordSpecification;
    }
    
    public ImmutableList<FieldSpecification> getFieldSpecificationsInCorrectOrder()
    {
        return ImmutableList.copyOf(fieldSpecifications);
    }
    
    public int acceptedTotalLineLength()
    {
        int totalLength = 0;
        for(FieldSpecification fieldSpecification: fieldSpecifications)
        {
            totalLength += fieldSpecification.length;
        }
        return totalLength;
    }
    
    public boolean conformsToSpecifications(Record record)
    {
        Preconditions.checkNotNull(record);
        
        if (record.size() != fieldSpecifications.size())
        {
            return false;
        }
        
        for (FieldSpecification fieldsSpecification: fieldSpecifications)
        {
            if(!record.containsKey(fieldsSpecification.name))
            {
                return false;
            }
            else
            {
                Object value = record.get(fieldsSpecification.name);
                
                if(fieldsSpecification.type == SikredeType.NUMERICAL)
                {
                    if(!(value instanceof Integer))
                    {
                        return false;
                    }
                }
                else if (fieldsSpecification.type == SikredeType.ALFANUMERICAL)
                {
                    if(!(value instanceof String))
                    {
                        return false;
                    }
                    else
                    {
                        String valueAsString = (String) value;
                        if(valueAsString.length() > fieldsSpecification.length)
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    throw new AssertionError("Field specification is in illegal state. Type must be set.");
                }
            }
        }
        
        return true;
    }
}