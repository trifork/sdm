package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Laegemiddelnavn extends TakstEntity {

    private Long drugid;        //Ref. t. LMS01, felt 01
    private String laegemidletsUforkortedeNavn;

    @Id
    @Output
    public Long getDrugid() {
        return this.drugid;
    }

    public void setDrugid(Long drugid) {
        this.drugid = drugid;
    }

    @Output
    public String getLaegemidletsUforkortedeNavn() {
        return this.laegemidletsUforkortedeNavn;
    }

    public void setLaegemidletsUforkortedeNavn(String laegemidletsUforkortedeNavn) {
        this.laegemidletsUforkortedeNavn = laegemidletsUforkortedeNavn;
    }

    public String getKey() {
        return "" + this.drugid;
    }

}