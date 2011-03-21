package dk.trifork.sdm.importer.sor;

import dk.trifork.sdm.importer.sor.model.*;
import dk.trifork.sdm.model.CompleteDataset;

public class SORDataSets {
	private CompleteDataset<Apotek> apotekDS;
	private CompleteDataset<Yder> yderDS;
	private CompleteDataset<Praksis> praksisDS;
	private CompleteDataset<Sygehus> sygehusDS;
	private CompleteDataset<SygehusAfdeling> sygehusAfdelingDS;
	
	public CompleteDataset<Apotek> getApotekDS() {
		return apotekDS;
	}
	public void setApotekDS(CompleteDataset<Apotek> apotekDS) {
		this.apotekDS = apotekDS;
	}
	public CompleteDataset<Yder> getYderDS() {
		return yderDS;
	}
	public void setYderDS(CompleteDataset<Yder> yderDS) {
		this.yderDS = yderDS;
	}
	public CompleteDataset<Praksis> getPraksisDS() {
		return praksisDS;
	}
	public void setPraksisDS(CompleteDataset<Praksis> praksisDS) {
		this.praksisDS = praksisDS;
	}
	public CompleteDataset<Sygehus> getSygehusDS() {
		return sygehusDS;
	}
	public void setSygehusDS(CompleteDataset<Sygehus> sygehusDS) {
		this.sygehusDS = sygehusDS;
	}
	public CompleteDataset<SygehusAfdeling> getSygehusAfdelingDS() {
		return sygehusAfdelingDS;
	}
	public void setSygehusAfdelingDS(
			CompleteDataset<SygehusAfdeling> sygehusAfdelingDS) {
		this.sygehusAfdelingDS = sygehusAfdelingDS;
	}
	


}
