//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.04.19 at 07:37:16 AM EST 
//


package au.edu.unimelb.plantcell.servers.mascot.unimod.v2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NeutralLoss_t complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NeutralLoss_t">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.unimod.org/xmlns/schema/unimod_2}composition_t">
 *       &lt;attribute name="flag" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NeutralLoss_t")
public class NeutralLossT
    extends CompositionT
{

    @XmlAttribute(name = "flag")
    protected Boolean flag;

    /**
     * Gets the value of the flag property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isFlag() {
        if (flag == null) {
            return false;
        } else {
            return flag;
        }
    }

    /**
     * Sets the value of the flag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFlag(Boolean value) {
        this.flag = value;
    }

}