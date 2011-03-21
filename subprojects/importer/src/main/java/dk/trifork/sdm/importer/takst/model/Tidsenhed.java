package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Tidsenhed extends TakstEntity {

    private final DivEnheder enheder;

    public Tidsenhed(DivEnheder enheder) {
        this.enheder = enheder;
    }

    @Id
    @Output
    public String getTidsenhedKode() {
        return enheder.getKode();
    }

    @Output
    public String getTidsenhedTekst() {
        return enheder.getTekst();
    }

    @Output
    public String getTidsenhedKortTekst() {
    	return enheder.getKortTekst();
    }

    @Override
    public String getKey() {
        return enheder.getKode();
    }

}
