package dk.trifork.sdm.importer.cpr.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

public class Foedselsregistreringsoplysninger extends CPREntity {

	private String foedselsregistreringsstedkode;
	private String foedselsregistreringstekst;
	@Id
	@Output
	public String getCpr() {
		return cpr;
	}

	@Output
	public String getFoedselsregistreringsstedkode() {
		return foedselsregistreringsstedkode;
	}
	public void setFoedselsregistreringsstedkode(
			String foedselsregistreringsstedkode) {
		this.foedselsregistreringsstedkode = foedselsregistreringsstedkode;
	}

	@Output
	public String getFoedselsregistreringstekst() {
		return foedselsregistreringstekst;
	}
	public void setFoedselsregistreringstekst(String foedselsregistreringstekst) {
		this.foedselsregistreringstekst = foedselsregistreringstekst;
	}
}
