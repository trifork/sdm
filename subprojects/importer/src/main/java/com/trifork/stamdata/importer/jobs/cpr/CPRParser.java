package com.trifork.stamdata.importer.jobs.cpr;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.trifork.stamdata.importer.jobs.FileParseException;
import com.trifork.stamdata.registre.cpr.BarnRelation;
import com.trifork.stamdata.registre.cpr.Foraeldremyndighedsrelation;
import com.trifork.stamdata.registre.cpr.Klarskriftadresse;
import com.trifork.stamdata.registre.cpr.Navnebeskyttelse;
import com.trifork.stamdata.registre.cpr.Navneoplysninger;
import com.trifork.stamdata.registre.cpr.Personoplysninger;
import com.trifork.stamdata.registre.cpr.UmyndiggoerelseVaergeRelation;


public class CPRParser
{
	public static final DateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyyMMddHHmm");
	public static final DateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	static final String EMPTY_DATE_STRING = "000000000000";


	public static CPRDataset parse(File f) throws FileParseException
	{
		CPRDataset cpr = new CPRDataset();

		BufferedReader reader = null;

		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "ISO-8859-1"));
			while (reader.ready())
			{
				String line = reader.readLine();
				if (line.length() > 0)
				{
					switch (getRecordType(line))
					{
					case 0:
						cpr.setValidFrom(getValidFrom(line));
						Date forrigeIKraftdato = getForrigeIkraftDato(line);
						if (forrigeIKraftdato != null) cpr.setPreviousFileValidFrom(forrigeIKraftdato);
						break;
					case 1:
						cpr.addEntity(personoplysninger(line));
						break;

					case 3:
						cpr.addEntity(klarskriftadresse(line));
						break;

					case 4:
						String beskyttelseskode = cut(line, 13, 17);
						if (beskyttelseskode.equals("0001"))
						{
							// We are only interested in name protection.
							cpr.addEntity(navneBeskyttelse(line));
						}
						break;

					case 8:
						cpr.addEntity(navneoplysninger(line));
						break;

					case 14:
						cpr.addEntity(barnRelation(line));
						break;

					case 16:
						cpr.addEntity(foraeldreMyndighedRelation(line));
						break;

					case 17:
						cpr.addEntity(umyndiggoerelseVaergeRelation(line));
						break;

					case 999:
						break;
					}

				}
			}
		}
		catch (IOException ioe)
		{
			throw new FileParseException("Der opstod en IO fejl under indlæsning af en CPR Person-fil.", ioe);
		}
		catch (ParseException pe)
		{
			throw new FileParseException("Der opstod en parsnings fejl under indlæsning af CPR Person fil.", pe);
		}

		finally
		{
			try
			{
				if (reader != null) reader.close();
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		return cpr;
	}


	public static UmyndiggoerelseVaergeRelation umyndiggoerelseVaergeRelation(String line) throws ParseException
	{

		UmyndiggoerelseVaergeRelation u = new UmyndiggoerelseVaergeRelation();
		u.setCpr(cut(line, 3, 13));
		u.setUmyndigStartDato(parseDate(yyyy_MM_dd, line, 13, 23));
		u.setUmyndigStartDatoMarkering(cut(line, 23, 24));
		u.setUmyndigSletteDato(parseDate(yyyy_MM_dd, line, 24, 34));
		u.setType(cut(line, 34, 38));
		u.setRelationCpr(cut(line, 38, 48));
		u.setRelationCprStartDato(parseDate(yyyy_MM_dd, line, 48, 58));
		u.setVaergesNavn(cut(line, 58, 92).trim());
		u.setVaergesNavnStartDato(parseDate(yyyy_MM_dd, line, 92, 102));
		u.setRelationsTekst1(cut(line, 102, 136).trim());
		u.setRelationsTekst2(cut(line, 136, 170).trim());
		u.setRelationsTekst3(cut(line, 170, 204).trim());
		u.setRelationsTekst4(cut(line, 204, 238).trim());
		u.setRelationsTekst5(cut(line, 238, 272).trim());
		return u;
	}


	public static Foraeldremyndighedsrelation foraeldreMyndighedRelation(String line) throws ParseException
	{

		Foraeldremyndighedsrelation f = new Foraeldremyndighedsrelation();

		f.setCpr(cut(line, 3, 13));
		f.setType(cut(line, 13, 17));
		f.setForaeldreMyndighedStartDato(parseDate(yyyy_MM_dd, line, 17, 27));
		f.setForaeldreMyndighedMarkering(cut(line, 27, 28));
		f.setForaeldreMyndighedSlettedato(parseDate(yyyy_MM_dd, line, 28, 38));
		f.setRelationCpr(cut(line, 38, 48));
		f.setRelationCprStartDato(parseDate(yyyy_MM_dd, line, 48, 58));

		return f;
	}


	public static BarnRelation barnRelation(String line)
	{

		BarnRelation b = new BarnRelation();
		b.setCpr(cut(line, 3, 13));
		b.setBarnCpr(cut(line, 13, 23));
		return b;
	}


	public static Navneoplysninger navneoplysninger(String line) throws ParseException
	{

		Navneoplysninger n = new Navneoplysninger();

		n.setCpr(cut(line, 3, 13));
		n.setFornavn(cut(line, 13, 63).trim());
		n.setFornavnMarkering(cut(line, 63, 64));
		n.setMellemnavn(cut(line, 64, 104).trim());
		n.setMellemnavnMarkering(cut(line, 104, 105));
		n.setEfternavn(cut(line, 105, 145).trim());
		n.setEfternavnMarkering(cut(line, 145, 146));
		n.setStartDato(parseDate(yyyyMMddHHmm, line, 146, 158));
		n.setStartDatoMarkering(cut(line, 158, 159));
		n.setAdresseringsNavn(cut(line, 159, 193).trim());

		return n;
	}


	public static Navnebeskyttelse navneBeskyttelse(String line) throws ParseException
	{

		Navnebeskyttelse n = new Navnebeskyttelse();

		n.setCpr(cut(line, 3, 13));
		n.setNavneBeskyttelseStartDato(parseDate(yyyy_MM_dd, line, 17, 27));
		n.setNavneBeskyttelseSletteDato(parseDate(yyyy_MM_dd, line, 27, 37));

		return n;
	}


	public static Klarskriftadresse klarskriftadresse(String line) throws FileParseException
	{

		Klarskriftadresse k = new Klarskriftadresse();

		k.setCpr(cut(line, 3, 13));
		k.setAdresseringsNavn(cut(line, 13, 47).trim());
		k.setCoNavn(cut(line, 47, 81).trim());
		k.setLokalitet(cut(line, 81, 115).trim());
		k.setStandardAdresse(cut(line, 115, 149).trim());
		k.setByNavn(cut(line, 149, 183).trim());
		k.setPostNummer(parseLong(line, 183, 187));
		k.setPostDistrikt(cut(line, 187, 207).trim());
		k.setKommuneKode(parseLong(line, 207, 211));
		k.setVejKode(parseLong(line, 211, 215));
		k.setHusNummer(removeLeadingZeros(cut(line, 215, 219).trim()));
		k.setEtage(removeLeadingZeros(cut(line, 219, 221).trim()));
		k.setSideDoerNummer(cut(line, 221, 225).trim());
		k.setBygningsNummer(cut(line, 225, 229).trim());
		k.setVejNavn(cut(line, 229, 249).trim());

		return k;
	}


	public static Personoplysninger personoplysninger(String line) throws ParseException
	{

		Personoplysninger p = new Personoplysninger();

		p.setCpr(cut(line, 3, 13));
		p.setGaeldendeCpr(cut(line, 13, 23).trim());
		p.setStatus(cut(line, 23, 25));
		p.setStatusDato(parseDate(yyyyMMddHHmm, line, 25, 37));
		p.setStatusMakering(cut(line, 37, 38));
		p.setKoen(cut(line, 38, 39));
		p.setFoedselsdato(parseDate(yyyy_MM_dd, line, 39, 49));
		p.setFoedselsdatoMarkering(cut(line, 49, 50));
		p.setStartDato(parseDate(yyyy_MM_dd, line, 50, 60));
		p.setStartDatoMarkering(cut(line, 60, 61));
		p.setSlutdato(parseDate(yyyy_MM_dd, line, 61, 71));
		p.setSlutDatoMarkering(cut(line, 71, 72));
		p.setStilling(cut(line, 72, 106).trim());

		return p;
	}


	private static int getRecordType(String line) throws FileParseException
	{

		return readInt(line, 0, 3);
	}


	private static String cut(String line, int beginIndex, int endIndex)
	{

		String res = "";

		if (line.length() > beginIndex)
		{
			int end = (line.length() < endIndex) ? line.length() : endIndex;
			res = line.substring(beginIndex, end);
		}

		return res;
	}


	private static int readInt(String line, int from, int to) throws FileParseException
	{

		try
		{
			return Integer.parseInt(cut(line, from, to));
		}
		catch (NumberFormatException nfe)
		{
			throw new FileParseException("Der opstod en fejl under parsning af heltal i linien: [" + line + "], på positionen from: " + from + ", to: " + to, nfe);
		}
		catch (StringIndexOutOfBoundsException se)
		{
			throw new FileParseException("Der opstod en fejl under parsning af heltal i linien: [" + line + "], på positionen from: " + from + ", to: " + to, se);
		}
	}


	private static Long parseLong(String line, int from, int to) throws FileParseException
	{

		try
		{
			return Long.parseLong(cut(line, from, to));
		}
		catch (NumberFormatException nfe)
		{
			throw new FileParseException("Der opstod en fejl under parsning af heltal i linien: [" + line + "], på positionen from: " + from + ", to: " + to, nfe);
		}
		catch (StringIndexOutOfBoundsException se)
		{
			throw new FileParseException("Der opstod en fejl under parsning af heltal i linien: [" + line + "], på positionen from: " + from + ", to: " + to, se);
		}
	}


	private static Date parseDate(DateFormat format, String line, int from, int to) throws ParseException
	{

		String dateString = cut(line, from, to);
		if (dateString != null && dateString.trim().length() == to - from && !dateString.equals(EMPTY_DATE_STRING))
		{
			return format.parse(dateString);
		}
		return null;
	}


	private static Date getValidFrom(String line) throws FileParseException
	{

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try
		{
			return sdf.parse(cut(line, 19, 27));
		}
		catch (ParseException pe)
		{
			throw new FileParseException("Der opstod en fejl und parsning af ikrafttrædelsesdato for cpr vejregister fil. " + pe.getMessage(), pe);
		}
	}


	private static Date getForrigeIkraftDato(String line) throws FileParseException
	{

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		if (line.length() >= 25)
		{
			try
			{
				return sdf.parse(cut(line, 27, 35));
			}
			catch (ParseException pe)
			{
				throw new FileParseException("Der opstod en fejl und parsning af FORRIGE ikrafttrædelsesdato for cpr vejregister fil. " + pe.getMessage(), pe);
			}
		}
		return null;
	}


	private static String removeLeadingZeros(String str)
	{

		if (str == null)
		{
			return null;
		}

		char[] chars = str.toCharArray();
		int index = 0;

		for (; index < str.length(); index++)
		{
			if (chars[index] != '0')
			{
				break;
			}
		}

		return (index == 0) ? str : str.substring(index);
	}
}
