//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.25 at 08:03:10 AM EST 
//


package au.edu.unimelb.plantcell.servers.proteowizard.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deisotopeFilteringType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deisotopeFilteringType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="hires" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="mz_tolerance" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deisotopeFilteringType")
public class DeisotopeFilteringType {

    @XmlAttribute(name = "hires")
    protected Boolean hires;
    @XmlAttribute(name = "mz_tolerance")
    protected Double mzTolerance;

    /**
     * Gets the value of the hires property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHires() {
        return hires;
    }

    /**
     * Sets the value of the hires property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHires(Boolean value) {
        this.hires = value;
    }

    /**
     * Gets the value of the mzTolerance property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMzTolerance() {
        return mzTolerance;
    }

    /**
     * Sets the value of the mzTolerance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMzTolerance(Double value) {
        this.mzTolerance = value;
    }

}
