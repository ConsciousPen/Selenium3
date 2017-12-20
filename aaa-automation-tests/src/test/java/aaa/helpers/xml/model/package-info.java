@XmlSchema(
		namespace = DocGenEnum.XmlnsNamespaces.DOC_URI,
		elementFormDefault = XmlNsForm.QUALIFIED,
		xmlns = {
				@XmlNs(prefix = DocGenEnum.XmlnsNamespaces.DOC_PREFIX, namespaceURI = DocGenEnum.XmlnsNamespaces.DOC_URI),
				@XmlNs(prefix = DocGenEnum.XmlnsNamespaces.AAAN_PREFIX, namespaceURI = DocGenEnum.XmlnsNamespaces.AAAN_URI),
				@XmlNs(prefix = DocGenEnum.XmlnsNamespaces.XSI_PREFIX, namespaceURI = DocGenEnum.XmlnsNamespaces.XSI_URI)
		}
)

package aaa.helpers.xml.model;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import aaa.main.enums.DocGenEnum;
