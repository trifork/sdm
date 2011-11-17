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

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.trifork.stamdata.importer.jobs.DirectoryInbox;
import com.trifork.stamdata.importer.jobs.Inbox;

import static com.trifork.stamdata.Preconditions.checkNotNull;

public class ParserModule extends AbstractModule
{
    private final ParserInfo parserInfo;

    public ParserModule(ParserInfo parserInfo)
    {
        this.parserInfo = checkNotNull(parserInfo);
    }

    public static ParserModule using(ParserInfo parserClass)
    {
        return new ParserModule(parserClass);
    }

    @Override
    protected void configure()
    {
        bind(ParserInfo.class).toInstance(parserInfo);

        bind(Parser.class).to(parserInfo.getParserClass()).in(Scopes.SINGLETON);
        bind(Inbox.class).to(DirectoryInbox.class).in(Scopes.SINGLETON);
    }
}
