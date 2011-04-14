package dk.trifork.sdm.importer.cpr.model;

import java.util.Date;

import dk.trifork.sdm.model.Output;

public class Haendelse extends CPREntity {
	Date ajourfoeringsdato;
	String haendelseskode;
	String afledtMarkering;
	String noeglekonstant;
	
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
