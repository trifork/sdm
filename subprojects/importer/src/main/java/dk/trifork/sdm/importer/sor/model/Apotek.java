package dk.trifork.sdm.importer.sor.model;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

import java.util.Calendar;

@Output(name="Apotek")
public class Apotek extends AbstractStamdataEntity {

	private Long sorNummer;
	private Long apotekNummer;  
	private Long filialNummer; 
	private Long eanLokationsnummer;
	private Long cvr;
	private Long pcvr;
    private String navn;
	private String telefon;
	private String vejnavn;
	private String postnummer;
	private String bynavn;
	private String email;
	private String www;
    private Calendar validFrom;
    private Calendar validTo;

    public Apotek() {
	}

    @Id
    @Output
	public Long getSorNummer() {
		return sorNummer;
	}


	public void setSorNummer(Long sorNummer) {
		this.sorNummer = sorNummer;
	}


    @Output
	public Long getApotekNummer() {
		return apotekNummer;
	}


	public void setApotekNummer(Long apotekNummer) {
		this.apotekNummer = apotekNummer;
	}
	
    @Output
    public Long getFilialNummer() {
		return filialNummer;
	}

	public void setFilialNummer(Long filialNummer) {
		this.filialNummer = filialNummer;
	}

	@Output
	public Long getEanLokationsnummer() {
		return eanLokationsnummer;
	}


	public void setEanLokationsnummer(Long eanLokationsnummer) {
		this.eanLokationsnummer = eanLokationsnummer;
	}


    @Output
	public Long getCvr() {
		return cvr;
	}


	public void setCvr(Long cvr) {
		this.cvr = cvr;
	}


    @Output
	public Long getPcvr() {
		return pcvr;
	}


	public void setPcvr(Long pcvr) {
		this.pcvr = pcvr;
	}


    @Output
	public String getNavn() {
		return navn;
	}


	public void setNavn(String navn) {
		this.navn = navn;
	}


    @Output
	public String getTelefon() {
		return telefon;
	}


	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}


    @Output
	public String getVejnavn() {
		return vejnavn;
	}


	public void setVejnavn(String vejnavn) {
		this.vejnavn = vejnavn;
	}


    @Output
	public String getPostnummer() {
		return postnummer;
	}


	public void setPostnummer(String postnummer) {
		this.postnummer = postnummer;
	}


    @Output
	public String getBynavn() {
		return bynavn;
	}


	public void setBynavn(String bynavn) {
		this.bynavn = bynavn;
	}


    @Output
	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


    @Output
	public String getWww() {
		return www;
	}


	public void setWww(String www) {
		this.www = www;
	}


	@Override
    public Calendar getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Calendar validFrom) {
        this.validFrom = validFrom;
    }
    
    @Override
    public Calendar getValidTo() {
        return (validTo != null) ? validTo : FUTURE;
    }

    public void setValidTo(Calendar validTo) {
        this.validTo = validTo;
    }

}
