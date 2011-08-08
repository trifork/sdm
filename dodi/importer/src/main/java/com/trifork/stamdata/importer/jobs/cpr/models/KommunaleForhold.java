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


public class KommunaleForhold extends CPREntity
{
	public enum Kommunalforholdstype
	{
		adskilt("1"),
		plejebarn("2"),
		pensionsforhold("3"),
		betalingskommunekode("4"),
		friVaerdimaengde1("5"),
		friVaerdimaengde2("6"),
		friVaerdimaengde3("7"),
		friVaerdimaengde4("8"),
		friVaerdimaengde5("9");

		private final String code;

		private Kommunalforholdstype(String kode)
		{
			this.code = kode;
		}

		public String getCode()
		{
			return code;
		}

		public static Kommunalforholdstype fromCode(String code)
		{
			for (Kommunalforholdstype forhold : values())
			{
				if (forhold.getCode().equals(code))
				{
					return forhold;
				}
			}
			throw new IllegalArgumentException("Ugyldigt kommunalt forhold: '" + code + "'");
		}
	}

	private Kommunalforholdstype kommunalforholdstype;
	private String kommunalforholdskode;
	private String startdatomarkering;
	private String bemaerkninger;
	private Date validFrom;

	@Id
	@Output
	public String getCpr()
	{
		return cpr;
	}

	public void setKommunalforholdstype(Kommunalforholdstype kommunalforholdstype)
	{
		this.kommunalforholdstype = kommunalforholdstype;
	}

	public Kommunalforholdstype getKommunalforholdstype()
	{
		return kommunalforholdstype;
	}

	public void setKommunalforholdstypekode(String kode)
	{
		this.kommunalforholdstype = Kommunalforholdstype.fromCode(kode);
	}

	@Output
	public String getKommunalforholdstypekode()
	{
		return kommunalforholdstype.getCode();
	}

	public void setKommunalforholdskode(String kommunalforholdskode)
	{
		this.kommunalforholdskode = kommunalforholdskode;
	}

	@Output
	public String getKommunalforholdskode()
	{
		return kommunalforholdskode;
	}

	public void setStartdatomarkering(String startdatomarkering)
	{
		this.startdatomarkering = startdatomarkering;
	}

	public String getStartdatomarkering()
	{
		return startdatomarkering;
	}

	public void setBemaerkninger(String bemaerkninger)
	{
		this.bemaerkninger = bemaerkninger;
	}

	@Output
	public String getBemaerkninger()
	{
		return bemaerkninger;
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
}
