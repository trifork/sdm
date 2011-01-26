package com.trifork.stamdata.registre.takst;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.*;
import com.trifork.stamdata.persistence.Dataset;


@Entity
public class Pakning extends TakstRecord
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
	private String opbevaringstidEnhed; // Ref. t. LMS15, enhedstype 1
	private String opbevaringsbetingelser; // Ref. t. LMS20
	private Long oprettelsesdato; // Format: YYYYMMDD
	private Long datoForSenestePrisaendring; // Format: YYYYMMDD
	private Long udgaaetDato; // YYYYMMDD
	private String beregningskodeAIPRegpris; // Ref. t. LMS13
	private String pakningOptagetITilskudsgruppe; // 2 muligh.: F eller blank
	private String faerdigfremstillingsgebyr; // 2 muligh.: B eller blank
	private Long pakningsdistributoer; // Ref. t. LMS09


	@Column(name = "DrugID")
	@XmlOrder(1)
	public Long getDrugid()
	{
		return drugid;
	}


	public void setDrugid(Long drugid)
	{
		this.drugid = drugid;
	}


	@Id
	@Column
	@XmlOrder(2)
	public Long getVarenummer()
	{
		return this.varenummer;
	}


	public void setVarenummer(Long varenummer)
	{
		this.varenummer = varenummer;
	}


	public Long getAlfabetSekvensnr()
	{
		return this.alfabetSekvensnr;
	}


	public void setAlfabetSekvensnr(Long alfabetSekvensnr)
	{
		this.alfabetSekvensnr = alfabetSekvensnr;
	}


	@Column(name = "VarenummerDelpakning")
	@XmlOrder(3)
	public Long getVarenummerForDelpakning()
	{
		return this.varenummerForDelpakning;
	}


	public void setVarenummerForDelpakning(Long varenummerForDelpakning)
	{
		this.varenummerForDelpakning = varenummerForDelpakning;
	}


	public Long getAntalDelpakninger()
	{
		return this.antalDelpakninger;
	}


	public void setAntalDelpakninger(Long antalDelpakninger)
	{
		this.antalDelpakninger = antalDelpakninger;
	}


	@Column(name = "PakningsstoerrelseTekst")
	@XmlOrder(4)
	public String getPakningsstoerrelseKlartekst()
	{
		return this.pakningsstoerrelseKlartekst;
	}


	public void setPakningsstoerrelseKlartekst(String pakningsstoerrelseKlartekst)
	{
		this.pakningsstoerrelseKlartekst = pakningsstoerrelseKlartekst;
	}


	@Column(name = "PakningsstoerrelseNumerisk")
	@XmlName("numeriskPakningsstoerrelse")
	@XmlOrder(5)
	public Double getPakningsstoerrelseNumerisk()
	{
		if (this.pakningsstoerrelseNumerisk == 0)
		{
			return null;
		}

		return this.pakningsstoerrelseNumerisk / 100.0;
	}


	public void setPakningsstoerrelseNumerisk(Long pakningsstoerrelseNumerisk)
	{

		this.pakningsstoerrelseNumerisk = pakningsstoerrelseNumerisk;
	}


	@Column(name = "Pakningsstoerrelsesenhed")
	@XmlOrder(6)
	public String getPakningsstorrelseEnhed()
	{

		if (this.pakningsstoerrelseNumerisk == 0)
		{
			return null;
		}

		return this.pakningsstoerrelseEnhed;
	}


	public void setPakningsstoerrelseEnhed(String pakningsstoerrelseEnhed)
	{
		this.pakningsstoerrelseEnhed = pakningsstoerrelseEnhed;
	}


	@Column(name = "EmballageTypeKode")
	@XmlOrder(7)
	@XmlName("emballagetypekode")
	public String getEmballagetype()
	{

		return emballagetype;
	}


	public void setEmballagetype(String emballagetype)
	{
		this.emballagetype = emballagetype;
	}


	public Udleveringsbestemmelser getUdleveringsbestemmelse()
	{
		return takst.getEntity(Udleveringsbestemmelser.class, udleveringsbestemmelse);
	}


	public void setUdleveringsbestemmelse(String udleveringsbestemmelse)
	{

		this.udleveringsbestemmelse = udleveringsbestemmelse;
	}


	public SpecialeForNBS getUdleveringSpeciale()
	{

		return takst.getEntity(SpecialeForNBS.class, udleveringSpeciale);
	}


	public void setUdleveringSpeciale(String udleveringSpeciale)
	{

		this.udleveringSpeciale = udleveringSpeciale;
	}


	@Column(name = "MedicintilskudsKode")
	@XmlName("medicintilskudskode")
	@XmlOrder(8)
	public String getMedicintilskudskode()
	{

		return this.medicintilskudskode;
	}


	public void setMedicintilskudskode(String medicintilskudskode)
	{
		this.medicintilskudskode = medicintilskudskode;
	}


	@Column(name = "KlausuleringsKode")
	@XmlName("klausuleringskode")
	@XmlOrder(9)
	public String getKlausulForMedicintilskud()
	{

		return this.klausulForMedicintilskud;
	}


	public void setKlausulForMedicintilskud(String klausulForMedicintilskud)
	{

		this.klausulForMedicintilskud = klausulForMedicintilskud;
	}


	public Double getAntalDDDPrPakning()
	{

		return (this.antalDDDPrPakning) / 1000.0;
	}


	public void setAntalDDDPrPakning(Long antalDDDPrPakning)
	{

		this.antalDDDPrPakning = antalDDDPrPakning;
	}


	public Long getOpbevaringstidNumerisk()
	{

		return this.opbevaringstidNumerisk;
	}


	public void setOpbevaringstidNumerisk(Long opbevaringstidNumerisk)
	{

		this.opbevaringstidNumerisk = opbevaringstidNumerisk;
	}


	public NumeriskMedEnhed getOpbevaringstid()
	{

		final int enhedstype = 1;
		DivEnheder enhed = takst.getDatasetOfType(DivEnheder.class).getRecordById(opbevaringstidEnhed + "-" + enhedstype);
		return new NumeriskMedEnhed(takst, null, opbevaringstidNumerisk, enhed);

	}


	public void setOpbevaringstidEnhed(String opbevaringstidEnhed)
	{

		this.opbevaringstidEnhed = opbevaringstidEnhed;
	}


	public Opbevaringsbetingelser getOpbevaringsbetingelser()
	{

		return takst.getDatasetOfType(Opbevaringsbetingelser.class).getRecordById(opbevaringsbetingelser);
	}


	public void setOpbevaringsbetingelser(String opbevaringsbetingelser)
	{

		this.opbevaringsbetingelser = opbevaringsbetingelser;
	}


	public String getOprettelsesdato()
	{

		return DateUtils.toISO8601date(this.oprettelsesdato);
	}


	public void setOprettelsesdato(Long oprettelsesdato)
	{

		this.oprettelsesdato = oprettelsesdato;
	}


	public String getDatoForSenestePrisaendring()
	{

		return DateUtils.toISO8601date(this.datoForSenestePrisaendring);
	}


	public void setDatoForSenestePrisaendring(Long datoForSenestePrisaendring)
	{

		this.datoForSenestePrisaendring = datoForSenestePrisaendring;
	}


	public String getUdgaaetDato()
	{

		return DateUtils.toISO8601date(this.udgaaetDato);
	}


	public void setUdgaaetDato(Long udgaaetDato)
	{

		this.udgaaetDato = udgaaetDato;
	}


	public Beregningsregler getBeregningskodeAIPRegpris()
	{

		return takst.getEntity(Beregningsregler.class, this.beregningskodeAIPRegpris);
	}


	public void setBeregningskodeAIPRegpris(String beregningskodeAIPRegpris)
	{

		this.beregningskodeAIPRegpris = beregningskodeAIPRegpris;
	}


	public boolean getPakningOptagetITilskudsgruppe()
	{

		return "F".equalsIgnoreCase(this.pakningOptagetITilskudsgruppe);
	}


	public void setPakningOptagetITilskudsgruppe(String pakningOptagetITilskudsgruppe)
	{

		this.pakningOptagetITilskudsgruppe = pakningOptagetITilskudsgruppe;
	}


	public boolean getFaerdigfremstillingsgebyr()
	{

		return "B".equalsIgnoreCase(this.faerdigfremstillingsgebyr);
	}


	public void setFaerdigfremstillingsgebyr(String faerdigfremstillingsgebyr)
	{

		this.faerdigfremstillingsgebyr = faerdigfremstillingsgebyr;
	}


	public Firma getPakningsdistributoer()
	{

		return takst.getEntity(Firma.class, pakningsdistributoer);
	}


	public void setPakningsdistributoer(Long pakningsdistributoer)
	{

		this.pakningsdistributoer = pakningsdistributoer;
	}


	public Laegemiddel getLaegemiddel()
	{

		Dataset<Laegemiddel> laegemidler = takst.getDatasetOfType(Laegemiddel.class);
		for (Record sde : laegemidler.getEntities())
		{
			Laegemiddel lm = (Laegemiddel) sde;
			if (drugid.equals(lm.getDrugid())) return lm;
		}
		return null;
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
				if (substitution.getSubstitutionsgruppenummer().equals(substgruppe) && !substitution.getReceptensVarenummer().equals(this.varenummer)) substitutioner.add(pakninger.getRecordById(substitution.getReceptensVarenummer()));
			}
			for (SubstitutionAfLaegemidlerUdenFastPris substitution : substufp.getEntities())
			{
				if (substitution.getSubstitutionsgruppenummer().equals(substgruppe) && !substitution.getVarenummer().equals(this.varenummer)) substitutioner.add(pakninger.getRecordById(substitution.getVarenummer()));
			}
		}
		return substitutioner;
	}


	public List<Pakning> getBilligsteSubstitution()
	{

		Dataset<Substitution> subst = takst.getDatasetOfType(Substitution.class);
		Dataset<Pakning> pakninger = takst.getDatasetOfType(Pakning.class);
		List<Pakning> substitutioner = new ArrayList<Pakning>();
		for (Substitution substitution : subst.getEntities())
		{
			if (substitution.getReceptensVarenummer().equals(varenummer) && !this.varenummer.equals(substitution.getBilligsteVarenummer()))
			{
				Pakning p = pakninger.getRecordById(substitution.getBilligsteVarenummer());
				if (p != null) substitutioner.add(p);
			}
		}

		if (substitutioner.size() == 0) return null;
		return substitutioner;
	}


	public Priser getPriser()
	{
		return takst.getEntity(Priser.class, varenummer);
	}


	public Boolean isTilHumanAnvendelse()
	{
		Laegemiddel lm = takst.getEntity(Laegemiddel.class, drugid);
		if (lm == null) return null;
		return lm.isTilHumanAnvendelse();
	}


	@Column
	@XmlOrder(10)
	public Integer getDosisdispenserbar()
	{
		return takst.getEntity(Laegemiddel.class, drugid).getEgnetTilDosisdispensering();
	}
}
