//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.30 at 02:27:38 PM EST 
//


package au.edu.unimelb.plantcell.servers.msconvertee.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KeepNThresholdType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KeepNThresholdType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="msLevels" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}MsLevelType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="what">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}MostOrLeast">
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="n" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KeepNThresholdType", propOrder = {
    "msLevels"
})
public class KeepNThresholdType {

    @XmlElement(required = true)
    protected MsLevelType msLevels;
    @XmlAttribute
    protected MostOrLeast what;
    @XmlAttribute
    protected Integer n;

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
     * Gets the value of the what property.
     * 
     * @return
     *     possible object is
     *     {@link MostOrLeast }
     *     
     */
    public MostOrLeast getWhat() {
        return what;
    }

    /**
     * Sets the value of the what property.
     * 
     * @param value
     *     allowed object is
     *     {@link MostOrLeast }
     *     
     */
    public void setWhat(MostOrLeast value) {
        this.what = value;
    }

    /**
     * Gets the value of the n property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getN() {
        return n;
    }

    /**
     * Sets the value of the n property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setN(Integer value) {
        this.n = value;
    }

}
