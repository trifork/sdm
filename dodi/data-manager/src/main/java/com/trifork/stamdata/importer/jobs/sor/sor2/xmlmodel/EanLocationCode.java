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

public class EanLocationCode {
	private long eanLocationCode;
	private boolean onlyInternalIndicator;
	private boolean nonActiveIndicator;
	private long systemSupplier;
	private long systemType;
	private long communicationSupplier;
	private long regionCode;
	private long ediAdministrator;
	private String sorNote;
	private SorStatus sorStatus;
	public long getEanLocationCode() {
		return eanLocationCode;
	}
	public void setEanLocationCode(long eanLocationCode) {
		this.eanLocationCode = eanLocationCode;
	}
	public boolean isOnlyInternalIndicator() {
		return onlyInternalIndicator;
	}
	public void setOnlyInternalIndicator(boolean onlyInternalIndicator) {
		this.onlyInternalIndicator = onlyInternalIndicator;
	}
	public boolean isNonActiveIndicator() {
		return nonActiveIndicator;
	}
	public void setNonActiveIndicator(boolean nonActiveIndicator) {
		this.nonActiveIndicator = nonActiveIndicator;
	}
	public long getSystemSupplier() {
		return systemSupplier;
	}
	public void setSystemSupplier(long systemSupplier) {
		this.systemSupplier = systemSupplier;
	}
	public long getSystemType() {
		return systemType;
	}
	public void setSystemType(long systemType) {
		this.systemType = systemType;
	}
	public long getCommunicationSupplier() {
		return communicationSupplier;
	}
	public void setCommunicationSupplier(long communicationSupplier) {
		this.communicationSupplier = communicationSupplier;
	}
	public long getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(long regionCode) {
		this.regionCode = regionCode;
	}
	public long getEdiAdministrator() {
		return ediAdministrator;
	}
	public void setEdiAdministrator(long ediAdministrator) {
		this.ediAdministrator = ediAdministrator;
	}
	public String getSorNote() {
		return sorNote;
	}
	public void setSorNote(String sorNote) {
		this.sorNote = sorNote;
	}
	public SorStatus getSorStatus() {
		return sorStatus;
	}
	public void setSorStatus(SorStatus sorStatus) {
		this.sorStatus = sorStatus;
	}
	@Override
	public String toString() {
		return "EanLocationCode [eanLocationCode=" + eanLocationCode
				+ ", onlyInternalIndicator=" + onlyInternalIndicator
				+ ", nonActiveIndicator=" + nonActiveIndicator
				+ ", systemSupplier=" + systemSupplier + ", systemType="
				+ systemType + ", communicationSupplier="
				+ communicationSupplier + ", regionCode=" + regionCode
				+ ", ediAdministrator=" + ediAdministrator + ", sorNote="
				+ sorNote + ", sorStatus=" + sorStatus + "]";
	}
	
	
}
