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
package com.trifork.stamdata.views;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.trifork.stamdata.persistence.Persistent;
import com.trifork.stamdata.views.autorisationsregisteret.Autorisation;
import com.trifork.stamdata.views.cpr.BarnRelation;
import com.trifork.stamdata.views.cpr.ForaeldremyndighedsRelation;
import com.trifork.stamdata.views.cpr.Person;
import com.trifork.stamdata.views.cpr.UmyndiggoerelseVaergeRelation;
import com.trifork.stamdata.views.dkma.ATC;
import com.trifork.stamdata.views.dkma.Administrationsvej;
import com.trifork.stamdata.views.dkma.Beregningsregler;
import com.trifork.stamdata.views.dkma.Dosering;
import com.trifork.stamdata.views.dkma.Doseringskode;
import com.trifork.stamdata.views.dkma.Drug;
import com.trifork.stamdata.views.dkma.EmballagetypeKoder;
import com.trifork.stamdata.views.dkma.Enhedspriser;
import com.trifork.stamdata.views.dkma.Firma;
import com.trifork.stamdata.views.dkma.Formbetegnelse;
import com.trifork.stamdata.views.dkma.Indholdsstoffer;
import com.trifork.stamdata.views.dkma.Indikation;
import com.trifork.stamdata.views.dkma.Indikationskode;
import com.trifork.stamdata.views.dkma.Klausulering;
import com.trifork.stamdata.views.dkma.LaegemiddelAdministrationsvejRelation;
import com.trifork.stamdata.views.dkma.Laegemiddelnavn;
import com.trifork.stamdata.views.dkma.Medicintilskud;
import com.trifork.stamdata.views.dkma.Opbevaringsbetingelser;
import com.trifork.stamdata.views.dkma.OplysningerOmDosisdispensering;
import com.trifork.stamdata.views.dkma.Pakning;
import com.trifork.stamdata.views.dkma.Pakningskombination;
import com.trifork.stamdata.views.dkma.PakningskombinationerUdenPriser;
import com.trifork.stamdata.views.dkma.Pakningsstoerrelsesenhed;
import com.trifork.stamdata.views.dkma.Priser;
import com.trifork.stamdata.views.dkma.Rekommandation;
import com.trifork.stamdata.views.dkma.SpecialeForNBS;
import com.trifork.stamdata.views.dkma.Styrkeenhed;
import com.trifork.stamdata.views.dkma.Substitution;
import com.trifork.stamdata.views.dkma.SubstitutionAfLaegemidlerUdenFastPris;
import com.trifork.stamdata.views.dkma.TakstVersion;
import com.trifork.stamdata.views.dkma.Tidsenhed;
import com.trifork.stamdata.views.dkma.Tilskudsinterval;
import com.trifork.stamdata.views.dkma.TilskudsprisgrupperPakningsniveau;
import com.trifork.stamdata.views.dkma.UdgaaedeNavne;
import com.trifork.stamdata.views.dkma.Udleveringsbestemmelse;
import com.trifork.stamdata.views.doseringsforslag.DosageStructure;
import com.trifork.stamdata.views.doseringsforslag.DosageUnit;
import com.trifork.stamdata.views.doseringsforslag.DosageVersion;
import com.trifork.stamdata.views.doseringsforslag.DrugDosageStructureRelation;
import com.trifork.stamdata.views.sikrede.SaerligSundhedskort;
import com.trifork.stamdata.views.sikrede.Sikrede;
import com.trifork.stamdata.views.sikrede.SikredeYderRelation;
import com.trifork.stamdata.views.sks.Institution;
import com.trifork.stamdata.views.sor.Apotek;
import com.trifork.stamdata.views.sor.Praksis;
import com.trifork.stamdata.views.sor.Sygehus;
import com.trifork.stamdata.views.sor.SygehusAfdeling;
import com.trifork.stamdata.views.sor.Yder;
import com.trifork.stamdata.views.yderregisteret.Yderregister;

public class ViewModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        Multibinder<View> views = Multibinder.newSetBinder(binder(), View.class, Persistent.class);
        views.addBinding().to(Dosering.class);
        
        // Autorisationsregisteret
        
        views.addBinding().to(Autorisation.class);
        
        // CPR
        
        views.addBinding().to(BarnRelation.class);
        views.addBinding().to(ForaeldremyndighedsRelation.class);
        views.addBinding().to(Person.class);
        views.addBinding().to(UmyndiggoerelseVaergeRelation.class);
        
        // DKMA
        
        views.addBinding().to(Administrationsvej.class);
        views.addBinding().to(ATC.class);
        views.addBinding().to(Beregningsregler.class);
        views.addBinding().to(Dosering.class);
        views.addBinding().to(Doseringskode.class);
        views.addBinding().to(Drug.class);
        views.addBinding().to(EmballagetypeKoder.class);
        views.addBinding().to(Enhedspriser.class);
        views.addBinding().to(Firma.class);
        views.addBinding().to(Formbetegnelse.class);
        views.addBinding().to(Indholdsstoffer.class);
        views.addBinding().to(Indikation.class);
        views.addBinding().to(Indikationskode.class);
        views.addBinding().to(Klausulering.class);
        views.addBinding().to(LaegemiddelAdministrationsvejRelation.class);
        views.addBinding().to(Laegemiddelnavn.class);
        views.addBinding().to(Medicintilskud.class);
        views.addBinding().to(Opbevaringsbetingelser.class);
        views.addBinding().to(OplysningerOmDosisdispensering.class);
        views.addBinding().to(Pakning.class);
        views.addBinding().to(Pakningskombination.class);
        views.addBinding().to(PakningskombinationerUdenPriser.class);
        views.addBinding().to(Pakningsstoerrelsesenhed.class);
        views.addBinding().to(Priser.class);
        views.addBinding().to(Rekommandation.class);
        views.addBinding().to(SpecialeForNBS.class);
        views.addBinding().to(Styrkeenhed.class);
        views.addBinding().to(Substitution.class);
        views.addBinding().to(SubstitutionAfLaegemidlerUdenFastPris.class);
        views.addBinding().to(TakstVersion.class);
        views.addBinding().to(Tidsenhed.class);
        views.addBinding().to(Tilskudsinterval.class);
        views.addBinding().to(TilskudsprisgrupperPakningsniveau.class);
        views.addBinding().to(UdgaaedeNavne.class);
        views.addBinding().to(Udleveringsbestemmelse.class);
        
        // Doseringsforslag
        
        views.addBinding().to(DosageStructure.class);
        views.addBinding().to(DosageUnit.class);
        views.addBinding().to(DosageVersion.class);
        views.addBinding().to(com.trifork.stamdata.views.doseringsforslag.Drug.class);
        views.addBinding().to(DrugDosageStructureRelation.class);
        
        // Sikrede
        
        views.addBinding().to(SaerligSundhedskort.class);
        views.addBinding().to(Sikrede.class);
        views.addBinding().to(SikredeYderRelation.class);
        
        // SOR
        
        views.addBinding().to(Apotek.class);
        views.addBinding().to(Praksis.class);
        views.addBinding().to(Sygehus.class);
        views.addBinding().to(SygehusAfdeling.class);
        views.addBinding().to(Yder.class);
        
        // SKS
        
        views.addBinding().to(Institution.class);
        
        // Yderregisteret
        
        views.addBinding().to(com.trifork.stamdata.views.yderregisteret.Person.class);
        views.addBinding().to(Yderregister.class);
    }
}
