package aaa.helpers.xml;

import toolkit.exceptions.IstfException;

import javax.xml.bind.*;
import java.io.StringReader;

public class XmlHelper {
	public static <T> T xmlToModel(String xmlContent, Class<T> modelClass) {
		return xmlToModel(xmlContent, modelClass, false);
	}

	@SuppressWarnings("unchecked")
	public static <T> T xmlToModel(String xmlContent, Class<T> modelClass, boolean strictMatch) {
		T model;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(modelClass);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			if (strictMatch) {
				jaxbUnmarshaller.setEventHandler(event -> false);
			}
			StringReader reader = new StringReader(xmlContent);
			model = (T) jaxbUnmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new IstfException("Unable to unmarshal object", e);
		}
		return model;
	}
}
