package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output(name = "IndikationATCRef")
public class Indikationskode extends TakstEntity {

    private String aTC;        //Ref. t. LMS01
    private Long indikationskode;        //Ref. t. LMS26
    private Long drugID;        //Ref. t. LMS01, felt 01

    @Id
    @Output
    public String getCID() {
        // TODO: Get rid of this ugly calculated ID. Should be handled by the DAO
        // A calculated ID. Necessary because the DAO implementation needs a single key
        return aTC + "-" + indikationskode;
    }

    @Output
    public String getATC() {
        return this.aTC;
    }

    public void setATC(String aTC) {
        this.aTC = aTC;
    }

    @Output(name = "IndikationKode")
    public Long getIndikationskode() {
        return this.indikationskode;
    }

    public void setIndikationskode(Long indikationskode) {
        this.indikationskode = indikationskode;
    }

    @Output
    public Long getDrugID() {
        return this.drugID;
    }

    public void setDrugID(Long drugID) {
        this.drugID = drugID;
    }

}