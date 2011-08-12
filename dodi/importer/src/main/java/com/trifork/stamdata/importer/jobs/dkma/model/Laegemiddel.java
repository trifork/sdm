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

import java.util.Date;

import com.trifork.stamdata.importer.jobs.dkma.TakstEntity;
import com.trifork.stamdata.importer.persistence.*;


@Output(name = "Laegemiddel")
public class Laegemiddel extends TakstEntity
{
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
	private String atc; // Ref. t. LMS12
	private String administrationsvej; // 4 x 2 kar. (Ref. t. LMS11)
	private boolean trafikadvarsel; // 2 muligh.: J eller blank
	private String substitution; // 2 muligh.: G eller blank
	private String laegemidletsSubstitutionsgruppe; // Substitutionsgruppenr. på
	
	// Drugid-niveau
	private boolean egnetTilDosisdispensering;
	
	private Date datoForAfregistrAfLaegemiddel; // Format: ååååmmdd
	private Date karantaenedato; // Format: ååååmmdd

	@Output
	public String getAdministrationsvejKode()
	{
		return administrationsvej;
	}

	@Output
	public String getAlfabetSekvensplads()
	{
		return alfabetSekvensplads;
	}

	@Output(name = "ATCKode")
	public String getATC()
	{
		return atc;
	}

	@Output(name = "ATCTekst")
	public String getATCTekst()
	{
		ATCKoderOgTekst atcObj = takst.getEntity(ATCKoderOgTekst.class, atc);
		if (atcObj == null)
		{
			return null;
		}
		
		return atcObj.getTekst();
	}

	@Output
	public Date getDatoForAfregistrAfLaegemiddel()
	{
		return datoForAfregistrAfLaegemiddel;
	}

	@Id
	@Output(name = "DrugID")
	public Long getDrugid()
	{
		return drugid;
	}

	@Output(name = "Dosisdispenserbar")
	public boolean getEgnetTilDosisdispensering()
	{
		return egnetTilDosisdispensering;
	}

	@Output(name = "FormTekst")
	public String getForm()
	{
		LaegemiddelformBetegnelser lmfb = takst.getEntity(LaegemiddelformBetegnelser.class, formKode);
		
		if (lmfb == null)
		{
			return null;
		}
		
		return lmfb.getTekst();
	}

	@Output(name = "FormKode")
	public String getFormKode()
	{
		return formKode;
	}

	@Output
	public Date getKarantaenedato()
	{
		return karantaenedato;
	}

	@Output
	public String getKodeForYderligereFormOplysn()
	{
		return kodeForYderligereFormOplysn;
	}

	@Output
	public String getLaegemiddelformTekst()
	{
		return laegemiddelformTekst;
	}

	@Output
	public String getLaegemidletsSubstitutionsgruppe()
	{
		return laegemidletsSubstitutionsgruppe;
	}

	@Output
	public Long getMTIndehaverKode()
	{
		return mTIndehaver;
	}

	@Output(name = "DrugName")
	public String getNavn()
	{
		return navn;
	}

	@Output
	public Long getRepraesentantDistributoerKode()
	{
		return repraesentantDistributoer;
	}

	@Output
	public Long getSpecNummer()
	{
		return specNummer;
	}

	@Output(name = "StyrkeEnhed")
	public String getStyrke()
	{
		// TODO: This seems strange. Logic is misplaced here.
		// Remove the field or move the logic to the parser.
		
		if (styrkeNumerisk == null || styrkeNumerisk == 0)
		{
			return null;
		}
		
		return styrkeEnhed;
	}

	@Output(name = "StyrkeTekst")
	public String getStyrkeKlarTekst()
	{
		return styrkeKlarTekst;
	}

	@Output(name = "StyrkeNumerisk")
	public Double getStyrkeNumerisk()
	{
		// TODO: This seems strange. Logic is misplaced here.
		// Remove the field or move the logic to the parser.
		
		if (styrkeNumerisk == null || styrkeNumerisk == 0)
		{
			return null;
		}
		
		return styrkeNumerisk / 1000.0;
	}

	@Output
	public String getSubstitution()
	{
		return substitution;
	}

	@Output
	public boolean getTrafikadvarsel()
	{
		return trafikadvarsel;
	}

	@Output
	public String getVaredeltype()
	{
		return varedeltype;
	}

	@Output
	public String getVaretype()
	{
		return varetype;
	}

	public Boolean isTilHumanAnvendelse()
	{
		if (atc == null)
		{
			return null;
		}
		return !atc.startsWith("Q");
	}

	public void setAdministrationsvej(String administrationsvej)
	{
		this.administrationsvej = administrationsvej;
	}

	public void setAlfabetSekvensplads(String alfabetSekvensplads)
	{
		this.alfabetSekvensplads = alfabetSekvensplads;
	}

	public void setATC(String aTC)
	{
		atc = aTC;
	}

	public void setDatoForAfregistrAfLaegemiddel(Date datoForAfregistrAfLaegemiddel)
	{
		this.datoForAfregistrAfLaegemiddel = datoForAfregistrAfLaegemiddel;
	}

	public void setDrugid(Long drugid)
	{
		this.drugid = drugid;
	}

	public void setEgnetTilDosisdispensering(boolean egnetTilDosisdispensering)
	{
		this.egnetTilDosisdispensering = egnetTilDosisdispensering;
	}

	public void setFormKode(String formKode)
	{
		this.formKode = formKode;
	}

	public void setKarantaenedato(Date karantaenedato)
	{
		this.karantaenedato = karantaenedato;
	}

	public void setKodeForYderligereFormOplysn(String kodeForYderligereFormOplysn)
	{
		this.kodeForYderligereFormOplysn = kodeForYderligereFormOplysn;
	}

	public void setLaegemiddelformTekst(String laegemiddelformTekst)
	{
		this.laegemiddelformTekst = laegemiddelformTekst;
	}

	public void setLaegemidletsSubstitutionsgruppe(String laegemidletsSubstitutionsgruppe)
	{
		this.laegemidletsSubstitutionsgruppe = laegemidletsSubstitutionsgruppe;
	}

	public void setMTIndehaver(Long mTIndehaver)
	{
		this.mTIndehaver = mTIndehaver;
	}

	public void setNavn(String navn)
	{
		this.navn = navn;
	}

	public void setRepraesentantDistributoer(Long repraesentantDistributoer)
	{
		this.repraesentantDistributoer = repraesentantDistributoer;
	}

	public void setSpecNummer(Long specNummer)
	{
		this.specNummer = specNummer;
	}

	public void setStyrkeEnhed(String styrkeEnhed)
	{
		this.styrkeEnhed = styrkeEnhed;
	}

	public void setStyrkeKlarTekst(String styrkeKlarTekst)
	{
		this.styrkeKlarTekst = styrkeKlarTekst;
	}

	public void setStyrkeNumerisk(Long styrkeNumerisk)
	{
		this.styrkeNumerisk = styrkeNumerisk;
	}

	public void setSubstitution(String substitution)
	{
		this.substitution = substitution;
	}

	public void setTrafikadvarsel(boolean trafikadvarsel)
	{
		this.trafikadvarsel = trafikadvarsel;
	}

	public void setVaredeltype(String varedeltype)
	{
		this.varedeltype = varedeltype;
	}

	public void setVaretype(String varetype)
	{
		this.varetype = varetype;
	}
}
