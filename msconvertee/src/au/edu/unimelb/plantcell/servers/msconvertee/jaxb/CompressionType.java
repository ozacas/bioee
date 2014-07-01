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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompressionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompressionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="use_32bit_for_mz" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="use_32bit_for_intensity" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="gzip_entire_file" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompressionType")
public class CompressionType {

    @XmlAttribute(name = "use_32bit_for_mz")
    protected Boolean use32BitForMz;
    @XmlAttribute(name = "use_32bit_for_intensity")
    protected Boolean use32BitForIntensity;
    @XmlAttribute(name = "gzip_entire_file")
    protected Boolean gzipEntireFile;

    /**
     * Gets the value of the use32BitForMz property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUse32BitForMz() {
        return use32BitForMz;
    }

    /**
     * Sets the value of the use32BitForMz property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUse32BitForMz(Boolean value) {
        this.use32BitForMz = value;
    }

    /**
     * Gets the value of the use32BitForIntensity property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUse32BitForIntensity() {
        return use32BitForIntensity;
    }

    /**
     * Sets the value of the use32BitForIntensity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUse32BitForIntensity(Boolean value) {
        this.use32BitForIntensity = value;
    }

    /**
     * Gets the value of the gzipEntireFile property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGzipEntireFile() {
        return gzipEntireFile;
    }

    /**
     * Sets the value of the gzipEntireFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGzipEntireFile(Boolean value) {
        this.gzipEntireFile = value;
    }

}
