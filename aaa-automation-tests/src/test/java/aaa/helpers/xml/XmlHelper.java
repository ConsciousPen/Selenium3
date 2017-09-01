package aaa.helpers.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.exceptions.IstfException;

import javax.xml.bind.*;
import java.io.StringReader;

public class XmlHelper {
	private static Logger log = LoggerFactory.getLogger(XmlHelper.class);

	public static <T> T xmlToModel(String xmlContent, Class<T> modelClass) {
		return xmlToModel(xmlContent, modelClass, true);
	}

	@SuppressWarnings("unchecked")
	public static <T> T xmlToModel(String xmlContent, Class<T> modelClass, boolean strictMatch) {
		T model;

		log.debug(String.format("Getting \"%1$s\" object model from provided xml content%2$s", modelClass.getSimpleName(), strictMatch ? " with strict match parsing." : "."));
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(modelClass);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			if (strictMatch) {
				jaxbUnmarshaller.setEventHandler(event -> false);
			}

			StringReader reader = new StringReader(xmlContent);
			model = (T) jaxbUnmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new IstfException(String.format("Unable to unmarshal xml content to model: \"%s\".", modelClass.getSimpleName()), e);
		}
		log.debug("Xml contents unmarshalling was successfull.");
		return model;
	}
}
