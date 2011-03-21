package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Firma extends TakstEntity {

    private Long firmanummer;        //Ref. t. LMS01, felt 13 - 14
    private String firmamaerkeKort;        //P.t. tomt
    private String firmamaerkeLangtNavn;
    private String parallelimportoerKode;

    @Id
    @Output
    public Long getFirmanummer() {
        return this.firmanummer;
    }

    public void setFirmanummer(Long firmanummer) {
        this.firmanummer = firmanummer;
    }

    @Output
    public String getFirmamaerkeKort() {
        return this.firmamaerkeKort;
    }

    public void setFirmamaerkeKort(String firmamaerkeKort) {
        this.firmamaerkeKort = firmamaerkeKort;
    }

    @Output
    public String getFirmamaerkeLangtNavn() {
        return this.firmamaerkeLangtNavn;
    }

    public void setFirmamaerkeLangtNavn(String firmamaerkeLangtNavn) {
        this.firmamaerkeLangtNavn = firmamaerkeLangtNavn;
    }

    @Output
    public String getParallelimportoerKode() {
        return this.parallelimportoerKode;
    }

    public void setParallelimportoerKode(String parallelimportoerKode) {
        this.parallelimportoerKode = parallelimportoerKode;
    }

    public Long getKey() {
        return this.firmanummer;
    }

}