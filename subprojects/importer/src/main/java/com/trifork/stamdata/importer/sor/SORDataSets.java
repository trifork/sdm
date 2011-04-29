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

package com.trifork.stamdata.importer.sor;

import com.trifork.stamdata.importer.sor.model.*;
import com.trifork.stamdata.model.CompleteDataset;

public class SORDataSets {
	private CompleteDataset<Apotek> apotekDS;
	private CompleteDataset<Yder> yderDS;
	private CompleteDataset<Praksis> praksisDS;
	private CompleteDataset<Sygehus> sygehusDS;
	private CompleteDataset<SygehusAfdeling> sygehusAfdelingDS;

	public CompleteDataset<Apotek> getApotekDS() {
		return apotekDS;
	}
	public void setApotekDS(CompleteDataset<Apotek> apotekDS) {
		this.apotekDS = apotekDS;
	}
	public CompleteDataset<Yder> getYderDS() {
		return yderDS;
	}
	public void setYderDS(CompleteDataset<Yder> yderDS) {
		this.yderDS = yderDS;
	}
	public CompleteDataset<Praksis> getPraksisDS() {
		return praksisDS;
	}
	public void setPraksisDS(CompleteDataset<Praksis> praksisDS) {
		this.praksisDS = praksisDS;
	}
	public CompleteDataset<Sygehus> getSygehusDS() {
		return sygehusDS;
	}
	public void setSygehusDS(CompleteDataset<Sygehus> sygehusDS) {
		this.sygehusDS = sygehusDS;
	}
	public CompleteDataset<SygehusAfdeling> getSygehusAfdelingDS() {
		return sygehusAfdelingDS;
	}
	public void setSygehusAfdelingDS(
			CompleteDataset<SygehusAfdeling> sygehusAfdelingDS) {
		this.sygehusAfdelingDS = sygehusAfdelingDS;
	}



}
