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
package com.trifork.stamdata.importer.jobs.bemyndigelse;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Bemyndigelser", namespace="http://nsi.dk/bemyndigelser/2012/04/")
public class Bemyndigelser {

    private List<Bemyndigelse> bemyndigelseList;
    private String dato;
    private String timestamp;
    private String version;
    private int antalPosteringer;

    public void setBemyndigelseList(List<Bemyndigelse> bemyndigelseList) {
        this.bemyndigelseList = bemyndigelseList;
    }

    @XmlElement(name="Bemyndigelse")
    public List<Bemyndigelse> getBemyndigelseList() {
        return bemyndigelseList;
    }
    
    public void addBemyndigelse(Bemyndigelse b) {
        if(bemyndigelseList == null) {
            bemyndigelseList = new ArrayList<Bemyndigelse>();
        }
        bemyndigelseList.add(b);
    }

    @XmlAttribute(name="Version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    @XmlAttribute(name="Dato")
    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    @XmlAttribute(name="TimeStamp")
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @XmlAttribute(name="AntalPost")
    public int getAntalPosteringer() {
        return antalPosteringer;
    }

    public void setAntalPosteringer(int antalPosteringer) {
        this.antalPosteringer = antalPosteringer;
    }
    
    
}
