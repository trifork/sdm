package com.trifork.stamdata.registre.cpr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "Person")
public class Personoplysninger extends CPRRecord {
	String gaeldendeCpr;
	String status;
	Date statusDato;
	String statusMakering;
	String koen;
	Date foedselsdato;
	String foedselsdatoMarkering;
	Date startDato;
	String startDatoMarkering;
	Date slutDato;
	String slutDatoMarkering;
	String stilling;


	@Id
	@Column
	@Override
	public String getCpr() {

		return super.getCpr();
	}


	@Column
	public String getGaeldendeCpr() {

		return gaeldendeCpr;
	}


	public void setGaeldendeCpr(String gaeldendeCpr) {

		this.gaeldendeCpr = gaeldendeCpr;
	}


	@Column
	public String getStatus() {

		return status;
	}


	public void setStatus(String status) {

		this.status = status;
	}


	public Date getStatusDato() {

		return statusDato;
	}


	public void setStatusDato(Date statusDato) {

		this.statusDato = statusDato;
	}


	public String getStatusMakering() {

		return statusMakering;
	}


	public void setStatusMakering(String statusMakering) {

		this.statusMakering = statusMakering;
	}


	@Column
	public String getKoen() {

		return koen;
	}


	public void setKoen(String koen) {

		this.koen = koen;
	}


	@Column
	public Date getFoedselsdato() {

		return foedselsdato;
	}


	public void setFoedselsdato(Date foedselsdato) {

		this.foedselsdato = foedselsdato;
	}


	public String getFoedselsdatoMarkering() {

		return foedselsdatoMarkering;
	}


	public void setFoedselsdatoMarkering(String foedselsdatoMarkering) {

		this.foedselsdatoMarkering = foedselsdatoMarkering;
	}


	public Date getStartDato() {

		return startDato;
	}


	public void setStartDato(Date startDato) {

		this.startDato = startDato;
	}


	public String getStartDatoMarkering() {

		return startDatoMarkering;
	}


	public void setStartDatoMarkering(String startDatoMarkering) {

		this.startDatoMarkering = startDatoMarkering;
	}


	public Date getSlutDato() {

		return slutDato;
	}


	public void setSlutdato(Date slutDato) {

		this.slutDato = slutDato;
	}


	public String getSlutDatoMarkering() {

		return slutDatoMarkering;
	}


	public void setSlutDatoMarkering(String slutDatoMarkering) {

		this.slutDatoMarkering = slutDatoMarkering;
	}


	@Column
	public String getStilling() {

		return stilling;
	}


	public void setStilling(String stilling) {

		this.stilling = stilling;
	}
}
