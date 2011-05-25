//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.3u2-hudson-jaxb-ri-2.2.3-4- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.25 at 09:38:22 AM CEST 
//


package oio.sagdok._2_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TilstandVirkningType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TilstandVirkningType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oio:sagdok:2.0.0}FraTidspunkt" minOccurs="0"/>
 *         &lt;element ref="{urn:oio:sagdok:2.0.0}AktoerRef"/>
 *         &lt;element ref="{http://rep.oio.dk/vejsektoren.dk/core/schemas/xml/2007/03/31/}CommentText" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TilstandVirkningType", propOrder = {
    "fraTidspunkt",
    "aktoerRef",
    "commentText"
})
public class TilstandVirkningType {

    @XmlElement(name = "FraTidspunkt")
    protected TidspunktType fraTidspunkt;
    @XmlElement(name = "AktoerRef", required = true)
    protected UnikIdType aktoerRef;
    @XmlElement(name = "CommentText", namespace = "http://rep.oio.dk/vejsektoren.dk/core/schemas/xml/2007/03/31/")
    protected String commentText;

    /**
     * Gets the value of the fraTidspunkt property.
     * 
     * @return
     *     possible object is
     *     {@link TidspunktType }
     *     
     */
    public TidspunktType getFraTidspunkt() {
        return fraTidspunkt;
    }

    /**
     * Sets the value of the fraTidspunkt property.
     * 
     * @param value
     *     allowed object is
     *     {@link TidspunktType }
     *     
     */
    public void setFraTidspunkt(TidspunktType value) {
        this.fraTidspunkt = value;
    }

    /**
     * Gets the value of the aktoerRef property.
     * 
     * @return
     *     possible object is
     *     {@link UnikIdType }
     *     
     */
    public UnikIdType getAktoerRef() {
        return aktoerRef;
    }

    /**
     * Sets the value of the aktoerRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnikIdType }
     *     
     */
    public void setAktoerRef(UnikIdType value) {
        this.aktoerRef = value;
    }

    /**
     * Gets the value of the commentText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommentText() {
        return commentText;
    }

    /**
     * Sets the value of the commentText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommentText(String value) {
        this.commentText = value;
    }

}