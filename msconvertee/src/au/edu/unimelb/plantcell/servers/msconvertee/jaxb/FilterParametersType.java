//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.30 at 01:33:14 PM EST 
//


package au.edu.unimelb.plantcell.servers.msconvertee.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FilterParametersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FilterParametersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="msLevelFilter" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}MsLevelType" minOccurs="0"/>
 *         &lt;element name="peakPicking" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}peakPickingType" minOccurs="0"/>
 *         &lt;element name="IntensityFilter" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}ThresholdParametersType" minOccurs="0"/>
 *         &lt;element name="mzWindowFilter" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}WindowFilterType" minOccurs="0"/>
 *         &lt;element name="polarityFilter" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="positive"/>
 *               &lt;enumeration value="negative"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="activationFilter" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}activationFilteringType" minOccurs="0"/>
 *         &lt;element name="analyzerFilter" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="quad"/>
 *               &lt;enumeration value="orbi"/>
 *               &lt;enumeration value="FT"/>
 *               &lt;enumeration value="IT"/>
 *               &lt;enumeration value="TOF"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="deisotopeFilter" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}deisotopeFilteringType" minOccurs="0"/>
 *         &lt;element name="etdFilter" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}etdFilteringType" minOccurs="0"/>
 *         &lt;element name="scanFilter" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}ScanFilterType" minOccurs="0"/>
 *         &lt;element name="chargeStateFilter" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}chargeStateFilterType" minOccurs="0"/>
 *         &lt;element name="mzPrecursorFilter" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}mzPrecursorFilterType" minOccurs="0"/>
 *         &lt;element name="zeroesFilter" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}zeroesFilterType" minOccurs="0"/>
 *         &lt;element name="ms2denoise" type="{http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee}MS2DenoiseType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterParametersType", propOrder = {
    "msLevelFilter",
    "peakPicking",
    "intensityFilter",
    "mzWindowFilter",
    "polarityFilter",
    "activationFilter",
    "analyzerFilter",
    "deisotopeFilter",
    "etdFilter",
    "scanFilter",
    "chargeStateFilter",
    "mzPrecursorFilter",
    "zeroesFilter",
    "ms2Denoise"
})
public class FilterParametersType {

    protected MsLevelType msLevelFilter;
    protected PeakPickingType peakPicking;
    @XmlElement(name = "IntensityFilter")
    protected ThresholdParametersType intensityFilter;
    protected WindowFilterType mzWindowFilter;
    protected String polarityFilter;
    protected ActivationFilteringType activationFilter;
    protected String analyzerFilter;
    protected DeisotopeFilteringType deisotopeFilter;
    protected EtdFilteringType etdFilter;
    protected ScanFilterType scanFilter;
    protected ChargeStateFilterType chargeStateFilter;
    protected MzPrecursorFilterType mzPrecursorFilter;
    protected ZeroesFilterType zeroesFilter;
    @XmlElement(name = "ms2denoise")
    protected MS2DenoiseType ms2Denoise;

    /**
     * Gets the value of the msLevelFilter property.
     * 
     * @return
     *     possible object is
     *     {@link MsLevelType }
     *     
     */
    public MsLevelType getMsLevelFilter() {
        return msLevelFilter;
    }

    /**
     * Sets the value of the msLevelFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link MsLevelType }
     *     
     */
    public void setMsLevelFilter(MsLevelType value) {
        this.msLevelFilter = value;
    }

    /**
     * Gets the value of the peakPicking property.
     * 
     * @return
     *     possible object is
     *     {@link PeakPickingType }
     *     
     */
    public PeakPickingType getPeakPicking() {
        return peakPicking;
    }

    /**
     * Sets the value of the peakPicking property.
     * 
     * @param value
     *     allowed object is
     *     {@link PeakPickingType }
     *     
     */
    public void setPeakPicking(PeakPickingType value) {
        this.peakPicking = value;
    }

    /**
     * Gets the value of the intensityFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ThresholdParametersType }
     *     
     */
    public ThresholdParametersType getIntensityFilter() {
        return intensityFilter;
    }

    /**
     * Sets the value of the intensityFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ThresholdParametersType }
     *     
     */
    public void setIntensityFilter(ThresholdParametersType value) {
        this.intensityFilter = value;
    }

    /**
     * Gets the value of the mzWindowFilter property.
     * 
     * @return
     *     possible object is
     *     {@link WindowFilterType }
     *     
     */
    public WindowFilterType getMzWindowFilter() {
        return mzWindowFilter;
    }

    /**
     * Sets the value of the mzWindowFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link WindowFilterType }
     *     
     */
    public void setMzWindowFilter(WindowFilterType value) {
        this.mzWindowFilter = value;
    }

    /**
     * Gets the value of the polarityFilter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolarityFilter() {
        return polarityFilter;
    }

    /**
     * Sets the value of the polarityFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolarityFilter(String value) {
        this.polarityFilter = value;
    }

    /**
     * Gets the value of the activationFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ActivationFilteringType }
     *     
     */
    public ActivationFilteringType getActivationFilter() {
        return activationFilter;
    }

    /**
     * Sets the value of the activationFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActivationFilteringType }
     *     
     */
    public void setActivationFilter(ActivationFilteringType value) {
        this.activationFilter = value;
    }

    /**
     * Gets the value of the analyzerFilter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnalyzerFilter() {
        return analyzerFilter;
    }

    /**
     * Sets the value of the analyzerFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnalyzerFilter(String value) {
        this.analyzerFilter = value;
    }

    /**
     * Gets the value of the deisotopeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link DeisotopeFilteringType }
     *     
     */
    public DeisotopeFilteringType getDeisotopeFilter() {
        return deisotopeFilter;
    }

    /**
     * Sets the value of the deisotopeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeisotopeFilteringType }
     *     
     */
    public void setDeisotopeFilter(DeisotopeFilteringType value) {
        this.deisotopeFilter = value;
    }

    /**
     * Gets the value of the etdFilter property.
     * 
     * @return
     *     possible object is
     *     {@link EtdFilteringType }
     *     
     */
    public EtdFilteringType getEtdFilter() {
        return etdFilter;
    }

    /**
     * Sets the value of the etdFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link EtdFilteringType }
     *     
     */
    public void setEtdFilter(EtdFilteringType value) {
        this.etdFilter = value;
    }

    /**
     * Gets the value of the scanFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ScanFilterType }
     *     
     */
    public ScanFilterType getScanFilter() {
        return scanFilter;
    }

    /**
     * Sets the value of the scanFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScanFilterType }
     *     
     */
    public void setScanFilter(ScanFilterType value) {
        this.scanFilter = value;
    }

    /**
     * Gets the value of the chargeStateFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeStateFilterType }
     *     
     */
    public ChargeStateFilterType getChargeStateFilter() {
        return chargeStateFilter;
    }

    /**
     * Sets the value of the chargeStateFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeStateFilterType }
     *     
     */
    public void setChargeStateFilter(ChargeStateFilterType value) {
        this.chargeStateFilter = value;
    }

    /**
     * Gets the value of the mzPrecursorFilter property.
     * 
     * @return
     *     possible object is
     *     {@link MzPrecursorFilterType }
     *     
     */
    public MzPrecursorFilterType getMzPrecursorFilter() {
        return mzPrecursorFilter;
    }

    /**
     * Sets the value of the mzPrecursorFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link MzPrecursorFilterType }
     *     
     */
    public void setMzPrecursorFilter(MzPrecursorFilterType value) {
        this.mzPrecursorFilter = value;
    }

    /**
     * Gets the value of the zeroesFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ZeroesFilterType }
     *     
     */
    public ZeroesFilterType getZeroesFilter() {
        return zeroesFilter;
    }

    /**
     * Sets the value of the zeroesFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ZeroesFilterType }
     *     
     */
    public void setZeroesFilter(ZeroesFilterType value) {
        this.zeroesFilter = value;
    }

    /**
     * Gets the value of the ms2Denoise property.
     * 
     * @return
     *     possible object is
     *     {@link MS2DenoiseType }
     *     
     */
    public MS2DenoiseType getMs2Denoise() {
        return ms2Denoise;
    }

    /**
     * Sets the value of the ms2Denoise property.
     * 
     * @param value
     *     allowed object is
     *     {@link MS2DenoiseType }
     *     
     */
    public void setMs2Denoise(MS2DenoiseType value) {
        this.ms2Denoise = value;
    }

}
