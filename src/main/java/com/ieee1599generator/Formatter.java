package com.ieee1599generator;

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
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author federica
 */
public class Formatter {

    private static final Logger LOGGER = Logger.getLogger(Generator.class.getName());

    private final Initializer initializer;
    private final Utils utils;
    private final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    private Document document;
    private final List<Element> eventsList = new ArrayList<>();
    private boolean areFirstEventsDefined = false;

    public Formatter(Initializer initializer, int seed) {
        this.initializer = initializer;
        this.utils = new Utils(seed);
    }

    public void execute(String creator, double version, String title, String authorName, int instrumentsNumber, List<Character> clefs, List<Integer> clefsSteps,
            int minHeight, int maxHeight, int maxNumberOfNotesInAChord, int minimumDelay, boolean areIrregularGroupsPresent, int octavesNumber, Map<Integer, String> pitchesMap) {

        createDocument();

        Element ieee1599 = addIeee1599Element(creator, version);

        Element general = addGeneralElement(ieee1599);

        Element description = addDescriptionElement(general);

        addMainTitleElement(title, description);

        addAuthorElement(authorName, description);

        Element logic = addLogicElement(ieee1599);

        Element spine = addSpineElement(logic);

        defineFirstEvents(instrumentsNumber, "Instrument_", "_voice0_measure1_ev0", spine);
        defineFirstEvents(instrumentsNumber, "TimeSignature_Instrument_", "_1", spine);
        defineFirstEvents(instrumentsNumber, "Clef_Instrument_", "_1", spine);

        createEvents(this.initializer.getMeasuresNumber(), instrumentsNumber, this.initializer.getMaxNumberOfEvents(), spine);

        Element los = addLosElement(logic);

        Element staffList = addStaffListElement(los);

        for (int i = 1; i <= instrumentsNumber; i++) {
            LOGGER.log(Level.INFO, "INSTRUMENT {0}", i);

            Element staff = addStaffElement(i, staffList);

            Element timeSignature = addTimeSignatureElement(i, staff);

            addTimeIndicationElement(this.initializer.getMetreInNumbers(), timeSignature);

            addClefElement(i, clefs, clefsSteps, staff);

            Element part = addPartElement(i, los);

            Element voiceList = addVoiceListElement(part);

            addVoiceItemElement(i, voiceList);

            for (int j = 1; j <= this.initializer.getMeasuresNumber(); j++) {
                LOGGER.log(Level.INFO, "MEASURE {0}", j);

                Element measure = addMeasureElement(j, part);

                Element voice = addVoiceElement(i, measure);

                String attributeString = "Instrument_" + i + "_voice0_measure" + j + "_";
                int eventsNumber = (int) this.eventsList.stream().filter(e -> e.getAttribute("id").contains(attributeString)).count();
                LOGGER.log(Level.INFO, "Events number: {0}", eventsNumber);

                int notesNumber = this.utils.getRandomInteger(1, this.initializer.getMaxNumberOfEvents());
                LOGGER.log(Level.INFO, "Notes number: {0}", notesNumber);

                int notesNumberInAMeasure = notesNumber / this.initializer.getMeasuresNumber();
                if (notesNumberInAMeasure > eventsNumber) {
                    notesNumberInAMeasure = eventsNumber;
                }
                LOGGER.log(Level.INFO, "Notes number in a measure: {0}", notesNumberInAMeasure);

                int restsNumberInAMeasure = eventsNumber - notesNumberInAMeasure;
                LOGGER.log(Level.INFO, "Rests number: {0}", restsNumberInAMeasure);

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
                List<Integer> randomPitches = this.utils.getRandomNonRepeatingIntegers(notesAndRests.size(), minHeight, maxHeight);

                int notesInAChord = this.utils.getRandomInteger(1, maxNumberOfNotesInAChord);
                LOGGER.log(Level.INFO, "Notes in a chord: {0}", notesInAChord);

                /*randomPitches.forEach(p -> {
                        System.out.println(p);
                    });*/
                for (int k = 0; k < notesAndRests.size(); k++) {

                    if (notesAndRests.get(k) == 'N') {
                        Element chord = addElementToDocument(this.document, "chord");

                        String eventRef = "Instrument_" + i + "_voice0_measure" + j + "_ev" + k;
                        setAttributeOfElement(chord, "event_ref", eventRef);
                        appendChildToElement(voice, chord);

                        // TODO gestire caso EMPTY dell'optional
                        Optional<Element> optionalEvent = this.eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
                        Element event = optionalEvent.get();

                        double randomNote = getRandomNote(notesInAChord);

                        int irregularGroup = this.utils.getRandomIrregularGroup(this.initializer.getIrregularGroupsMap());

                        Element duration = addElementToDocument(this.document, "duration");
                        if (randomNote == 1) {
                            setAttributeOfElement(duration, "den", "" + this.initializer.getMetreInNumbers()[1]);
                            setAttributeOfElement(duration, "num", "" + this.initializer.getMetreInNumbers()[0]);
                            appendChildToElement(chord, duration);

                            addEventAttributes(k, event, minimumDelay, duration);

                            additionForIrregularGroupsPresence(areIrregularGroupsPresent, irregularGroup, duration);

                        } else {
                            setAttributeOfElement(duration, "den", "" + this.initializer.getNotesMap().get(randomNote)[0]);
                            setAttributeOfElement(duration, "num", "" + 1);
                            appendChildToElement(chord, duration);

                            addEventAttributes(k, event, minimumDelay, duration);

                            additionForIrregularGroupsPresence(areIrregularGroupsPresent, irregularGroup, randomNote, duration);
                        }

                        for (int h = 1; h <= notesInAChord; h++) {
                            Element notehead = addNoteheadElement(chord);

                            Element pitch = addPitchElement(octavesNumber, pitchesMap, randomPitches, k, notehead);

                            if (pitchesMap.get(randomPitches.get(k)).length() > 1) {
                                setAttributeOfElement(pitch, "actual_accidental", "sharp");

                                addPrintedAccidentalsElement(notehead);

                                addSharpElement(notehead);
                            }
                        }

                    } else {
                        Element rest = addElementToDocument(this.document, "rest");

                        String eventRef = "Instrument_" + i + "_voice0_measure" + j + "_ev" + k;
                        setAttributeOfElement(rest, "event_ref", eventRef);
                        appendChildToElement(voice, rest);

                        // TODO gestire caso EMPTY dell'optional
                        Optional<Element> optionalEvent = this.eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
                        Element event = optionalEvent.get();

                        double randomNote = getRandomNote(this.initializer.getMetreInNumbers()[0]);

                        if (randomNote == 1) {
                            Element duration = addDurationElement(this.initializer.getMetreInNumbers(), rest);
                            addEventAttributes(k, event, minimumDelay, duration);

                        } else {
                            Element duration = addDurationElement(this.initializer.getNotesMap(), randomNote, rest);
                            addEventAttributes(k, event, minimumDelay, duration);
                        }
                    }
                }
            }
        }
    }
    
    public Document getDocument() {
        return this.document;
    }

    private double getRandomNote(int notesInAChord) {
        double randomNote = this.utils.getRandomNote(this.initializer.getNotesMap());
        while (randomNote * notesInAChord > 1) {
            randomNote = this.utils.getRandomNote(this.initializer.getNotesMap());
        }
        return randomNote;
    }

    private void additionForIrregularGroupsPresence(boolean areIrregularGroupsPresent, int irregularGroup, double randomNote, Element duration) {
        if (areIrregularGroupsPresent) {
            if (this.utils.getRandomBoolean()) {
                addTupletRatioElement(irregularGroup, this.initializer.getMetreInNumbers(), this.initializer.getIrregularGroupsMap(), this.initializer.getNotesMap(), randomNote, duration);
            }
        }
    }

    private void additionForIrregularGroupsPresence(boolean areIrregularGroupsPresent, int irregularGroup, Element duration) {
        if (areIrregularGroupsPresent) {
            if (this.utils.getRandomBoolean()) {
                addTupletRatioElement(irregularGroup, this.initializer.getMetreInNumbers(), this.initializer.getIrregularGroupsMap(), duration);
            }
        }
    }

    private void addEventAttributes(int k, Element event, int minimumDelay, Element duration) throws NumberFormatException {
        if (k > 0) {
            setAttributeOfElement(event, "timing", "" + minimumDelay * (this.initializer.getMinDuration()[0] / Integer.parseInt(duration.getAttribute("den"))));
            setAttributeOfElement(event, "hpos", "" + minimumDelay * (this.initializer.getMinDuration()[0] / Integer.parseInt(duration.getAttribute("den"))));
        } else {
            setAttributeOfElement(event, "timing", "" + 0);
            setAttributeOfElement(event, "hpos", "" + 0);
        }
    }

    private Element addDurationElement(Map<Double, int[]> notesMap, double randomNote, Element rest) {
        Element duration = addElementToDocument(this.document, "duration");
        setAttributeOfElement(duration, "den", "" + notesMap.get(randomNote)[0]);
        setAttributeOfElement(duration, "num", "" + 1);
        appendChildToElement(rest, duration);
        return duration;
    }

    private Element addDurationElement(int[] metreInNumbers, Element rest) {
        Element duration = addElementToDocument(this.document, "duration");
        setAttributeOfElement(duration, "den", "" + metreInNumbers[1]);
        setAttributeOfElement(duration, "num", "" + metreInNumbers[0]);
        appendChildToElement(rest, duration);
        return duration;
    }

    private void addSharpElement(Element notehead) {
        Element sharp = addElementToDocument(this.document, "sharp");
        appendChildToElement(notehead, sharp);
    }

    private void addPrintedAccidentalsElement(Element notehead) {
        Element printedAccidentals = addElementToDocument(this.document, "printed_accidentals");
        appendChildToElement(notehead, printedAccidentals);
    }

    private Element addPitchElement(int octavesNumber, Map<Integer, String> pitchesMap, List<Integer> randomPitches, int k, Element notehead) {
        Element pitch = addElementToDocument(this.document, "pitch");
        setAttributeOfElement(pitch, "actual_accidental", "natural");
        int randomOctave = this.utils.getRandomInteger(0, octavesNumber);
        setAttributeOfElement(pitch, "octave", "" + randomOctave);
        setAttributeOfElement(pitch, "step", "" + pitchesMap.get(randomPitches.get(k)).charAt(0));
        appendChildToElement(notehead, pitch);
        return pitch;
    }

    private Element addNoteheadElement(Element chord) {
        Element notehead = addElementToDocument(this.document, "notehead");
        appendChildToElement(chord, notehead);
        return notehead;
    }

    private void addTupletRatioElement(int irregularGroup, int[] metreInNumbers, Map<Integer, Integer> irregularGroupsMap, Map<Double, int[]> notesMap, double randomNote, Element duration) {
        Element tupletRatio = addElementToDocument(this.document, "tuplet_ratio");
        setAttributeOfElement(tupletRatio, "enter_num", "" + irregularGroup);
        setAttributeOfElement(tupletRatio, "enter_den", "" + metreInNumbers[1] * irregularGroupsMap.get(irregularGroup));
        setAttributeOfElement(tupletRatio, "in_num", "" + 1);
        setAttributeOfElement(tupletRatio, "in_den", "" + notesMap.get(randomNote)[0]);
        appendChildToElement(duration, tupletRatio);
    }

    private void addTupletRatioElement(int irregularGroup, int[] metreInNumbers, Map<Integer, Integer> irregularGroupsMap, Element duration) {
        Element tupletRatio = addElementToDocument(this.document, "tuplet_ratio");
        setAttributeOfElement(tupletRatio, "enter_num", "" + irregularGroup);
        setAttributeOfElement(tupletRatio, "enter_den", "" + metreInNumbers[1] * irregularGroupsMap.get(irregularGroup));
        setAttributeOfElement(tupletRatio, "in_num", "" + 1);
        setAttributeOfElement(tupletRatio, "in_den", "" + metreInNumbers[1]);
        appendChildToElement(duration, tupletRatio);
    }

    private Element addVoiceElement(int i, Element measure) {
        Element voice = addElementToDocument(this.document, "voice");
        setAttributeOfElement(voice, "voice_item_ref", "Instrument_" + i + "_0_voice");
        appendChildToElement(measure, voice);
        return voice;
    }

    private Element addMeasureElement(int j, Element part) {
        Element measure = addElementToDocument(this.document, "measure");
        setAttributeOfElement(measure, "number", "" + j);
        appendChildToElement(part, measure);
        return measure;
    }

    private void addVoiceItemElement(int i, Element voiceList) {
        Element voiceItem = addElementToDocument(this.document, "voice_item");
        setAttributeOfElement(voiceItem, "id", "Instrument_" + i + "_0_voice");
        setAttributeOfElement(voiceItem, "staff_ref", "Instrument_" + i + "_staff");
        appendChildToElement(voiceList, voiceItem);
    }

    private Element addVoiceListElement(Element part) {
        Element voiceList = addElementToDocument(this.document, "voice_list");
        appendChildToElement(part, voiceList);
        return voiceList;
    }

    private Element addPartElement(int i, Element los) {
        Element part = addElementToDocument(this.document, "part");
        setAttributeOfElement(part, "id", "Instrument_" + i);
        appendChildToElement(los, part);
        return part;
    }

    private void addClefElement(int i, List<Character> clefs, List<Integer> clefsSteps, Element staff) {
        Element clef = addElementToDocument(this.document, "clef");
        setAttributeOfElement(clef, "event_ref", "Clef_Instrument_" + i + "_1");
        setAttributeOfElement(clef, "shape", "" + this.utils.getRandomCharFromList(clefs));
        setAttributeOfElement(clef, "staff_step", "" + this.utils.getRandomIntegerFromList(clefsSteps));
        setAttributeOfElement(clef, "octave_num", "" + 0);
        appendChildToElement(staff, clef);
    }

    private void addTimeIndicationElement(int[] metreInNumbers, Element timeSignature) {
        Element timeIndication = addElementToDocument(this.document, "time_indication");
        setAttributeOfElement(timeIndication, "den", "" + metreInNumbers[1]);
        setAttributeOfElement(timeIndication, "num", "" + metreInNumbers[0]);
        appendChildToElement(timeSignature, timeIndication);
    }

    private Element addTimeSignatureElement(int i, Element staff) {
        Element timeSignature = addElementToDocument(this.document, "time_signature");
        setAttributeOfElement(timeSignature, "event_ref", "TimeSignature_Instrument_" + i + "_1");
        appendChildToElement(staff, timeSignature);
        return timeSignature;
    }

    private Element addStaffElement(int i, Element staffList) {
        Element staff = addElementToDocument(this.document, "staff");
        setAttributeOfElement(staff, "id", "Instrument_" + i + "_staff");
        setAttributeOfElement(staff, "line_number", "" + 5);
        appendChildToElement(staffList, staff);
        return staff;
    }

    private Element addStaffListElement(Element los) {
        Element staffList = addElementToDocument(this.document, "staff_list");
        appendChildToElement(los, staffList);
        return staffList;
    }

    private Element addLosElement(Element logic) {
        Element los = addElementToDocument(this.document, "los");
        appendChildToElement(logic, los);
        return los;
    }

    private Element addSpineElement(Element logic) {
        Element spine = addElementToDocument(this.document, "spine");
        appendChildToElement(logic, spine);
        return spine;
    }

    private Element addLogicElement(Element ieee1599) {
        Element logic = addElementToDocument(this.document, "logic");
        appendChildToElement(ieee1599, logic);
        return logic;
    }

    private void addAuthorElement(String authorName, Element description) {
        Element author = addElementToDocument(this.document, "author");
        setTextContentOfElement(author, authorName);
        appendChildToElement(description, author);
    }

    private void addMainTitleElement(String title, Element description) {
        Element mainTitle = addElementToDocument(this.document, "main_title");
        setTextContentOfElement(mainTitle, title);
        appendChildToElement(description, mainTitle);
    }

    private Element addDescriptionElement(Element general) {
        Element description = addElementToDocument(this.document, "description");
        appendChildToElement(general, description);
        return description;
    }

    private Element addGeneralElement(Element ieee1599) {
        Element general = addElementToDocument(this.document, "general");
        appendChildToElement(ieee1599, general);
        return general;
    }

    private Element addIeee1599Element(String creator, double version) throws DOMException {
        Element ieee1599 = addElementToDocument(this.document, "ieee1599");
        setAttributeOfElement(ieee1599, "creator", creator);
        setAttributeOfElement(ieee1599, "version", "" + version);
        this.document.appendChild(ieee1599);
        return ieee1599;
    }

    private void createEvents(int measuresNumber, int instrumentsNumber, int maxNumberOfEvents, Element spine) {
        for (int j = 1; j <= measuresNumber; j++) {
            List<Integer> randomInstruments = this.utils.getRandomNonRepeatingIntegers(instrumentsNumber, 1, instrumentsNumber);
            if (this.areFirstEventsDefined) {
                for (int i = 0; i < randomInstruments.size(); i++) {
                    int eventsNumber = this.utils.getRandomInteger(1, maxNumberOfEvents);
                    for (int k = 1; k < eventsNumber; k++) {
                        defineOtherEvents(i, j, k, randomInstruments, spine);
                    }
                }
                this.areFirstEventsDefined = false;
            } else {
                for (int i = 0; i < randomInstruments.size(); i++) {
                    int eventsNumber = this.utils.getRandomInteger(1, maxNumberOfEvents);
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
}
