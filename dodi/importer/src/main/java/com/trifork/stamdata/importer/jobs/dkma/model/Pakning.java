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

package com.trifork.stamdata.importer.jobs.dkma.model;

import java.util.*;

import com.trifork.stamdata.importer.jobs.dkma.TakstEntity;
import com.trifork.stamdata.importer.persistence.*;


@Output
public class Pakning extends TakstEntity
{
	private Long drugid; // Ref. t. LMS01, felt 01
	private Long varenummer;
	private Long alfabetSekvensnr;
	private Long varenummerForDelpakning; // Udfyldes for multipakningen
	private Long antalDelpakninger; // Antal delpakn. i stor/multipakning
	private String pakningsstoerrelseKlartekst;
	private Long pakningsstoerrelseNumerisk; // Brutto
	private String pakningsstoerrelseEnhed; // Ref. t. LMS15, enhedstype 4
	private String emballagetype; // Ref. t. LMS14
	private String udleveringsbestemmelse; // Ref. t. LMS18
	private String udleveringSpeciale; // Ref. t. LMS19
	private String medicintilskudskode; // Ref. t. LMS16
	private String klausulForMedicintilskud; // Ref. t. LMS17
	private Long antalDDDPrPakning;
	
	private Long opbevaringstidNumerisk; // Hos distributør
	private String opbevaringstidEnhed;
	private String opbevaringsbetingelser; // Ref. t. LMS20
	
	private Date oprettelsesdato; // Format: ååååmmdd
	private Date datoForSenestePrisaendring; // Format: ååååmmdd
	private Date udgaaetDato; // Format: ååååmmdd
	private String beregningskodeAIPRegpris; // Ref. t. LMS13
	private String pakningOptagetITilskudsgruppe; // 2 muligh.: F eller blank
	private String faerdigfremstillingsgebyr; // 2 muligh.: B eller blank
	private Long pakningsdistributoer; // Ref. t. LMS09

	@Output
	public Long getAlfabetSekvensnr()
	{
		return alfabetSekvensnr;
	}

	@Output
	public Long getAntalDDDPrPakning()
	{
		return antalDDDPrPakning;
	}

	@Output
	public Long getAntalDelpakninger()
	{
		return antalDelpakninger;
	}

	@Output
	public String getBeregningskodeFraAIPTilRegPris()
	{
		return beregningskodeAIPRegpris;
	}

	@Output
	public Date getDatoForSenestePrisaendring()
	{
		return datoForSenestePrisaendring;
	}

	@Output(name = "DrugID")
	public Long getDrugID()
	{
		return drugid;
	}

	@Output(name = "EmballageTypeKode")
	public String getEmballagetype()
	{
		return emballagetype;
	}

	@Output
	public String getFaerdigfremstillingsgebyr()
	{
		return faerdigfremstillingsgebyr;
	}

	@Output(name = "KlausuleringsKode")
	public String getKlausulForMedicintilskud()
	{
		return klausulForMedicintilskud;
	}

	@Output(name = "MedicintilskudsKode")
	public String getMedicintilskudskode()
	{
		return medicintilskudskode;
	}

	@Output
	public String getOpbevaringsbetingelser()
	{
		return opbevaringsbetingelser;
	}

	@Output
	public String getOpbevaringstidEnhed()
	{
		return opbevaringstidEnhed;
	}
	
	public void setOpbevaringstidEnhed(String value)
	{
		opbevaringstidEnhed = value;
	}

	@Output
	public Long getOpbevaringstidNumerisk()
	{
		return opbevaringstidNumerisk;
	}

	@Output
	public Date getOprettelsesdato()
	{
		return oprettelsesdato;
	}

	@Output
	public String getPakningOptagetITilskudsgruppe()
	{
		return pakningOptagetITilskudsgruppe;
	}

	@Output
	public Long getPakningsdistributoer()
	{
		return pakningsdistributoer;
	}

	@Output(name = "PakningsstoerrelseTekst")
	public String getPakningsstoerrelseKlartekst()
	{
		return pakningsstoerrelseKlartekst;
	}

	@Output(name = "PakningsstoerrelseNumerisk")
	public Long getPakningsstoerrelseNumerisk()
	{
		return pakningsstoerrelseNumerisk;
	}

	@Output(name = "Pakningsstoerrelsesenhed")
	public String getPakningsstorrelseEnhed()
	{
		return pakningsstoerrelseEnhed;
	}

	public Priser getPriser()
	{
		return takst.getEntity(Priser.class, varenummer);
	}

	@Output
	public Date getUdgaaetDato()
	{
		return udgaaetDato;
	}

	@Output
	public String getUdleveringsbestemmelse()
	{
		return udleveringsbestemmelse;
	}

	@Output
	public String getUdleveringSpeciale()
	{
		return udleveringSpeciale;
	}

	@Id
	@Output(name = "Varenummer")
	public Long getVarenummer()
	{
		return varenummer;
	}

	@Output(name = "VarenummerDelpakning")
	public Long getVarenummerForDelpakning()
	{
		return varenummerForDelpakning;
	}

	@Deprecated
	public boolean isTilHumanAnvendelse()
	{
		Laegemiddel lm = takst.getEntity(Laegemiddel.class, drugid);

		if (lm == null)
		{
			return true;
		}

		return lm.isTilHumanAnvendelse();
	}

	public void setAlfabetSekvensnr(Long alfabetSekvensnr)
	{
		this.alfabetSekvensnr = alfabetSekvensnr;
	}

	public void setAntalDDDPrPakning(Long antalDDDPrPakning)
	{
		this.antalDDDPrPakning = antalDDDPrPakning;
	}

	public void setAntalDelpakninger(Long antalDelpakninger)
	{
		this.antalDelpakninger = antalDelpakninger;
	}

	public void setBeregningskodeAIPRegpris(String beregningskodeAIPRegpris)
	{
		this.beregningskodeAIPRegpris = beregningskodeAIPRegpris;
	}

	public void setDatoForSenestePrisaendring(Date datoForSenestePrisaendring)
	{
		this.datoForSenestePrisaendring = datoForSenestePrisaendring;
	}

	public void setDrugid(Long drugid)
	{
		this.drugid = drugid;
	}

	public void setEmballagetype(String emballagetype)
	{
		this.emballagetype = emballagetype;
	}

	public void setFaerdigfremstillingsgebyr(String faerdigfremstillingsgebyr)
	{
		this.faerdigfremstillingsgebyr = faerdigfremstillingsgebyr;
	}

	public void setKlausulForMedicintilskud(String klausulForMedicintilskud)
	{
		this.klausulForMedicintilskud = klausulForMedicintilskud;
	}

	public void setMedicintilskudskode(String medicintilskudskode)
	{
		this.medicintilskudskode = medicintilskudskode;
	}

	public void setOpbevaringsbetingelser(String opbevaringsbetingelser)
	{
		this.opbevaringsbetingelser = opbevaringsbetingelser;
	}

	public void setOpbevaringstidNumerisk(Long opbevaringstidNumerisk)
	{
		this.opbevaringstidNumerisk = opbevaringstidNumerisk;
	}

	public void setOprettelsesdato(Date oprettelsesdato)
	{
		this.oprettelsesdato = oprettelsesdato;
	}

	public void setPakningOptagetITilskudsgruppe(String pakningOptagetITilskudsgruppe)
	{
		this.pakningOptagetITilskudsgruppe = pakningOptagetITilskudsgruppe;
	}

	public void setPakningsdistributoer(Long pakningsdistributoer)
	{
		this.pakningsdistributoer = pakningsdistributoer;
	}

	public void setPakningsstoerrelseEnhed(String pakningsstoerrelseEnhed)
	{
		this.pakningsstoerrelseEnhed = pakningsstoerrelseEnhed;
	}

	public void setPakningsstoerrelseKlartekst(String pakningsstoerrelseKlartekst)
	{
		this.pakningsstoerrelseKlartekst = pakningsstoerrelseKlartekst;
	}

	public void setPakningsstoerrelseNumerisk(Long pakningsstoerrelseNumerisk)
	{
		this.pakningsstoerrelseNumerisk = pakningsstoerrelseNumerisk;
	}

	public void setUdgaaetDato(Date udgaaetDato)
	{
		this.udgaaetDato = udgaaetDato;
	}

	public void setUdleveringsbestemmelse(String udleveringsbestemmelse)
	{
		this.udleveringsbestemmelse = udleveringsbestemmelse;
	}

	public void setUdleveringSpeciale(String udleveringSpeciale)
	{
		this.udleveringSpeciale = udleveringSpeciale;
	}

	public void setVarenummer(Long varenummer)
	{
		this.varenummer = varenummer;
	}

	public void setVarenummerForDelpakning(Long varenummerForDelpakning)
	{
		this.varenummerForDelpakning = varenummerForDelpakning;
	}
}
