package com.ieee1599generator;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Contains utility methods to format the entire IEEE1599 document
 *
 * @author Federica Paoli', id: 961887, e-mail:
 * federica.paoli1@studenti.unimi.it
 */
public class FormatterUtils {

    private static final Logger logger = LogManager.getLogger(FormatterUtils.class.getName());

    /**
     * <p>
     * adds a child element to the input element setting four attributes of the
     * child element
     * </p>
     *
     * @param document the input document
     * @param element the element whose child is to be appended
     * @param childName the string constituting the name of the child
     * @param firstAttributeName the string constituting the first attribute
     * name
     * @param firstAttributeValue the string constituting the first attribute
     * value
     * @param secondAttributeName the string constituting the second attribute
     * name
     * @param secondAttributeValue the string constituting the second attribute
     * @param thirdAttributeName the string constituting the third attribute
     * name
     * @param thirdAttributeValue the string constituting the third attribute
     * value
     * @param fourthAttributeName the string constituting the fourth attribute
     * name
     * @param fourthAttributeValue the string constituting the fourth attribute
     * value
     *
     */
    protected static void addElementAndSetFourAttributes(Document document, Element element, String childName, String firstAttributeName, String firstAttributeValue, String secondAttributeName, String secondAttributeValue, String thirdAttributeName, String thirdAttributeValue, String fourthAttributeName, String fourthAttributeValue) {
        Element child = addElementToDocument(document, childName);

        setAttributeOfElement(child, firstAttributeName, firstAttributeValue);
        setAttributeOfElement(child, secondAttributeName, secondAttributeValue);
        setAttributeOfElement(child, thirdAttributeName, thirdAttributeValue);
        setAttributeOfElement(child, fourthAttributeName, fourthAttributeValue);

        appendChildToElement(element, child);
    }

    /**
     * <p>
     * adds a child element to the input element setting three attributes of the
     * child element
     * </p>
     *
     * @param document the input document
     * @param element the element whose child is to be appended
     * @param childName the string constituting the name of the child
     * @param firstAttributeName the string constituting the first attribute
     * name
     * @param firstAttributeValue the string constituting the first attribute
     * value
     * @param secondAttributeName the string constituting the second attribute
     * name
     * @param secondAttributeValue the string constituting the second attribute
     * @param thirdAttributeName the string constituting the third attribute
     * name
     * @param thirdAttributeValue the string constituting the third attribute
     * value
     *
     * @return the added Element
     */
    protected static Element addElementAndSetThreeAttributesReturningTheElement(Document document, Element element, String childName, String firstAttributeName, String firstAttributeValue, String secondAttributeName, String secondAttributeValue, String thirdAttributeName, String thirdAttributeValue) {
        Element child = addElementToDocument(document, childName);

        setAttributeOfElement(child, firstAttributeName, firstAttributeValue);
        setAttributeOfElement(child, secondAttributeName, secondAttributeValue);
        setAttributeOfElement(child, thirdAttributeName, thirdAttributeValue);

        appendChildToElement(element, child);

        return child;
    }

    /**
     * <p>
     * adds a child element to the input element setting two attributes of the
     * child element
     * </p>
     *
     * @param document the input document
     * @param element the element whose child is to be appended
     * @param childName the string constituting the name of the child
     * @param firstAttributeName the string constituting the first attribute
     * name
     * @param firstAttributeValue the string constituting the first attribute
     * value
     * @param secondAttributeName the string constituting the second attribute
     * name
     * @param secondAttributeValue the string constituting the second attribute
     * value
     *
     */
    protected static void addElementAndSetTwoAttributes(Document document, Element element, String childName, String firstAttributeName, String firstAttributeValue, String secondAttributeName, String secondAttributeValue) {
        Element child = addElementToDocument(document, childName);

        setAttributeOfElement(child, firstAttributeName, firstAttributeValue);
        setAttributeOfElement(child, secondAttributeName, secondAttributeValue);

        appendChildToElement(element, child);
    }

    /**
     * <p>
     * adds a child element to the input element setting two attributes of the
     * child element
     * </p>
     *
     * @param document the input document
     * @param element the element whose child is to be appended
     * @param childName the string constituting the name of the child
     * @param firstAttributeName the string constituting the first attribute
     * name
     * @param firstAttributeValue the string constituting the first attribute
     * value
     * @param secondAttributeName the string constituting the second attribute
     * name
     * @param secondAttributeValue the string constituting the second attribute
     * value
     *
     * @return the added Element
     */
    protected static Element addElementAndSetTwoAttributesReturningTheElement(Document document, Element element, String childName, String firstAttributeName, String firstAttributeValue, String secondAttributeName, String secondAttributeValue) {
        Element child = addElementToDocument(document, childName);

        setAttributeOfElement(child, firstAttributeName, firstAttributeValue);
        setAttributeOfElement(child, secondAttributeName, secondAttributeValue);

        appendChildToElement(element, child);

        return child;
    }

    /**
     * <p>
     * adds a child element to the input element setting an attribute of the
     * child element
     * </p>
     *
     * @param document the input document
     * @param element the element whose child is to be appended
     * @param childName the string constituting the name of the child
     * @param attributeName the string constituting the attribute name
     * @param attributeValue the string constituting the attribute value
     *
     * @return the added Element
     */
    protected static Element addElementAndSetOneAttributeReturningTheElement(Document document, Element element, String childName, String attributeName, String attributeValue) {
        Element child = addElementToDocument(document, childName);

        setAttributeOfElement(child, attributeName, attributeValue);

        appendChildToElement(element, child);

        return child;
    }

    /**
     * <p>
     * adds a child element to the input element setting the text content of the
     * child element
     * </p>
     *
     * @param document the input document
     * @param element the element whose child is to be appended
     * @param childName the string constituting the name of the child
     * @param textContent the string constituting the text content
     */
    protected static void addElementAndSetTextContext(Document document, Element element, String childName, String textContent) {
        Element child = addElementToDocument(document, childName);

        setTextContentOfElement(child, textContent);

        appendChildToElement(element, child);
    }

    /**
     * <p>
     * adds a child element to the input element
     * </p>
     *
     * @param document the input document
     * @param element the element whose child is to be appended
     * @param childName the string constituting the name of the child
     *
     */
    protected static void addElement(Document document, Element element, String childName) {
        Element child = addElementToDocument(document, childName);

        appendChildToElement(element, child);
    }

    /**
     * <p>
     * adds a child element to the input element and returns it
     * </p>
     *
     * @param document the input document
     * @param element the element whose child is to be appended
     * @param childName the string constituting the name of the child
     *
     * @return the added child element
     */
    protected static Element addElementAndReturnIt(Document document, Element element, String childName) {
        Element child = addElementToDocument(document, childName);

        appendChildToElement(element, child);

        return child;
    }

    /**
     * <p>
     * adds the IEEE1599 element of the general layer to the IEEE1599 document
     * </p>
     *
     * @param document the input document
     * @param creator the value of the creator attribute of the ieee1599 element
     * @param version the value of the version attribute of the ieee1599 element
     *
     * @return the added Element
     */
    protected static Element addIeee1599Element(Document document, String creator, double version) {
        Element ieee1599 = addElementToDocument(document, "ieee1599");

        setAttributeOfElement(ieee1599, "creator", creator);
        setAttributeOfElement(ieee1599, "version", "" + version);

        document.appendChild(ieee1599);
        return ieee1599;
    }

    /**
     * <p>
     * adds an element to the IEEE1599 document
     * </p>
     *
     * @param document the document whose element is to be created
     * @param elementName the element constituting the element to be added to
     * the document
     *
     * @return the added Element
     */
    protected static Element addElementToDocument(Document document, String elementName) {
        return document.createElement(elementName);
    }

    /**
     * <p>
     * appends a child to and element of the IEEE1599 document
     * </p>
     *
     * @param firstElement the element whose child is to be appended
     * @param secondElement the element constituting the child of the first
     * element
     */
    protected static void appendChildToElement(Element firstElement, Element secondElement) {
        firstElement.appendChild(secondElement);
    }

    /**
     * <p>
     * sets an attribute of an element of the IEEE1599 document
     * </p>
     *
     * @param element the element whose attribute is to be set
     * @param attributeName the string constituting the attribute name
     * @param attributeValue the string constituting the attribute value
     */
    protected static void setAttributeOfElement(Element element, String attributeName, String attributeValue) {
        element.setAttribute(attributeName, attributeValue);
    }

    /**
     * <p>
     * sets the text content of an element of the IEEE1599 document
     * </p>
     *
     * @param element the element whose text is to be set
     * @param string the string constituting the text content
     */
    private static void setTextContentOfElement(Element element, String string) {
        element.setTextContent(string);
    }

    /**
     * <p>
     * initializes the document field
     * </p>
     *
     * @return the created document
     *
     * @throws ParserConfigurationException if there is a configuration error
     * for the DocumentBuilderFactory class
     */
    protected static Document createDocument() throws ParserConfigurationException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            logger.error(ex.getClass() + " : the document cannot be created due to a configuration error");
            throw new ParserConfigurationException(" the document cannot be created due to a configuration error");
        }
        return docBuilder.newDocument();

    }

    /**
     * <p>
     * saves document into a xml file
     * </p>
     *
     * @param document the document to save
     *
     * @throws TransformerConfigurationException if there is a configuration
     * error for the DocumentBuilderFactory class
     * @throws TransformerException if there is a configuration error for the
     * DocumentBuilderFactory class
     */
    protected static void saveXMLFile(Document document) throws TransformerConfigurationException, TransformerException {
        Result output = new StreamResult(new File("ieee1599.xml"));
        Source input = new DOMSource(document);
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t;
            t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
            t.transform(input, output);
        } catch (TransformerConfigurationException ex) {
            logger.error(ex.getClass() + " : the document cannot be created due to a configuration error");
            throw new TransformerConfigurationException(" the document cannot be created due to a configuration error");
        } catch (TransformerException ex) {
            logger.error(ex.getClass() + " : the document cannot be created due to a configuration error");
            throw new TransformerException(" the document cannot be created due to a configuration error");
        }
    }

    /**
     * <p>
     * converts the input map content into a string
     * </p>
     *
     * @param <T> the type of arguments to the map
     * @param <R> the type of arguments to the map
     * @param map the input map
     *
     * @return the formatted string
     */
    protected static <T, R> String mapAsString(Map<T, R> map) {
        String mapAsString = map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        return mapAsString;
    }

    /**
     * <p>
     * converts the input map content into a string
     * </p>
     *
     * @param map the input map
     *
     * @return the formatted string
     */
    protected static String notesMapAsString(Map<Float, List<String>> map) {
        String mapAsString = map.keySet().stream()
                .map(key -> key + "=" + map.get(key).stream().map(s -> s).collect(Collectors.joining(", ", "[", "]")))
                .collect(Collectors.joining("; ", "{", "}"));
        return mapAsString;
    }

    /**
     * <p>
     * converts the input map content into a string
     * </p>
     *
     * @param map the input map
     *
     * @return the formatted string
     */
    protected static String instrumentNotesMapAsString(Map<Double, int[]> map) {
        String mapAsString = map.keySet().stream()
                .map(key -> key + "=" + map.get(key)[0] + "/" + map.get(key)[1])
                .collect(Collectors.joining(", ", "{", "}"));
        return mapAsString;
    }

}
