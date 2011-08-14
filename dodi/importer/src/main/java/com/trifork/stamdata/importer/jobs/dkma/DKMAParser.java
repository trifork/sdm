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

import static com.google.common.base.Preconditions.*;

import java.io.*;
import java.sql.Connection;
import java.text.ParseException;
import java.util.*;

import org.joda.time.Period;
import org.slf4j.*;

import com.google.common.collect.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.trifork.stamdata.importer.jobs.FileParserJob;
import com.trifork.stamdata.importer.jobs.dkma.model.*;
import com.trifork.stamdata.importer.persistence.*;
import com.trifork.stamdata.importer.util.Dates;


/**
 * Parser for the DKMA register. Also known as 'Taksten'.
 * 
 * DKMA is an acronym for 'Danish Medicines Agency'.
 * 
 * @author Rune Skov Larsen <rsl@trifork.com>
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
		return JOB_IDENTIFIER;
	}

	@Override
	public String getHumanName()
	{
		return "DKMA Parser (Taksten)";
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
	public void run(File[] input, Persister persister, Connection connection, long changeset) throws Exception
	{
		TakstVersion takst = parseFiles(input);

		persister.persistCompleteDataset(takst.getDatasets().toArray(new CompleteDataset[] {}));
	}

	// TODO: Update only version number on standard dkma imports, not for delta
	// updates.

	private <T extends TakstEntity> void add(TakstVersion takst, FixedLengthFileParser parser, FixedLengthParserConfiguration<T> config, Class<T> type) throws Exception
	{
		List<T> entities = parser.parse(config, type);
		takst.addDataset(new TakstDataset<T>(takst, entities, type));
	}

	private <T extends TakstEntity> void addOptional(File[] input, TakstVersion takst, FixedLengthFileParser parser, FixedLengthParserConfiguration<T> config, Class<T> type) throws Exception
	{
		File file = getFileByName(config.getFilename(), input);

		if (file != null && file.isFile())
		{
			List<T> entities = parser.parse(config, type);
			takst.addDataset(new TakstDataset<T>(takst, entities, type));
		}
	}

	public TakstVersion parseFiles(File[] input) throws Exception
	{
		checkNotNull(input, "input");
		
		// Parse required meta information first.

		String systemline = getSystemLine(input);
		String version = getVersion(systemline);

		if (!SUPPORTED_FILE_FORMAT_VERSION.equals(version))
		{
			logger.warn("Parsing unsupported of the takst! supported={}, actual={}", version, SUPPORTED_FILE_FORMAT_VERSION);
		}

		Date fromDate = getValidFromDate(systemline);

		TakstVersion takst = new TakstVersion(fromDate, Dates.THE_END_OF_TIME);

		// Add the takst itself to the takst as a "meta entity" to represent
		// in DB that the takst was loaded.

		takst.setValidityWeekNumber(getValidWeek(systemline));
		takst.setValidityYear(getValidYear(systemline));

		List<TakstVersion> takstMetaEntity = new ArrayList<TakstVersion>();
		takstMetaEntity.add(takst);
		takst.addDataset(new TakstDataset<TakstVersion>(takst, takstMetaEntity, TakstVersion.class));

		// Now parse the required data files.

		FixedLengthFileParser parser = new FixedLengthFileParser(input);

		add(takst, parser, new LaegemiddelFactory(), Laegemiddel.class);
		add(takst, parser, new PakningFactory(), Pakning.class);
		add(takst, parser, new PriserFactory(), Priser.class);
		add(takst, parser, new SubstitutionFactory(), Substitution.class);
		add(takst, parser, new SubstitutionAfLaegemidlerUdenFastPrisFactory(), SubstitutionAfLegemiddelUdenFastPris.class);
		add(takst, parser, new TilskudsprisgrupperPakningsniveauFactory(), TilskudsprisgruppePaaPakningsniveau.class);
		add(takst, parser, new FirmaFactory(), Firma.class);
		add(takst, parser, new UdgaaedeNavneFactory(), UdgaaetNavn.class);
		add(takst, parser, new AdministrationsvejFactory(), Administrationsvej.class);
		add(takst, parser, new LMS12Parser(), ATC.class);
		add(takst, parser, new BeregningsreglerFactory(), Beregningsregler.class);
		add(takst, parser, new EmballagetypeKoderFactory(), EmballagetypeKoder.class);
		add(takst, parser, new DivEnhederFactory(), Enhed.class);
		add(takst, parser, new MedicintilskudFactory(), Medicintilskud.class);
		add(takst, parser, new KlausuleringFactory(), Klausulering.class);
		add(takst, parser, new UdleveringsbestemmelserFactory(), Udleveringsbestemmelser.class);
		add(takst, parser, new SpecialeForNBSFactory(), SpecialeForNBS.class);
		add(takst, parser, new OpbevaringsbetingelserFactory(), Opbevaringsbetingelse.class);
		add(takst, parser, new TilskudsintervallerFactory(), Tilskudsinterval.class);
		add(takst, parser, new OplysningerOmDosisdispenseringFactory(), Dosisdispensering.class);
		add(takst, parser, new IndikationskodeFactory(), Indikationskode.class);
		add(takst, parser, new IndikationFactory(), Indikation.class);
		add(takst, parser, new DoseringskodeFactory(), Doseringskode.class);
		add(takst, parser, new DoseringFactory(), Dosering.class);

		// Now parse optional files one at a time, to be robust to them not
		// being present.

		addOptional(input, takst, parser, new LaegemiddelnavnFactory(), Laegemiddelnavn.class);
		addOptional(input, takst, parser, new LaegemiddelformBetegnelserFactory(), LaegemiddelformBetegnelser.class);
		addOptional(input, takst, parser, new RekommandationerFactory(), Rekommandation.class);
		addOptional(input, takst, parser, new IndholdsstofferFactory(), Indholdsstof.class);
		addOptional(input, takst, parser, new EnhedspriserFactory(), Enhedspriser.class);
		addOptional(input, takst, parser, new PakningskombinationerFactory(), Pakningskombination.class);
		addOptional(input, takst, parser, new PakningskombinationerUdenPriserFactory(), PakningskombinationUdenPris.class);

		// Post process the data.
		
		filterOutVetDrugs(takst);

		return takst;
	}

	private String getVersion(String systemline)
	{
		return systemline.substring(2, 7).trim();
	}

	/**
	 * Filtes out veterinary medicin from a dataset.
	 * 
	 * @param takst
	 */
	public static void filterOutVetDrugs(TakstVersion takst)
	{
		// TODO: Do this while parsing instead.
		
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

		Dataset<ATC> atckoder = takst.getDatasetOfType(ATC.class);

		if (atckoder != null)
		{
			List<ATC> atcToBeRemoved = Lists.newArrayList();

			for (ATC atc : atckoder.getEntities())
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
		String dateSegment = line.substring(47, 55);
		return Dates.DK_yyyyMMdd.parseDateTime(dateSegment).toDate();
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
