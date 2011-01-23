package com.trifork.stamdata.registre.takst;

public class OplysningerOmDosisdispensering extends TakstRecord {

	private Long drugid; // Ref. t. LMS01, felt 01
	private Long varenummer; // Ref. t. LMS02, felt 02
	private String laegemidletsSubstitutionsgruppe; // Ref. t. LMS01, felt 22.
													// Kan være blank
	private Long mindsteAIPPrEnhed; // Mindste AIP for alle aktive pakn. pr.
									// Drugid
	private Long mindsteRegisterprisEnh; // Mindste reg.pris for alle aktive
											// pakn. pr. Drugid
	private Long tSPPrEnhed; // Tilskudspris pr. enhed
	private String kodeForBilligsteDrugid; // Værdier = A - B - C
	private Long billigsteDrugid; // Henvisning til billigste Drugid


	public Long getDrugid() {

		return this.drugid;
	}


	public void setDrugid(Long drugid) {

		this.drugid = drugid;
	}


	public Long getVarenummer() {

		return this.varenummer;
	}


	public void setVarenummer(Long varenummer) {

		this.varenummer = varenummer;
	}


	public String getLaegemidletsSubstitutionsgruppe() {

		return this.laegemidletsSubstitutionsgruppe;
	}


	public void setLaegemidletsSubstitutionsgruppe(String laegemidletsSubstitutionsgruppe) {

		this.laegemidletsSubstitutionsgruppe = laegemidletsSubstitutionsgruppe;
	}


	public Long getMindsteAIPPrEnhed() {

		return this.mindsteAIPPrEnhed;
	}


	public void setMindsteAIPPrEnhed(Long mindsteAIPPrEnhed) {

		this.mindsteAIPPrEnhed = mindsteAIPPrEnhed;
	}


	public Long getMindsteRegisterprisEnh() {

		return this.mindsteRegisterprisEnh;
	}


	public void setMindsteRegisterprisEnh(Long mindsteRegisterprisEnh) {

		this.mindsteRegisterprisEnh = mindsteRegisterprisEnh;
	}


	public Long getTSPPrEnhed() {

		return this.tSPPrEnhed;
	}


	public void setTSPPrEnhed(Long tSPPrEnhed) {

		this.tSPPrEnhed = tSPPrEnhed;
	}


	public String getKodeForBilligsteDrugid() {

		return this.kodeForBilligsteDrugid;
	}


	public void setKodeForBilligsteDrugid(String kodeForBilligsteDrugid) {

		this.kodeForBilligsteDrugid = kodeForBilligsteDrugid;
	}


	public Long getBilligsteDrugid() {

		return this.billigsteDrugid;
	}


	public void setBilligsteDrugid(Long billigsteDrugid) {

		this.billigsteDrugid = billigsteDrugid;
	}


	@Override
	public Long getKey() {

		return varenummer;
	}

}