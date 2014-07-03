//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.03 at 11:56:54 AM EST 
//


package au.edu.unimelb.plantcell.servers.msconvertee.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for peakPickingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="peakPickingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="preferVendor" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="msLevels" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}MsLevelType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="fixMetadata" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "peakPickingType", propOrder = {
    "preferVendor",
    "msLevels"
})
public class PeakPickingType {

    protected boolean preferVendor;
    @XmlElement(required = true)
    protected MsLevelType msLevels;
    @XmlAttribute
    protected Boolean fixMetadata;

    /**
     * Gets the value of the preferVendor property.
     * 
     */
    public boolean isPreferVendor() {
        return preferVendor;
    }

    /**
     * Sets the value of the preferVendor property.
     * 
     */
    public void setPreferVendor(boolean value) {
        this.preferVendor = value;
    }

    /**
     * Gets the value of the msLevels property.
     * 
     * @return
     *     possible object is
     *     {@link MsLevelType }
     *     
     */
    public MsLevelType getMsLevels() {
        return msLevels;
    }

    /**
     * Sets the value of the msLevels property.
     * 
     * @param value
     *     allowed object is
     *     {@link MsLevelType }
     *     
     */
    public void setMsLevels(MsLevelType value) {
        this.msLevels = value;
    }

    /**
     * Gets the value of the fixMetadata property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFixMetadata() {
        return fixMetadata;
    }

    /**
     * Sets the value of the fixMetadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFixMetadata(Boolean value) {
        this.fixMetadata = value;
    }

}
