//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.11 at 08:14:37 AM EST 
//


package au.edu.unimelb.plantcell.servers.msconvertee.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for etdFilteringType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="etdFilteringType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tolerance" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="remove_precursor" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="remove_charge_reduced" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="remove_neutral_loss" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "etdFilteringType", propOrder = {
    "tolerance"
})
public class EtdFilteringType {

    @XmlElement(required = true)
    protected String tolerance;
    @XmlAttribute(name = "remove_precursor")
    protected Boolean removePrecursor;
    @XmlAttribute(name = "remove_charge_reduced")
    protected Boolean removeChargeReduced;
    @XmlAttribute(name = "remove_neutral_loss")
    protected Boolean removeNeutralLoss;

    /**
     * Gets the value of the tolerance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTolerance() {
        return tolerance;
    }

    /**
     * Sets the value of the tolerance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTolerance(String value) {
        this.tolerance = value;
    }

    /**
     * Gets the value of the removePrecursor property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemovePrecursor() {
        return removePrecursor;
    }

    /**
     * Sets the value of the removePrecursor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemovePrecursor(Boolean value) {
        this.removePrecursor = value;
    }

    /**
     * Gets the value of the removeChargeReduced property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemoveChargeReduced() {
        return removeChargeReduced;
    }

    /**
     * Sets the value of the removeChargeReduced property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemoveChargeReduced(Boolean value) {
        this.removeChargeReduced = value;
    }

    /**
     * Gets the value of the removeNeutralLoss property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemoveNeutralLoss() {
        return removeNeutralLoss;
    }

    /**
     * Sets the value of the removeNeutralLoss property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemoveNeutralLoss(Boolean value) {
        this.removeNeutralLoss = value;
    }

}
