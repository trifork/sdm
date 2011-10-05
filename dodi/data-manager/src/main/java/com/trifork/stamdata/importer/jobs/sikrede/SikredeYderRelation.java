/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata.importer.jobs.sikrede;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "SikredeYderRelation")
public class SikredeYderRelation extends CPREntity
{
	protected String ydernummer;
	protected Date ydernummerIkraftDato; // assigned from.
	protected Date ydernummerUdlobDato; // assigned to (optional)
	protected Date ydernummerRegistreringDato;
	protected String sikringsgruppeKode;
	protected Date gruppeKodeIkraftDato;
	protected Date gruppekodeRegistreringDato;
	private YderType type;

	public enum YderType
	{
		current("C"), future("F"), previous("P");

		private final String code;

		private YderType(String code)
		{
			this.code = code;
		}

		public String getCode()
		{
			return code;
		}
	}

	@Id
	@Column
	public String getId()
	{
		return cpr + "-" + type.getCode();
	}

	@Column
	public String getCpr()
	{
		return cpr;
	}

	@Column
	public Date getGruppeKodeIkraftDato()
	{
		return gruppeKodeIkraftDato;
	}

	public void setGruppeKodeIkraftDato(Date gruppeKodeIkraftDato)
	{
		this.gruppeKodeIkraftDato = gruppeKodeIkraftDato;
	}

	@Column
	public Date getGruppekodeRegistreringDato()
	{
		return gruppekodeRegistreringDato;
	}

	public void setGruppekodeRegistreringDato(Date gruppekodeRegistreringDato)
	{
		this.gruppekodeRegistreringDato = gruppekodeRegistreringDato;
	}

	@Column
	public String getSikringsgruppeKode()
	{
		return sikringsgruppeKode;
	}

	public void setSikringsgruppeKode(String sikringsgruppeKode)
	{
		this.sikringsgruppeKode = sikringsgruppeKode;
	}

	@Column
	public String getYdernummer()
	{
		return ydernummer;
	}

	public void setYdernummer(String ydernummer)
	{
		this.ydernummer = ydernummer;
	}

	@Column
	public Date getYdernummerIkraftDato()
	{
		return ydernummerIkraftDato;
	}

	public void setYdernummerIkraftDato(Date ydernummerIkraftDato)
	{
		this.ydernummerIkraftDato = ydernummerIkraftDato;
	}

    @Column
    public Date getYdernummerUdlobDato() {
        return ydernummerUdlobDato;
    }

    public void setYdernummerUdlobDato(Date ydernummerUdlobDato) {
        this.ydernummerUdlobDato = ydernummerUdlobDato;
    }

    @Column
	public Date getYdernummerRegistreringDato()
	{
		return ydernummerRegistreringDato;
	}

	public void setYdernummerRegistreringDato(Date ydernummerRegistreringDato)
	{
		this.ydernummerRegistreringDato = ydernummerRegistreringDato;
	}

	public void setType(YderType type)
	{
		this.type = type;
	}

	@Column
	public String getType()
	{
		return type.getCode();
	}

   	@Override
	public Date getValidFrom()
	{
		return (ydernummerIkraftDato == null) ? super.getValidFrom() : ydernummerIkraftDato;
	}

	@Override
	public Date getValidTo()
	{
		return (ydernummerUdlobDato == null) ? super.getValidTo() : ydernummerUdlobDato;
	}

	@Override
	public String toString()
	{
		return "SikredeYderRelation{" + "gruppeKodeIkraftDato=" + gruppeKodeIkraftDato + ", ydernummer='" + ydernummer + '\'' + ", ydernummerIkraftDato=" + ydernummerIkraftDato + ", ydernummerRegistreringDato=" + ydernummerRegistreringDato + ", sikringsgruppeKode='" + sikringsgruppeKode + '\'' + ", gruppekodeRegistreringDato=" + gruppekodeRegistreringDato + ", type=" + type + '}';
	}
}
