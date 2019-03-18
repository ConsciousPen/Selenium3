@XmlSchema(
		namespace = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI,
		elementFormDefault = XmlNsForm.QUALIFIED,
		xmlns = {
				@XmlNs(prefix = DocGenEnum.XmlnsNamespaces.DOC_PREFIX, namespaceURI = DocGenEnum.XmlnsNamespaces.PAS_DOC_URI),
				@XmlNs(prefix = DocGenEnum.XmlnsNamespaces.XSI_PREFIX, namespaceURI = DocGenEnum.XmlnsNamespaces.XSI_URI)
		}
)

package aaa.helpers.xml.model.pasdoc;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import aaa.main.enums.DocGenEnum;
