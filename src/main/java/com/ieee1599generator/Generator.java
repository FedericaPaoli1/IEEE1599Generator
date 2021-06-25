package com.ieee1599generator;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
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
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author federica
 */
public class Generator {

    public int getMeasuresNumber(int bpm, long pieceLength, int[] metre) {
        double bps = (double) bpm / 60;
        System.out.println("bps: " + bps);
        int beatsNumber = metre[0];
        System.out.println("Number of beats: " + beatsNumber);
        double oneBeatLength = 1 / bps;
        System.out.println("oneBeatLength: " + oneBeatLength);
        return (int) (pieceLength / (oneBeatLength * beatsNumber));
    }

    public int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public double getRandomNote(Map<Double, int[]> notesMap) {
        Double[] keySetArray = notesMap.keySet().toArray(new Double[notesMap.keySet().size()]);
        Random random = new Random();
        int randomIndex = random.nextInt(notesMap.keySet().size());
        return keySetArray[randomIndex];
    }

    public static void main(String[] args) throws TransformerException {
        Generator g = new Generator();
        // parametri da chiedere all'utente a riga di comando
        String titleName = "Title";
        String authorName = "Author";
        int minutes = 2;
        String pieceLength = "PT" + minutes + "M";
        int bpm = 108;
        String metre = 4 + ":" + 4;
        int[] metreInNumbers = {Integer.parseInt(String.valueOf(metre.charAt(0))), Integer.parseInt(String.valueOf(metre.charAt(2)))};
        int instrumentsNumber = 5;
        int maxNumberOfPlayedNotes = 10;
        int maxNumberOfNotesInAChord = 3;
        int[] minDuration = {32, 1};
        int[] maxDuration = {1, 1};

        //
        long seconds = Duration.parse(pieceLength).getSeconds();
        System.out.println("Piece length: " + seconds);
        int measuresNumber = g.getMeasuresNumber(bpm, seconds, metreInNumbers);
        System.out.println("Measures number: " + measuresNumber);

        Map<Double, int[]> notesMap = new TreeMap<>();
        for (int i = maxDuration[0]; i <= minDuration[0]; i *= 2) {
            notesMap.put((double) 1 / i, new int[]{i, 1});
        }

        double noteValueForAMeasure = (double) 1 / metreInNumbers[1];
        System.out.println("Value of a note in a measure: " + 1 + "/" + metreInNumbers[1] + " = " + noteValueForAMeasure);
        
        List<Element> eventsList = new ArrayList<>();

        /* Printing notesMap
        System.out.println("Notes map keys: " + notesMap.keySet());
        for(int[] n : notesMap.values()) {
            System.out.println("Notes map values: " + n[0] + " " + n[1] + "\n");
        }
         */
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

            boolean firstEventsDefined = false;

            for (int i = 1; i <= instrumentsNumber; i++) {
                Element event = doc.createElement("event");
                event.setAttribute("id", "Instrument_" + i + "_voice0_measure1_ev0");
                event.setAttribute("timing", "null");
                event.setAttribute("hpos", "null");
                spine.appendChild(event);
                eventsList.add(event);
                firstEventsDefined = true;
            }
            
            for (int i = 1; i <= instrumentsNumber; i++) {
                Element event = doc.createElement("event");
                event.setAttribute("id", "TimeSignature_Instrument_" + i + "_1");
                event.setAttribute("timing", "null");
                event.setAttribute("hpos", "null");
                spine.appendChild(event);
                eventsList.add(event);
            }

            for (int j = 1; j <= measuresNumber; j++) {
                System.out.println("MEASURE " + j);
                int randomInstrumentsNumber = g.getRandomNumber(1, instrumentsNumber + 1); // TODO extract always all instruments, but randomly
                if (firstEventsDefined) {
                    for (int i = 1; i <= randomInstrumentsNumber; i++) {
                        int notesNumber = g.getRandomNumber(1, maxNumberOfPlayedNotes + 1);
                        System.out.println("Notes number for instrument " + i + " : " + notesNumber);
                        for (int k = 1; k < notesNumber; k++) {
                            Element event = doc.createElement("event");
                            event.setAttribute("id", "Instrument_" + i + "_voice0_measure" + j + "_ev" + k);
                            event.setAttribute("timing", "null");
                            event.setAttribute("hpos", "null");
                            spine.appendChild(event);
                            eventsList.add(event);
                        }
                    }
                    firstEventsDefined = false;
                } else {
                    for (int i = 1; i <= randomInstrumentsNumber; i++) {
                        int notesNumber = g.getRandomNumber(1, maxNumberOfPlayedNotes + 1);
                        System.out.println("Notes number for instrument " + i + " : " + notesNumber);
                        for (int k = 0; k < notesNumber; k++) {
                            Element event = doc.createElement("event");
                            event.setAttribute("id", "Instrument_" + i + "_voice0_measure" + j + "_ev" + k);
                            event.setAttribute("timing", "null");
                            event.setAttribute("hpos", "null");
                            spine.appendChild(event);
                            eventsList.add(event);
                        }
                    }

                }
            }

            Element los = doc.createElement("los");
            logic.appendChild(los);

            Element staffList = doc.createElement("staff_list");
            los.appendChild(staffList);

            for (int i = 1; i <= instrumentsNumber; i++) {
                Element staff = doc.createElement("staff");
                staff.setAttribute("id", "Instrument_" + i + "_staff");
                staff.setAttribute("line_number", "5");
                staffList.appendChild(staff);

                Element timeSignature = doc.createElement("time_signature");
                timeSignature.setAttribute("event_ref", "TimeSignature_Instrument_" + i + "_1");
                staff.appendChild(timeSignature);
                Element timeIndication = doc.createElement("time_indication");
                timeIndication.setAttribute("den", "" + metreInNumbers[1]);
                timeIndication.setAttribute("num", "" + metreInNumbers[0]);
                timeSignature.appendChild(timeIndication);

                Element part = doc.createElement("part");
                part.setAttribute("id", "Instrument_" + i);
                los.appendChild(part);

                Element voiceList = doc.createElement("voice_list");
                part.appendChild(voiceList);
                Element voiceItem = doc.createElement("voice_item");
                voiceItem.setAttribute("id", "Instrument_" + i + "_0_voice");
                voiceItem.setAttribute("staff_ref", "Instrument_" + i + "_staff");
                voiceList.appendChild(voiceItem);

                for (int j = 1; j <= measuresNumber; j++) {
                    Element measure = doc.createElement("measure");
                    measure.setAttribute("number", "" + j);
                    part.appendChild(measure);

                    Element voice = doc.createElement("voice");
                    voice.setAttribute("voice_item_ref", "Instrument_" + i + "_0_voice");
                    measure.appendChild(voice);

                    String s = "Instrument_" + i + "_voice0_measure" + j + "_";
                    var eventsNumber = eventsList.stream().filter(e -> e.getAttribute("id").contains(s)).count();
                    System.out.println(s);
                    System.out.println(eventsNumber);
                    
                    for (int k = 0; k < eventsNumber; k++) {

                        Element chord = doc.createElement("chord");
                        chord.setAttribute("event_ref", "Instrument_" + i + "_voice0_measure" + j + "_ev" + k);
                        voice.appendChild(chord);

                        double randomNote = g.getRandomNote(notesMap);
                        while (randomNote * metreInNumbers[0] > 1) {
                            randomNote = g.getRandomNote(notesMap);
                        }

                        if (randomNote == 1) {
                            Element duration = doc.createElement("duration");
                            duration.setAttribute("den", "" + metreInNumbers[1]);
                            duration.setAttribute("num", "" + metreInNumbers[0]);
                            chord.appendChild(duration);
                        } else {
                            Element duration = doc.createElement("duration");
                            duration.setAttribute("den", "" + notesMap.get(randomNote)[0]);
                            duration.setAttribute("num", "" + 1);
                            chord.appendChild(duration);
                        }
                        int randomNotesNumberInAChord = g.getRandomNumber(1, maxNumberOfNotesInAChord + 1);
                        /*for (int k = 1; k <= randomNotesNumberInAChord; k++) {
                        // k: number of noteheads
                    }*/

                    }
                }
            }
            
            // TODO
            // insert chords and rests

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
