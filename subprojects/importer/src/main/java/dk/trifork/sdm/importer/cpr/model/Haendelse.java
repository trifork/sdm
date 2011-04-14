package dk.trifork.sdm.importer.cpr.model;

import java.util.Date;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

public class Haendelse extends CPREntity {
	String uuid;
	Date ajourfoeringsdato;
	String haendelseskode;
	String afledtMarkering;
	String noeglekonstant;

	@Id
	@Output
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	@Output
	public String getCpr() {
		return cpr;
	}
	
	@Output
	public Date getAjourfoeringsdato() {
		return ajourfoeringsdato;
	}

	public void setAjourfoeringsdato(Date ajourfoeringsdato) {
		this.ajourfoeringsdato = ajourfoeringsdato;
	}

	@Output
	public String getHaendelseskode() {
		return haendelseskode;
	}

	public void setHaendelseskode(String haendelseskode) {
		this.haendelseskode = haendelseskode;
	}
	
	@Output
	public String getAfledtMarkering() {
		return afledtMarkering;
	}

	public void setAfledtMarkering(String afledtMarkering) {
		this.afledtMarkering = afledtMarkering;
	}

	@Output
	public String getNoeglekonstant() {
		return noeglekonstant;
	}

	public void setNoeglekonstant(String noeglekonstant) {
		this.noeglekonstant = noeglekonstant;
	}
}
