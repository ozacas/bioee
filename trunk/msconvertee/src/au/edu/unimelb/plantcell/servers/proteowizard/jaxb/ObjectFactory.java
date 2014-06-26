//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.27 at 09:47:07 AM EST 
//


package au.edu.unimelb.plantcell.servers.proteowizard.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the au.edu.unimelb.plantcell.servers.proteowizard.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ProteowizardJob_QNAME = new QName("http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee", "ProteowizardJob");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: au.edu.unimelb.plantcell.servers.proteowizard.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FilterParametersType }
     * 
     */
    public FilterParametersType createFilterParametersType() {
        return new FilterParametersType();
    }

    /**
     * Create an instance of {@link NoiseFilteringType }
     * 
     */
    public NoiseFilteringType createNoiseFilteringType() {
        return new NoiseFilteringType();
    }

    /**
     * Create an instance of {@link ThresholdParametersType }
     * 
     */
    public ThresholdParametersType createThresholdParametersType() {
        return new ThresholdParametersType();
    }

    /**
     * Create an instance of {@link ScanFilterType }
     * 
     */
    public ScanFilterType createScanFilterType() {
        return new ScanFilterType();
    }

    /**
     * Create an instance of {@link EtdFilteringType }
     * 
     */
    public EtdFilteringType createEtdFilteringType() {
        return new EtdFilteringType();
    }

    /**
     * Create an instance of {@link MS2DeisotopeType }
     * 
     */
    public MS2DeisotopeType createMS2DeisotopeType() {
        return new MS2DeisotopeType();
    }

    /**
     * Create an instance of {@link ProteowizardJobType }
     * 
     */
    public ProteowizardJobType createProteowizardJobType() {
        return new ProteowizardJobType();
    }

    /**
     * Create an instance of {@link MzPrecursorFilterType }
     * 
     */
    public MzPrecursorFilterType createMzPrecursorFilterType() {
        return new MzPrecursorFilterType();
    }

    /**
     * Create an instance of {@link MS2DenoiseType }
     * 
     */
    public MS2DenoiseType createMS2DenoiseType() {
        return new MS2DenoiseType();
    }

    /**
     * Create an instance of {@link RelativeThresholdType }
     * 
     */
    public RelativeThresholdType createRelativeThresholdType() {
        return new RelativeThresholdType();
    }

    /**
     * Create an instance of {@link DeisotopeFilteringType }
     * 
     */
    public DeisotopeFilteringType createDeisotopeFilteringType() {
        return new DeisotopeFilteringType();
    }

    /**
     * Create an instance of {@link MsLevelType }
     * 
     */
    public MsLevelType createMsLevelType() {
        return new MsLevelType();
    }

    /**
     * Create an instance of {@link CompressionType }
     * 
     */
    public CompressionType createCompressionType() {
        return new CompressionType();
    }

    /**
     * Create an instance of {@link PrecursorCorrectionType }
     * 
     */
    public PrecursorCorrectionType createPrecursorCorrectionType() {
        return new PrecursorCorrectionType();
    }

    /**
     * Create an instance of {@link ActivationFilteringType }
     * 
     */
    public ActivationFilteringType createActivationFilteringType() {
        return new ActivationFilteringType();
    }

    /**
     * Create an instance of {@link MzWindowFilterType }
     * 
     */
    public MzWindowFilterType createMzWindowFilterType() {
        return new MzWindowFilterType();
    }

    /**
     * Create an instance of {@link WindowFilterType }
     * 
     */
    public WindowFilterType createWindowFilterType() {
        return new WindowFilterType();
    }

    /**
     * Create an instance of {@link AbsoluteThresholdType }
     * 
     */
    public AbsoluteThresholdType createAbsoluteThresholdType() {
        return new AbsoluteThresholdType();
    }

    /**
     * Create an instance of {@link ChargeStateFilterType }
     * 
     */
    public ChargeStateFilterType createChargeStateFilterType() {
        return new ChargeStateFilterType();
    }

    /**
     * Create an instance of {@link KeepNThresholdType }
     * 
     */
    public KeepNThresholdType createKeepNThresholdType() {
        return new KeepNThresholdType();
    }

    /**
     * Create an instance of {@link ZeroesFilterType }
     * 
     */
    public ZeroesFilterType createZeroesFilterType() {
        return new ZeroesFilterType();
    }

    /**
     * Create an instance of {@link DataFileType }
     * 
     */
    public DataFileType createDataFileType() {
        return new DataFileType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProteowizardJobType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.plantcell.unimelb.edu.au/bioinformatics/schemas/v1/msconvertee", name = "ProteowizardJob")
    public JAXBElement<ProteowizardJobType> createProteowizardJob(ProteowizardJobType value) {
        return new JAXBElement<ProteowizardJobType>(_ProteowizardJob_QNAME, ProteowizardJobType.class, null, value);
    }

}
