package dk.trifork.sdm.importer.sks.model;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

import java.util.Calendar;

public class Organisation extends AbstractStamdataEntity {
    private Calendar validFrom;
    private Calendar validTo;
    private String navn;
    private String nummer;

    public enum Organisationstype {
        Afdeling, Sygehus
    }


    private final Organisationstype organisationstype;


    public Organisation(Organisationstype organisationstype) {
        this.organisationstype = organisationstype;
    }

    public Calendar getValidTo() {
        return validTo;
    }


    public void setValidTo(Calendar validTo) {
        this.validTo = validTo;
    }


    @Output
    public String getNavn() {
        return navn;
    }


    public void setNavn(String navn) {
        this.navn = navn;
    }


    @Id
    @Output
    public String getNummer() {
        return nummer;
    }


    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    @Output
    public String getOrganisationstype() {
        if (organisationstype == Organisationstype.Afdeling)
            return "Afdeling";
        else if (organisationstype == Organisationstype.Sygehus)
            return "Sygehus";
        return null;
    }


    public void setValidFrom(Calendar validFrom) {
        this.validFrom = validFrom;
    }


    @Override
    public Calendar getValidFrom() {
        return validFrom;
    }


}
