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
package dk.nsi.stamdata.replication.tools;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import dk.nsi.stamdata.views.View;
import dk.nsi.stamdata.views.ViewModule;
import dk.nsi.stamdata.views.autorisationsregisteret.Autorisation;
import dk.nsi.stamdata.views.cpr.BarnRelation;
import dk.nsi.stamdata.views.cpr.Person;
import org.dom4j.DocumentFactory;
import org.dom4j.io.DOMWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

public class SchemaGeneratorTest
{
    private SchemaGenerator generator;

    @Inject
    private Map<String, Class<? extends View>> views;

    @Before
    public void setUp() throws Exception
    {
        generator = new SchemaGenerator();

        Guice.createInjector(new ViewModule()).injectMembers(this);
    }

    @Test
    public void testThatWeCanGenerateASchemaForAllViewTypes() throws Exception
    {
        for (Class<? extends View> view : views.values())
        {
            StringWriter writer = new StringWriter();
            generator.generate(view, writer);
        }
    }
}
