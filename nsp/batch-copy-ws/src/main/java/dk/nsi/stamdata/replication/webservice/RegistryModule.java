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

package dk.nsi.stamdata.replication.webservice;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.persistence.Persistent;

import dk.nsi.stamdata.replication.webservice.annotations.Registry;
import dk.nsi.stamdata.views.View;
import dk.nsi.stamdata.views.ViewPath;


public class RegistryModule extends ServletModule
{
    @Override
    protected final void configureServlets()
    {
        bind(AtomFeedWriter.class);
    }


    @Provides
    @Registry
    @SuppressWarnings("unchecked")
    protected Map<String, Class<? extends View>> provideViewMap(@Persistent Set<Object> entities)
    {
        Map<String, Class<? extends View>> viewMap = Maps.newTreeMap();

        for (Object entity : entities)
        {
            ViewPath annotation = entity.getClass().getAnnotation(ViewPath.class);

            if (entity.getClass().isAnnotationPresent(ViewPath.class))
            {
                Class<? extends View> viewClass = (Class<? extends View>) entity.getClass();

                viewMap.put(annotation.value(), viewClass);
            }
        }

        return viewMap;
    }
}
