/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.importer.jobs.takst.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.importer.jobs.takst.TakstEntity;
import com.trifork.stamdata.importer.persistence.Dataset;


@Entity(name = "Pakning")
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
	private String opbevaringsbetingelser; // Ref. t. LMS20
	private Date oprettelsesdato; // Format: ååååmmdd
	private Date datoForSenestePrisaendring; // Format: ååååmmdd
	private Date udgaaetDato; // Format: ååååmmdd
	private String beregningskodeAIPRegpris; // Ref. t. LMS13
	private String pakningOptagetITilskudsgruppe; // 2 muligh.: F eller blank
	private String faerdigfremstillingsgebyr; // 2 muligh.: B eller blank
	private Long pakningsdistributoer; // Ref. t. LMS09

	@Column
	public Long getAlfabetSekvensnr()
	{
		return this.alfabetSekvensnr;
	}

	@Column
	public Double getAntalDDDPrPakning()
	{
		return (this.antalDDDPrPakning) / 1000.0;
	}

	@Column
	public Long getAntalDelpakninger()
	{
		return this.antalDelpakninger;
	}

	@Column
	public String getBeregningskodeAIRegpris()
	{
		return this.beregningskodeAIPRegpris;
	}

	@Column
	public Date getDatoForSenestePrisaendring()
	{
		return datoForSenestePrisaendring;
	}

	@Column
	public boolean getDosisdispenserbar()
	{
		return takst.getEntity(drugid, Laegemiddel.class).getEgnetTilDosisdispensering();
	}

	@Column(name = "DrugID")
	public Long getDrugid()
	{
		return this.drugid;
	}

	@Column(name = "EmballageTypeKode")
	public String getEmballagetype()
	{
		return emballagetype;
	}

	@Column
	public boolean getFaerdigfremstillingsgebyr()
	{
		return "B".equalsIgnoreCase(this.faerdigfremstillingsgebyr);
	}

	@Column(name = "KlausuleringsKode")
	public String getKlausulForMedicintilskud()
	{
		return klausulForMedicintilskud;
	}

	@Column(name = "MedicintilskudsKode")
	public String getMedicintilskudskode()
	{
		return this.medicintilskudskode;
	}

	@Column
	public String getOpbevaringsbetingelser()
	{
		return this.opbevaringsbetingelser;
	}
	@Column
	public Long getOpbevaringstid()
	{
		return this.opbevaringstidNumerisk;
	}

	@Column
	public Long getOpbevaringstidNumerisk()
	{
		return this.opbevaringstidNumerisk;
	}

	@Column
	public Date getOprettelsesdato()
	{
		return oprettelsesdato;
	}

	@Column
	public boolean getPakningOptagetITilskudsgruppe()
	{
		return "F".equalsIgnoreCase(this.pakningOptagetITilskudsgruppe);
	}

	@Column
	public Long getPakningsdistributoer()
	{
		return this.pakningsdistributoer;
	}

	public Firma getPakningsdistributoerRef()
	{
		return takst.getEntity(pakningsdistributoer, Firma.class);
	}

	@Column(name = "PakningsstoerrelseTekst")
	public String getPakningsstoerrelseKlartekst()
	{
		return this.pakningsstoerrelseKlartekst;
	}

	@Column(name = "PakningsstoerrelseNumerisk")
	public Double getPakningsstoerrelseNumerisk()
	{
		if (this.pakningsstoerrelseNumerisk == 0)
		{
			return null;
		}
		return this.pakningsstoerrelseNumerisk / 100.0;
	}

	@Column(name = "Pakningsstoerrelsesenhed")
	public String getPakningsstorrelseEnhed()
	{
		if (this.pakningsstoerrelseNumerisk == 0)
		{
			return null;
		}
		return this.pakningsstoerrelseEnhed;
	}

	public Priser getPriser()
	{
		return takst.getEntity(varenummer, Priser.class);
	}

	public List<Pakning> getSubstitutioner()
	{
		Dataset<Substitution> subst = takst.getDatasetOfType(Substitution.class);
		Dataset<SubstitutionAfLaegemidlerUdenFastPris> substufp = takst.getDatasetOfType(SubstitutionAfLaegemidlerUdenFastPris.class);
		List<Long> substitutionsgrupper = new ArrayList<Long>();
		for (Substitution substitution : subst.getEntities())
		{
			if (substitution.getReceptensVarenummer().equals(varenummer)) substitutionsgrupper.add(substitution.getSubstitutionsgruppenummer());
		}
		for (SubstitutionAfLaegemidlerUdenFastPris substitutionufp : substufp.getEntities())
		{
			if (substitutionufp.getVarenummer().equals(varenummer)) substitutionsgrupper.add(substitutionufp.getSubstitutionsgruppenummer());
		}

		Dataset<Pakning> pakninger = takst.getDatasetOfType(Pakning.class);
		List<Pakning> substitutioner = new ArrayList<Pakning>();
		for (Long substgruppe : substitutionsgrupper)
		{
			for (Substitution substitution : subst.getEntities())
			{
				if (substitution.getSubstitutionsgruppenummer().equals(substgruppe) && !substitution.getReceptensVarenummer().equals(this.varenummer)) substitutioner.add(pakninger.getEntityById(substitution.getReceptensVarenummer()));
			}
			for (SubstitutionAfLaegemidlerUdenFastPris substitution : substufp.getEntities())
			{
				if (substitution.getSubstitutionsgruppenummer().equals(substgruppe) && !substitution.getVarenummer().equals(this.varenummer)) substitutioner.add(pakninger.getEntityById(substitution.getVarenummer()));
			}
		}
		return substitutioner;
	}

	@Column
	public Date getUdgaaetDato()
	{
		return udgaaetDato;
	}

	@Column
	public String getUdleveringsbestemmelse()
	{
		return this.udleveringsbestemmelse;
	}

	public Udleveringsbestemmelser getUdleveringsbestemmelseRef()
	{
		return takst.getEntity(udleveringsbestemmelse, Udleveringsbestemmelser.class);
	}

	@Column
	public String getUdleveringSpeciale()
	{
		return this.udleveringSpeciale;
	}

	public SpecialeForNBS getUdleveringSpecialeRef()
	{
		return takst.getEntity(udleveringSpeciale, SpecialeForNBS.class);
	}

	@Id
	@Column
	public Long getVarenummer()
	{
		return this.varenummer;
	}

	@Column(name = "VarenummerDelpakning")
	public Long getVarenummerForDelpakning()
	{
		return this.varenummerForDelpakning;
	}

	public boolean isTilHumanAnvendelse()
	{
		Laegemiddel lm = takst.getEntity(drugid, Laegemiddel.class);
		
		if (lm == null) return true;
		
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
