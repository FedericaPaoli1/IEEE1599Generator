package com.ieee1599generator;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 *
 * @author federica
 */
public class Generator {

    private static final Logger LOGGER = Logger.getLogger(Generator.class.getName());

    public Generator() {
    }

    public void saveXMLFile(Document document) {
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
           LOGGER.log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

}
