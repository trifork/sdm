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

package com.trifork.stamdata.importer.parsers.takst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.model.Dataset;
import com.trifork.stamdata.importer.parsers.takst.model.ATCKoderOgTekst;
import com.trifork.stamdata.importer.parsers.takst.model.ATCKoderOgTekstFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Administrationsvej;
import com.trifork.stamdata.importer.parsers.takst.model.AdministrationsvejFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Beregningsregler;
import com.trifork.stamdata.importer.parsers.takst.model.BeregningsreglerFactory;
import com.trifork.stamdata.importer.parsers.takst.model.DivEnheder;
import com.trifork.stamdata.importer.parsers.takst.model.DivEnhederFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Dosering;
import com.trifork.stamdata.importer.parsers.takst.model.DoseringFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Doseringskode;
import com.trifork.stamdata.importer.parsers.takst.model.DoseringskodeFactory;
import com.trifork.stamdata.importer.parsers.takst.model.EmballagetypeKoder;
import com.trifork.stamdata.importer.parsers.takst.model.EmballagetypeKoderFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Enhedspriser;
import com.trifork.stamdata.importer.parsers.takst.model.EnhedspriserFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Firma;
import com.trifork.stamdata.importer.parsers.takst.model.FirmaFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Indholdsstoffer;
import com.trifork.stamdata.importer.parsers.takst.model.IndholdsstofferFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Indikation;
import com.trifork.stamdata.importer.parsers.takst.model.IndikationFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Indikationskode;
import com.trifork.stamdata.importer.parsers.takst.model.IndikationskodeFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Klausulering;
import com.trifork.stamdata.importer.parsers.takst.model.KlausuleringFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Laegemiddel;
import com.trifork.stamdata.importer.parsers.takst.model.LaegemiddelAdministrationsvejRef;
import com.trifork.stamdata.importer.parsers.takst.model.LaegemiddelFactory;
import com.trifork.stamdata.importer.parsers.takst.model.LaegemiddelformBetegnelser;
import com.trifork.stamdata.importer.parsers.takst.model.LaegemiddelformBetegnelserFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Laegemiddelnavn;
import com.trifork.stamdata.importer.parsers.takst.model.LaegemiddelnavnFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Medicintilskud;
import com.trifork.stamdata.importer.parsers.takst.model.MedicintilskudFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Opbevaringsbetingelser;
import com.trifork.stamdata.importer.parsers.takst.model.OpbevaringsbetingelserFactory;
import com.trifork.stamdata.importer.parsers.takst.model.OplysningerOmDosisdispensering;
import com.trifork.stamdata.importer.parsers.takst.model.OplysningerOmDosisdispenseringFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Pakning;
import com.trifork.stamdata.importer.parsers.takst.model.PakningFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Pakningskombinationer;
import com.trifork.stamdata.importer.parsers.takst.model.PakningskombinationerFactory;
import com.trifork.stamdata.importer.parsers.takst.model.PakningskombinationerUdenPriser;
import com.trifork.stamdata.importer.parsers.takst.model.PakningskombinationerUdenPriserFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Pakningsstoerrelsesenhed;
import com.trifork.stamdata.importer.parsers.takst.model.Priser;
import com.trifork.stamdata.importer.parsers.takst.model.PriserFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Rekommandationer;
import com.trifork.stamdata.importer.parsers.takst.model.RekommandationerFactory;
import com.trifork.stamdata.importer.parsers.takst.model.SpecialeForNBS;
import com.trifork.stamdata.importer.parsers.takst.model.SpecialeForNBSFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Styrkeenhed;
import com.trifork.stamdata.importer.parsers.takst.model.Substitution;
import com.trifork.stamdata.importer.parsers.takst.model.SubstitutionAfLaegemidlerUdenFastPris;
import com.trifork.stamdata.importer.parsers.takst.model.SubstitutionAfLaegemidlerUdenFastPrisFactory;
import com.trifork.stamdata.importer.parsers.takst.model.SubstitutionFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Tidsenhed;
import com.trifork.stamdata.importer.parsers.takst.model.Tilskudsintervaller;
import com.trifork.stamdata.importer.parsers.takst.model.TilskudsintervallerFactory;
import com.trifork.stamdata.importer.parsers.takst.model.TilskudsprisgrupperPakningsniveau;
import com.trifork.stamdata.importer.parsers.takst.model.TilskudsprisgrupperPakningsniveauFactory;
import com.trifork.stamdata.importer.parsers.takst.model.UdgaaedeNavne;
import com.trifork.stamdata.importer.parsers.takst.model.UdgaaedeNavneFactory;
import com.trifork.stamdata.importer.parsers.takst.model.Udleveringsbestemmelser;
import com.trifork.stamdata.importer.parsers.takst.model.UdleveringsbestemmelserFactory;
import com.trifork.stamdata.importer.util.DateUtils;


public class TakstParser
{
	private static final String SUPPORTED_TAKST_VERSION = "12.0";
	private static final Logger logger = LoggerFactory.getLogger(TakstParser.class);

	private <T extends TakstEntity> void add(Takst takst, FixedLengthFileParser parser, FixedLengthParserConfiguration<T> config, Class<T> type) throws IOException
	{
		List<T> entities = parser.parse(config, type);
		takst.addDataset(new TakstDataset<T>(takst, entities, type));
	}

	private <T extends TakstEntity> void addOptional(Takst takst, FixedLengthFileParser parser, FixedLengthParserConfiguration<T> config, Class<T> type) throws IOException
	{
		List<T> entities = parser.parse(config, type);
		takst.addDataset(new TakstDataset<T>(takst, entities, type));
	}

	public Takst parseFiles(File[] input) throws Exception
	{
		// Parse required meta information first.
		
		String systemline = getSystemLine(input);
		String version = getVersion(systemline);

		if (!SUPPORTED_TAKST_VERSION.equals(version)) logger.warn("Parsing unsupported version={} of the takst! expected={}", version, SUPPORTED_TAKST_VERSION);

		Date fromDate = getValidFromDate(systemline);

		Takst takst = new Takst(fromDate, DateUtils.FUTURE);

		// Add the takst itself to the takst as a "meta entity" to represent
		// in DB that the takst was loaded.

		takst.setValidityWeekNumber(getValidWeek(systemline));
		takst.setValidityYear(getValidYear(systemline));

		List<Takst> takstMetaEntity = new ArrayList<Takst>();
		takstMetaEntity.add(takst);
		takst.addDataset(new TakstDataset<Takst>(takst, takstMetaEntity, Takst.class));

		// Now parse the required data files.

		FixedLengthFileParser parser = new FixedLengthFileParser(input);

		add(takst, parser, new LaegemiddelFactory(), Laegemiddel.class);
		add(takst, parser, new PakningFactory(), Pakning.class);
		add(takst, parser, new PriserFactory(), Priser.class);
		add(takst, parser, new SubstitutionFactory(), Substitution.class);
		add(takst, parser, new SubstitutionAfLaegemidlerUdenFastPrisFactory(), SubstitutionAfLaegemidlerUdenFastPris.class);
		add(takst, parser, new TilskudsprisgrupperPakningsniveauFactory(), TilskudsprisgrupperPakningsniveau.class);
		add(takst, parser, new FirmaFactory(), Firma.class);
		add(takst, parser, new UdgaaedeNavneFactory(), UdgaaedeNavne.class);
		add(takst, parser, new AdministrationsvejFactory(), Administrationsvej.class);
		add(takst, parser, new ATCKoderOgTekstFactory(), ATCKoderOgTekst.class);
		add(takst, parser, new BeregningsreglerFactory(), Beregningsregler.class);
		add(takst, parser, new EmballagetypeKoderFactory(), EmballagetypeKoder.class);
		add(takst, parser, new DivEnhederFactory(), DivEnheder.class);
		add(takst, parser, new MedicintilskudFactory(), Medicintilskud.class);
		add(takst, parser, new KlausuleringFactory(), Klausulering.class);
		add(takst, parser, new UdleveringsbestemmelserFactory(), Udleveringsbestemmelser.class);
		add(takst, parser, new SpecialeForNBSFactory(), SpecialeForNBS.class);
		add(takst, parser, new OpbevaringsbetingelserFactory(), Opbevaringsbetingelser.class);
		add(takst, parser, new TilskudsintervallerFactory(), Tilskudsintervaller.class);
		add(takst, parser, new OplysningerOmDosisdispenseringFactory(), OplysningerOmDosisdispensering.class);
		add(takst, parser, new IndikationskodeFactory(), Indikationskode.class);
		add(takst, parser, new IndikationFactory(), Indikation.class);
		add(takst, parser, new DoseringskodeFactory(), Doseringskode.class);
		add(takst, parser, new DoseringFactory(), Dosering.class);

		// Now parse optional files one at a time, to be robust to them not
		// being present.

		addOptional(takst, parser, new LaegemiddelnavnFactory(), Laegemiddelnavn.class);
		addOptional(takst, parser, new LaegemiddelformBetegnelserFactory(), LaegemiddelformBetegnelser.class);
		addOptional(takst, parser, new RekommandationerFactory(), Rekommandationer.class);
		addOptional(takst, parser, new IndholdsstofferFactory(), Indholdsstoffer.class);
		addOptional(takst, parser, new EnhedspriserFactory(), Enhedspriser.class);
		addOptional(takst, parser, new PakningskombinationerFactory(), Pakningskombinationer.class);
		addOptional(takst, parser, new PakningskombinationerUdenPriserFactory(), PakningskombinationerUdenPriser.class);
		
		// Post process the data.
		
		addTypedDivEnheder(takst);
		addLaegemiddelAdministrationsvejRefs(takst);
		filterOutVetDrugs(takst);
		
		return takst;
	}

	/**
	 * Extracts the administrationsveje and adds them to the takst
	 * 
	 * @param takst
	 */
	private void addLaegemiddelAdministrationsvejRefs(Takst takst)
	{
		TakstDataset<Laegemiddel> lmr = takst.getDatasetOfType(Laegemiddel.class);
		List<LaegemiddelAdministrationsvejRef> lars = new ArrayList<LaegemiddelAdministrationsvejRef>();
		
		for (Laegemiddel lm : lmr.getEntities())
		{
			for (Administrationsvej av : lm.getAdministrationsveje())
			{
				lars.add(new LaegemiddelAdministrationsvejRef(lm, av));
			}
		}
		
		takst.addDataset(new TakstDataset<LaegemiddelAdministrationsvejRef>(takst, lars, LaegemiddelAdministrationsvejRef.class));
	}

	private String getVersion(String systemline)
	{
		return systemline.substring(2, 7).trim();
	}

	/**
	 * Sorterer DivEnheder ud på stærke(re) typede entiteter for at matche fmk
	 * stamtabel skemaet
	 * 
	 * @param takst
	 */
	private void addTypedDivEnheder(Takst takst)
	{
		List<Tidsenhed> tidsenhed = new ArrayList<Tidsenhed>();
		List<Pakningsstoerrelsesenhed> pakEnheder = new ArrayList<Pakningsstoerrelsesenhed>();
		List<Styrkeenhed> styrkeEnheder = new ArrayList<Styrkeenhed>();
		Dataset<DivEnheder> divEnheder = takst.getDatasetOfType(DivEnheder.class);
		
		for (DivEnheder enhed : divEnheder.getEntities())
		{
			if (enhed.isEnhedstypeTid())
			{
				tidsenhed.add(new Tidsenhed(enhed));
			}
			else if (enhed.isEnhedstypePakning())
			{
				pakEnheder.add(new Pakningsstoerrelsesenhed(enhed));
			}
			else if (enhed.isEnhedstypeStyrke())
			{
				styrkeEnheder.add(new Styrkeenhed(enhed));
			}
		}

		takst.addDataset(new TakstDataset<Tidsenhed>(takst, tidsenhed, Tidsenhed.class));
		takst.addDataset(new TakstDataset<Pakningsstoerrelsesenhed>(takst, pakEnheder, Pakningsstoerrelsesenhed.class));
		takst.addDataset(new TakstDataset<Styrkeenhed>(takst, styrkeEnheder, Styrkeenhed.class));
	}

	/**
	 * Filter out veterinære entities
	 * 
	 * @param takst
	 */
	static void filterOutVetDrugs(Takst takst)
	{
		List<Pakning> pakningerToBeRemoved = new ArrayList<Pakning>();
		Dataset<Pakning> pakninger = takst.getDatasetOfType(Pakning.class);
		
		if (pakninger != null)
		{
			for (Pakning pakning : pakninger.getEntities())
			{
				if (!pakning.isTilHumanAnvendelse()) pakningerToBeRemoved.add(pakning);
			}
			pakninger.removeEntities(pakningerToBeRemoved);
		}
		
		Dataset<Laegemiddel> lmr = takst.getDatasetOfType(Laegemiddel.class);
		List<Laegemiddel> laegemidlerToBeRemoved = new ArrayList<Laegemiddel>();
		
		if (lmr != null)
		{
			for (Laegemiddel lm : lmr.getEntities())
			{
				if (!lm.isTilHumanAnvendelse()) laegemidlerToBeRemoved.add(lm);
			}
			lmr.removeEntities(laegemidlerToBeRemoved);
		}
		
		Dataset<ATCKoderOgTekst> atckoder = takst.getDatasetOfType(ATCKoderOgTekst.class);
		List<ATCKoderOgTekst> atcToBeRemoved = new ArrayList<ATCKoderOgTekst>();
		
		if (atckoder != null)
		{
			for (ATCKoderOgTekst atc : atckoder.getEntities())
			{
				if (!atc.isTilHumanAnvendelse()) atcToBeRemoved.add(atc);
			}
			atckoder.removeEntities(atcToBeRemoved);
		}
	}

	private int getValidYear(String line)
	{

		return Integer.parseInt(line.substring(87, 91));
	}

	private int getValidWeek(String line)
	{

		return Integer.parseInt(line.substring(91, 93));
	}

	public Date getValidFromDate(String line)
	{
		try
		{
			String dateline = line.substring(47, 55);
			DateFormat df = new SimpleDateFormat("yyyyMMdd");

			return df.parse(dateline);
		}
		catch (ParseException e)
		{
			logger.error("getValidFromDate(" + line + ")", e);
			return null;
		}
	}

	private String getSystemLine(File[] input) throws Exception
	{
		return new BufferedReader(new FileReader(getFileByName("system.txt", input))).readLine();
	}

	public static File getFileByName(String filename, File[] files)
	{
		File result = null;

		for (File f : files)
		{
			if (f.getName().equalsIgnoreCase(filename))
			{
				result = f;
				break;
			}
		}

		return result;
	}
}
