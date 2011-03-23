package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class LaegemiddelAdministrationsvejRef extends TakstEntity {
    private long drugId;
    private String AdministrationsvejKode;


    public LaegemiddelAdministrationsvejRef(Laegemiddel lm, Administrationsvej av) {
        this.drugId = lm.getDrugid();
        this.AdministrationsvejKode = av.getKode();
    }

    @Id
    @Output
    public String getCID() {
        return drugId + "-" + AdministrationsvejKode;
    }

    @Output
    public long getDrugId() {
        return drugId;
    }

    public void setDrugId(long drugId) {
        this.drugId = drugId;
    }

    @Output
    public String getAdministrationsvejKode() {
        return AdministrationsvejKode;
    }

    public void setAdministrationsvejKode(String administrationsvejKode) {
        AdministrationsvejKode = administrationsvejKode;
    }

}
