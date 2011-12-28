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
package dk.nsi.stamdata.cpr.pvit.proxy;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class CprAbbsBodyBuilder 
{
    private static final String ABBRIVIATION = "cprabbs";
    private static final String NAMESPACE = "http://nsi.dk/cprabbs/2011/10";
    private static final String TAG_NAME = "CprAbbsRequest";
    
    public String createCprAbbsSoapBody()
    {
        return closedTag(ABBRIVIATION, NAMESPACE, TAG_NAME);
    }
    
    public String createCprAbbsSoapBody(DateTime since)
    {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        String sinceAsString = formatter.print(since);
        
        String sinceTag = String.format("<%s:since>%s</%s:since>", ABBRIVIATION, sinceAsString, ABBRIVIATION);
        
        return openTag(ABBRIVIATION, NAMESPACE, TAG_NAME, sinceTag);
    }
    
    private String closedTag(String abbriviation, String namespace, String tagName)
    {
        return String.format("<%s:%s xmlns:%s=\"%s\"/>", abbriviation, tagName, abbriviation, namespace);
    }

    private String openTag(String abbriviation, String namespace, String tagName, String content)
    {
        return String.format("<%s:%s xmlns:%s=\"%s\">%s</%s:%s>", abbriviation, tagName, abbriviation, namespace, content, abbriviation, tagName);
    }
}
