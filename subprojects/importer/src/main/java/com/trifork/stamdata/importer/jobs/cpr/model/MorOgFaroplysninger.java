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

package com.trifork.stamdata.importer.jobs.cpr.model;

import java.util.Date;

import com.trifork.stamdata.importer.persistence.Id;
import com.trifork.stamdata.importer.persistence.Output;


public class MorOgFaroplysninger extends CPREntity
{
	public enum Foraeldertype
	{
		mor("M"), far("F");
		private final String code;

		private Foraeldertype(String code)
		{
			this.code = code;
		}

		public String getCode()
		{
			return code;
		}
	}

	Foraeldertype foraeldertype;
	Date dato;
	String datousikkerhedsmarkering;
	String foraeldercpr;
	Date foedselsdato;
	String foedselsdatousikkerhedsmarkering;
	String navn;
	String navnmarkering;

	@Id
	@Output
	public String getId()
	{
		return cpr + "-" + foraeldertype.getCode();
	}

	@Output
	public String getCpr()
	{
		return cpr;
	}

	public Foraeldertype getForaeldertype()
	{
		return foraeldertype;
	}

	public void setForaeldertype(Foraeldertype foraeldertype)
	{
		this.foraeldertype = foraeldertype;
	}

	@Output
	public String getForaelderkode()
	{
		return foraeldertype.getCode();
	}

	@Output
	public Date getDato()
	{
		return dato;
	}

	public void setDato(Date dato)
	{
		this.dato = dato;
	}

	public String getDatousikkerhedsmarkering()
	{
		return datousikkerhedsmarkering;
	}

	public void setDatousikkerhedsmarkering(String datousikkerhedsmarkering)
	{
		this.datousikkerhedsmarkering = datousikkerhedsmarkering;
	}

	public void setForaeldercpr(String foraeldercpr)
	{
		this.foraeldercpr = foraeldercpr;
	}

	public String getForaeldercpr()
	{
		return foraeldercpr;
	}

	public boolean hasCpr()
	{
		return foraeldercpr != null && !foraeldercpr.equals("0000000000");
	}

	@Output
	public Date getFoedselsdato()
	{
		return foedselsdato;
	}

	public void setFoedselsdato(Date foedselsdato)
	{
		this.foedselsdato = foedselsdato;
	}

	public String getFoedselsdatousikkerhedsmarkering()
	{
		return foedselsdatousikkerhedsmarkering;
	}

	public void setFoedselsdatousikkerhedsmarkering(String foedselsdatousikkerhedsmarkering)
	{
		this.foedselsdatousikkerhedsmarkering = foedselsdatousikkerhedsmarkering;
	}

	@Output
	public String getNavn()
	{
		return navn;
	}

	public void setNavn(String navn)
	{
		this.navn = navn;
	}

	public String getNavnmarkering()
	{
		return navnmarkering;
	}

	public void setNavnmarkering(String navnmarkering)
	{
		this.navnmarkering = navnmarkering;
	}
}
