package dk.trifork.sdm.importer.sor.xmlmodel;

import java.util.ArrayList;
import java.util.List;


public class InstitutionOwnerEntity extends InstitutionOwner{
	private Long RegionCode;
	private List<HealthInstitutionEntity> healthInstitutionEntities;

	public InstitutionOwnerEntity() {
		super();
		this.healthInstitutionEntities = new ArrayList<HealthInstitutionEntity>();
	}
	public Long getRegionCode() {
		return RegionCode;
	}
	public void setRegionCode(Long regionCode) {
		RegionCode = regionCode;
	}
	public List<HealthInstitutionEntity> getHealthInstitutionEntity() {
		return healthInstitutionEntities;
	}
	public void setHealthInstitutionEntity(
			HealthInstitutionEntity healthInstitutionEntity) {
		healthInstitutionEntity.setInstitutionOwnerEntity(this);
		this.healthInstitutionEntities.add(healthInstitutionEntity);
		
	}
	
}

