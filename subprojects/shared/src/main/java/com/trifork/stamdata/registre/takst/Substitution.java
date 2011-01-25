package com.trifork.stamdata.registre.takst;


public class Substitution extends TakstRecord
{
	private Long substitutionsgruppenummer; // Substitutionsgruppe for pakningen
	private Long receptensVarenummer; // Varenr. hvis substitutionsmuligheder
										// skal findes
	private Long numeriskPakningsstoerrelse; // Felt 07 i LMS02
	private String prodAlfabetiskeSekvensplads; // Felt 04 i LMS01
	private String substitutionskodeForPakning; // Værdier=A-B-C (for varenr. i
												// felt 02)
	private Long billigsteVarenummer; // Henvisning til billigste pakning


	public Long getSubstitutionsgruppenummer()
	{

		return this.substitutionsgruppenummer;
	}


	public void setSubstitutionsgruppenummer(Long substitutionsgruppenummer)
	{

		this.substitutionsgruppenummer = substitutionsgruppenummer;
	}


	public Long getReceptensVarenummer()
	{

		return this.receptensVarenummer;
	}


	public void setReceptensVarenummer(Long receptensVarenummer)
	{

		this.receptensVarenummer = receptensVarenummer;
	}


	public Long getNumeriskPakningsstoerrelse()
	{

		return this.numeriskPakningsstoerrelse;
	}


	public void setNumeriskPakningsstoerrelse(Long numeriskPakningsstoerrelse)
	{

		this.numeriskPakningsstoerrelse = numeriskPakningsstoerrelse;
	}


	public String getProdAlfabetiskeSekvensplads()
	{

		return this.prodAlfabetiskeSekvensplads;
	}


	public void setProdAlfabetiskeSekvensplads(String prodAlfabetiskeSekvensplads)
	{

		this.prodAlfabetiskeSekvensplads = prodAlfabetiskeSekvensplads;
	}


	public String getSubstitutionskodeForPakning()
	{

		return this.substitutionskodeForPakning;
	}


	public void setSubstitutionskodeForPakning(String substitutionskodeForPakning)
	{

		this.substitutionskodeForPakning = substitutionskodeForPakning;
	}


	public Long getBilligsteVarenummer()
	{

		return this.billigsteVarenummer;
	}


	public void setBilligsteVarenummer(Long billigsteVarenummer)
	{

		this.billigsteVarenummer = billigsteVarenummer;
	}


	@Override
	public Long getKey()
	{

		return receptensVarenummer;
	}

}
