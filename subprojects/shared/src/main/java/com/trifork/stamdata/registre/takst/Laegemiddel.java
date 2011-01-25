package com.trifork.stamdata.registre.takst;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.XmlName;
import com.trifork.stamdata.persistence.Dataset;


@Entity
public class Laegemiddel extends TakstRecord
{

	private Logger logger = LoggerFactory.getLogger(TakstRecord.class);

	private Long drugid;
	private String varetype; // Udfyldt med SP (Specialiteter)
	private String varedeltype; // Udfyldt med LM (lægemiddel, reg.)
	private String alfabetSekvensplads;
	private Long specNummer; // D.sp.nr. (decentrale) - Alm. nr (centrale)
	private String navn; // Evt. forkortet
	private String laegemiddelformTekst; // Evt. forkortet
	private String formKode; // Ref. t. LMS22, felt 01
	private String kodeForYderligereFormOplysn; // Feltet er tomt pt.
	private String styrkeKlarTekst;
	private Long styrkeNumerisk;
	private String styrkeEnhed; // Ref. t. LMS15, enhedstype 3
	private Long mTIndehaver; // Ref. t. LMS09
	private Long repraesentantDistributoer; // Ref. t. LMS09
	private String aTC; // Ref. t. LMS12
	private String administrationsvej; // 4 x 2 kar. (Ref. t. LMS11)
	private String trafikadvarsel; // 2 muligh.: J eller blank
	private String substitution; // 2 muligh.: G eller blank
	private String laegemidletsSubstitutionsgruppe; // Substitutionsgruppenr. på Drugid-niveau
	private String egnetTilDosisdispensering; // 2 muligh.: D eller blank
	private String datoForAfregistrAfLaegemiddel; // Format: ååååmmdd
	private String karantaenedato; // Format: ååååmmdd


	@Id
	@Column(name = "DrugID")
	public Long getDrugid()
	{

		return this.drugid;
	}


	public void setDrugid(Long drugid)
	{

		this.drugid = drugid;
	}


	public String getVaretype()
	{

		return this.varetype;
	}


	public void setVaretype(String varetype)
	{

		this.varetype = varetype;
	}


	public String getVaredeltype()
	{

		return this.varedeltype;
	}


	public void setVaredeltype(String varedeltype)
	{

		this.varedeltype = varedeltype;
	}


	public String getAlfabetSekvensplads()
	{

		return this.alfabetSekvensplads;
	}


	public void setAlfabetSekvensplads(String alfabetSekvensplads)
	{

		this.alfabetSekvensplads = alfabetSekvensplads;
	}


	public Long getSpecNummer()
	{

		return this.specNummer;
	}


	public void setSpecNummer(Long specNummer)
	{

		this.specNummer = specNummer;
	}


	@Column(name = "DrugName")
	@XmlName("navn")
	public String getNavn()
	{
		if (this.navn == null || this.navn.trim().equals(""))
		{
			return "Ikke angivet";
		}

		return this.navn;
	}


	public void setNavn(String navn)
	{
		this.navn = navn;
	}


	public String getLaegemiddelformTekst()
	{
		return laegemiddelformTekst;
	}


	public void setLaegemiddelformTekst(String laegemiddelformTekst)
	{
		this.laegemiddelformTekst = laegemiddelformTekst;
	}


	@Column(name = "FormKode")
	@XmlName("formkode")
	public String getFormKode()
	{
		return formKode;
	}


	public void setFormKode(String formKode)
	{
		this.formKode = formKode;
	}


	public String getKodeForYderligereFormOplysn()
	{
		return kodeForYderligereFormOplysn;
	}


	public void setKodeForYderligereFormOplysn(String kodeForYderligereFormOplysn)
	{

		this.kodeForYderligereFormOplysn = kodeForYderligereFormOplysn;
	}


	@Column(name = "StyrkeTekst")
	@XmlName("styrketekst")
	public String getStyrkeKlarTekst()
	{
		return this.styrkeKlarTekst;
	}


	public void setStyrkeKlarTekst(String styrkeKlarTekst)
	{
		this.styrkeKlarTekst = styrkeKlarTekst;
	}


	@Column(name = "StyrkeNumerisk")
	@XmlName("numeriskStyrke")
	public Double getStyrkeNumerisk()
	{
		if (styrkeNumerisk == null || styrkeNumerisk == 0)
		{
			return null;
		}

		return this.styrkeNumerisk / 1000.0;
	}


	public void setStyrkeNumerisk(Long styrkeNumerisk)
	{
		this.styrkeNumerisk = styrkeNumerisk;
	}


	@Column(name = "StyrkeEnhed")
	public String getStyrke()
	{
		if (styrkeNumerisk == null || styrkeNumerisk == 0)
		{
			return null;
		}

		return styrkeEnhed;
	}


	public void setStyrkeEnhed(String styrkeEnhed)
	{
		this.styrkeEnhed = styrkeEnhed;
	}


	public Firma getMTIndehaver()
	{
		return takst.getEntity(Firma.class, this.mTIndehaver);
	}


	public void setMTIndehaver(Long mTIndehaver)
	{
		this.mTIndehaver = mTIndehaver;
	}


	public Firma getRepraesentantDistributoer()
	{
		return takst.getEntity(Firma.class, this.repraesentantDistributoer);
	}


	public void setRepraesentantDistributoer(Long repraesentantDistributoer)
	{
		this.repraesentantDistributoer = repraesentantDistributoer;
	}


	public void setATC(String aTC)
	{
		this.aTC = aTC;
	}


	public void setAdministrationsvej(String administrationsvej)
	{
		this.administrationsvej = administrationsvej;
	}


	public Boolean getTrafikadvarsel()
	{
		return "J".equalsIgnoreCase(this.trafikadvarsel);
	}


	public void setTrafikadvarsel(String trafikadvarsel)
	{
		this.trafikadvarsel = trafikadvarsel;
	}


	public String getSubstitution()
	{
		return this.substitution;
	}


	public void setSubstitution(String substitution)
	{
		this.substitution = substitution;
	}


	public String getLaegemidletsSubstitutionsgruppe()
	{
		return this.laegemidletsSubstitutionsgruppe;
	}


	public void setLaegemidletsSubstitutionsgruppe(String laegemidletsSubstitutionsgruppe)
	{
		this.laegemidletsSubstitutionsgruppe = laegemidletsSubstitutionsgruppe;
	}


	/* TODO: INT? */
	@Column(name = "Dosisdispenserbar")
	public Integer getEgnetTilDosisdispensering()
	{
		return ("D".equals(this.egnetTilDosisdispensering)) ? 1 : 0;
	}


	public void setEgnetTilDosisdispensering(String egnetTilDosisdispensering)
	{
		this.egnetTilDosisdispensering = egnetTilDosisdispensering;
	}


	public String getDatoForAfregistrAfLaegemiddel()
	{
		if (this.datoForAfregistrAfLaegemiddel == null || "".equals(this.datoForAfregistrAfLaegemiddel))
		{
			return null;
		}

		SimpleDateFormat informat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat outformat = new SimpleDateFormat("yyyy-MM-dd");

		try
		{
			return outformat.format(informat.parse(this.datoForAfregistrAfLaegemiddel));
		}
		catch (ParseException e)
		{
			logger.error("Error converting DatoForAfregistrAfLaegemiddel to iso 8601 date format. Returning unformated string: '" + this.datoForAfregistrAfLaegemiddel + "'");
			return this.datoForAfregistrAfLaegemiddel;
		}
	}


	public void setDatoForAfregistrAfLaegemiddel(String datoForAfregistrAfLaegemiddel)
	{

		this.datoForAfregistrAfLaegemiddel = datoForAfregistrAfLaegemiddel;
	}


	public String getKarantaenedato()
	{
		if (this.karantaenedato == null || "".equals(this.karantaenedato)) return null;

		SimpleDateFormat informat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat outformat = new SimpleDateFormat("yyyy-MM-dd");

		try
		{
			return outformat.format(informat.parse(this.datoForAfregistrAfLaegemiddel));
		}
		catch (ParseException e)
		{
			logger.error("Error converting DatoForAfregistrAfLaegemiddel to iso 8601 date format. Returning unformated string");
			return this.datoForAfregistrAfLaegemiddel;
		}
	}


	public void setKarantaenedato(String karantaenedato)
	{
		this.karantaenedato = karantaenedato;
	}


	public List<UdgaaedeNavne> getUdgaaedeNavne()
	{
		List<UdgaaedeNavne> unavne = new ArrayList<UdgaaedeNavne>();
		Dataset<UdgaaedeNavne> unds = takst.getDatasetOfType(UdgaaedeNavne.class);

		if (unds == null) return null;

		for (UdgaaedeNavne un : unds.getEntities())
		{
			if (un.getDrugid().equals(drugid)) unavne.add(un);
		}

		return unavne;
	}


	public List<Administrationsvej> getAdministrationsveje()
	{
		List<Administrationsvej> adminveje = new ArrayList<Administrationsvej>();

		for (int idx = 0; idx < administrationsvej.length(); idx += 2)
		{
			String avKode = administrationsvej.substring(idx, idx + 2);
			Administrationsvej adminVej = takst.getEntity(Administrationsvej.class, avKode);
			if (adminVej == null)
				logger.warn("Administaritonvej not found for kode: '" + avKode + "'");
			else
				adminveje.add(adminVej);
		}

		return adminveje;
	}


	@Column(name = "FormTekst")
	public String getForm()
	{
		LaegemiddelformBetegnelser lmfb = takst.getEntity(LaegemiddelformBetegnelser.class, formKode);

		if (lmfb == null)
		{
			return null;
		}

		return lmfb.getTekst();
	}


	/*
	 * tom@trifork.com: Doseringer vil jeg nok udelade lige nu, her er vi ved at lave store
	 * ændringer (evt. kan du bare udkommentere koden i første omgang, hvis vi fortryder)
	 */
	public List<Dosering> getDoseringer()
	{

		List<Dosering> result = new ArrayList<Dosering>();
		Dataset<Doseringskode> doseringskoder = takst.getDatasetOfType(Doseringskode.class);
		if (doseringskoder == null) return null;
		for (Doseringskode d : doseringskoder.getEntities())
		{
			if (this.drugid.equals(getDrugid()))
			{
				result.add(takst.getEntity(Dosering.class, d.getDoseringskode()));
			}
		}
		return result;
	}


	public List<Indikation> getIndikationer()
	{

		List<Indikation> result = new ArrayList<Indikation>();
		Dataset<Indikationskode> indikationskode = takst.getDatasetOfType(Indikationskode.class);
		if (indikationskode == null) return null;
		for (Indikationskode i : indikationskode.getEntities())
		{
			if (i.getDrugID().equals(this.drugid))
			{
				result.add(takst.getEntity(Indikation.class, i.getIndikationskode()));
			}
		}
		return result;
	}


	public List<Pakning> getPakninger()
	{

		Dataset<Pakning> pakninger = takst.getDatasetOfType(Pakning.class);
		if (pakninger == null) return null;
		List<Pakning> pakningerWithThisLaegemiddel = new ArrayList<Pakning>();
		for (Pakning pakning : pakninger.getEntities())
		{
			if (pakning.getDrugid().equals(this.drugid))
			{
				pakningerWithThisLaegemiddel.add(pakning);
			}
		}
		return pakningerWithThisLaegemiddel;
	}


	@Column(name = "ATCKode")
	@XmlName("atc")
	public String getATC()
	{

		return aTC;
	}


	@Column(name = "ATCTekst")
	@XmlName("atcTekst")
	public String getATCTekst()
	{

		ATCKoderOgTekst atcObj = takst.getEntity(ATCKoderOgTekst.class, aTC);
		if (atcObj == null) return null;
		return atcObj.getTekst();
	}


	public String getAdministrationsvejKode()
	{
		return administrationsvej;
	}


	public Object getMTIndehaverKode()
	{
		return mTIndehaver;
	}


	public Object getRepraesentantDistributoerKode()
	{
		return repraesentantDistributoer;
	}


	public List<Indholdsstoffer> getIndholdsstoffer()
	{
		List<Indholdsstoffer> result = new ArrayList<Indholdsstoffer>();
		Dataset<Indholdsstoffer> indholdsstoffer = takst.getDatasetOfType(Indholdsstoffer.class);
		
		if (indholdsstoffer == null) return null;
		
		for (Indholdsstoffer stof : indholdsstoffer.getEntities())
		{
			if (stof.getDrugID().equals(this.drugid))
			{
				if (!result.contains(stof))
				{
					result.add(stof);
				}
			}
		}

		return result;
	}


	public Boolean getEksperimentieltLaegemiddel()
	{
		return ("" + drugid).startsWith("2742");
	}


	public Boolean getMagistreltLaegemiddel()
	{
		return ("" + drugid).startsWith("8");
	}


	public Boolean isTilHumanAnvendelse()
	{
		if (aTC == null) return null;
		return !aTC.startsWith("Q");
	}
}
