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

package com.trifork.stamdata.importer.jobs.dkma;

import java.io.*;
import java.text.*;
import java.util.*;

import org.joda.time.Period;
import org.slf4j.*;

import com.google.common.collect.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.trifork.stamdata.importer.jobs.FileParserJob;
import com.trifork.stamdata.importer.jobs.dkma.model.*;
import com.trifork.stamdata.importer.persistence.*;
import com.trifork.stamdata.importer.util.DateUtils;


/**
 * Parser for the DKMA register. Also known as 'Taksten'.
 * 
 * DKMA is an acroynm for 'Danish Medicines Agency'.
 */
public class DKMAParser implements FileParserJob
{
	private static final Logger logger = LoggerFactory.getLogger(DKMAParser.class);

	private static final String SUPPORTED_FILE_FORMAT_VERSION = "12.0";
	private static final String JOB_IDENTIFIER = "dkma_parser";

	private final Period maxTimeGap;

	@Inject
	DKMAParser(@Named(JOB_IDENTIFIER + "." + MAX_TIME_GAP) String maxTimeGap)
	{
		this.maxTimeGap = Period.minutes(Integer.parseInt(maxTimeGap));
	}

	@Override
	public String getIdentifier()
	{
		return "dkma";
	}

	@Override
	public String getHumanName()
	{
		return "DKMA";
	}

	@Override
	public Period getMaxTimeGap()
	{
		return maxTimeGap;
	}

	@Override
	public boolean checkFileSet(File[] input)
	{
		final String[] requiredFileNames = new String[] { "system.txt", "lms01.txt", "lms02.txt", "lms03.txt", "lms04.txt", "lms05.txt", "lms07.txt", "lms09.txt", "lms10.txt", "lms11.txt", "lms12.txt", "lms13.txt", "lms14.txt", "lms15.txt", "lms16.txt", "lms17.txt", "lms18.txt", "lms19.txt", "lms20.txt", "lms23.txt", "lms24.txt", "lms25.txt", "lms26.txt", "lms27.txt", "lms28.txt" };

		Map<String, File> fileMap = Maps.newHashMap();

		for (File f : input)
		{
			fileMap.put(f.getName(), f);
		}

		for (String reqFile : requiredFileNames)
		{
			if (!fileMap.containsKey(reqFile))
			{
				return false;
			}
		}

		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run(File[] input, Persister persister) throws Exception
	{
		Takst takst = parseFiles(input);

		persister.persistCompleteDataset(takst.getDatasets().toArray(new CompleteDataset[] {}));
	}

	// TODO: Update only version number on standard dkma imports, not for delta
	// updates.

	private <T extends TakstEntity> void add(Takst takst, FixedLengthFileParser parser, FixedLengthParserConfiguration<T> config, Class<T> type) throws Exception
	{
		List<T> entities = parser.parse(config, type);
		takst.addDataset(new TakstDataset<T>(takst, entities, type));
	}

	private <T extends TakstEntity> void addOptional(File[] input, Takst takst, FixedLengthFileParser parser, FixedLengthParserConfiguration<T> config, Class<T> type) throws Exception
	{
		File file = getFileByName(config.getFilename(), input);

		if (file != null && file.isFile())
		{
			List<T> entities = parser.parse(config, type);
			takst.addDataset(new TakstDataset<T>(takst, entities, type));
		}
	}

	public Takst parseFiles(File[] input) throws Exception
	{
		// Parse required meta information first.

		String systemline = getSystemLine(input);
		String version = getVersion(systemline);

		if (!SUPPORTED_FILE_FORMAT_VERSION.equals(version))
		{
			logger.warn("Parsing unsupported of the takst! supported={}, actual={}", version, SUPPORTED_FILE_FORMAT_VERSION);
		}

		Date fromDate = getValidFromDate(systemline);

		Takst takst = new Takst(fromDate, DateUtils.THE_END_OF_TIME);

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

		addOptional(input, takst, parser, new LaegemiddelnavnFactory(), Laegemiddelnavn.class);
		addOptional(input, takst, parser, new LaegemiddelformBetegnelserFactory(), LaegemiddelformBetegnelser.class);
		addOptional(input, takst, parser, new RekommandationerFactory(), Rekommandationer.class);
		addOptional(input, takst, parser, new IndholdsstofferFactory(), Indholdsstoffer.class);
		addOptional(input, takst, parser, new EnhedspriserFactory(), Enhedspriser.class);
		addOptional(input, takst, parser, new PakningskombinationerFactory(), Pakningskombinationer.class);
		addOptional(input, takst, parser, new PakningskombinationerUdenPriserFactory(), PakningskombinationerUdenPriser.class);

		// Post process the data.

		addTypedDivEnheder(takst);
		addLaegemiddelAdministrationsvejRefs(takst);
		filterOutVetDrugs(takst);

		return takst;
	}

	private String getVersion(String systemline)
	{
		return systemline.substring(2, 7).trim();
	}

	/**
	 * Extracts the routes of administration and adds them to the takst.
	 */
	private void addLaegemiddelAdministrationsvejRefs(Takst takst)
	{
		TakstDataset<Laegemiddel> lmr = takst.getDatasetOfType(Laegemiddel.class);
		List<LaegemiddelAdministrationsvejRef> lars = new ArrayList<LaegemiddelAdministrationsvejRef>();

		for (Laegemiddel lm : lmr.getEntities())
		{
			for (Administrationsvej av : getAdministrationsveje(lm, takst))
			{
				lars.add(new LaegemiddelAdministrationsvejRef(lm, av));
			}
		}

		takst.addDataset(new TakstDataset<LaegemiddelAdministrationsvejRef>(takst, lars, LaegemiddelAdministrationsvejRef.class));
	}

	private List<Administrationsvej> getAdministrationsveje(Laegemiddel drug, Takst takst)
	{
		List<Administrationsvej> adminveje = Lists.newArrayList();

		for (int idx = 0; idx < drug.getAdministrationsvejKode().length(); idx += 2)
		{
			String avKode = drug.getAdministrationsvejKode().substring(idx, idx + 2);
			Administrationsvej adminVej = takst.getEntity(Administrationsvej.class, avKode);

			if (adminVej == null)
			{
				logger.warn("Administaritonvej not found for kode: '" + avKode + "'");
			}
			else
			{
				adminveje.add(adminVej);
			}
		}

		return adminveje;
	}

	/**
	 * Sorterer DivEnheder ud på stærke(re) typede entiteter for at matche fmk
	 * stamtabel skemaet.
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
	 * Filtes out veterinary medicin from a dataset.
	 * 
	 * @param takst
	 */
	public static void filterOutVetDrugs(Takst takst)
	{
		Dataset<Pakning> pakninger = takst.getDatasetOfType(Pakning.class);

		if (pakninger != null)
		{
			List<Pakning> pakningerToBeRemoved = Lists.newArrayList();

			for (Pakning pakning : pakninger.getEntities())
			{
				if (!pakning.isTilHumanAnvendelse())
				{
					pakningerToBeRemoved.add(pakning);
				}
			}

			pakninger.removeEntities(pakningerToBeRemoved);
		}

		Dataset<Laegemiddel> lmr = takst.getDatasetOfType(Laegemiddel.class);

		if (lmr != null)
		{
			List<Laegemiddel> laegemidlerToBeRemoved = Lists.newArrayList();

			for (Laegemiddel lm : lmr.getEntities())
			{
				if (!lm.isTilHumanAnvendelse())
				{
					laegemidlerToBeRemoved.add(lm);
				}
			}

			lmr.removeEntities(laegemidlerToBeRemoved);
		}

		Dataset<ATCKoderOgTekst> atckoder = takst.getDatasetOfType(ATCKoderOgTekst.class);

		if (atckoder != null)
		{
			List<ATCKoderOgTekst> atcToBeRemoved = Lists.newArrayList();

			for (ATCKoderOgTekst atc : atckoder.getEntities())
			{
				if (!atc.isTilHumanAnvendelse())
				{
					atcToBeRemoved.add(atc);
				}
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

	public Date getValidFromDate(String line) throws ParseException
	{
		String dateline = line.substring(47, 55);
		DateFormat df = new SimpleDateFormat("yyyyMMdd");

		return df.parse(dateline);

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
