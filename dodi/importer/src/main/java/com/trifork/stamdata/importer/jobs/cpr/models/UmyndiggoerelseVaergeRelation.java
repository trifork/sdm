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

package com.trifork.stamdata.importer.jobs.cpr.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;



@Entity
public class UmyndiggoerelseVaergeRelation extends CPREntity
{
	public enum VaergeRelationType
	{
		ikkeICPR, CPRFindes, adresseFindes
	}

	String cpr;
	Date umyndigStartDato;
	String umyndigStartDatoMarkering;
	Date umyndigSletteDato;
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
		return cpr + "-" + typeKode;
	}

	@Column
	public String getCpr()
	{

		return cpr;
	}

	public void setCpr(String cpr)
	{
		this.cpr = cpr;
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
		return (umyndigStartDato == null) ? super.getValidFrom() : umyndigStartDato;
	}

	@Override
	public Date getValidTo()
	{
		return (umyndigSletteDato == null) ? super.getValidTo() : umyndigSletteDato;
	}
}
