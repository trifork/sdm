package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class SubstitutionAfLaegemidlerUdenFastPris extends TakstEntity {

    private Long substitutionsgruppenummer;        //Substitutionsgruppe for pakningen
    private Long varenummer;

    @Output
    public Long getSubstitutionsgruppenummer() {
        return this.substitutionsgruppenummer;
    }

    public void setSubstitutionsgruppenummer(Long substitutionsgruppenummer) {
        this.substitutionsgruppenummer = substitutionsgruppenummer;
    }

    @Id
    @Output
    public Long getVarenummer() {
        return this.varenummer;
    }

    public void setVarenummer(Long varenummer) {
        this.varenummer = varenummer;
    }

    public Long getKey() {
        return varenummer;
    }

}