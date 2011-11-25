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

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.trifork.stamdata.importer.parsers.ParserState;
import com.trifork.stamdata.importer.parsers.annotations.InboxRootPath;
import org.hibernate.mapping.Collection;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

@Singleton
public class ParserServlet extends HttpServlet
{
    private final Set<ParserState> parsers;
    private final File inboxRoot;

    @Inject
    ParserServlet(Set<ParserState> parsers, @InboxRootPath String inboxRoot)
    {
        this.parsers = parsers;
        this.inboxRoot = new File(inboxRoot);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String id = getIdFromQuery(request);
        ParserState parser = getParserById(id);

        String info = getInfoFromQuery(request);

        if (info == null)
        {
            show(parser, response.getWriter());
        }
        else if (info.equals("inbox"))
        {
            inbox(parser, response.getWriter());
        }
    }

    public void show(ParserState parser, Writer writer) throws IOException
    {
        Map<String, Object> values = Maps.newHashMap();

        values.put("name", parser.name());
        values.put("id", parser.identifier());
        values.put("inProgress", parser.isInProgress());
        values.put("locked", parser.isLocked());

        String json = new Gson().toJson(values);

        writer.write(json);
    }
    
    public void inbox(ParserState parser, Writer writer) throws IOException
    {
        String inboxPath = new File(inboxRoot, parser.identifier()).getAbsolutePath();
        writer.write(inboxPath);
    }

    //
    // Helpers
    //
    
    protected String getIdFromQuery(HttpServletRequest request)
    {
        return request.getParameter("id");
    }
    
    protected String getInfoFromQuery(HttpServletRequest request)
    {
        return request.getParameter("info");
    }

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
}
