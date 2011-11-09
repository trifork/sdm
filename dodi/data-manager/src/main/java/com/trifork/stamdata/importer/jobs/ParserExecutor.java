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
package com.trifork.stamdata.importer.jobs;

import com.trifork.stamdata.importer.parsers.Parser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class ParserExecutor implements Runnable
{
    private final Parser parser;
    private final Inbox inbox;

    @Inject
    ParserExecutor(Parser parser, Inbox inbox)
    {
        this.parser = parser;
        this.inbox = inbox;
    }

    @Override
    public void run()
    {
        try
        {

        }
        catch (Exception e)
        {
            
        }
    }

    private File checkInbox() throws IOException
    {
        inbox.update();

        // TODO: Log the size of the inbox, so we can see if it starts growing.

        return inbox.peek();
    }
}
