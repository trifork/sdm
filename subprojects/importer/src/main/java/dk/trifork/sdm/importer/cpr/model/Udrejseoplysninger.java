package dk.trifork.sdm.importer.cpr.model;

import java.util.Date;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Udrejseoplysninger extends CPREntity {
	String udrejseLandekode;
	Date udrejsedato;
	String udrejsedatoUsikkerhedsmarkering;
	String udlandsadresse1;
	String udlandsadresse2;
	String udlandsadresse3;
	String udlandsadresse4;
	String udlandsadresse5;

	@Id
	@Output
	public String getCpr() {
		return cpr;
	}

	@Output
	public String getUdrejseLandekode() {
		return udrejseLandekode;
	}

	public void setUdrejseLandekode(String udrejseLandekode) {
		this.udrejseLandekode = udrejseLandekode;
	}

	@Output
	public Date getUdrejsedato() {
		return udrejsedato;
	}

	public void setUdrejsedato(Date udrejsedato) {
		this.udrejsedato = udrejsedato;
	}

	@Output
	public String getUdrejsedatoUsikkerhedsmarkering() {
		return udrejsedatoUsikkerhedsmarkering;
	}

	public void setUdrejsedatoUsikkerhedsmarkering(
			String udrejsedatoUsikkerhedsmarkering) {
		this.udrejsedatoUsikkerhedsmarkering = udrejsedatoUsikkerhedsmarkering;
	}

	@Output
	public String getUdlandsadresse1() {
		return udlandsadresse1;
	}

	public void setUdlandsadresse1(String udlandsadresse1) {
		this.udlandsadresse1 = udlandsadresse1;
	}

	@Output
	public String getUdlandsadresse2() {
		return udlandsadresse2;
	}

	public void setUdlandsadresse2(String udlandsadresse2) {
		this.udlandsadresse2 = udlandsadresse2;
	}

	@Output
	public String getUdlandsadresse3() {
		return udlandsadresse3;
	}

	public void setUdlandsadresse3(String udlandsadresse3) {
		this.udlandsadresse3 = udlandsadresse3;
	}

	@Output
	public String getUdlandsadresse4() {
		return udlandsadresse4;
	}

	public void setUdlandsadresse4(String udlandsadresse4) {
		this.udlandsadresse4 = udlandsadresse4;
	}

	@Output
	public String getUdlandsadresse5() {
		return udlandsadresse5;
	}

	public void setUdlandsadresse5(String udlandsadresse5) {
		this.udlandsadresse5 = udlandsadresse5;
	}

}
