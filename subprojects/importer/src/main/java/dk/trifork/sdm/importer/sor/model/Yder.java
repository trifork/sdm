package dk.trifork.sdm.importer.sor.model;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;
import dk.trifork.sdm.model.StamdataEntity;

import java.util.Calendar;

@Output(name="Yder")
public class Yder extends AbstractStamdataEntity implements StamdataEntity{
	private String nummer;
	private Long eanLokationsnummer;
	private String telefon;
	private String navn;
	private String vejnavn;
	private String postnummer;
	private String bynavn;
	private String email;
	private String www;
	private Long hovedSpecialeKode;
	private String hovedSpecialeTekst;
    private Long sorNummer;
    private Long praktisSorNummer;
    private Calendar validFrom;
    private Calendar validTo;

    @Output	
	public String getNummer() {
		return nummer;
	}

	public void setNummer(String nummer) {
		this.nummer = nummer;
	}

    @Output
    public Long getEanLokationsnummer() {
		return eanLokationsnummer;
	}
	public void setEanLokationsnummer(Long eanLokationsnummer) {
		this.eanLokationsnummer = eanLokationsnummer;
	}
	
    @Output	
	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

    @Output
    public String getNavn() {
		return navn;
	}

	public void setNavn(String navn) {
		this.navn = navn;
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

    @Output
    public Long getHovedSpecialeKode() {
		return hovedSpecialeKode;
	}

	public void setHovedSpecialeKode(Long hovedSpecialeKode) {
		this.hovedSpecialeKode = hovedSpecialeKode;
	}

    @Output
    public String getHovedSpecialeTekst() {
		return hovedSpecialeTekst;
	}

	public void setHovedSpecialeTekst(String hovedSpecialeTekst) {
		this.hovedSpecialeTekst = hovedSpecialeTekst;
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
    public Long getPraksisSorNummer() {
        return praktisSorNummer;
    }


    public void setPraksisSorNummer(Long praktisSorNummer) {
        this.praktisSorNummer = praktisSorNummer;
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

