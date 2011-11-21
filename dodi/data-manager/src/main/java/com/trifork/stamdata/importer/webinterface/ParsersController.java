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
package com.trifork.stamdata.importer.webinterface;

import com.google.inject.Inject;
import com.trifork.stamdata.importer.parsers.ParserState;
import com.trifork.stamdata.importer.parsers.annotations.InboxRootPath;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.Set;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ParsersController
{
    private final Set<ParserState> parsers;
    private final File inboxRoot;

    @Inject
    ParsersController(Set<ParserState> parsers, @InboxRootPath String inboxRoot)
    {
        this.parsers = parsers;
        this.inboxRoot = new File(inboxRoot);
    }

    @GET
    public Iterable<ParserState> index()
    {
        return parsers;
    }

    @GET
    @Path("/{id}")
    public ParserState show(@PathParam("id") String id)
    {
        return getParserById(id);
    }

    //
    // Helpers
    //

    protected ParserState getParserById(String id)
    {
        for (ParserState parser : parsers)
        {
            if (parser.identifier().equals(id))
            {
                return parser;
            }
        }

        return null;
    }

    @GET @Path("/{id}/inbox")
    public String inboxPath(@PathParam("id") String id)
    {
        return new File(inboxRoot, id).getAbsolutePath();
    }
}
