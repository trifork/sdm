package dk.trifork.sdm.importer.sor.xmlmodel;

import java.util.ArrayList;
import java.util.List;

public 	class OrganizationalUnitEntity extends OrganizationalUnit {
	private HealthInstitutionEntity healthInstitutionEntity;
	private HealthInstitutionEntity belongsTo;
	private OrganizationalUnitEntity parrent;
	private List<OrganizationalUnitEntity> sons;

	public OrganizationalUnitEntity(OrganizationalUnitEntity parrent) {
		super();
		sons = new ArrayList<OrganizationalUnitEntity>();
		setParrent(parrent);
	}

	public OrganizationalUnitEntity getParrent() {
		return parrent;
	}

	public void setParrent(OrganizationalUnitEntity parrent) {
		if (parrent != null) parrent.setSon(this);
		this.parrent = parrent;
	}

	public HealthInstitutionEntity getHealthInstitutionEntity() {
		return healthInstitutionEntity;
	}

	public void setHealthInstitutionEntity(
			HealthInstitutionEntity healthInstitutionEntity) {
		this.healthInstitutionEntity = healthInstitutionEntity;
	}
	
	
	public HealthInstitutionEntity getBelongsTo() {
		return belongsTo;
	}

	public void setBelongsTo(HealthInstitutionEntity belongsTo) {
		this.belongsTo = belongsTo;
	}

	public void setSons(List<OrganizationalUnitEntity> sons) {
		this.sons = sons;
	}

	public List<OrganizationalUnitEntity> getSons() {
		return sons;
	}

	public void setSon(OrganizationalUnitEntity son) {
		sons.add(son);
	}
}

