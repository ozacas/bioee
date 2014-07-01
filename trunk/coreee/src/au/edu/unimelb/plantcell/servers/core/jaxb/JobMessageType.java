//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.30 at 02:37:10 PM EST 
//


package au.edu.unimelb.plantcell.servers.core.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JobMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JobMessageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="jobID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="inputData" type="{http://www.example.org/JobMessage}inputDataType"/>
 *         &lt;element name="inputParameters" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="results" type="{http://www.example.org/JobMessage}resultsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JobMessageType", propOrder = {
    "jobID",
    "inputData",
    "inputParameters",
    "results"
})
public class JobMessageType {

    @XmlElement(required = true)
    protected String jobID;
    @XmlElement(required = true)
    protected InputDataType inputData;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String inputParameters;
    @XmlElement(required = true)
    protected ResultsType results;

    /**
     * Gets the value of the jobID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobID() {
        return jobID;
    }

    /**
     * Sets the value of the jobID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobID(String value) {
        this.jobID = value;
    }

    /**
     * Gets the value of the inputData property.
     * 
     * @return
     *     possible object is
     *     {@link InputDataType }
     *     
     */
    public InputDataType getInputData() {
        return inputData;
    }

    /**
     * Sets the value of the inputData property.
     * 
     * @param value
     *     allowed object is
     *     {@link InputDataType }
     *     
     */
    public void setInputData(InputDataType value) {
        this.inputData = value;
    }

    /**
     * Gets the value of the inputParameters property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInputParameters() {
        return inputParameters;
    }

    /**
     * Sets the value of the inputParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInputParameters(String value) {
        this.inputParameters = value;
    }

    /**
     * Gets the value of the results property.
     * 
     * @return
     *     possible object is
     *     {@link ResultsType }
     *     
     */
    public ResultsType getResults() {
        return results;
    }

    /**
     * Sets the value of the results property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultsType }
     *     
     */
    public void setResults(ResultsType value) {
        this.results = value;
    }

}
