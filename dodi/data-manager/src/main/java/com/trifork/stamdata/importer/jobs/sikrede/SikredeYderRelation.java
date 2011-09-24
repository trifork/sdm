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
	public String toString()
	{
		return "SikredeYderRelation{" + "gruppeKodeIkraftDato=" + gruppeKodeIkraftDato + ", ydernummer='" + ydernummer + '\'' + ", ydernummerIkraftDato=" + ydernummerIkraftDato + ", ydernummerRegistreringDato=" + ydernummerRegistreringDato + ", sikringsgruppeKode='" + sikringsgruppeKode + '\'' + ", gruppekodeRegistreringDato=" + gruppekodeRegistreringDato + ", type=" + type + '}';
	}
}
