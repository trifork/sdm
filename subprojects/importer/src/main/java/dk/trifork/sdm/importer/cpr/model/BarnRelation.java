package dk.trifork.sdm.importer.cpr.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class BarnRelation extends CPREntity {

	String cpr;
	String barnCpr;
	
	@Id
	@Output
	public String getId() {
		return cpr + "-" + barnCpr;
	}
	
	@Output
	public String getCpr() {
		return cpr;
	}

	public void setCpr(String cpr) {
		this.cpr = cpr;
	}

	@Output
	public String getBarnCpr() {
		return barnCpr;
	}

	public void setBarnCpr(String barnCpr) {
		this.barnCpr = barnCpr;
	}
}
