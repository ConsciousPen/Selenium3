package aaa.helpers.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author - Tyrone C Jemison
 * @description - This class is used for parsing a XML to get a value using the associate tag name. Contains method for value validation. Can be used to parse SoapMessages if converted to XMLs.
 */
public class xmlParser
{
    /** Class Variables */
    static DocumentBuilderFactory _docFactory;
    static DocumentBuilder _docBuilder;
    static Document _doc;
    static String _systemUsername = System.getProperty("user.name");
    static String _defaultFullPath = "C:/Users/"+_systemUsername+"/Desktop/test.xml";

    public static ArrayList<String> ReturnValueFromXMLNode(String xmlFileName, String tagName) throws IOException, SAXException {
        try {
            _docFactory = DocumentBuilderFactory.newInstance();
            _docBuilder = _docFactory.newDocumentBuilder();
            File inputFile = new File("src\\test\\resources\\uploadingfiles\\xmlParser\\" + xmlFileName);
            _doc = _docBuilder.parse(inputFile);
            _doc.normalize();
        } catch (ParserConfigurationException e) {
            System.out.println("Failed creating document using default location of:" + _defaultFullPath);
            e.printStackTrace();
        }

        NodeList nodeList =  _doc.getElementsByTagName(tagName);
        Node node = null;
        ArrayList<String> valuesFound = new ArrayList<String>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
               valuesFound.add(node.getFirstChild().getNodeValue().toString());
            }
        }

        return valuesFound;
    }

    public static ArrayList<String> ReturnValueFromXMLNode(String filePath, String xmlFileName, String tagName) throws IOException, SAXException {
        try {
            _docFactory = DocumentBuilderFactory.newInstance();
            _docBuilder = _docFactory.newDocumentBuilder();
            File inputFile = new File(filePath + xmlFileName);
            _doc = _docBuilder.parse(inputFile);
            _doc.normalize();
        } catch (ParserConfigurationException e) {
            System.out.println("Failed creating document using default location of:" + _defaultFullPath);
            e.printStackTrace();
        }

        NodeList nodeList =  _doc.getElementsByTagName(tagName);
        Node node = null;
        ArrayList<String> valuesFound = new ArrayList<String>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                valuesFound.add(node.getFirstChild().getNodeValue().toString());
            }
        }

        return valuesFound;
    }

    public static Boolean DoesArrayListContainValue(ArrayList<String> arrayValuesReturned, String valueToValidate) {
        Boolean bFoundValue = false;
        for (String value : arrayValuesReturned) {
            if (value.equalsIgnoreCase(valueToValidate)) {
                bFoundValue = true;
            }
        }
        return bFoundValue;
    }
}
