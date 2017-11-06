package aaa.helpers.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.exceptions.IstfException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public class XmlHelper {
	private static Logger log = LoggerFactory.getLogger(XmlHelper.class);

	public static <T> T xmlToModel(String xmlContent, Class<T> modelClass) {
		return xmlToModel(xmlContent, modelClass, true);
	}

	public static <T> T xmlToModel(String xmlContent, Class<T> modelClass, boolean strictMatch) {
		T model;
		StreamSource source = new StreamSource(new StringReader(xmlContent));

		log.debug(String.format("Getting \"%1$s\" object model from provided xml content%2$s.", modelClass.getSimpleName(), strictMatch ? " with strict match parsing" : ""));
		try {
			Unmarshaller jaxbUnmarshaller = getUnmarshaller(modelClass);
			if (strictMatch) {
				jaxbUnmarshaller.setEventHandler(event -> false);
			}

			model = jaxbUnmarshaller.unmarshal(source, modelClass).getValue();
		} catch (JAXBException e) {
			throw new IstfException(String.format("Unable to unmarshal xml content to model: \"%s\".", modelClass.getSimpleName()), e);
		}

		log.debug("Xml unmarshalling was successful.");
		return model;
	}



	/**
	 * Create JAXB model class Using part of xml
	 * @param xmlContent part of xml
	 * @param modelClass jaxb model class
	 * @return XML content in String format
	 */
	public static <T> T xmlToModelByPartOfXml(String xmlContent, Class<T> modelClass)  {
		T model = null;
		XMLInputFactory xif = XMLInputFactory.newFactory();
		StreamSource source = new StreamSource(new StringReader(xmlContent));
		try{
			XMLStreamReader xsr = xif.createXMLStreamReader(source);
			Unmarshaller unmarshaller = getUnmarshaller(modelClass);
			JAXBElement<T> jb = unmarshaller.unmarshal(xsr, modelClass);
			xsr.close();
			model = jb.getValue();
		} catch (XMLStreamException | JAXBException e){
			log.error("Error appears in attempt to unmarshal xml");
		}

		return model;
	}

	private static <T> Unmarshaller getUnmarshaller(Class<T> modelClass) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(modelClass);
		return jaxbContext.createUnmarshaller();
	}
}
