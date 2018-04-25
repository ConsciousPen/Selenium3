package aaa.helpers.soap;

import org.w3c.dom.Document;
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

import static aaa.admin.modules.IAdmin.log;
import static aaa.modules.BaseTest.printToDebugLog;
import static aaa.modules.BaseTest.printToLog;

/**
 * @author - Tyrone C Jemison
 * @description - This class is used to create connections and send/receive messages via Soap.
 * Process - InputFile_XML to Raw_XML to SoapMessage(Request). Request sent. Response Received. SoapMessage(Response) to Raw_XML to OutputFile_XML
 */
public class SoapClient {

    public static SOAPMessage callSoapWebService(SOAPMessage soapRequest, String _soapEndpointURL, String _soapAction) throws SOAPException, IOException {

        // Create Soap Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        MimeHeaders headers = soapRequest.getMimeHeaders();
        headers.addHeader("SOAPAction", _soapAction);

        //Print request message for debugging
        printToDebugLog("++++ Beginning of SOAP Request ++++");
        printToDebugLog(convertSoapMessageToXMLString(soapRequest));
        printToDebugLog("---- End of SOAP Request ----");

        // Added to attempt to work around error with connection. Could be due to proxies.
        System.setProperty("java.net.useSystemProxies", "true");

        // Send SOAP Message to SOAP Server
        //soapResponse = soapConnection.call(createSOAPRequest(_soapAction), _soapEndpointURL);
        SOAPMessage soapResponse = soapConnection.call(soapRequest, _soapEndpointURL);

        //Close Connection
        soapConnection.close();

        //Print request message for debugging
        printToDebugLog("++++ Beginning of SOAP Response ++++");
        printToDebugLog(convertSoapMessageToXMLString(soapResponse));
        printToDebugLog("---- End of SOAP Response ----");

        return soapResponse;
    }

    public String convertXMLFileToString(String fileName) throws ParserConfigurationException, IOException, SAXException, TransformerException
    {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        InputStream inputStream = new FileInputStream(new File(fileName));
        org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
        StringWriter stw = new StringWriter();
        Transformer serializer = TransformerFactory.newInstance().newTransformer();
        serializer.transform(new DOMSource(doc), new StreamResult(stw));

        return stw.toString();
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
            log.info("TRANSFORMER EXCEPTION occurred when converting soapMessage to XML String", e.getCause());
            log.info(e.getStackTrace().toString());
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
            log.info("PARSER CONFIGURATION EXCEPTION occurred when building an xml file from string.", e.getCause());
            log.info(e.getStackTrace().toString());
        } catch (SAXException e) {
            log.info("SAX EXCEPTION occurred when building an xml file from string.", e.getCause());
            log.info(e.getStackTrace().toString());
        } catch (IOException e) {
            log.info("IO EXCEPTION occurred when building an xml file from string.", e.getCause());
            log.info(e.getStackTrace().toString());
        } catch (TransformerConfigurationException e) {
            log.info("TRANSFORMER CONFIGURATION EXCEPTION occurred when building an xml file from string.", e.getCause());
            log.info(e.getStackTrace().toString());
        } catch (TransformerException e) {
            log.info("TRANSFORMER EXCEPTION occurred when building an xml file from string.", e.getCause());
            log.info(e.getStackTrace().toString());
        }
    }

    public static String convertSoapMessageToXMLString(SOAPMessage message) {
        StringWriter sw = new StringWriter();
        String xmlString = "";
        // Converts message and puts into string using string writer.
        try {
            TransformerFactory.newInstance().newTransformer().transform(
                    new DOMSource(message.getSOAPPart()),
                    new StreamResult(sw));
            xmlString = sw.toString();
        } catch (TransformerException e) {
            log.info("TRANSFORMER EXCEPTION occurred when converting soapMessage to XML String", e.getCause());
            log.info(e.getStackTrace().toString());
        }
        return xmlString;
    }

    public static SOAPMessage getSOAPMessageFromRawString(String rawXmlString) throws SOAPException, IOException{
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(rawXmlString.getBytes(Charset.forName("UTF-8"))));
        return message;
    }
}
