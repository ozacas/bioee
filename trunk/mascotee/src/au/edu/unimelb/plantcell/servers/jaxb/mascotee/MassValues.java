//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.30 at 08:19:45 AM EST 
//


package au.edu.unimelb.plantcell.servers.jaxb.mascotee;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MassValues.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MassValues">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MH+"/>
 *     &lt;enumeration value="Mr"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MassValues")
@XmlEnum
public enum MassValues {

    @XmlEnumValue("MH+")
    MH("MH+"),
    @XmlEnumValue("Mr")
    MR("Mr");
    private final String value;

    MassValues(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MassValues fromValue(String v) {
        for (MassValues c: MassValues.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
