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
package com.trifork.stamdata.importer.config;

import com.google.common.collect.Sets;
import com.google.inject.Binder;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.ParserContext;
import org.apache.commons.configuration.CompositeConfiguration;

import java.util.Set;

public class ParserConfiguration
{
    @Deprecated
    public static Set<OldParserContext> getOldConfiguredParsers(final CompositeConfiguration config)
    {
        Set<OldParserContext> parsers = Sets.newHashSet();

        for (String line : config.getStringArray("parser"))
        {
            String[] values = line.split(";");

            if (values.length != 2)
            {
                throw new RuntimeException("All parsers must be configured with an expected import frequency. " + line);
            }

             OldParserContext parser = new OldParserContext();

            try
            {
                parser.parserClass = Class.forName(values[0].trim()).asSubclass(FileParser.class);
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }

            parser.minimumImportFrequency = Integer.parseInt(values[1].trim());
            parsers.add(parser);
        }

        return parsers;
    }

    public static Set<ParserContext> getConfiguredParsers(final CompositeConfiguration config, Binder binder)
    {
        Set<ParserContext> parsers = Sets.newHashSet();

        for (String line : config.getStringArray("parser_v2"))
        {
            String[] values = line.split(";");

            if (values.length != 2)
            {
                throw new RuntimeException("All parsers must be configured with an expected import frequency. " + line);
            }

            try
            {
                Class<? extends Parser> parserClass = Class.forName(values[0].trim()).asSubclass(Parser.class);
                int minimumImportFrequency = Integer.parseInt(values[1].trim());

                ParserContext context = new ParserContext(parserClass, minimumImportFrequency);

                binder.requestInjection(context);

                parsers.add(context);
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }
        }

        return parsers;
    }
}
