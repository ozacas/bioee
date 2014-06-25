package au.edu.unimelb.plantcell.servers.proteowizard.endpoints;

import javax.xml.bind.annotation.XmlRootElement;

import au.edu.unimelb.plantcell.servers.proteowizard.jaxb.ProteowizardJobType;

@XmlRootElement
public class ProteowizardJob extends ProteowizardJobType {
	// nothing here, just @XmlRootElement needed by JAXB for correct marshalling/unmarshalling of data
}
