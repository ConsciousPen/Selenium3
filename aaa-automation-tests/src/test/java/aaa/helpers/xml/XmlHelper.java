package aaa.helpers.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.exceptions.IstfException;

import javax.xml.bind.*;
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
			JAXBContext jaxbContext = JAXBContext.newInstance(modelClass);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

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
}