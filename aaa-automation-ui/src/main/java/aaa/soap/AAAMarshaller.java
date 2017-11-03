package aaa.soap;

import aaa.rest.IModel;
import toolkit.exceptions.IstfException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class AAAMarshaller {
	//todo request adding to the etcsa core
	public static String modelToXml(IModel responseObj) {
		StringWriter sw = new StringWriter();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(responseObj.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(responseObj, sw);
		} catch (JAXBException e) {
			throw new IstfException("Unable to marshal object", e.getCause());
		}
		return sw.toString();
	}
}
