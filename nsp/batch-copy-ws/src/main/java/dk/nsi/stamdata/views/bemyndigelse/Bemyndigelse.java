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


package dk.nsi.stamdata.views.bemyndigelse;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import dk.nsi.stamdata.views.View;
import dk.nsi.stamdata.views.ViewPath;


@Entity
@XmlRootElement
@ViewPath("bemyndigelsesservice/bemyndigelse/v1")
public class Bemyndigelse extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "BemyndigelsePID")
	private BigInteger recordID;

	protected String kode;
    protected String bemyndigende_cpr;
    protected String bemyndigede_cpr;
    protected String bemyndigede_cvr;
    protected String system;
    protected String arbejdsfunktion;
    protected String rettighed;
    protected String status;
    protected String godkendelses_dato;
    protected String oprettelses_dato;
    protected String modificeret_dato;
    protected String gyldig_fra_dato;
    protected String gyldig_til_dato;

    @Temporal(TIMESTAMP)
    protected Date validFrom;
    @Temporal(TIMESTAMP)
    protected Date validTo;
    @XmlTransient
    @Temporal(TIMESTAMP)
    private Date modifiedDate;

    public String getKode() {
		return kode;
	}

	public String getBemyndigendeCPR() {
		return bemyndigende_cpr;
	}

    public String getBemyndigedeCPR() {
        return bemyndigede_cpr;
    }

    public String getBemyndigedeCVR() {
        return bemyndigede_cvr;
    }
    
    public String getSystem() {
        return system;
    }
    
    public String getArbejdsfunktion() {
        return arbejdsfunktion;
    }
    
    public String getRettighed() {
        return rettighed;
    }
        
    public String getStatus() {
        return status;
    }

    public String getGodkendelses_dato() {
        return godkendelses_dato;
    }

    public String getOprettelses_dato() {
        return oprettelses_dato;
    }

    public String getModificeret_dato() {
        return modificeret_dato;
    }

    public String getGyldig_fra_dato() {
        return gyldig_fra_dato;
    }

    public String getGyldig_til_dato() {
        return gyldig_til_dato;
    }
    

	public Date getValidFrom() {
		return validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	@Override
	public BigInteger getRecordID() {
		return recordID;
	}

	@Override
	public String getId() {
		return kode;
	}

	@Override
	public Date getUpdated() {
		return modifiedDate;
	}
}
