package dk.trifork.sdm.importer.sor.xmlmodel;

import java.util.ArrayList;
import java.util.List;

public class HealthInstitutionEntity extends HealthInstitution {
	private InstitutionOwnerEntity institutionOwnerEntity;
	private List<OrganizationalUnitEntity> organizationalUnitEntities;
	
	public HealthInstitutionEntity() {
		organizationalUnitEntities = new ArrayList<OrganizationalUnitEntity>(); 
	}
	
	public List<OrganizationalUnitEntity> getOrganizationalUnitEntities() {
		return organizationalUnitEntities;
	}
	public void setOrganizationalUnitEntity(
			OrganizationalUnitEntity organizationalUnitEntity) {
		this.organizationalUnitEntities.add(organizationalUnitEntity);
	}
	
	public InstitutionOwnerEntity getInstitutionOwnerEntity() {
		return institutionOwnerEntity;
	}
	
	public void setInstitutionOwnerEntity(
			InstitutionOwnerEntity institutionOwnerEntity) {
		this.institutionOwnerEntity = institutionOwnerEntity;
	}
}
