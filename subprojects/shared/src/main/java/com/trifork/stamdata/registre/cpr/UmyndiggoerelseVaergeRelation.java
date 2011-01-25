package com.trifork.stamdata.registre.cpr;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class UmyndiggoerelseVaergeRelation extends CPRRecord
{

	public enum VaergeRelationType
	{
		ikkeICPR, CPRFindes, adresseFindes
	}


	Date umyndigStartDato;
	Date umyndigSletteDato;
	String umyndigStartDatoMarkering;

	String typeKode;

	VaergeRelationType type;

	String relationCpr;
	Date relationCprStartDato;

	String vaergesNavn;
	Date vaergesNavnStartDato;

	String relationsTekst1;
	String relationsTekst2;
	String relationsTekst3;
	String relationsTekst4;
	String relationsTekst5;


	@Id
	@Column
	public String getId()
	{

		return getCpr() + "-" + typeKode;
	}


	@Column
	@Override
	public String getCpr()
	{

		return super.getCpr();
	}


	public Date getUmyndigStartDato()
	{

		return umyndigStartDato;
	}


	public void setUmyndigStartDato(Date umyndigStartDato)
	{

		this.umyndigStartDato = umyndigStartDato;
	}


	public String getUmyndigStartDatoMarkering()
	{

		return umyndigStartDatoMarkering;
	}


	public void setUmyndigStartDatoMarkering(String umyndigStartDatoMarkering)
	{

		this.umyndigStartDatoMarkering = umyndigStartDatoMarkering;
	}


	public Date getUmyndigSletteDato()
	{

		return umyndigSletteDato;
	}


	public void setUmyndigSletteDato(Date umyndigSletteDato)
	{

		this.umyndigSletteDato = umyndigSletteDato;
	}


	@Column
	public String getTypeTekst()
	{

		if (type == null)
			return "Ukendt værge relation";
		else if (type == VaergeRelationType.ikkeICPR)
			return "Værge findes ikke i CPR";
		else if (type == VaergeRelationType.CPRFindes)
			return "Værges CPR findes";
		else if (type == VaergeRelationType.adresseFindes) return "Værges adresse findes";
		return null;
	}


	@Column
	public String getTypeKode()
	{

		return typeKode;
	}


	public void setType(String type)
	{

		if (type.equals("0000"))
			this.type = VaergeRelationType.ikkeICPR;
		else if (type.equals("0001"))
			this.type = VaergeRelationType.CPRFindes;
		else if (type.equals("0002"))
			this.type = VaergeRelationType.adresseFindes;
		else
			this.type = null;

		this.typeKode = type;
	}


	@Column
	public String getRelationCpr()
	{

		return relationCpr;
	}


	public void setRelationCpr(String relationCpr)
	{

		this.relationCpr = relationCpr;
	}


	@Column
	public Date getRelationCprStartDato()
	{

		return relationCprStartDato;
	}


	public void setRelationCprStartDato(Date relationCprStartDato)
	{

		this.relationCprStartDato = relationCprStartDato;
	}


	@Column
	public String getVaergesNavn()
	{

		return vaergesNavn;
	}


	public void setVaergesNavn(String vaergesNavn)
	{

		this.vaergesNavn = vaergesNavn;
	}


	@Column
	public Date getVaergesNavnStartDato()
	{

		return vaergesNavnStartDato;
	}


	public void setVaergesNavnStartDato(Date vaergesNavnStartDato)
	{

		this.vaergesNavnStartDato = vaergesNavnStartDato;
	}


	@Column
	public String getRelationsTekst1()
	{

		return relationsTekst1;
	}


	public void setRelationsTekst1(String relationsTekst1)
	{

		this.relationsTekst1 = relationsTekst1;
	}


	@Column
	public String getRelationsTekst2()
	{

		return relationsTekst2;
	}


	public void setRelationsTekst2(String relationsTekst2)
	{

		this.relationsTekst2 = relationsTekst2;
	}


	@Column
	public String getRelationsTekst3()
	{

		return relationsTekst3;
	}


	public void setRelationsTekst3(String relationsTekst3)
	{

		this.relationsTekst3 = relationsTekst3;
	}


	@Column
	public String getRelationsTekst4()
	{

		return relationsTekst4;
	}


	public void setRelationsTekst4(String relationsTekst4)
	{

		this.relationsTekst4 = relationsTekst4;
	}


	@Column
	public String getRelationsTekst5()
	{

		return relationsTekst5;
	}


	public void setRelationsTekst5(String relationsTekst5)
	{

		this.relationsTekst5 = relationsTekst5;
	}


	@Override
	public Date getValidFrom()
	{
		// Hvis umyndiggørelses start dato er sat til senere end
		// produktionsdatoen for udtrækket brug det ellers brug
		// produktionsdatoen.

		Date validFrom;

		if (umyndigStartDato == null)
		{

			validFrom = super.getValidFrom();
		}
		else if (umyndigStartDato.getTime() > super.getValidFrom().getTime())
		{

			validFrom = umyndigStartDato;
		}
		else
		{
			validFrom = super.getValidFrom();
		}

		return validFrom;
	}


	@Override
	public Date getValidTo()
	{
		return (umyndigSletteDato == null) ? super.getValidTo() : umyndigSletteDato;
	}
}
