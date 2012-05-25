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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.jobs.FileParserJob;
import com.trifork.stamdata.importer.jobs.bemyndigelse.BemyndigelseParser;
import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.ParserContext;
import com.trifork.stamdata.importer.parsers.ParserState;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Set;

public class ParserConfiguration
{
    private static Logger log = Logger.getLogger(ParserConfiguration.class); 

    @Deprecated
    private static Set<FileParserJob> bindOldParsers(CompositeConfiguration config, File rootDir)
    {
        Set<FileParserJob> parsers = Sets.newHashSet();

        for (String line : config.getStringArray("parser"))
        {
            String[] values = line.split(";");

            if (values.length != 2)
            {
                throw new RuntimeException("All parsers must be configured with an expected import frequency. " + line);
            }

            Class<? extends FileParser> parserClass;

            try
            {
                parserClass = Class.forName(values[0].trim()).asSubclass(FileParser.class);
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }

            int minimumImportFrequency = Integer.parseInt(values[1].trim());

            FileParserJob job = new FileParserJob(rootDir, parserClass, minimumImportFrequency);

            parsers.add(job);
        }

        return parsers;
    }

    private static Set<ParserContext> bindNewParsers(CompositeConfiguration config, Binder binder)
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

                ParserContext descriptor = new ParserContext(parserClass, minimumImportFrequency);

                // Currently the context needs access to the the parser's inbox to check if
                // it is locked so we have to inject it.
                //
                // TODO: This is not particularly pretty.
                //
                binder.requestInjection(descriptor);

                parsers.add(descriptor);
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }
        }

        return parsers;
    }

    public static void bindParsers(CompositeConfiguration config, File rootDir, Binder binder) {
        if(log.isDebugEnabled()) {
            log.debug("Old parsers: "+config.getString("parser"));
            log.debug("New parsers: "+config.getString("parser_v2"));
        }
        
        Set<FileParserJob> oldParsers = bindOldParsers(config, rootDir);
        Set<ParserContext> newParsers = bindNewParsers(config, binder);

        if(log.isDebugEnabled()) {
            log.debug("Old parsers: "+oldParsers);
            log.debug("New parsers: "+newParsers);
        }

        binder.bind(new TypeLiteral<Set<FileParserJob>>() {}).toInstance(ImmutableSet.copyOf(oldParsers));
        binder.bind(new TypeLiteral<Set<ParserContext>>() {}).toInstance(ImmutableSet.copyOf(newParsers));

        Set<ParserState> states = Sets.newHashSet();
        states.addAll(oldParsers);
        states.addAll(newParsers);

        binder.bind(new TypeLiteral<Set<ParserState>>() {}).toInstance(states);
    }
}
