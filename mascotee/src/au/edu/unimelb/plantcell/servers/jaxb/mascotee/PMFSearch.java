//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.30 at 08:19:45 AM EST 
//


package au.edu.unimelb.plantcell.servers.jaxb.mascotee;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PMFSearch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PMFSearch">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identification" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/ns/MascotEE/v2}Identification"/>
 *         &lt;element name="parameters" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/ns/MascotEE/v2}KeyParameters"/>
 *         &lt;element name="constraints" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/ns/MascotEE/v2}PMFConstraints"/>
 *         &lt;element name="pmf_data" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/ns/MascotEE/v2}PMFData"/>
 *         &lt;element name="reporting" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/ns/MascotEE/v2}Reporting"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PMFSearch", propOrder = {
    "identification",
    "parameters",
    "constraints",
    "pmfData",
    "reporting"
})
public class PMFSearch {

    @XmlElement(required = true)
    protected Identification identification;
    @XmlElement(required = true)
    protected KeyParameters parameters;
    @XmlElement(required = true)
    protected PMFConstraints constraints;
    @XmlElement(name = "pmf_data", required = true)
    protected PMFData pmfData;
    @XmlElement(required = true)
    protected Reporting reporting;

    /**
     * Gets the value of the identification property.
     * 
     * @return
     *     possible object is
     *     {@link Identification }
     *     
     */
    public Identification getIdentification() {
        return identification;
    }

    /**
     * Sets the value of the identification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Identification }
     *     
     */
    public void setIdentification(Identification value) {
        this.identification = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link KeyParameters }
     *     
     */
    public KeyParameters getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyParameters }
     *     
     */
    public void setParameters(KeyParameters value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the constraints property.
     * 
     * @return
     *     possible object is
     *     {@link PMFConstraints }
     *     
     */
    public PMFConstraints getConstraints() {
        return constraints;
    }

    /**
     * Sets the value of the constraints property.
     * 
     * @param value
     *     allowed object is
     *     {@link PMFConstraints }
     *     
     */
    public void setConstraints(PMFConstraints value) {
        this.constraints = value;
    }

    /**
     * Gets the value of the pmfData property.
     * 
     * @return
     *     possible object is
     *     {@link PMFData }
     *     
     */
    public PMFData getPmfData() {
        return pmfData;
    }

    /**
     * Sets the value of the pmfData property.
     * 
     * @param value
     *     allowed object is
     *     {@link PMFData }
     *     
     */
    public void setPmfData(PMFData value) {
        this.pmfData = value;
    }

    /**
     * Gets the value of the reporting property.
     * 
     * @return
     *     possible object is
     *     {@link Reporting }
     *     
     */
    public Reporting getReporting() {
        return reporting;
    }

    /**
     * Sets the value of the reporting property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reporting }
     *     
     */
    public void setReporting(Reporting value) {
        this.reporting = value;
    }

}
