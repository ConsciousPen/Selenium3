package aaa.helpers.soap;

import org.w3c.dom.Document;
import akka.testkit.TestActor;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.nio.charset.Charset;

import static javax.xml.soap.SOAPMessage.*;
import static scala.collection.concurrent.RestartException.getMessage;

public class SoapClient {

    public static SOAPMessage callSoapWebService(SOAPMessage soapRequest, String _soapEndpointURL, String _soapAction) {

        SOAPMessage soapResponse = null;
        try {
            // Create Soap Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            MimeHeaders headers = soapRequest.getMimeHeaders();
            headers.addHeader("SOAPAction", _soapAction);

            //Print request message for debugging
            System.out.println("Request SOAP Message: ");
            soapRequest.writeTo(System.out);
            System.out.println("");

            // Added to attempt to work around error with connection. Could be due to proxies.
            System.setProperty("java.net.useSystemProxies", "true");

            // Send SOAP Message to SOAP Server
            //soapResponse = soapConnection.call(createSOAPRequest(_soapAction), _soapEndpointURL);
            soapResponse = soapConnection.call(soapRequest, _soapEndpointURL);

            //Close Connection
            soapConnection.close();
        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("!! Error occurred while sending SOAP Request to Server !!");
            System.err.println("!! Make sure you have the correct endpoint URL and SOAPAction !!");
            e.printStackTrace();
        }

        //Print request message for debugging
        System.out.println("Response SOAP Message: ");
        try {
            soapResponse.writeTo(System.out);
        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("");

        return soapResponse;
    }

    public String convertXMLFileToString(String fileName)
    {
        try{
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            InputStream inputStream = new FileInputStream(new File(fileName));
            org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
            StringWriter stw = new StringWriter();
            Transformer serializer = TransformerFactory.newInstance().newTransformer();
            serializer.transform(new DOMSource(doc), new StreamResult(stw));
            return stw.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void convertSoapMessageToXMLFile(SOAPMessage message, String fileOutputLocation) {
        StringWriter sw = new StringWriter();
        String xmlString = "";
        // Converts message and puts into string using string writer.
        try {
            TransformerFactory.newInstance().newTransformer().transform(
                    new DOMSource(message.getSOAPPart()),
                    new StreamResult(sw));
            xmlString = sw.toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        // Builds xml file from string.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(
                    new StringReader(xmlString)));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer myTransformer = transformerFactory.newTransformer();
            Source src = new DOMSource(document);
            Result destination = new StreamResult(new File(fileOutputLocation));
            myTransformer.transform(src, destination);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static SOAPMessage getSOAPMessageFromRawString(String rawXmlString) {
        SOAPMessage message = null;
        try {
            MessageFactory factory = MessageFactory.newInstance();
            message = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(rawXmlString.getBytes(Charset.forName("UTF-8"))));
        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }
}
