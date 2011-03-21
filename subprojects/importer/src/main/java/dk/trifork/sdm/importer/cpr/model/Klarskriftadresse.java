package dk.trifork.sdm.importer.cpr.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output(name="Person")
public class Klarskriftadresse extends CPREntity {

	String cpr;
	String adresseringsNavn;
	String coNavn;
	String Lokalitet;
	String standardAdresse;
	String byNavn;
	Long postNummer;
	String postDistrikt;
	Long kommuneKode;
	Long vejKode;
	String husNummer;
	String etage;
	String sideDoerNummer;
	String bygningsNummer;
	String vejNavn;
	
	@Id
	@Output
	public String getCpr() {
		return cpr;
	}

	public void setCpr(String cpr) {
		this.cpr = cpr;
	}

	public String getAdresseringsNavn() {
		return adresseringsNavn;
	}

	public void setAdresseringsNavn(String adresseringsNavn) {
		this.adresseringsNavn = adresseringsNavn;
	}

	@Output
	public String getCoNavn() {
		return coNavn;
	}

	public void setCoNavn(String coNavn) {
		this.coNavn = coNavn;
	}

	@Output
	public String getLokalitet() {
		return Lokalitet;
	}

	public void setLokalitet(String lokalitet) {
		Lokalitet = lokalitet;
	}

	public String getStandardAdresse() {
		return standardAdresse;
	}

	public void setStandardAdresse(String standardAdresse) {
		this.standardAdresse = standardAdresse;
	}

	@Output
	public String getByNavn() {
		return byNavn;
	}

	public void setByNavn(String byNavn) {
		this.byNavn = byNavn;
	}

	@Output
	public Long getPostNummer() {
		return postNummer;
	}

	public void setPostNummer(Long postNummer) {
		this.postNummer = postNummer;
	}

	@Output
	public String getPostDistrikt() {
		return postDistrikt;
	}

	public void setPostDistrikt(String postDistrikt) {
		this.postDistrikt = postDistrikt;
	}

	@Output
	public Long getKommuneKode() {
		return kommuneKode;
	}

	public void setKommuneKode(Long kommuneKode) {
		this.kommuneKode = kommuneKode;
	}

	@Output
	public Long getVejKode() {
		return vejKode;
	}

	public void setVejKode(Long vejKode) {
		this.vejKode = vejKode;
	}

	@Output
	public String getHusNummer() {
		return husNummer;
	}

	public void setHusNummer(String husNummer) {
		this.husNummer = husNummer;
	}

	@Output
	public String getEtage() {
		return etage;
	}

	public void setEtage(String etage) {
		this.etage = etage;
	}

	@Output
	public String getSideDoerNummer() {
		return sideDoerNummer;
	}

	public void setSideDoerNummer(String sideDoerNummer) {
		this.sideDoerNummer = sideDoerNummer;
	}

	@Output
	public String getBygningsNummer() {
		return bygningsNummer;
	}

	public void setBygningsNummer(String bygningsNummer) {
		this.bygningsNummer = bygningsNummer;
	}

	@Output
	public String getVejNavn() {
		return vejNavn;
	}

	public void setVejNavn(String vejNavn) {
		this.vejNavn = vejNavn;
	}
}
