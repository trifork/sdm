package dk.trifork.sdm.importer.yderregister.model;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;
import dk.trifork.sdm.model.StamdataEntity;
import dk.trifork.sdm.util.DateUtils;

import java.util.Calendar;
import java.util.Date;

@Output
public class YderregisterPerson extends AbstractStamdataEntity implements StamdataEntity{
	private String nummer;
	private String histIdPerson;
	private String cpr;
	private Long personrolleKode;
	private String personrolleTxt;
	private Date tilgangDato;
    private Date afgangDato;	

    
	@Id
	@Output
	public String getId() {
		return nummer + "-" + cpr;
	}

	
	@Output
    public String getNummer() {
		return nummer;
	}


	public void setNummer(String nummer) {
		this.nummer = nummer;
	}

	@Output
	public String getHistIdPerson() {
		return histIdPerson;
	}


	public void setHistIdPerson(String histIdPerson) {
		this.histIdPerson = histIdPerson;
	}


	@Output
	public String getCpr() {
		return cpr;
	}


	public void setCpr(String cpr) {
		this.cpr = cpr;
	}


	@Output
	public Long getPersonrolleKode() {
		return personrolleKode;
	}


	public void setPersonrolleKode(Long personrolleKode) {
		this.personrolleKode = personrolleKode;
	}


	@Output
	public String getPersonrolleTxt() {
		return personrolleTxt;
	}


	public void setPersonrolleTxt(String personrolleTxt) {
		this.personrolleTxt = personrolleTxt;
	}


	public Date getTilgangDato() {
		return tilgangDato;
	}

	public void setTilgangDato(Date tilgangDato) {
		this.tilgangDato = tilgangDato;
	}

	public Date getAfgangDato() {
		return afgangDato;
	}

	public void setAfgangDato(Date afgangDato) {
		this.afgangDato = afgangDato;
	}
	
	@Override
	public Calendar getValidFrom() {
		return DateUtils.toCalendar(tilgangDato);
	}

	@Override
	public Calendar getValidTo() {
		if (afgangDato != null)
			return DateUtils.toCalendar(afgangDato);
		return FUTURE;
	}

}

