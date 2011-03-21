package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Styrkeenhed extends TakstEntity {

    private final DivEnheder enheder;

    public Styrkeenhed(DivEnheder enheder) {
        this.enheder = enheder;
    }

    @Id
    @Output
    public String getStyrkeenhedKode() {
        return enheder.getKode();
    }

    @Output
    public String getStyrkeenhedTekst() {
        return enheder.getTekst();
    }

    @Output
    public String getStyrkeenhedKortTekst() {
    	return enheder.getKortTekst();
    }

    @Override
    public String getKey() {
        return enheder.getKode();
    }

}