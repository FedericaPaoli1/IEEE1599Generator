package com.ieee1599generator;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author federica
 */
public class Generator {

    public static void main(String[] args) throws TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder docBuilder;
            docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element ieee1599 = doc.createElement("ieee1599");
            ieee1599.setAttribute("version", "1.0");
            doc.appendChild(ieee1599);

            // general
            Element general = doc.createElement("general");
            ieee1599.appendChild(general);

            Element description = doc.createElement("description");
            general.appendChild(description);

            Element main_title = doc.createElement("main_title");
            description.appendChild(main_title);

            Element author = doc.createElement("author");
            description.appendChild(author);

            // logic
            Element logic = doc.createElement("logic");
            ieee1599.appendChild(logic);

            Element spine = doc.createElement("spine");
            logic.appendChild(spine);

            Element event = doc.createElement("event");
            event.setAttribute("id", "event_0");
            event.setAttribute("timing", "null");
            event.setAttribute("hpos", "null");
            spine.appendChild(event);

            // salvataggio
            Result output = new StreamResult(new File("ieee1599.xml"));
            Source input = new DOMSource(doc);
            try {
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer t;
                t = tf.newTransformer();
                t.setOutputProperty(OutputKeys.INDENT, "yes");
                t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
                t.transform(input, output);
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
