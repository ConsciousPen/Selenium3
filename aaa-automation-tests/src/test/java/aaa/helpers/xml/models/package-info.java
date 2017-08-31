@XmlSchema(
		namespace = "http://www.aaancnuie.com/DCS/2012/01/DocumentCreation",
		elementFormDefault = XmlNsForm.QUALIFIED,
		xmlns = {
				@XmlNs(prefix="doc", namespaceURI="http://www.aaancnuie.com/DCS/2012/01/DocumentCreation"),
				@XmlNs(prefix="aaan", namespaceURI="http://www.aaancnuit.com.AAANCNU_IDocumentCreation_version1"),
				@XmlNs(prefix="xsi", namespaceURI="http://www.w3.org/2001/XMLSchema-instance")
		}
)

package aaa.helpers.xml.models;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
