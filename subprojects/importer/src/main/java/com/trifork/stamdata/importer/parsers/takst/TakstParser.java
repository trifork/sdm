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
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.model.Dataset;
import com.trifork.stamdata.importer.parsers.exceptions.FileParseException;
import com.trifork.stamdata.importer.parsers.takst.model.*;
import com.trifork.stamdata.importer.util.DateUtils;


public class TakstParser
{
	private static final String SUPPORTED_TAKST_VERSION = "12.0";
	static Logger logger = LoggerFactory.getLogger(TakstParser.class);

	public Takst parseFiles(List<File> files) throws FileParseException
	{
		String rootFolder = "";

		for (File file : files)
		{
			if (file.getName().endsWith(TakstImporter.requiredFileNames[0]))
			{
				rootFolder = file.getParent() + "/";
				break;
			}
		}

		if ("".equals(rootFolder)) throw new FileParseException("Cannot extract root folder for takst parsing");

		return parseDirectory(rootFolder);
	}

	public Takst parseDirectory(String rootFolder) throws FileParseException
	{
		Takst takst;

		try
		{
			// Parse required meta information first
			String systemline = getSystemLine(rootFolder);
			String version = getVersion(systemline);
			if (!SUPPORTED_TAKST_VERSION.equals(version)) logger.warn("Trying to parse unknown version: '" + version + "' of the takst! Only known version is: '" + SUPPORTED_TAKST_VERSION + "'");
			Date fromDate = getValidFromDate(systemline);
			logger.debug("Parsing takst version: '" + version + "' validFrom '" + fromDate.toString());
			takst = new Takst(fromDate, DateUtils.FUTURE);

			// Add the takst itself to the takst as a "meta entity" to represent
			// in DB that the takst was loaded
			takst.setValidityWeekNumber(getValidWeek(systemline));
			takst.setValidityYear(getValidYear(systemline));
			List<Takst> takstMetaEntity = new ArrayList<Takst>();
			takstMetaEntity.add(takst);
			takst.addDataset(new TakstDataset<Takst>(takst, takstMetaEntity, Takst.class));
		}
		catch (Exception e)
		{
			throw new FileParseException("An error occured while reading takst metadata", e);
		}

		try
		{
			// Now parse the required data files
			takst.addDataset(new TakstDataset<Laegemiddel>(takst, LaegemiddelFactory.read(rootFolder), Laegemiddel.class));
			takst.addDataset(new TakstDataset<Pakning>(takst, PakningFactory.read(rootFolder), Pakning.class));
			takst.addDataset(new TakstDataset<Priser>(takst, PriserFactory.read(rootFolder), Priser.class));
			takst.addDataset(new TakstDataset<Substitution>(takst, SubstitutionFactory.read(rootFolder), Substitution.class));
			takst.addDataset(new TakstDataset<SubstitutionAfLaegemidlerUdenFastPris>(takst, SubstitutionAfLaegemidlerUdenFastPrisFactory.read(rootFolder), SubstitutionAfLaegemidlerUdenFastPris.class));
			takst.addDataset(new TakstDataset<TilskudsprisgrupperPakningsniveau>(takst, TilskudsprisgrupperPakningsniveauFactory.read(rootFolder), TilskudsprisgrupperPakningsniveau.class));
			takst.addDataset(new TakstDataset<Firma>(takst, FirmaFactory.read(rootFolder), Firma.class));
			takst.addDataset(new TakstDataset<UdgaaedeNavne>(takst, UdgaaedeNavneFactory.read(rootFolder), UdgaaedeNavne.class));
			takst.addDataset(new TakstDataset<Administrationsvej>(takst, AdministrationsvejFactory.read(rootFolder), Administrationsvej.class));
			takst.addDataset(new TakstDataset<ATCKoderOgTekst>(takst, ATCKoderOgTekstFactory.read(rootFolder), ATCKoderOgTekst.class));
			takst.addDataset(new TakstDataset<Beregningsregler>(takst, BeregningsreglerFactory.read(rootFolder), Beregningsregler.class));
			takst.addDataset(new TakstDataset<EmballagetypeKoder>(takst, EmballagetypeKoderFactory.read(rootFolder), EmballagetypeKoder.class));
			takst.addDataset(new TakstDataset<DivEnheder>(takst, DivEnhederFactory.read(rootFolder), DivEnheder.class));
			takst.addDataset(new TakstDataset<Medicintilskud>(takst, MedicintilskudFactory.read(rootFolder), Medicintilskud.class));
			takst.addDataset(new TakstDataset<Klausulering>(takst, KlausuleringFactory.read(rootFolder), Klausulering.class));
			takst.addDataset(new TakstDataset<Udleveringsbestemmelser>(takst, UdleveringsbestemmelserFactory.read(rootFolder), Udleveringsbestemmelser.class));
			takst.addDataset(new TakstDataset<SpecialeForNBS>(takst, SpecialeForNBSFactory.read(rootFolder), SpecialeForNBS.class));
			takst.addDataset(new TakstDataset<Opbevaringsbetingelser>(takst, OpbevaringsbetingelserFactory.read(rootFolder), Opbevaringsbetingelser.class));
			takst.addDataset(new TakstDataset<Tilskudsintervaller>(takst, TilskudsintervallerFactory.read(rootFolder), Tilskudsintervaller.class));
			takst.addDataset(new TakstDataset<OplysningerOmDosisdispensering>(takst, OplysningerOmDosisdispenseringFactory.read(rootFolder), OplysningerOmDosisdispensering.class));
			takst.addDataset(new TakstDataset<Indikationskode>(takst, IndikationskodeFactory.read(rootFolder), Indikationskode.class));
			takst.addDataset(new TakstDataset<Indikation>(takst, IndikationFactory.read(rootFolder), Indikation.class));
			takst.addDataset(new TakstDataset<Doseringskode>(takst, DoseringskodeFactory.read(rootFolder), Doseringskode.class));
			takst.addDataset(new TakstDataset<Dosering>(takst, DoseringFactory.read(rootFolder), Dosering.class));
		}
		catch (Exception e)
		{
			throw new FileParseException("An error occured while reading takst data", e);
		}
		// Now parse optional files one at a time, to be robust to them not
		// being present
		try
		{
			takst.addDataset(new TakstDataset<Laegemiddelnavn>(takst, LaegemiddelnavnFactory.read(rootFolder), Laegemiddelnavn.class));
		}
		catch (IOException e)
		{
			logger.debug(LaegemiddelFactory.getLmsName() + " could not be read. Ignoring as it is not required");
		}
		try
		{
			takst.addDataset(new TakstDataset<LaegemiddelformBetegnelser>(takst, LaegemiddelformBetegnelserFactory.read(rootFolder), LaegemiddelformBetegnelser.class));
		}
		catch (IOException e)
		{
			logger.debug(LaegemiddelformBetegnelserFactory.getLmsName() + " could not be read. Ignoring as it is not required");
		}
		try
		{
			takst.addDataset(new TakstDataset<Rekommandationer>(takst, RekommandationerFactory.read(rootFolder), Rekommandationer.class));
		}
		catch (IOException e)
		{
			logger.debug(RekommandationerFactory.getLmsName() + " could not be read. Ignoring as it is not required");
		}
		try
		{
			takst.addDataset(new TakstDataset<Indholdsstoffer>(takst, IndholdsstofferFactory.read(rootFolder), Indholdsstoffer.class));
		}
		catch (IOException e)
		{
			logger.debug(IndholdsstofferFactory.getLmsName() + " could not be read. Ignoring as it is not required");
		}
		try
		{
			takst.addDataset(new TakstDataset<Enhedspriser>(takst, EnhedspriserFactory.read(rootFolder), Enhedspriser.class));
		}
		catch (IOException e)
		{
			logger.debug(EnhedspriserFactory.getLmsName() + " could not be read. Ignoring as it is not required");
		}
		try
		{
			takst.addDataset(new TakstDataset<Pakningskombinationer>(takst, PakningskombinationerFactory.read(rootFolder), Pakningskombinationer.class));
			takst.addDataset(new TakstDataset<PakningskombinationerUdenPriser>(takst, PakningskombinationerUdenPriserFactory.read(rootFolder), PakningskombinationerUdenPriser.class));
		}
		catch (IOException e)
		{
			logger.debug(PakningskombinationerFactory.getLmsName() + " or " + PakningskombinationerUdenPriserFactory.getLmsName() + " could not be read. Ignoring as they are not required");
		}
		try
		{
			// Post process takst
			addTypedDivEnheder(takst);
			addLaegemiddelAdministrationsvejRefs(takst);
			filterOutVetDrugs(takst);
			return takst;
		}
		catch (Exception e)
		{
			throw new FileParseException("An error occured while post-processing takst", e);
		}
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
				lars.add(new LaegemiddelAdministrationsvejRef(lm, av));
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

		logger.debug("Number of entities before filtering: " + takst.getEntities().size());
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
		logger.debug("Number of entities after filtering pakninger: " + takst.getEntities().size());
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
		logger.debug("Number of entities after filtering lmr: " + takst.getEntities().size());
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
		logger.debug("Number of entities after filtering atc: " + takst.getEntities().size());
	}

	private int getValidYear(String line)
	{

		return Integer.parseInt(line.substring(87, 91));
	}

	private int getValidWeek(String line)
	{

		return Integer.parseInt(line.substring(91, 93));
	}

	private Date getValidFromDate(String line)
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

	private String getSystemLine(String rootFolder) throws FileParseException
	{

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(rootFolder + "/system.txt"));
			return br.readLine();
		}
		catch (Exception e)
		{
			throw new FileParseException("Error parsing takst: Could not read from " + rootFolder + "/system.txt", e);
		}
	}
}
