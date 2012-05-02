/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */

package com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel;


public class InstitutionOwner {

    private long sorIdentifier;

	private String entityName;
    
    private long ownerType;

    private EanLocationCode eanLocationCode;

    private PostalAddressInformation postalAddressInformation;

    private VirtualAddressInformation virtualAddressInformation;

    private SorStatus sorStatus;

    public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public long getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(long ownerType) {
		this.ownerType = ownerType;
	}

	public EanLocationCode getEanLocationCode() {
		return eanLocationCode;
	}

	public void setEanLocationCode(EanLocationCode eanLocationCode) {
		this.eanLocationCode = eanLocationCode;
	}

	public PostalAddressInformation getPostalAddressInformation() {
		return postalAddressInformation;
	}

	public void setPostalAddressInformation(
			PostalAddressInformation postalAddressInformation) {
		this.postalAddressInformation = postalAddressInformation;
	}

	public VirtualAddressInformation getVirtualAddressInformation() {
		return virtualAddressInformation;
	}

	public void setVirtualAddressInformation(
			VirtualAddressInformation virtualAddressInformation) {
		this.virtualAddressInformation = virtualAddressInformation;
	}

	public SorStatus getSorStatus() {
		return sorStatus;
	}

	public void setSorStatus(SorStatus sorStatus) {
		this.sorStatus = sorStatus;
	}
    
	public long getSorIdentifier() {
		return sorIdentifier;
	}

	public void setSorIdentifier(long sorIdentifier) {
		this.sorIdentifier = sorIdentifier;
	}

	@Override
	public String toString() {
		return "InstitutionOwner [sorIdentifier=" + sorIdentifier
				+ ", entityName=" + entityName + ", ownerType=" + ownerType
				+ ", eanLocationCode=" + eanLocationCode
				+ ", postalAddressInformation=" + postalAddressInformation
				+ ", virtualAddressInformation=" + virtualAddressInformation
				+ ", sorStatus=" + sorStatus + "]";
	}

}
