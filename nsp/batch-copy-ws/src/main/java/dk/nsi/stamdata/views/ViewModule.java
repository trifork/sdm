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
package dk.nsi.stamdata.views;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.trifork.stamdata.persistence.Persistent;

import dk.nsi.stamdata.views.doseringsforslag.DosageStructure;
import dk.nsi.stamdata.views.doseringsforslag.DosageUnit;
import dk.nsi.stamdata.views.doseringsforslag.DosageVersion;
import dk.nsi.stamdata.views.doseringsforslag.DrugDosageStructureRelation;
import dk.nsi.stamdata.views.vaccination.Disease;
import dk.nsi.stamdata.views.vaccination.DiseaseVaccine;
import dk.nsi.stamdata.views.vaccination.Dosageoption;
import dk.nsi.stamdata.views.vaccination.SSIDrug;
import dk.nsi.stamdata.views.vaccination.VaccinationPlan;
import dk.nsi.stamdata.views.vaccination.VaccinationPlanItem;
import dk.nsi.stamdata.views.vaccination.Vaccine;
import dk.nsi.stamdata.views.vaccination.VaccineDrug;

public class ViewModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        Multibinder<Object> views = Multibinder.newSetBinder(binder(), Object.class, Persistent.class);

        // Doseringsforslag
        
        views.addBinding().to(DosageStructure.class);
        views.addBinding().to(DosageUnit.class);
        views.addBinding().to(DosageVersion.class);
        views.addBinding().to(dk.nsi.stamdata.views.doseringsforslag.Drug.class);
        views.addBinding().to(DrugDosageStructureRelation.class);

        // Vaccinationer
        views.addBinding().to(Disease.class);
        views.addBinding().to(DiseaseVaccine.class);
        views.addBinding().to(Dosageoption.class);
        views.addBinding().to(SSIDrug.class);
        views.addBinding().to(VaccinationPlan.class);
        views.addBinding().to(VaccinationPlanItem.class);
        views.addBinding().to(Vaccine.class);
        views.addBinding().to(VaccineDrug.class);
    }
    

    @Provides
    @SuppressWarnings("unchecked")
    protected Map<String, Class<? extends View>> provideViewMap(@Persistent Set<Object> entities)
    {
        // Filter out views from the persistent classes.
        
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
