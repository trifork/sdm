// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.parsers.cpr.model;

import java.util.Date;

import com.trifork.stamdata.importer.model.Id;
import com.trifork.stamdata.importer.model.Output;
import com.trifork.stamdata.importer.util.DateUtils;


@Output
public class ForaeldreMyndighedRelation extends CPREntity
{

	public enum ForaeldreMyndighedRelationsType
	{
		mor, far, andenIndehaver1, andenIndehaver2
	}

	String cpr;
	String typeKode;
	ForaeldreMyndighedRelationsType type;
	Date foraeldreMyndighedStartDato;
	String foraeldreMyndighedMarkering;
	Date foraeldreMyndighedSlettedato;
	String relationCpr; // Hvis relationstypen ikke er mor eller far
	Date relationCprStartDato;

	@Id
	@Output
	public String getId()
	{

		if (type == ForaeldreMyndighedRelationsType.mor)
			return cpr + "-mor";
		else if (type == ForaeldreMyndighedRelationsType.far) return cpr + "-far";
		return cpr + "-" + relationCpr;
	}

	@Output
	public String getCpr()
	{

		return cpr;
	}

	public void setCpr(String cpr)
	{

		this.cpr = cpr;
	}

	@Output
	public String getTypeTekst()
	{

		if (type == null)
			return "Ukendt forældre myndigheds relation";
		else if (type == ForaeldreMyndighedRelationsType.mor)
			return "Mor";
		else if (type == ForaeldreMyndighedRelationsType.far)
			return "Far";
		else if (type == ForaeldreMyndighedRelationsType.andenIndehaver1)
			return "Anden indenhaver 1";
		else if (type == ForaeldreMyndighedRelationsType.andenIndehaver2) return "Anden indenhaver 1";
		return null;
	}

	@Output
	public String getTypeKode()
	{

		return typeKode;
	}

	public void setType(String type)
	{

		if (type.equals("0003"))
			this.type = ForaeldreMyndighedRelationsType.mor;
		else if (type.equals("0004"))
			this.type = ForaeldreMyndighedRelationsType.far;
		else if (type.equals("0005"))
			this.type = ForaeldreMyndighedRelationsType.andenIndehaver1;
		else if (type.equals("0006"))
			this.type = ForaeldreMyndighedRelationsType.andenIndehaver2;
		else
			this.type = null;

		this.typeKode = type;
	}

	public Date getForaeldreMyndighedStartDato()
	{

		return foraeldreMyndighedStartDato;
	}

	public void setForaeldreMyndighedStartDato(Date foraeldreMyndighedStartDato)
	{

		this.foraeldreMyndighedStartDato = foraeldreMyndighedStartDato;
	}

	public String getForaeldreMyndighedMarkering()
	{

		return foraeldreMyndighedMarkering;
	}

	public void setForaeldreMyndighedMarkering(String foraeldreMyndighedMarkering)
	{

		this.foraeldreMyndighedMarkering = foraeldreMyndighedMarkering;
	}

	public Date getForaeldreMyndighedSlettedato()
	{

		return foraeldreMyndighedSlettedato;
	}

	public void setForaeldreMyndighedSlettedato(Date foraeldreMyndighedSlettedato)
	{

		this.foraeldreMyndighedSlettedato = foraeldreMyndighedSlettedato;
	}

	@Output
	public String getRelationCpr()
	{

		return relationCpr;
	}

	public void setRelationCpr(String relationCpr)
	{

		this.relationCpr = relationCpr;
	}

	public Date getRelationCprStartDato()
	{

		return relationCprStartDato;
	}

	public void setRelationCprStartDato(Date relationCprStartDato)
	{

		this.relationCprStartDato = relationCprStartDato;
	}

	@Override
	public Date getValidFrom()
	{

		return (foraeldreMyndighedStartDato == null) ? super.getValidFrom() : DateUtils.toCalendar(foraeldreMyndighedStartDato);
	}

	@Override
	public Date getValidTo()
	{

		return (foraeldreMyndighedSlettedato == null) ? super.getValidTo() : DateUtils.toCalendar(foraeldreMyndighedSlettedato);
	}
}
