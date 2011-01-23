package com.trifork.stamdata.registre.cpr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "ForaeldreMyndighedRelation")
public class Foraeldremyndighedsrelation extends CPRRecord {

	public enum ForaeldreMyndighedRelationsType {
		MOTHER, FARTHER, OTHER_RELATIONSHIP_1, OTHER_RELATIONSHIP_2
	}

	String typeKode;
	ForaeldreMyndighedRelationsType type;
	Date foraeldreMyndighedStartDato;
	String foraeldreMyndighedMarkering;
	Date foraeldreMyndighedSlettedato;

	// If the relation is neither Mother or Farther.
	String relationCpr;

	Date relationCprStartDato;


	@Id
	@Column
	public String getId() {

		if (type == ForaeldreMyndighedRelationsType.MOTHER) {
			return getCpr() + "-mor";
		}
		else if (type == ForaeldreMyndighedRelationsType.FARTHER) {
			return getCpr() + "-far";
		}
		return getCpr() + "-" + relationCpr;
	}


	@Column
	@Override
	public String getCpr() {

		return super.getCpr();
	}


	@Column
	public String getTypeTekst() {

		if (type == null)
			return "Ukendt forældre myndigheds relation";
		else if (type == ForaeldreMyndighedRelationsType.MOTHER)
			return "Mor";
		else if (type == ForaeldreMyndighedRelationsType.FARTHER)
			return "Far";
		else if (type == ForaeldreMyndighedRelationsType.OTHER_RELATIONSHIP_1)
			return "Anden indenhaver 1";
		else if (type == ForaeldreMyndighedRelationsType.OTHER_RELATIONSHIP_2) return "Anden indenhaver 1";
		return null;
	}


	@Column
	public String getTypeKode() {

		return typeKode;
	}


	public void setType(String type) {

		if (type.equals("0003"))
			this.type = ForaeldreMyndighedRelationsType.MOTHER;
		else if (type.equals("0004"))
			this.type = ForaeldreMyndighedRelationsType.FARTHER;
		else if (type.equals("0005"))
			this.type = ForaeldreMyndighedRelationsType.OTHER_RELATIONSHIP_1;
		else if (type.equals("0006"))
			this.type = ForaeldreMyndighedRelationsType.OTHER_RELATIONSHIP_2;
		else
			this.type = null;

		this.typeKode = type;
	}


	public Date getForaeldreMyndighedStartDato() {

		return foraeldreMyndighedStartDato;
	}


	public void setForaeldreMyndighedStartDato(Date foraeldreMyndighedStartDato) {

		this.foraeldreMyndighedStartDato = foraeldreMyndighedStartDato;
	}


	public String getForaeldreMyndighedMarkering() {

		return foraeldreMyndighedMarkering;
	}


	public void setForaeldreMyndighedMarkering(String foraeldreMyndighedMarkering) {

		this.foraeldreMyndighedMarkering = foraeldreMyndighedMarkering;
	}


	public Date getForaeldreMyndighedSlettedato() {

		return foraeldreMyndighedSlettedato;
	}


	public void setForaeldreMyndighedSlettedato(Date foraeldreMyndighedSlettedato) {

		this.foraeldreMyndighedSlettedato = foraeldreMyndighedSlettedato;
	}


	@Column
	public String getRelationCpr() {

		return relationCpr;
	}


	public void setRelationCpr(String relationCpr) {

		this.relationCpr = relationCpr;
	}


	public Date getRelationCprStartDato() {

		return relationCprStartDato;
	}


	public void setRelationCprStartDato(Date relationCprStartDato) {

		this.relationCprStartDato = relationCprStartDato;
	}


	@Override
	public Date getValidFrom() {

		return (foraeldreMyndighedStartDato == null) ? super.getValidFrom() : foraeldreMyndighedStartDato;
	}


	@Override
	public Date getValidTo() {

		return (foraeldreMyndighedSlettedato == null) ? super.getValidTo() : foraeldreMyndighedSlettedato;
	}

}
