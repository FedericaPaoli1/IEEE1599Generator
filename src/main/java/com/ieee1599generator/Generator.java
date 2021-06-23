package com.ieee1599generator;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    public int getMeasuresNumber(int bpm, long pieceLength, String metre) {
        double bps = (double) bpm / 60;
        System.out.println("bps: " + bps);
        int beatsNumber = Integer.parseInt(String.valueOf(metre.charAt(0)));
        System.out.println("Number of beats: " + beatsNumber);
        double oneBeatLength = 1 / bps;
        System.out.println("oneBeatLength: " + oneBeatLength);
        return (int) (pieceLength / (oneBeatLength * beatsNumber));
    }

    public int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static void main(String[] args) throws TransformerException {
        Generator g = new Generator();
        // parametri da chiedere all'utente a riga di comando
        String titleName = "Title";
        String authorName = "Author";
        int minutes = 2;
        String pieceLength = "PT" + minutes + "M";
        int bpm = 108;
        int num = 4;
        int den = 4;
        String metre = num + ":" + den;
        int instrumentsNumber = 5;
        int maxNumberOfPlayedNotes = 10;
        //

        long seconds = Duration.parse(pieceLength).getSeconds();
        System.out.println("Piece length: " + seconds);
        int measuresNumber = g.getMeasuresNumber(bpm, seconds, metre);
        System.out.println("Measures number: " + measuresNumber);
        int notesNumber = g.getRandomNumber(1, maxNumberOfPlayedNotes + 1);
        System.out.println("Notes number: " + notesNumber);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder docBuilder;
            docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element ieee1599 = doc.createElement("ieee1599");
            ieee1599.setAttribute("creator", "Federica Paoli'");
            ieee1599.setAttribute("version", "1.0");
            doc.appendChild(ieee1599);

            // general
            Element general = doc.createElement("general");
            ieee1599.appendChild(general);

            Element description = doc.createElement("description");
            general.appendChild(description);

            Element main_title = doc.createElement("main_title");
            main_title.setTextContent(titleName);
            description.appendChild(main_title);

            Element author = doc.createElement("author");
            author.setTextContent(authorName);
            description.appendChild(author);

            // logic
            Element logic = doc.createElement("logic");
            ieee1599.appendChild(logic);

            Element spine = doc.createElement("spine");
            logic.appendChild(spine);

            for (int i = 1; i <= instrumentsNumber; i++) {
                Element event = doc.createElement("event");
                event.setAttribute("id", "Instrument_" + i + "_measure1_ev0");
                event.setAttribute("timing", "null");
                event.setAttribute("hpos", "null");
                spine.appendChild(event);
            }

            for (int i = 1; i <= instrumentsNumber; i++) {
                Element event = doc.createElement("event");
                event.setAttribute("id", "TimeSignature_Instrument_" + i + "_1");
                event.setAttribute("timing", "null");
                event.setAttribute("hpos", "null");
                spine.appendChild(event);
            }

            for (int j = 1; j <= measuresNumber; j++) {
                int randomInstrumentsNumber = g.getRandomNumber(1, instrumentsNumber + 1);
                for (int k = 1; k <= notesNumber; k++) {
                    for (int i = 1; i <= randomInstrumentsNumber; i++) {
                        Element event = doc.createElement("event");
                        event.setAttribute("id", "Instrument_" + i + "_measure" + j + "_ev" + k);
                        event.setAttribute("timing", "null");
                        event.setAttribute("hpos", "null");
                        spine.appendChild(event);
                    }
                }
            }

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
