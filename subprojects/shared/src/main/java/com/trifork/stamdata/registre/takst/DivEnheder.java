package com.trifork.stamdata.registre.takst;


/**
 * Entities of this type are output by these classes:
 * Tidsenhed, Styrkeenhed, Pakningsstoerrelsesenhed
 */
public class DivEnheder extends TakstRecord
{
	private static final long ENHEDSTYPE_TID = 1;
	private static final long ENHEDSTYPE_STYRKE = 3;
	private static final long ENHEDSTYPE_PAKNING = 4;

	private Long enhedstype; // Styrke=3, pakning=4, tid=1
	private String kode; // LMS01, felt 12 og LMS02, felt 08 og 16
	private String kortTekst;
	private String tekst;


	public boolean isEnhedstypeTid()
	{
		return this.enhedstype == ENHEDSTYPE_TID;
	}


	public boolean isEnhedstypeStyrke()
	{

		return this.enhedstype == ENHEDSTYPE_STYRKE;
	}


	public boolean isEnhedstypePakning()
	{

		return this.enhedstype == ENHEDSTYPE_PAKNING;
	}


	public void setEnhedstype(Long enhedstype)
	{

		this.enhedstype = enhedstype;
	}


	public String getKode()
	{

		return this.kode;
	}


	public void setKode(String kode)
	{

		this.kode = kode;
	}


	public String getKortTekst()
	{

		return this.kortTekst;
	}


	public void setKortTekst(String kortTekst)
	{

		this.kortTekst = kortTekst;
	}


	public String getTekst()
	{

		return this.tekst;
	}


	public void setTekst(String tekst)
	{

		this.tekst = tekst;
	}


	@Override
	public String getKey()
	{

		/*
		 * This is a pseudo table that is not referenced or persisted The 'kode' is not unique alone
		 * because the different 'Enhed' can have the same code
		 */
		return enhedstype + kode;
	}
}
