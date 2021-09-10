package com.ieee1599generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private static final Logger LOGGER = Logger.getLogger(Generator.class.getName());

    private DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    private Document document;
    private boolean areFirstEventsDefined = false;

    private List<Element> eventsList = new ArrayList<>();

    public Generator() {
    }

    public void execute(String creator, double version, String title, String authorName, int instrumentsNumber,
            int measuresNumber, int maxNumberOfEvents, int[] metreInNumbers, List<Character> clefs, List<Integer> clefsSteps,
            int maxNumberOfPlayedNotes, int maxNumberOfNotesInAChord, int minHeight, int maxHeight, Map<Double, int[]> notesMap,
            Map<Integer, Integer> irregularGroupsMap, int minimumDelay, int[] minDuration, boolean areIrregularGroupsPresent,
            int octavesNumber, Map<Integer, String> pitchesMap) {
        createDocument();
        Element ieee1599 = addElementToDocument(this.document, "ieee1599");
        setAttributeOfElement(ieee1599, "creator", creator);
        setAttributeOfElement(ieee1599, "version", "" + version);
        this.document.appendChild(ieee1599);

        Element general = addElementToDocument(this.document, "general");
        appendChildToElement(ieee1599, general);

        Element description = addElementToDocument(this.document, "description");
        appendChildToElement(general, description);
        Element mainTitle = addElementToDocument(this.document, "main_title");
        setTextContentOfElement(mainTitle, title);
        appendChildToElement(description, mainTitle);
        Element author = addElementToDocument(this.document, "author");
        setTextContentOfElement(author, authorName);
        appendChildToElement(description, author);

        Element logic = addElementToDocument(this.document, "logic");
        appendChildToElement(ieee1599, logic);
        Element spine = addElementToDocument(this.document, "spine");
        appendChildToElement(logic, spine);

        defineFirstEvents(instrumentsNumber, "Instrument_", "_voice0_measure1_ev0", spine);
        defineFirstEvents(instrumentsNumber, "TimeSignature_Instrument_", "_1", spine);
        defineFirstEvents(instrumentsNumber, "Clef_Instrument_", "_1", spine);

        createEvents(measuresNumber, instrumentsNumber, maxNumberOfEvents, spine);

        Element los = addElementToDocument(this.document, "los");
        appendChildToElement(logic, los);

        Element staffList = addElementToDocument(this.document, "staff_list");
        appendChildToElement(los, staffList);

        for (int i = 1; i <= instrumentsNumber; i++) {
            LOGGER.info("INSTRUMENT " + i);

            Element staff = addElementToDocument(this.document, "staff");
            setAttributeOfElement(staff, "id", "Instrument_" + i + "_staff");
            setAttributeOfElement(staff, "line_number", "" + 5);
            appendChildToElement(staffList, staff);

            Element timeSignature = addElementToDocument(this.document, "time_signature");
            setAttributeOfElement(timeSignature, "event_ref", "TimeSignature_Instrument_" + i + "_1");
            appendChildToElement(staff, timeSignature);

            Element timeIndication = addElementToDocument(this.document, "time_indication");
            setAttributeOfElement(timeIndication, "den", "" + metreInNumbers[1]);
            setAttributeOfElement(timeIndication, "num", "" + metreInNumbers[0]);
            appendChildToElement(timeSignature, timeIndication);

            Element clef = addElementToDocument(this.document, "clef");
            setAttributeOfElement(clef, "event_ref", "Clef_Instrument_" + i + "_1");
            setAttributeOfElement(clef, "shape", "" + Utils.getRandomCharFromList(clefs));
            setAttributeOfElement(clef, "staff_step", "" + Utils.getRandomIntegerFromList(clefsSteps));
            setAttributeOfElement(clef, "octave_num", "" + 0);
            appendChildToElement(staff, clef);

            Element part = addElementToDocument(this.document, "part");
            setAttributeOfElement(part, "id", "Instrument_" + i);
            appendChildToElement(los, part);

            Element voiceList = addElementToDocument(this.document, "voice_list");
            appendChildToElement(part, voiceList);

            Element voiceItem = addElementToDocument(this.document, "voice_item");
            setAttributeOfElement(voiceItem, "id", "Instrument_" + i + "_0_voice");
            setAttributeOfElement(voiceItem, "staff_ref", "Instrument_" + i + "_staff");
            appendChildToElement(voiceList, voiceItem);

            for (int j = 1; j <= measuresNumber; j++) {
                LOGGER.info("MEASURE " + j);

                Element measure = addElementToDocument(this.document, "measure");
                setAttributeOfElement(measure, "number", "" + j);
                appendChildToElement(part, measure);

                Element voice = addElementToDocument(this.document, "voice");
                setAttributeOfElement(voice, "voice_item_ref", "Instrument_" + i + "_0_voice");
                appendChildToElement(measure, voice);

                String attributeString = "Instrument_" + i + "_voice0_measure" + j + "_";
                int eventsNumber = (int) eventsList.stream().filter(e -> e.getAttribute("id").contains(attributeString)).count();
                LOGGER.info("Events number: " + eventsNumber);

                int notesNumber = Utils.getRandomInteger(1, maxNumberOfPlayedNotes);
                LOGGER.info("Notes number: " + notesNumber);

                int notesNumberInAMeasure = notesNumber / measuresNumber;
                if (notesNumberInAMeasure > eventsNumber) {
                    notesNumberInAMeasure = eventsNumber;
                }
                LOGGER.info("Notes number in a measure: " + notesNumberInAMeasure);

                int restsNumberInAMeasure = eventsNumber - notesNumberInAMeasure;
                LOGGER.info("Rests number: " + restsNumberInAMeasure);

                List<Character> notesAndRests = new ArrayList<>();
                int count = 0;
                while (count < eventsNumber) {
                    for (int n = 1; n <= notesNumberInAMeasure; n++) {
                        notesAndRests.add('N');
                        count++;
                    }
                    for (int r = 1; r <= restsNumberInAMeasure; r++) {
                        notesAndRests.add('R');
                        count++;
                    }
                }

                Collections.shuffle(notesAndRests);

                /*notesAndRests.forEach(c -> {
                        System.out.println(c);
                    });*/
                List<Integer> randomPitches = Utils.getRandomNonRepeatingIntegers(notesAndRests.size(), minHeight, maxHeight);

                int notesInAChord = Utils.getRandomInteger(1, maxNumberOfNotesInAChord);
                LOGGER.info("Notes in a chord: " + notesInAChord);

                /*randomPitches.forEach(p -> {
                        System.out.println(p);
                    });*/
                for (int k = 0; k < notesAndRests.size(); k++) {

                    if (notesAndRests.get(k) == 'N') {
                        Element chord = addElementToDocument(this.document, "chord");

                        String eventRef = "Instrument_" + i + "_voice0_measure" + j + "_ev" + k;
                        setAttributeOfElement(chord, "event_ref", eventRef);
                        appendChildToElement(voice, chord);

                        //TODO riuscire a fare meglio quest'operazione: evitare Optional Empty
                        Optional<Element> optionalEvent = eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
                        Element event = optionalEvent.get();

                        double randomNote = Utils.getRandomNote(notesMap);
                        while (randomNote * notesInAChord > 1) {
                            randomNote = Utils.getRandomNote(notesMap);
                        }

                        int irregularGroup = Utils.getRandomIrregularGroup(irregularGroupsMap);

                        Element duration = addElementToDocument(this.document, "duration");
                        if (randomNote == 1) {
                            setAttributeOfElement(duration, "den", "" + metreInNumbers[1]);
                            setAttributeOfElement(duration, "num", "" + metreInNumbers[0]);
                            appendChildToElement(chord, duration);

                            if (k > 0) {
                                event.setAttribute("timing", "" + minimumDelay * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                                event.setAttribute("hpos", "" + minimumDelay * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                            } else {
                                event.setAttribute("timing", "" + 0);
                                event.setAttribute("hpos", "" + 0);
                            }

                            if (areIrregularGroupsPresent) {
                                if (Utils.getRandomBoolean()) {
                                    Element tupletRatio = addElementToDocument(this.document, "tuplet_ratio");
                                    setAttributeOfElement(tupletRatio, "enter_num", "" + irregularGroup);
                                    setAttributeOfElement(tupletRatio, "enter_den", "" + metreInNumbers[1] * irregularGroupsMap.get(irregularGroup));
                                    setAttributeOfElement(tupletRatio, "in_num", "" + 1);
                                    setAttributeOfElement(tupletRatio, "in_den", "" + metreInNumbers[1]);
                                    appendChildToElement(duration, tupletRatio);
                                }
                            }
                        } else {
                            setAttributeOfElement(duration, "den", "" + notesMap.get(randomNote)[0]);
                            setAttributeOfElement(duration, "num", "" + 1);
                            appendChildToElement(chord, duration);

                            if (k > 0) {
                                setAttributeOfElement(event, "timing", "" + minimumDelay * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                                setAttributeOfElement(event, "hpos", "" + minimumDelay * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                            } else {
                                setAttributeOfElement(event, "timing", "" + 0);
                                setAttributeOfElement(event, "hpos", "" + 0);
                            }

                            if (areIrregularGroupsPresent) {
                                if (Utils.getRandomBoolean()) {
                                    Element tupletRatio = addElementToDocument(this.document, "tuplet_ratio");
                                    setAttributeOfElement(tupletRatio, "enter_num", "" + irregularGroup);
                                    setAttributeOfElement(tupletRatio, "enter_den", "" + metreInNumbers[1] * irregularGroupsMap.get(irregularGroup));
                                    setAttributeOfElement(tupletRatio, "in_num", "" + 1);
                                    setAttributeOfElement(tupletRatio, "in_den", "" + notesMap.get(randomNote)[0]);
                                    appendChildToElement(duration, tupletRatio);
                                }
                            }
                        }

                        for (int h = 1; h <= notesInAChord; h++) {
                            Element notehead = addElementToDocument(this.document, "notehead");
                            appendChildToElement(chord, notehead);

                            Element pitch = addElementToDocument(this.document, "pitch");
                            setAttributeOfElement(pitch, "actual_accidental", "natural");
                            int randomOctave = Utils.getRandomInteger(0, octavesNumber);
                            setAttributeOfElement(pitch, "octave", "" + randomOctave);
                            setAttributeOfElement(pitch, "step", "" + pitchesMap.get(randomPitches.get(k)).charAt(0));
                            appendChildToElement(notehead, pitch);

                            if (pitchesMap.get(randomPitches.get(k)).length() > 1) {
                                setAttributeOfElement(pitch, "actual_accidental", "sharp");

                                Element printedAccidentals = addElementToDocument(this.document, "printed_accidentals");
                                appendChildToElement(notehead, printedAccidentals);

                                Element sharp = addElementToDocument(this.document, "sharp");
                                appendChildToElement(notehead, sharp);
                            }
                        }

                    } else {
                        Element rest = addElementToDocument(this.document, "rest");

                        String eventRef = "Instrument_" + i + "_voice0_measure" + j + "_ev" + k;
                        setAttributeOfElement(rest, "event_ref", eventRef);
                        appendChildToElement(voice, rest);

                        //TODO riuscire a fare meglio quest'operazione: evitare Optional Empty
                        Optional<Element> optionalEvent = eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
                        Element event = optionalEvent.get();

                        double randomNote = Utils.getRandomNote(notesMap);
                        while (randomNote * metreInNumbers[0] > 1) {
                            randomNote = Utils.getRandomNote(notesMap);
                        }

                        if (randomNote == 1) {
                            Element duration = addElementToDocument(this.document, "duration");
                            setAttributeOfElement(duration, "den", "" + metreInNumbers[1]);
                            setAttributeOfElement(duration, "num", "" + metreInNumbers[0]);
                            appendChildToElement(rest, duration);

                            if (k > 0) {
                                setAttributeOfElement(event, "timing", "" + minimumDelay * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                                setAttributeOfElement(event, "hpos", "" + minimumDelay * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                            } else {
                                setAttributeOfElement(event, "timing", "" + 0);
                                setAttributeOfElement(event, "hpos", "" + 0);
                            }

                        } else {
                            Element duration = addElementToDocument(this.document, "duration");
                            setAttributeOfElement(duration, "den", "" + notesMap.get(randomNote)[0]);
                            setAttributeOfElement(duration, "num", "" + 1);
                            appendChildToElement(rest, duration);

                            if (k > 0) {
                                setAttributeOfElement(event, "timing", "" + minimumDelay * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                                setAttributeOfElement(event, "hpos", "" + minimumDelay * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                            } else {
                                setAttributeOfElement(event, "timing", "" + 0);
                                setAttributeOfElement(event, "hpos", "" + 0);
                            }
                        }
                    }
                }
            }
        }
    }

    private void createEvents(int measuresNumber, int instrumentsNumber, int maxNumberOfEvents, Element spine) {
        for (int j = 1; j <= measuresNumber; j++) {
            List<Integer> randomInstruments = Utils.getRandomNonRepeatingIntegers(instrumentsNumber, 1, instrumentsNumber);
            if (this.areFirstEventsDefined) {
                for (int i = 0; i < randomInstruments.size(); i++) {
                    int eventsNumber = Utils.getRandomInteger(1, maxNumberOfEvents);
                    for (int k = 1; k < eventsNumber; k++) {
                        defineOtherEvents(i, j, k, randomInstruments, spine);
                    }
                }
                this.areFirstEventsDefined = false;
            } else {
                for (int i = 0; i < randomInstruments.size(); i++) {
                    int eventsNumber = Utils.getRandomInteger(1, maxNumberOfEvents);
                    for (int k = 0; k < eventsNumber; k++) {
                        defineOtherEvents(i, j, k, randomInstruments, spine);
                    }
                }
            }
        }
    }

    private void defineOtherEvents(int i, int j, int k, List<Integer> randomInstruments, Element spine) {
        Element event = addElementToDocument(this.document, "event");
        setAttributeOfElement(event, "id", "Instrument_" + randomInstruments.get(i) + "_voice0_measure" + j + "_ev" + k);
        appendChildToElement(spine, event);
        this.eventsList.add(event);
    }

    private void defineFirstEvents(int instrumentsNumber, String firstPart, String secondPart, Element spine) {
        for (int i = 1; i <= instrumentsNumber; i++) {
            Element event = addElementToDocument(this.document, "event");
            setAttributeOfElement(event, "id", firstPart + i + secondPart);
            setAttributeOfElement(event, "timing", "" + 0);
            setAttributeOfElement(event, "hpos", "" + 0);
            appendChildToElement(spine, event);
            this.eventsList.add(event);
            this.areFirstEventsDefined = true;
        }
    }

    private void createDocument() {
        try {
            DocumentBuilder docBuilder;
            docBuilder = this.docFactory.newDocumentBuilder();
            this.document = docBuilder.newDocument();

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Element addElementToDocument(Document document, String elementName) {
        return document.createElement(elementName);
    }

    private void appendChildToElement(Element firstElement, Element secondElement) {
        firstElement.appendChild(secondElement);
    }

    private void setAttributeOfElement(Element element, String firstString, String secondString) {
        element.setAttribute(firstString, secondString);
    }

    private void setTextContentOfElement(Element element, String string) {
        element.setTextContent(string);
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
            Logger.getLogger(GeneratorToRemove.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
