package dk.trifork.sdm.importer.dosagesuggestions.models;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;


@Output
public class DosageStructure extends DosageRecord {

	// Reference til releaseNumber i Version. Obligatorisk. Heltal, 15 cifre.
	private long releaseNumber;

	// Unik kode for doseringstrukturen. Obligatorisk. Heltal, 11 cifre.
	private long code;

	// Typen af dosering, enten "M+M+A+N", "PN", "N daglig",
	// "Fritekst" eller "Kompleks". Obligatorisk. Streng, 100 tegn.
	private String type;

	// For simple typer (dvs. alt andet end "Kompleks")
	// indeholder feltet doseringen på simpel form. Optionelt. Streng, 100 tegn.
	private String simpleString;

	// For simple typer en eventuel supplerende tekst.
	// Optionelt. Streng, 200 tegn.
	private String supplementaryText;

	// FMKs strukturerede dosering i XML format. Bemærk at enkelte
	// værdier vil være escaped. Obligatorisk. Streng, 10000 tegn.
	private String xml;

	// Såfremt det er muligt at lave en kort
	// doseringstekst på baggrund af xml og lægemidlets doseringsenhed vil
	// dette felt indeholde denne. Optionelt. Streng, 70 tegn.
	private String shortTranslation;

	// En lang doseringstekst baggrund af xml og
	// lægemidlets doseringsenhed. Obligatorisk. Strengm 10000 tegn.
	private String longTranslation;

	@Output
	public long getReleaseNumber() {

		return releaseNumber;
	}

	@Id
	@Output
	public long getCode() {

		return code;
	}

	@Output
	public String getType() {

		return type;
	}

	@Output
	public String getSimpleString() {

		return simpleString;
	}

	@Output
	public String getSupplementaryText() {

		return supplementaryText;
	}

	@Output
	public String getXml() {

		return xml;
	}

	@Output
	public String getShortTranslation() {

		return shortTranslation;
	}

	@Output
	public String getLongTranslation() {

		return longTranslation;
	}
}
