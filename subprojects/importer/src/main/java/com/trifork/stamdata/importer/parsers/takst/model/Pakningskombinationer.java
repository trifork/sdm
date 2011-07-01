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

package com.trifork.stamdata.importer.parsers.takst.model;

import com.trifork.stamdata.importer.model.Id;
import com.trifork.stamdata.importer.model.Output;


@Output
public class Pakningskombinationer extends TakstEntity
{

	private Long varenummerOrdineret; // Vnr. på pakningen anført på recepten
	private Long varenummerSubstitueret; // Vnr. på en pakning der evt. kan
											// substitueres til
	private Long varenummerAlternativt; // Vnr. for en mindre, billigere pakning
	private Long antalPakninger; // Antal af den alternative pakning
	private Long ekspeditionensSamledePris; // ESP for den alternative
											// pakningskombination
	private String informationspligtMarkering; // Markering (stjerne *) for
												// informationspligt

	@Output
	public Long getVarenummerOrdineret()
	{
		return this.varenummerOrdineret;
	}

	public void setVarenummerOrdineret(Long varenummerOrdineret)
	{
		this.varenummerOrdineret = varenummerOrdineret;
	}

	@Output
	public Long getVarenummerSubstitueret()
	{
		return this.varenummerSubstitueret;
	}

	public void setVarenummerSubstitueret(Long varenummerSubstitueret)
	{
		this.varenummerSubstitueret = varenummerSubstitueret;
	}

	@Output
	public Long getVarenummerAlternativt()
	{
		return this.varenummerAlternativt;
	}

	public void setVarenummerAlternativt(Long varenummerAlternativt)
	{
		this.varenummerAlternativt = varenummerAlternativt;
	}

	@Output
	public Long getAntalPakninger()
	{
		return this.antalPakninger;
	}

	public void setAntalPakninger(Long antalPakninger)
	{
		this.antalPakninger = antalPakninger;
	}

	@Output
	public Long getEkspeditionensSamledePris()
	{
		return this.ekspeditionensSamledePris;
	}

	public void setEkspeditionensSamledePris(Long ekspeditionensSamledePris)
	{
		this.ekspeditionensSamledePris = ekspeditionensSamledePris;
	}

	@Output
	public String getInformationspligtMarkering()
	{
		return this.informationspligtMarkering;
	}

	public void setInformationspligtMarkering(String informationspligtMarkering)
	{
		this.informationspligtMarkering = informationspligtMarkering;
	}

	@Id
	@Output(name = "CID")
	public String getKey()
	{
		return "" + varenummerOrdineret + '-' + varenummerSubstitueret + '-' + varenummerAlternativt + '-' + antalPakninger;
	}

}
