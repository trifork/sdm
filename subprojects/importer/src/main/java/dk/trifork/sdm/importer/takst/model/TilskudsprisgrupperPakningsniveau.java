package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class TilskudsprisgrupperPakningsniveau extends TakstEntity {

    private Long tilskudsprisGruppe;
    private Long varenummer;        //Ref. t. LMS02

    @Output
    public Long getTilskudsprisGruppe() {
        return this.tilskudsprisGruppe;
    }

    public void setTilskudsprisGruppe(Long tilskudsprisGruppe) {
        this.tilskudsprisGruppe = tilskudsprisGruppe;
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