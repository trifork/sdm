package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Indholdsstoffer extends TakstEntity {

    private Long drugID;        //Ref. t. LMS01
    private Long varenummer;        //Ref. t. LMS02
    private String stofklasse;
    private String substansgruppe;
    private String substans;        //Kun aktive substanser

    @Output
    public Long getDrugID() {
        return this.drugID;
    }

    public void setDrugID(Long drugID) {
        this.drugID = drugID;
    }

    @Output
    public Long getVarenummer() {
        return this.varenummer;
    }

    public void setVarenummer(Long varenummer) {
        this.varenummer = varenummer;
    }

    @Output
    public String getStofklasse() {
        return this.stofklasse;
    }

    public void setStofklasse(String stofklasse) {
        this.stofklasse = stofklasse;
    }

    @Output
    public String getSubstansgruppe() {
        return this.substansgruppe;
    }

    public void setSubstansgruppe(String substansgruppe) {
        this.substansgruppe = substansgruppe;
    }

    @Output
    public String getSubstans() {
        return this.substans;
    }

    public void setSubstans(String substans) {
        this.substans = substans;
    }

    @Id
    @Output(name = "CID")
    public String getKey() {
        return substans + "-" + substansgruppe + "-" + stofklasse + "-" + drugID;
    }


    public boolean equals(Object o) {
        if (o.getClass() != Indholdsstoffer.class)
            return false;
        Indholdsstoffer stof = (Indholdsstoffer) o;
        return getKey().equals(stof.getKey());

    }

}