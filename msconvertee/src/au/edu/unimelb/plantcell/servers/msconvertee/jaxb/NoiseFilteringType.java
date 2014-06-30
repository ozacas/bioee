//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.30 at 01:33:14 PM EST 
//


package au.edu.unimelb.plantcell.servers.msconvertee.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for noiseFilteringType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="noiseFilteringType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="peaks_in_window" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="window_width_da" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="multicharge_fragment_relaxation" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "noiseFilteringType")
public class NoiseFilteringType {

    @XmlAttribute(name = "peaks_in_window")
    protected Double peaksInWindow;
    @XmlAttribute(name = "window_width_da")
    protected Double windowWidthDa;
    @XmlAttribute(name = "multicharge_fragment_relaxation")
    protected Boolean multichargeFragmentRelaxation;

    /**
     * Gets the value of the peaksInWindow property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getPeaksInWindow() {
        return peaksInWindow;
    }

    /**
     * Sets the value of the peaksInWindow property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setPeaksInWindow(Double value) {
        this.peaksInWindow = value;
    }

    /**
     * Gets the value of the windowWidthDa property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWindowWidthDa() {
        return windowWidthDa;
    }

    /**
     * Sets the value of the windowWidthDa property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWindowWidthDa(Double value) {
        this.windowWidthDa = value;
    }

    /**
     * Gets the value of the multichargeFragmentRelaxation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultichargeFragmentRelaxation() {
        return multichargeFragmentRelaxation;
    }

    /**
     * Sets the value of the multichargeFragmentRelaxation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultichargeFragmentRelaxation(Boolean value) {
        this.multichargeFragmentRelaxation = value;
    }

}
