//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.11 at 08:14:37 AM EST 
//


package au.edu.unimelb.plantcell.servers.msconvertee.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for activationFilteringType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="activationFilteringType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="activationToAccept" maxOccurs="20">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="ETD"/>
 *               &lt;enumeration value="CID"/>
 *               &lt;enumeration value="SA"/>
 *               &lt;enumeration value="HCD"/>
 *               &lt;enumeration value="BIRD"/>
 *               &lt;enumeration value="ECD"/>
 *               &lt;enumeration value="IRMPD"/>
 *               &lt;enumeration value="PD"/>
 *               &lt;enumeration value="PSD"/>
 *               &lt;enumeration value="PQD"/>
 *               &lt;enumeration value="SID"/>
 *               &lt;enumeration value="SORI"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "activationFilteringType", propOrder = {
    "activationToAccept"
})
public class ActivationFilteringType {

    @XmlElement(required = true)
    protected List<String> activationToAccept;

    /**
     * Gets the value of the activationToAccept property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the activationToAccept property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActivationToAccept().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getActivationToAccept() {
        if (activationToAccept == null) {
            activationToAccept = new ArrayList<String>();
        }
        return this.activationToAccept;
    }

}
