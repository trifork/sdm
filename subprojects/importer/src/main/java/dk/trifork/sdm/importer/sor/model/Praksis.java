package dk.trifork.sdm.importer.sor.model;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

import java.util.Calendar;

@Output
public class Praksis extends AbstractStamdataEntity {
    private Calendar validFrom;
    private String navn;
    private Long eanLokationsnummer;
    private Long regionCode;
    private Long sorNummer;
    private Calendar validTo;
   

    public Praksis() {
    }

    @Output
    public String getNavn() {
        return navn;
    }
    public void setNavn(String navn) {
        this.navn = navn;
    }

    @Output
    public Long getEanLokationsnummer() {
		return eanLokationsnummer;
	}
	public void setEanLokationsnummer(Long eanLokationsnummer) {
		this.eanLokationsnummer = eanLokationsnummer;
	}
	

    @Output
    public Long getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(Long regionCode) {
		this.regionCode = regionCode;
	}

	@Id
    @Output
    public Long getSorNummer() {
        return sorNummer;
    }
    public void setSorNummer(Long sorNummer) {
        this.sorNummer = sorNummer;
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
