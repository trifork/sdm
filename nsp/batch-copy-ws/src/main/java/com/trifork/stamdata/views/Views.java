// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.views;

import static com.trifork.stamdata.Preconditions.checkArgument;
import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;
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


/**
 * Convenience methods for working with views.
 *
 * @author Thomas Børlum (thb@trifork.com)
 */
public final class Views {

	private static final Pattern URI_REGEX = Pattern.compile("stamdata://(.+)");

	/**
	 * Checks that the view has be configured correctly.
	 *
	 * All views must be annotated with {@link Entity}.
	 * All views must be annotated with {@link ViewPath}.
	 * All views must be annotated with {@link XmlRootElement}.
	 */
	public static void checkViewIntegrity(Class<? extends View> viewClass) {

		checkNotNull(viewClass);

		checkArgument(viewClass.isAnnotationPresent(Entity.class));
		checkArgument(viewClass.isAnnotationPresent(XmlRootElement.class));
		checkArgument(viewClass.isAnnotationPresent(ViewPath.class));
	}

	public static String convertStamdataUriToViewName(String stamdataURI) {

		checkNotNull(stamdataURI);

		Matcher matcher = URI_REGEX.matcher(stamdataURI);
		return matcher.find() ? matcher.group(1) : null;
	}

	public static String getViewPath(Class<? extends View> viewClass) {

		checkViewIntegrity(viewClass);
		return viewClass.getAnnotation(ViewPath.class).value();
	}

	public static Set<Class<? extends View>> findAllViews() {

		Set<Class<? extends View>> views = Sets.newHashSet();
		
		// Autorisationsregisteret
		
		views.add(Autorisation.class);
		
		// CPR
		
		views.add(BarnRelation.class);
		views.add(ForaeldremyndighedsRelation.class);
		views.add(Person.class);
		views.add(UmyndiggoerelseVaergeRelation.class);
		
		// DKMA
		
		views.add(Administrationsvej.class);
		views.add(ATC.class);
		views.add(Beregningsregler.class);
		views.add(Dosering.class);
		views.add(Doseringskode.class);
		views.add(Drug.class);
		views.add(EmballagetypeKoder.class);
		views.add(Enhedspriser.class);
		views.add(Firma.class);
		views.add(Formbetegnelse.class);
		views.add(Indholdsstoffer.class);
		views.add(Indikation.class);
		views.add(Indikationskode.class);
		views.add(Klausulering.class);
		views.add(LaegemiddelAdministrationsvejRelation.class);
		views.add(Laegemiddelnavn.class);
		views.add(Medicintilskud.class);
		views.add(Opbevaringsbetingelser.class);
		views.add(OplysningerOmDosisdispensering.class);
		views.add(Pakning.class);
		views.add(Pakningskombination.class);
		views.add(PakningskombinationerUdenPriser.class);
		views.add(Pakningsstoerrelsesenhed.class);
		views.add(Priser.class);
		views.add(Rekommandation.class);
		views.add(SpecialeForNBS.class);
		views.add(Styrkeenhed.class);
		views.add(Substitution.class);
		views.add(SubstitutionAfLaegemidlerUdenFastPris.class);
		views.add(TakstVersion.class);
		views.add(Tidsenhed.class);
		views.add(Tilskudsinterval.class);
		views.add(TilskudsprisgrupperPakningsniveau.class);
		views.add(UdgaaedeNavne.class);
		views.add(Udleveringsbestemmelse.class);
		
		// Doseringsforslag
		
		views.add(DosageStructure.class);
		views.add(DosageUnit.class);
		views.add(DosageVersion.class);
		views.add(com.trifork.stamdata.views.doseringsforslag.Drug.class);
		views.add(DrugDosageStructureRelation.class);
		
		// Sikrede
		
		views.add(SaerligSundhedskort.class);
		views.add(Sikrede.class);
		views.add(SikredeYderRelation.class);
		
		// SOR
		
		views.add(Apotek.class);
		views.add(Praksis.class);
		views.add(Sygehus.class);
		views.add(SygehusAfdeling.class);
		views.add(Yder.class);
		
		// SKS
		
		views.add(Institution.class);
		
		// Yderregisteret
		
		views.add(com.trifork.stamdata.views.yderregisteret.Person.class);
		views.add(Yderregister.class);
		
		return views;
	}
}
