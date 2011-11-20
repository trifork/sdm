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
package com.trifork.stamdata.importer.parsers;

import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.importer.parsers.annotations.ParserInformation;

/**
 * Utility methods for working with classes that implement the {@link Parser} interface.
 *
 * @author Thomas Børlum <thb@trifork.com>
 */
public final class Parsers
{
    protected Parsers() {}
    
    public static String getIdentifier(Parser parser)
    {
        return getIdentifier(parser.getClass());
    }
    
    public static String getIdentifier(Class<? extends Parser> parserClass)
    {
        return getParserInformation(parserClass).id();
    }

    public static String getName(Class<? extends Parser> parserClass)
    {
        return getParserInformation(parserClass).name();
    }

    private static ParserInformation getParserInformation(Class<? extends Parser> parserClass)
    {
        checkParserIntegrity(parserClass);

        return parserClass.getAnnotation(ParserInformation.class);
    }

    private static void checkParserIntegrity(Class<? extends Parser> parserClass)
    {
        Preconditions.checkNotNull(parserClass, "parserClass");
        Preconditions.checkArgument(parserClass.isAnnotationPresent(ParserInformation.class), "Parsers must be annotated with @ParserInformation.");
    }
}
