package dk.trifork.sdm.importer.cpr;

import static dk.trifork.sdm.util.DateUtils.yyyyMMddHHmm;
import static dk.trifork.sdm.util.DateUtils.yyyy_MM_dd;
import dk.trifork.sdm.importer.cpr.model.*;
import dk.trifork.sdm.importer.exceptions.FileParseException;
import dk.trifork.sdm.util.DateUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CPRParser {

	static final String EMPTY_DATE_STRING = "000000000000";


	public static CPRDataset parse(File f) throws FileParseException {
		CPRDataset cpr = new CPRDataset();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "ISO-8859-1"));
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.length() > 0) {
					switch (getRecordType(line)) {
					case 0:
						cpr.setValidFrom(getValidFrom(line));
						Calendar forrigeIKraftdato = getForrigeIkraftDato(line);
						if (forrigeIKraftdato != null) 
							cpr.setPreviousFileValidFrom(forrigeIKraftdato);
						break;
					case 1:
						cpr.addEntity(personoplysninger(line));
						break;
					case 3:
						cpr.addEntity(klarskriftadresse(line));
						break;
					case 4:
						String beskyttelseskode = cut(line,13, 17);
						if (beskyttelseskode.equals("0001")) {
							// Vi er kun interesseret i navnebeskyttelse
							cpr.addEntity(navneBeskyttelse(line));
						}
						break;
					case 5:
						cpr.addEntity(udrejseoplysninger(line));
						break;
					case 8:
						cpr.addEntity(navneoplysninger(line));
						break;
					case 9:
						cpr.addEntity(foedselsregistreringsoplysninger(line));
						break;
					case 10:
						cpr.addEntity(statsborgerskab(line));
						break;
					case 11:
						cpr.addEntity(folkekirkeoplysninger(line));
						break;
					case 12:
						cpr.addEntity(aktuelCivilstand(line));
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
					case 18:
						cpr.addEntity(kommunaleForhold(line));
						break;
					case 20:
						cpr.addEntity(valgoplysninger(line));
						break;
					case 999:
						break;
					}

				}
			}
		} catch (IOException ioe) {
			throw new FileParseException("Der opstod en IO fejl under læsning af CPR Person fil.", ioe);
		} catch (ParseException pe) {
			throw new FileParseException("Der opstod en parsnings fejl under læsning af CPR Person fil.", pe);
		}

		finally {
			try {
				if (reader != null)
                    reader.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return cpr;
	}

	static UmyndiggoerelseVaergeRelation umyndiggoerelseVaergeRelation(
			String line) throws ParseException {
		UmyndiggoerelseVaergeRelation u = new UmyndiggoerelseVaergeRelation();
		u.setCpr(cut(line,3, 13));
		u.setUmyndigStartDato(parseDate(yyyy_MM_dd, line, 13, 23));
		u.setUmyndigStartDatoMarkering(cut(line,23,24));
		u.setUmyndigSletteDato(parseDate(yyyy_MM_dd, line, 24, 34));
		u.setType(cut(line,34,38));
		u.setRelationCpr(cut(line,38,48));
		u.setRelationCprStartDato(parseDate(yyyy_MM_dd, line, 48, 58));
		u.setVaergesNavn(cut(line,58,92).trim());
		u.setVaergesNavnStartDato(parseDate(yyyy_MM_dd, line, 92,102));
		u.setRelationsTekst1(cut(line,102,136).trim());
		u.setRelationsTekst2(cut(line,136,170).trim());
		u.setRelationsTekst3(cut(line,170,204).trim());
		u.setRelationsTekst4(cut(line,204,238).trim());
		u.setRelationsTekst5(cut(line,238,272).trim());
		return u;
	}

	static ForaeldreMyndighedRelation foraeldreMyndighedRelation(String line) throws ParseException {
		ForaeldreMyndighedRelation f = new ForaeldreMyndighedRelation();
		f.setCpr(cut(line,3, 13));
		f.setType(cut(line,13,17));
		f.setForaeldreMyndighedStartDato(parseDate(yyyy_MM_dd, line, 17, 27));
		f.setForaeldreMyndighedMarkering(cut(line,27,28));
		f.setForaeldreMyndighedSlettedato(parseDate(yyyy_MM_dd, line, 28, 38));
		f.setRelationCpr(cut(line,38,48));
		f.setRelationCprStartDato(parseDate(yyyy_MM_dd, line, 48, 58));
		return f;
	}

	static BarnRelation barnRelation(String line) {
		BarnRelation b = new BarnRelation();
		b.setCpr(cut(line,3, 13));
		b.setBarnCpr(cut(line,13, 23));
		return b;
	}

	static Navneoplysninger navneoplysninger(String line) throws ParseException {
		Navneoplysninger n = new Navneoplysninger();
		n.setCpr(cut(line,3, 13));
		n.setFornavn(cut(line,13,63).trim());
		n.setFornavnMarkering(cut(line,63,64));
		n.setMellemnavn(cut(line,64,104).trim());
		n.setMellemnavnMarkering(cut(line,104,105));
		n.setEfternavn(cut(line,105,145).trim());
		n.setEfternavnMarkering(cut(line,145,146));
		n.setStartDato(parseDate(yyyyMMddHHmm, line, 146, 158));
		n.setStartDatoMarkering(cut(line,158,159));
		n.setAdresseringsNavn(cut(line,159,193).trim());
		return n;
	}

	static NavneBeskyttelse navneBeskyttelse(String line) throws ParseException {
		NavneBeskyttelse n = new NavneBeskyttelse();
		n.setCpr(cut(line,3, 13));
		n.setNavneBeskyttelseStartDato(parseDate(yyyy_MM_dd, line, 17, 27));
		n.setNavneBeskyttelseSletteDato(parseDate(yyyy_MM_dd, line, 27, 37));
		return n;
	}

	static Klarskriftadresse klarskriftadresse(String line) throws FileParseException {
		Klarskriftadresse k = new Klarskriftadresse();
		k.setCpr(cut(line,3, 13));
		k.setAdresseringsNavn(cut(line,13,47).trim());
		k.setCoNavn(cut(line,47,81).trim());
		k.setLokalitet(cut(line,81,115).trim());
		k.setStandardAdresse(cut(line,115,149).trim());
		k.setByNavn(cut(line,149,183).trim());
		k.setPostNummer(parseLong(line, 183, 187));
		k.setPostDistrikt(cut(line,187,207).trim());
		k.setKommuneKode(parseLong(line,207,211));
		k.setVejKode(parseLong(line,211,215));
		k.setHusNummer(removeLeadingZeros(cut(line,215,219).trim()));
		k.setEtage(removeLeadingZeros(cut(line,219,221).trim()));
		k.setSideDoerNummer(cut(line,221,225).trim());
		k.setBygningsNummer(cut(line,225,229).trim());
		k.setVejNavn(cut(line,229,249).trim());
		return k;
	}

	static Personoplysninger personoplysninger(String line) throws ParseException {
		Personoplysninger p = new Personoplysninger();
		p.setCpr(cut(line,3, 13));
		p.setGaeldendeCpr(cut(line,13,23).trim());
		p.setStatus(cut(line,23,25));
		p.setStatusDato(parseDate(yyyyMMddHHmm, line, 25, 37));
		p.setStatusMakering(cut(line,37,38));
		p.setKoen(cut(line,38,39));
		p.setFoedselsdato(parseDate(yyyy_MM_dd, line, 39, 49));
		p.setFoedselsdatoMarkering(cut(line,49,50));
		p.setStartDato(parseDate(yyyy_MM_dd, line, 50, 60));
		p.setStartDatoMarkering(cut(line,60,61));
		p.setSlutdato(parseDate(yyyy_MM_dd, line, 61, 71));
		p.setSlutDatoMarkering(cut(line,71,72));
		p.setStilling(cut(line,72,106).trim());
		return p;
	}
	
	public static Folkekirkeoplysninger folkekirkeoplysninger(String line) throws ParseException {
		Folkekirkeoplysninger result = new Folkekirkeoplysninger();
		result.setCpr(cut(line,3, 13));
		result.setForholdskode(cut(line, 13, 14));
		result.setStartdato(parseDate(yyyy_MM_dd, line, 14, 24));
		result.setStartdatomarkering(cut(line, 24, 25));
		return result;
	}

	public static AktuelCivilstand aktuelCivilstand(String line) throws ParseException {
		AktuelCivilstand result = new AktuelCivilstand();
		result.setCpr(cut(line,3, 13));
		result.setCivilstandskode(cut(line, 13, 14));
		result.setAegtefaellepersonnummer(cut(line, 14, 24).trim());
		result.setAegtefaellefoedselsdato(parseDate(yyyy_MM_dd, line, 24, 34));
		result.setAegtefaellefoedselsdatomarkering(cut(line, 34, 35));
		result.setAegtefaellenavn(cut(line, 35, 69).trim());
		result.setAegtefaellenavnmarkering(cut(line, 69, 70));
		result.setStartdato(parseDate(yyyyMMddHHmm, line, 70, 82));
		result.setStartdatomarkering(cut(line, 82, 83));
		result.setSeparation(parseDate(yyyyMMddHHmm, line, 83, 95));
		return result;
	}
	
	public static Udrejseoplysninger udrejseoplysninger(String line) throws ParseException {
		Udrejseoplysninger u = new Udrejseoplysninger();
		u.setCpr(cut(line, 3, 13));
		u.setUdrejseLandekode(cut(line, 13, 17));
		u.setUdrejsedato(parseDate(yyyyMMddHHmm, line, 17, 29));
		u.setUdrejsedatoUsikkerhedsmarkering(cut(line, 29, 30));
		u.setUdlandsadresse1(cut(line, 30, 64).trim());
		u.setUdlandsadresse2(cut(line, 64, 98).trim());
		u.setUdlandsadresse3(cut(line, 98, 132).trim());
		u.setUdlandsadresse4(cut(line, 132, 166).trim());
		u.setUdlandsadresse5(cut(line, 166, 200).trim());
		return u;
	}
	
	public static Foedselsregistreringsoplysninger foedselsregistreringsoplysninger(String line) throws ParseException {
		Foedselsregistreringsoplysninger r = new Foedselsregistreringsoplysninger();
		r.setCpr(cut(line, 3, 13));
		r.setFoedselsregistreringsstedkode(cut(line, 13, 17));
		r.setFoedselsregistreringstekst(cut(line, 17, 37));
		return r;
	}
	
	public static Statsborgerskab statsborgerskab(String line) throws ParseException {
		Statsborgerskab s = new Statsborgerskab();
		s.setCpr(cut(line, 3, 13));
		s.setLandekode(cut(line, 13, 17));
		s.setStatsborgerskabstartdato(parseDate(yyyyMMddHHmm, line, 17, 29));
		s.setStatsborgerskabstartdatousikkerhedsmarkering(cut(line, 29, 30));
		return s;
	}

	public static KommunaleForhold kommunaleForhold(String line) throws ParseException {
		KommunaleForhold result = new KommunaleForhold();
		result.setCpr(cut(line, 3, 13));
		result.setKommunalforholdstypekode(cut(line, 13, 14));
		result.setKommunalforholdskode(cut(line, 14, 19).trim());
		result.setStartdato(parseDate(yyyy_MM_dd, line, 19, 29));
		result.setStartdatomarkering(cut(line, 29, 30));
		result.setBemaerkninger(line.substring(30).trim());
		return result;
	}

	public static Valgoplysninger valgoplysninger(String line) throws ParseException {
		Valgoplysninger result = new Valgoplysninger();
		result.setCpr(cut(line, 3, 13));
		result.setValgkode(removeLeadingZeros(cut(line, 13, 17)));
		result.setValgretsdato(parseDate(yyyy_MM_dd, line, 17, 27));
		result.setStartdato(parseDate(yyyy_MM_dd, line, 27, 37));
		result.setSlettedato(parseDate(yyyy_MM_dd, line, 37, 47));
		return result;
	}

	private static int getRecordType(String line) throws FileParseException {
		return readInt(line, 0, 3);
	}

	private static String cut(String line, int beginIndex, int endIndex) {
		String res = "";
		if (line.length() > beginIndex) {
			int end = (line.length() < endIndex) ? line.length() : endIndex;
			res = line.substring(beginIndex, end);
		}
		return res;
	}
	private static int readInt(String line, int from, int to) throws FileParseException {
		try {
			return Integer.parseInt(cut(line,from, to));
		} catch (NumberFormatException nfe) {
			throw new FileParseException("Der opstod en fejl under parsning af heltal i linien: [" + line
					+ "], på positionen from: " + from + ", to: " + to, nfe);
		} catch (StringIndexOutOfBoundsException se) {
			throw new FileParseException("Der opstod en fejl under parsning af heltal i linien: [" + line
					+ "], på positionen from: " + from + ", to: " + to, se);
		}
	}

	private static Long parseLong(String line, int from, int to) throws FileParseException {
		try {
			return Long.parseLong(cut(line,from, to));
		} catch (NumberFormatException nfe) {
			throw new FileParseException("Der opstod en fejl under parsning af heltal i linien: [" + line
					+ "], på positionen from: " + from + ", to: " + to, nfe);
		} catch (StringIndexOutOfBoundsException se) {
			throw new FileParseException("Der opstod en fejl under parsning af heltal i linien: [" + line
					+ "], på positionen from: " + from + ", to: " + to, se);
		}
	}

	private static Date parseDate(DateFormat format, String line, int from, int to) throws ParseException {
		String dateString = cut(line,from, to);
		if (dateString != null && dateString.trim().length() == to - from && !dateString.equals(EMPTY_DATE_STRING)) {
			return format.parse(dateString);
		}
		return null;
	}

	private static Calendar getValidFrom(String line) throws FileParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			return DateUtils.toCalendar(sdf.parse(cut(line,19, 27)));
		} catch (ParseException pe) {
			throw new FileParseException("Der opstod en fejl und parsning af ikrafttrædelsesdato for cpr vejregister fil. "
					+ pe.getMessage(), pe);
		}
	}

	private static Calendar getForrigeIkraftDato(String line) throws FileParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		if (line.length() >= 25) {
			try {
				return DateUtils.toCalendar(sdf.parse(cut(line,27, 35)));
			} catch (ParseException pe) {
				throw new FileParseException("Der opstod en fejl und parsning af FORRIGE ikrafttrædelsesdato for cpr vejregister fil. "
						+ pe.getMessage(), pe);
			}
		}
		return null;
	}
	
	private static String removeLeadingZeros(String str) {
	    if (str == null) {
	        return null;
	    }
	    for (int index = 0; index < str.length(); index++) {
	        if (str.charAt(index) != '0') {
	        	return str.substring(index);
	        }
	    }
	    return "";
	}
}
