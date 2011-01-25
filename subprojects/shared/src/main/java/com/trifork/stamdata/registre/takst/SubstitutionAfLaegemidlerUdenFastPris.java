package com.trifork.stamdata.registre.takst;


public class SubstitutionAfLaegemidlerUdenFastPris extends TakstRecord
{

	private Long substitutionsgruppenummer; // Substitutionsgruppe for pakningen
	private Long varenummer;


	public Long getSubstitutionsgruppenummer()
	{

		return this.substitutionsgruppenummer;
	}


	public void setSubstitutionsgruppenummer(Long substitutionsgruppenummer)
	{

		this.substitutionsgruppenummer = substitutionsgruppenummer;
	}


	public Long getVarenummer()
	{

		return this.varenummer;
	}


	public void setVarenummer(Long varenummer)
	{

		this.varenummer = varenummer;
	}


	@Override
	public Long getKey()
	{

		return varenummer;
	}

}
