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

import com.trifork.stamdata.importer.persistence.Id;
import com.trifork.stamdata.importer.persistence.Output;
import com.trifork.stamdata.importer.util.DateUtils;


public class Valgoplysninger extends CPREntity
{
	private Valgret valgret;
	private Date valgretsdato;
	private Date validFrom;
	private Date validTo;


	public enum Valgret
	{
		ukendt(""), almindeligValgret("1"), diplomatDerStemmerIKøbenhavn("2"), diplomatOptagetPaaValglisteITidligereBopaelskommune("3"), euValgJa("4"), euValgNej("5"), euValgKoebenhavn("6");

		private final String code;

		private Valgret(String code)
		{

			this.code = code;
		}

		public String getCode()
		{

			return code;
		}

		public static Valgret fromCode(String code)
		{

			for (Valgret valgret : values())
			{
				if (valgret.getCode().equals(code))
				{
					return valgret;
				}
			}
			throw new IllegalArgumentException("Ugyldig valgret: '" + code + "'");
		}
	}

	@Id
	@Output
	public String getCpr()
	{

		return cpr;
	}

	public Valgret getValgret()
	{

		return valgret;
	}

	public void setValgret(Valgret valgret)
	{

		this.valgret = valgret;
	}

	@Output
	public String getValgkode()
	{

		return valgret.getCode();
	}

	public void setValgkode(String valgkode)
	{

		this.valgret = Valgret.fromCode(valgkode);
	}

	@Output
	public Date getValgretsdato()
	{

		return valgretsdato;
	}

	public void setValgretsdato(Date valgretsdato)
	{

		this.valgretsdato = valgretsdato;
	}

	@Override
	public Date getValidFrom()
	{
		return validFrom;
	}

	public void setValidFrom(Date validFrom)
	{
		this.validFrom = validFrom;
	}

	@Override
	public Date getValidTo()
	{
		return validTo;
	}

	public void setValidTo(Date validTo)
	{
		if (validTo == null)
		{
			this.validTo = DateUtils.FUTURE;
		}
		else
		{
			this.validTo = validTo;
		}
	}
}
