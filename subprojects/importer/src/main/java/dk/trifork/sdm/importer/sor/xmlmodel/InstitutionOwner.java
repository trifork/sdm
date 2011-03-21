package dk.trifork.sdm.importer.sor.xmlmodel;

import java.util.Calendar;


public 	class InstitutionOwner extends AddressInformation{
	private Long sorIdentifier;
	private String entityName;
	private Calendar fromDate;
	private Calendar toDate;
	
	public Long getSorIdentifier() {
		return sorIdentifier;
	}
	public void setSorIdentifier(Long sorIdentifier) {
		this.sorIdentifier = sorIdentifier;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public Calendar getFromDate() {
		return fromDate;
	}
	public void setFromDate(Calendar validFrom) {
		this.fromDate = validFrom;
	}
	public Calendar getToDate() {
		return toDate;
	}
	public void setToDate(Calendar toDate) {
		this.toDate = toDate;
	}
}
