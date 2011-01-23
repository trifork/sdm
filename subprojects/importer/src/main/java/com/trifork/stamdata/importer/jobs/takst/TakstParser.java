package com.trifork.stamdata.importer.jobs.takst;


import static com.trifork.stamdata.util.DateUtils.FOREVER;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.trifork.stamdata.importer.jobs.FileParseException;
import com.trifork.stamdata.importer.jobs.takst.factories.ATCKoderOgTekstFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.AdministrationsvejFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.BeregningsreglerFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.DivEnhederFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.DoseringFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.DoseringskodeFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.EmballagetypeKoderFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.EnhedspriserFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.FirmaFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.IndholdsstofferFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.IndikationFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.IndikationskodeFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.KlausuleringFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.LaegemiddelFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.LaegemiddelformBetegnelserFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.LaegemiddelnavnFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.MedicintilskudFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.OpbevaringsbetingelserFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.OplysningerOmDosisdispenseringFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.PakningFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.PakningskombinationerFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.PakningskombinationerUdenPriserFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.PriserFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.RekommandationerFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.SpecialeForNBSFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.SubstitutionAfLaegemidlerUdenFastPrisFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.SubstitutionFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.TilskudsintervallerFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.TilskudsprisgrupperPakningsniveauFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.UdgaaedeNavneFactory;
import com.trifork.stamdata.importer.jobs.takst.factories.UdleveringsbestemmelserFactory;
import com.trifork.stamdata.persistence.Dataset;
import com.trifork.stamdata.registre.takst.ATCKoderOgTekst;
import com.trifork.stamdata.registre.takst.Administrationsvej;
import com.trifork.stamdata.registre.takst.Beregningsregler;
import com.trifork.stamdata.registre.takst.DivEnheder;
import com.trifork.stamdata.registre.takst.Dosering;
import com.trifork.stamdata.registre.takst.Doseringskode;
import com.trifork.stamdata.registre.takst.EmballagetypeKoder;
import com.trifork.stamdata.registre.takst.Enhedspriser;
import com.trifork.stamdata.registre.takst.Firma;
import com.trifork.stamdata.registre.takst.Indholdsstoffer;
import com.trifork.stamdata.registre.takst.Indikation;
import com.trifork.stamdata.registre.takst.Indikationskode;
import com.trifork.stamdata.registre.takst.Klausulering;
import com.trifork.stamdata.registre.takst.Laegemiddel;
import com.trifork.stamdata.registre.takst.LaegemiddelAdministrationsvejRelation;
import com.trifork.stamdata.registre.takst.LaegemiddelformBetegnelser;
import com.trifork.stamdata.registre.takst.Laegemiddelnavn;
import com.trifork.stamdata.registre.takst.Medicintilskud;
import com.trifork.stamdata.registre.takst.Opbevaringsbetingelser;
import com.trifork.stamdata.registre.takst.OplysningerOmDosisdispensering;
import com.trifork.stamdata.registre.takst.Pakning;
import com.trifork.stamdata.registre.takst.Pakningskombinationer;
import com.trifork.stamdata.registre.takst.PakningskombinationerUdenPriser;
import com.trifork.stamdata.registre.takst.Pakningsstoerrelsesenhed;
import com.trifork.stamdata.registre.takst.Priser;
import com.trifork.stamdata.registre.takst.Rekommandationer;
import com.trifork.stamdata.registre.takst.SpecialeForNBS;
import com.trifork.stamdata.registre.takst.Styrkeenhed;
import com.trifork.stamdata.registre.takst.Substitution;
import com.trifork.stamdata.registre.takst.SubstitutionAfLaegemidlerUdenFastPris;
import com.trifork.stamdata.registre.takst.Takst;
import com.trifork.stamdata.registre.takst.TakstDataset;
import com.trifork.stamdata.registre.takst.Tidsenhed;
import com.trifork.stamdata.registre.takst.Tilskudsintervaller;
import com.trifork.stamdata.registre.takst.TilskudsprisgrupperPakningsniveau;
import com.trifork.stamdata.registre.takst.UdgaaedeNavne;
import com.trifork.stamdata.registre.takst.Udleveringsbestemmelser;


public class TakstParser
{
	private static final String FORMAT_VERSION = "12.0";
	private static final Logger LOGGER = getLogger(TakstParser.class);


	public Takst parseTakst(File rootDir) throws FileParseException, IOException
	{
		Takst takst;

		// Parse meta-data in the 'system.txt' file first.

		String systemline = getSystemLine(rootDir);
		String version = getVersion(systemline);

		if (!FORMAT_VERSION.equals(version))
		{
			LOGGER.warn("Trying to parse unsupported version= '{}' of the taksten!", FORMAT_VERSION);
		}

		Date effectuationDate = getEffectuationDate(systemline);

		takst = new Takst(effectuationDate, FOREVER);

		// TODO: Very recursive and coupled design.

		// Add the rate itself to the rate as a "meta entity" to represent
		// in DB that the takst was loaded.

		takst.setValidityWeekNumber(getValidWeek(systemline));
		takst.setValidityYear(getValidYear(systemline));

		List<Takst> takstMetaEntity = new ArrayList<Takst>();
		takstMetaEntity.add(takst);
		takst.addDataset(new TakstDataset<Takst>(takst, takstMetaEntity, Takst.class));

		// Now parse the required data files.

		// TODO (thb): These would be much nicer if we actually used the
		// structur the factories share and made a super-class that handled
		// what every single one of them does anyway.

		takst.addDataset(new TakstDataset<Laegemiddel>(takst, LaegemiddelFactory.read(rootDir),
				Laegemiddel.class));

		// HACK: I couldn't be bothered to change every factory method to take a
		// file,
		// so I send them a string instead. This should be changed when we fix
		// the todo
		// above.

		String rootString = rootDir.getAbsolutePath();

		takst.addDataset(new TakstDataset<Pakning>(takst, PakningFactory.read(rootString), Pakning.class));
		takst.addDataset(new TakstDataset<Priser>(takst, PriserFactory.read(rootString), Priser.class));
		takst.addDataset(new TakstDataset<Substitution>(takst, SubstitutionFactory.read(rootString),
					Substitution.class));
		takst.addDataset(new TakstDataset<SubstitutionAfLaegemidlerUdenFastPris>(takst,
					SubstitutionAfLaegemidlerUdenFastPrisFactory.read(rootString),
					SubstitutionAfLaegemidlerUdenFastPris.class));
		takst.addDataset(new TakstDataset<TilskudsprisgrupperPakningsniveau>(takst,
					TilskudsprisgrupperPakningsniveauFactory.read(rootString), TilskudsprisgrupperPakningsniveau.class));
		takst.addDataset(new TakstDataset<Firma>(takst, FirmaFactory.read(rootString), Firma.class));
		takst.addDataset(new TakstDataset<UdgaaedeNavne>(takst, UdgaaedeNavneFactory.read(rootString),
					UdgaaedeNavne.class));
		takst.addDataset(new TakstDataset<Administrationsvej>(takst, AdministrationsvejFactory.read(rootString),
					Administrationsvej.class));
		takst.addDataset(new TakstDataset<ATCKoderOgTekst>(takst, ATCKoderOgTekstFactory.read(rootString),
					ATCKoderOgTekst.class));
		takst.addDataset(new TakstDataset<Beregningsregler>(takst, BeregningsreglerFactory.read(rootString),
					Beregningsregler.class));
		takst.addDataset(new TakstDataset<EmballagetypeKoder>(takst, EmballagetypeKoderFactory.read(rootString),
					EmballagetypeKoder.class));
		takst.addDataset(new TakstDataset<DivEnheder>(takst, DivEnhederFactory.read(rootString), DivEnheder.class));
		takst.addDataset(new TakstDataset<Medicintilskud>(takst, MedicintilskudFactory.read(rootString),
					Medicintilskud.class));
		takst.addDataset(new TakstDataset<Klausulering>(takst, KlausuleringFactory.read(rootString),
					Klausulering.class));
		takst.addDataset(new TakstDataset<Udleveringsbestemmelser>(takst, UdleveringsbestemmelserFactory
					.read(rootString), Udleveringsbestemmelser.class));
		takst.addDataset(new TakstDataset<SpecialeForNBS>(takst, SpecialeForNBSFactory.read(rootString),
					SpecialeForNBS.class));
		takst.addDataset(new TakstDataset<Opbevaringsbetingelser>(takst, OpbevaringsbetingelserFactory
					.read(rootString), Opbevaringsbetingelser.class));
		takst.addDataset(new TakstDataset<Tilskudsintervaller>(takst, TilskudsintervallerFactory.read(rootString),
					Tilskudsintervaller.class));
		takst.addDataset(new TakstDataset<OplysningerOmDosisdispensering>(takst,
					OplysningerOmDosisdispenseringFactory.read(rootString), OplysningerOmDosisdispensering.class));
		takst.addDataset(new TakstDataset<Indikationskode>(takst, IndikationskodeFactory.read(rootString),
					Indikationskode.class));
		takst.addDataset(new TakstDataset<Indikation>(takst, IndikationFactory.read(rootString), Indikation.class));
		takst.addDataset(new TakstDataset<Doseringskode>(takst, DoseringskodeFactory.read(rootString),
					Doseringskode.class));
		takst.addDataset(new TakstDataset<Dosering>(takst, DoseringFactory.read(rootString), Dosering.class));

		// Now parse optional files one at a time, to be robust to them not
		// being present.

		try
		{
			takst.addDataset(new TakstDataset<Laegemiddelnavn>(takst, LaegemiddelnavnFactory.read(rootString),
					Laegemiddelnavn.class));
		}
		catch (IOException e)
		{
			LOGGER.warn(LaegemiddelFactory.getLmsName() + " could not be read. Ignoring as it is not required");
		}

		try
		{
			takst.addDataset(new TakstDataset<LaegemiddelformBetegnelser>(takst, LaegemiddelformBetegnelserFactory
					.read(rootString), LaegemiddelformBetegnelser.class));
		}
		catch (IOException e)
		{
			LOGGER.warn(LaegemiddelformBetegnelserFactory.getLmsName()
					+ " could not be read. Ignoring as it is not required");
		}

		try
		{
			takst.addDataset(new TakstDataset<Rekommandationer>(takst, RekommandationerFactory.read(rootString),
					Rekommandationer.class));
		}
		catch (IOException e)
		{
			LOGGER.debug(RekommandationerFactory.getLmsName() + " could not be read. Ignoring as it is not required");
		}

		try
		{
			takst.addDataset(new TakstDataset<Indholdsstoffer>(takst, IndholdsstofferFactory.read(rootString),
					Indholdsstoffer.class));
		}
		catch (IOException e)
		{
			LOGGER.debug(IndholdsstofferFactory.getLmsName() + " could not be read. Ignoring as it is not required");
		}

		try
		{
			takst.addDataset(new TakstDataset<Enhedspriser>(takst, EnhedspriserFactory.read(rootString),
					Enhedspriser.class));
		}
		catch (IOException e)
		{
			LOGGER.debug(EnhedspriserFactory.getLmsName() + " could not be read. Ignoring as it is not required");
		}

		try
		{
			takst.addDataset(new TakstDataset<Pakningskombinationer>(takst, PakningskombinationerFactory
					.read(rootString), Pakningskombinationer.class));
			takst.addDataset(new TakstDataset<PakningskombinationerUdenPriser>(takst,
					PakningskombinationerUdenPriserFactory.read(rootString), PakningskombinationerUdenPriser.class));
		}
		catch (IOException e)
		{
			LOGGER.debug(PakningskombinationerFactory.getLmsName() + " or "
					+ PakningskombinationerUdenPriserFactory.getLmsName()
					+ " could not be read. Ignoring as they are not required");
		}

		// Post process takst the data.

		try
		{
			addTypedDivEnheder(takst);
			addLaegemiddelAdministrationsvejRelations(takst);
			filterOutVetDrugs(takst);
		}
		catch (Exception e)
		{
			throw new FileParseException("An error occured while post-processing takst.", e);
		}

		return takst;
	}


	/**
	 * Extracts the administrationsveje and adds them to the takst.
	 * 
	 * @param takst
	 */
	private void addLaegemiddelAdministrationsvejRelations(Takst takst)
	{
		TakstDataset<Laegemiddel> lmr = takst.getDatasetOfType(Laegemiddel.class);
		List<LaegemiddelAdministrationsvejRelation> lars = new ArrayList<LaegemiddelAdministrationsvejRelation>();

		for (Laegemiddel drug : lmr.getEntities())
		{
			for (Administrationsvej vej : drug.getAdministrationsveje())
			{
				lars.add(new LaegemiddelAdministrationsvejRelation(drug, vej));
			}
		}

		takst.addDataset(new TakstDataset<LaegemiddelAdministrationsvejRelation>(takst, lars,
				LaegemiddelAdministrationsvejRelation.class));

	}


	private String getVersion(String systemline)
	{
		return systemline.substring(2, 7).trim();
	}


	/**
	 * Divides different types or units into sub-categories.
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
	 * Filter out veterinary drugs.
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
			pakninger.removeRecords(pakningerToBeRemoved);
		}

		Dataset<Laegemiddel> lmr = takst.getDatasetOfType(Laegemiddel.class);
		List<Laegemiddel> laegemidlerToBeRemoved = new ArrayList<Laegemiddel>();

		if (lmr != null)
		{
			for (Laegemiddel lm : lmr.getEntities())
			{
				if (!lm.isTilHumanAnvendelse()) laegemidlerToBeRemoved.add(lm);
			}
			lmr.removeRecords(laegemidlerToBeRemoved);
		}

		Dataset<ATCKoderOgTekst> atckoder = takst.getDatasetOfType(ATCKoderOgTekst.class);
		List<ATCKoderOgTekst> atcToBeRemoved = new ArrayList<ATCKoderOgTekst>();

		if (atckoder != null)
		{
			for (ATCKoderOgTekst atc : atckoder.getEntities())
			{
				if (!atc.isTilHumanAnvendelse()) atcToBeRemoved.add(atc);
			}
			atckoder.removeRecords(atcToBeRemoved);
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


	public Date getEffectuationDate(String line)
	{
		Date effectuationDate = null;

		try
		{
			String dateline = line.substring(47, 55);
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			effectuationDate = dateFormat.parse(dateline);
		}
		catch (ParseException e)
		{
			LOGGER.error("Could not get effectuation date from system line. line='{}'", line, e);
		}

		return effectuationDate;
	}


	private String getSystemLine(File rootDir) throws IOException
	{
		File systemFile = new File(rootDir, "system.txt");

		return FileUtils.readLines(systemFile).get(0);
	}
}
