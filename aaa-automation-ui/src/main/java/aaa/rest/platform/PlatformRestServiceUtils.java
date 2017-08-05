/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.rest.platform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.jsoup.Jsoup;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.exigen.ipb.etcsa.base.app.Application;

import toolkit.config.ClassConfigurator;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;

/**
 * Entity insertion utils using Platform JPA REST service
 * @author Deivydas Piliukaitis
 */
public class PlatformRestServiceUtils {

    @ClassConfigurator.Configurable
    private static int CONNECT_TIMEOUT = 0;
    @ClassConfigurator.Configurable
    private static int READ_TIMEOUT = 0;
    @ClassConfigurator.Configurable
    private static String USR = "qa";
    @ClassConfigurator.Configurable
    private static String PWD = "qa";
    @ClassConfigurator.Configurable
    private String endpoint = "";
    @ClassConfigurator.Configurable
    public String DATASETS_ROOT = "rest/platform-rs/";

    public static final String ENCODING = "UTF-8";

    private Builder builder;
    private BaseDataSetLoader baseDataSetLoader = new BaseDataSetLoader();

    public String xmlString;

    public PlatformRestServiceUtils() {
        initiateClient(USR, PWD);
    }

    public PlatformRestServiceUtils(String username, String password) {
        initiateClient(username, password);
    }

    /**
     * Create new Web Client to REST with the given @params
     * @param username
     * @param password
     */
    public void createNewWebClient(String username, String password) {
        initiateClient(username, password);
    }

    /**
     * Initiating web client
     * @param username, password used for authentication
     */
    public void initiateClient(String username, String password) {
        endpoint = generateEndpoint();

        ClientConfig configuration = new ClientConfig()
                .property("jersey.config.client.connectTimeout", Integer.valueOf(CONNECT_TIMEOUT))
                .property("jersey.config.client.readTimeout", Integer.valueOf(READ_TIMEOUT))
                .register(MultiPartFeature.class);

        Client client = ClientBuilder.newClient(configuration);

        WebTarget rootTarget = client.target(endpoint);

        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("username", new String(Base64.encodeBase64(username.getBytes())));
        headers.add("password", new String(Base64.encodeBase64(password.getBytes())));
        headers.add("Accept-Language", "en-us");

        builder = rootTarget.request().headers(headers);
    }

    private String generateEndpoint() {
        String endpoint = PropertyProvider.getProperty(String.format("rest.%1$s.endpoint", "platform-rs"));
        if (endpoint.isEmpty()) {
            String[] slitURL = Application.formatURL(Application.AppType.EU).split(":");
            endpoint = slitURL[0] + ":" + slitURL[1] + ":8020/ipb-testsupport-app-mssql/services/platform-rs/dev/v1/persistence/entity";
        }
        return endpoint;
    }

    /**
     * Posts entity which represented in xml file.
     * @param xmlName file name representing entity
     * @return inserted w3c Document
     */
    public Document insertEntity(String xmlName) {
        HashMap<String, String> placeHolderValues = new HashMap<>();
        return insertEntity(xmlName, placeHolderValues);
    }

    /**
     * Posts entity which represented in xml file.
     * @param doc - w3c Document which represents entity
     */
    public Document insertEntity(Document doc) {
        HashMap<String, String> xpathParams = new HashMap<>();
        HashMap<String, String> params = new HashMap<>();
        return insertEntity(doc, xpathParams, params);
    }

    /**
     * Posts entity which represented in xml file, maps place holder values.
     * @param doc - w3c Document which represents entity
     * @param placeHolderValues values to be replaced in xml file matching attribute name
     * @return inserted w3c Document
     */
    public Document insertEntity(Document doc, Map<String, String> placeHolderValues) {
        HashMap<String, String> xpathParams = new HashMap<>();
        return insertEntity(doc, xpathParams, placeHolderValues);
    }

    /**
     * Posts entity which represented in xml file, maps place holder values.
     * @param xmlName file name representing entity
     * @param placeHolderValues values to be replaced in xml file matching attribute name
     * @return inserted w3c Document
     */
    public Document insertEntity(String xmlName, HashMap<String, String> placeHolderValues) {
        try {
            xmlString = baseDataSetLoader.findXmlEntitySource(DATASETS_ROOT + xmlName);
            xmlString = baseDataSetLoader.applyDateOffset(xmlString, 0);
            xmlString = baseDataSetLoader.applyPlaceholderValues(xmlString, placeHolderValues);

            InputStream stream = IOUtils.toInputStream(xmlString, ENCODING);

            Response response = builder.post(Entity.entity(entityAttachment(stream), MediaType.MULTIPART_FORM_DATA_TYPE));
            Assert.assertTrue(response.getStatus() == 200, "Insertion failed with status: " + response.getStatus());

            xmlString = response.readEntity(String.class);
            Document xml = loadXMLFrom(new ByteArrayInputStream(xmlString.getBytes()));

            return xml;
        } catch (IOException e) {
            throw new IstfException(e);
        }
    }

    /**
     * Posts entity which represented in xml file, maps place holder values.
     * @param doc - w3c Document which represents entity
     * @param xpathPlaceHolderValues values to be replaced in xml file via xpath
     * @param placeHolderValues values to be replaced in xml file matching attribute name
     * @return inserted w3c Document
     */
    public Document insertEntity(Document doc, Map<String, String> xpathPlaceHolderValues, Map<String, String> placeHolderValues) {
        try {

            doc = applyValues(doc, xpathPlaceHolderValues);
            xmlString = toString(doc);
            xmlString = baseDataSetLoader.applyDateOffset(xmlString, 0);
            xmlString = baseDataSetLoader.applyPlaceholderValues(xmlString, placeHolderValues);

            InputStream stream = IOUtils.toInputStream(xmlString, ENCODING);

            Response response = builder.post(Entity.entity(entityAttachment(stream), MediaType.MULTIPART_FORM_DATA_TYPE));

            Assert.assertTrue(response.getStatus() == 200);

            xmlString = response.readEntity(String.class);
            doc = loadXMLFrom(new ByteArrayInputStream(xmlString.getBytes()));
            return doc;
        } catch (IOException e) {
            throw new IstfException(e);
        }
    }

    /**
     * Forms multi part entity
     * @param stream - input stream made from xml
     * @return FormDataMultiPart entity
     */
    private FormDataMultiPart entityAttachment(InputStream stream) {
        FormDataMultiPart fDMP = new FormDataMultiPart();
        fDMP.field("data", stream, MediaType.APPLICATION_XML_TYPE)
                .contentDisposition(FormDataContentDisposition.name("data").fileName(RandomStringUtils.randomAlphanumeric(10)).build());
        return fDMP;
    }

    /**
     * Gets value from xml by @param from just inserted entity
     * @param attributeName - attribute name in xml (e.g. policyNumber)
     * @return value from xml
     */
    public String getXmlAttributeValue(String attributeName) {
        return getXmlAttributeValue(xmlString, attributeName);
    }

    /**
     * Gets value from xml by @param
     * @param xml - Document type xml in which you want to search
     * @param xpath - locator of element in xml (e.g. //policyNumber)
     * @return value from xml
     */
    public String getXmlAttributeValue(Document xml, String xpath) {
        return findNode(xml, xpath).getTextContent();
    }

    /**
     * Gets value from xml by @param
     * @param providedXmlString - provided Xml String
     * @param attributeName - attribute name in xml (e.g. policyNumber)
     * @return value from xml
     */
    public String getXmlAttributeValue(String providedXmlString, String attributeName) {
        return Jsoup.parse(providedXmlString).select(attributeName).first().text();
    }

    /**
     * Load file from project and convert to w3c Document
     * @param xmlName
     * @return Document type xml
     */
    public Document loadXmlFrom(String xmlName) {
        try {
            xmlString = baseDataSetLoader.findXmlEntitySource(xmlName);
            InputStream stream = IOUtils.toInputStream(xmlString, ENCODING);
            Assert.assertNotNull(stream, "File not found: " + xmlName);
            return loadXMLFrom(stream);
        } catch (IOException e) {
            throw new IstfException(e);
        }
    }

    /**
     * Converts inputStream of XML to w3c Document
     * @param is - inputStream
     * @return Document type xml
     * @throws IllegalArgumentException
     */
    public Document loadXMLFrom(InputStream is) {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            is.close();
        } catch (javax.xml.parsers.ParserConfigurationException ex) {
            throw new IllegalArgumentException("DocumentBuilderFactory configuration exception", ex);
        } catch (org.xml.sax.SAXException ex) {
            throw new IllegalArgumentException("DocumentBuilderFactory parsing exception", ex);
        } catch (java.io.IOException ex) {
            throw new IllegalArgumentException("I/O Exception", ex);
        }
        return doc;
    }

    /**
     * Change values by xpath.
     * @param xmlDocument
     * @param params - key is xpath and value is new value for the node
     * @return xmlDocument
     */
    public Document applyValues(Document xmlDocument, Map<String, String> params) {
        for (Entry<String, String> param : params.entrySet()) {
            Node node = findNode(xmlDocument, param.getKey());
            Assert.assertNotNull(node, "Node not found" + param.getKey());
            node.setTextContent(param.getValue());
        }
        return xmlDocument;
    }

    /**
     * Find nodes by xpath in the document
     * @param xmlDocument
     * @param xpath - locator of elements in xml
     * @return NodeList - list of nodes
     * @throws IllegalArgumentException if node is not found
     */
    public NodeList findNodes(Node xmlDocument, String xpath) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            NodeList nodeList = (NodeList) xPath.compile(xpath).evaluate(xmlDocument, XPathConstants.NODESET);
            Assert.assertNotNull(nodeList, "Node not found" + xpath);
            return nodeList;
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Find node by xpath in the document
     * @param xmlDocument
     * @param xpath- locator of element in xml
     * @return Node
     * @throws IllegalArgumentException if node is not found
     */
    public Node findNode(Node xmlDocument, String xpath) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            Node node = (Node) xPath.compile(xpath).evaluate(xmlDocument, XPathConstants.NODE);
            Assert.assertNotNull(node, "Node not found" + xpath);
            return node;
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Find node by xpath in the document
     * @param xmlDocument
     * @param xpath
     * @return Boolean
     * @throws IllegalArgumentException if node is not found
     */
    public Boolean isNodeExist(Node xmlDocument, String xpath) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            NodeList nodeList = (NodeList) xPath.compile(xpath).evaluate(xmlDocument, XPathConstants.NODESET);
            return nodeList.getLength() != 0;
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Create Node by given param
     * @param document - node will be generated in this document
     * @param parentElement - parent element of the node you creating
     * @param attributeName - new Node attribute name
     * @param value - new Node attribute value
     * @return created Node
     */
    public Node createNode(Document document, Node parentElement, String attributeName, String value) {
        return parentElement.appendChild(document.createElement(attributeName)).appendChild(document.createTextNode(value));
    }

    /**
     * Transforms {@link Node} to {@link StringBuffer}. {@link StringBuilder} is not used as it was required more investigation.
     * @param document
     * @return
     */
    public String toString(Node document) {
        try {
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(document);
            StringWriter xmlAsWriter = new StringWriter();
            StreamResult result = new StreamResult(xmlAsWriter);
            transformer.transform(source, result);
            StringBuffer xmldata = xmlAsWriter.getBuffer();
            return xmldata.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
