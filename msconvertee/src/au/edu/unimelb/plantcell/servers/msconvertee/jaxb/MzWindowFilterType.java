//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.30 at 01:33:14 PM EST 
//


package au.edu.unimelb.plantcell.servers.msconvertee.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mzWindowFilterType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mzWindowFilterType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="lower" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="upper" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mzWindowFilterType", propOrder = {
    "lower",
    "upper"
})
public class MzWindowFilterType {

    protected double lower;
    protected double upper;

    /**
     * Gets the value of the lower property.
     * 
     */
    public double getLower() {
        return lower;
    }

    /**
     * Sets the value of the lower property.
     * 
     */
    public void setLower(double value) {
        this.lower = value;
    }

    /**
     * Gets the value of the upper property.
     * 
     */
    public double getUpper() {
        return upper;
    }

    /**
     * Sets the value of the upper property.
     * 
     */
    public void setUpper(double value) {
        this.upper = value;
    }

}
