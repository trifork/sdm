package dk.trifork.sdm.importer.sor.xmlmodel;

import java.util.Calendar;

public 	class HealthInstitution extends AddressInformation {
	private Long sorIdentifier;
	private String entityName;
	private Long institutionType;
	private String pharmacyIdentifier;
	private String shakIdentifier;
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
	public Long getInstitutionType() {
		return institutionType;
	}
	public void setInstitutionType(Long institutionType) {
		this.institutionType = institutionType;
	}
	public String getPharmacyIdentifier() {
		return pharmacyIdentifier;
	}
	public void setPharmacyIdentifier(String pharmacyIdentifier) {
		this.pharmacyIdentifier = pharmacyIdentifier;
	}
	public String getShakIdentifier() {
		return shakIdentifier;
	}
	public void setShakIdentifier(String shakIdentifier) {
		this.shakIdentifier = shakIdentifier;
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

