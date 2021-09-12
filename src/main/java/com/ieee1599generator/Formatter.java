package com.ieee1599generator;

import java.util.ArrayList;
import java.util.Arrays;
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

    private final Randomizer randomizer;
    private Document document;
    private final List<Element> eventsList = new ArrayList<>();
    private boolean areFirstEventsDefined = false;
    private String creator;
    private double docVersion;
    private String title;
    private String author;
    private int instrumentsNumber;
    private List<Instrument> instruments;
    private List<Character> clefs;
    private List<Integer> clefsSteps;
    private Map<Integer, String> pitchesMap;
    private int octavesNumber;
    private int[] metreInNumbers;
    private int measuresNumber;

    public Formatter(long seed, String creator, double docVersion, String title, String author, int instrumentsNumber, List<Instrument> instruments, List<Character> clefs, List<Integer> clefsSteps, Map<Integer, String> pitchesMap, int octavesNumber, int[] metreInNumbers, int measuresNumber) {
        this.randomizer = new Randomizer(seed);
        LOGGER.log(Level.INFO, "Randomizer seed: " + seed);
        this.creator = creator;
        LOGGER.log(Level.INFO, "Creator: " + this.creator);
        this.docVersion = docVersion;
        LOGGER.log(Level.INFO, "Document version: " + this.docVersion);
        this.title = title;
        LOGGER.log(Level.INFO, "Title: " + this.title);
        this.author = author;
        LOGGER.log(Level.INFO, "Author: " + this.author);
        this.instrumentsNumber = instrumentsNumber;
        LOGGER.log(Level.INFO, "Number of instruments: " + this.instrumentsNumber);
        this.instruments = instruments;
        //LOGGER.log(Level.INFO, "Instruments: " + );
        this.clefs = clefs;
        this.clefsSteps = clefsSteps;
        this.pitchesMap = pitchesMap;
        this.octavesNumber = octavesNumber;
        this.metreInNumbers = metreInNumbers;
        this.measuresNumber = measuresNumber;
    }

    public void format() {

        createDocument();

        Element ieee1599 = createGeneralLayer(creator, docVersion, title, author);

        createLogicLayer(ieee1599, instrumentsNumber, clefs, clefsSteps, octavesNumber, pitchesMap);
    }

    private void createLogicLayer(Element ieee1599, int instrumentsNumber, List<Character> clefs, List<Integer> clefsSteps, int octavesNumber, Map<Integer, String> pitchesMap) throws NumberFormatException {
        Element logic = addLogicElement(ieee1599);

        createSpineContainer(logic, instrumentsNumber);

        createLosContainer(logic, instrumentsNumber, clefs, clefsSteps, octavesNumber, pitchesMap);
    }

    private void createLosContainer(Element logic, int instrumentsNumber, List<Character> clefs, List<Integer> clefsSteps, int octavesNumber, Map<Integer, String> pitchesMap) throws NumberFormatException {
        Element los = addLosElement(logic);

        Element staffList = addStaffListElement(los);

        for (int i = 1; i <= instrumentsNumber; i++) {
            LOGGER.log(Level.INFO, "INSTRUMENT {0}", i);

            addStaffListComponents(i, staffList, clefs, clefsSteps);

            createPartElement(i, los, octavesNumber, pitchesMap);
        }
    }

    private void createPartElement(int i, Element los, int octavesNumber, Map<Integer, String> pitchesMap) throws NumberFormatException {
        Element part = addPartElement(i, los);

        createVoiceListContainer(part, i);

        createMeasureElements(part, i, octavesNumber, pitchesMap);
    }

    private void createMeasureElements(Element part, int i, int octavesNumber, Map<Integer, String> pitchesMap) throws NumberFormatException {
        for (int j = 1; j <= measuresNumber; j++) {
            LOGGER.log(Level.INFO, "MEASURE {0}", j);

            Element measure = addMeasureElement(j, part);

            createVoiceElement(i, measure, j, octavesNumber, pitchesMap);
        }
    }

    private void createVoiceElement(int i, Element measure, int j, int octavesNumber, Map<Integer, String> pitchesMap) throws NumberFormatException {
        Element voice = addVoiceElement(i, measure);

        String attributeString = "Instrument_" + i + "_voice0_measure" + j + "_";
        int eventsNumber = (int) this.eventsList.stream().filter(e -> e.getAttribute("id").contains(attributeString)).count();
        LOGGER.log(Level.INFO, "Events number: {0}", eventsNumber);

        int notesNumber = this.randomizer.getRandomInteger(1, instruments.get(i).getMaxNumberOfPlayedNotes());
        LOGGER.log(Level.INFO, "Notes number: {0}", notesNumber);

        int notesNumberInAMeasure = computeNotesNumberInAMeasure(notesNumber, eventsNumber);
        LOGGER.log(Level.INFO, "Notes number in a measure: {0}", notesNumberInAMeasure);

        int restsNumberInAMeasure = eventsNumber - notesNumberInAMeasure;
        LOGGER.log(Level.INFO, "Rests number: {0}", restsNumberInAMeasure);

        List<Character> notesAndRests = createNotesAndRestsList(eventsNumber, notesNumberInAMeasure, restsNumberInAMeasure);

        Collections.shuffle(notesAndRests);

        /*notesAndRests.forEach(c -> {
        System.out.println(c);
        });*/
        List<Integer> randomPitches = this.randomizer.getRandomNonRepeatingIntegers(notesAndRests.size(), instruments.get(i).getMinHeight(), instruments.get(i).getMaxHeight());

        int notesInAChord = this.randomizer.getRandomInteger(1, instruments.get(i).getMaxNumberOfNotesInAChord());
        LOGGER.log(Level.INFO, "Notes in a chord: {0}", notesInAChord);

        /*randomPitches.forEach(p -> {
        System.out.println(p);
        });*/
        for (int k = 0; k < notesAndRests.size(); k++) {

            if (notesAndRests.get(k) == 'N') {
                createChordElements(i, j, k, voice, notesInAChord, instruments.get(i).getMinimumDelay(), instruments.get(i).getAreIrregularGroupsPresent(), octavesNumber, pitchesMap, randomPitches);

            } else {
                createRestElements(i, j, k, voice, instruments.get(i).getMinimumDelay());
            }
        }
    }

    private void createRestElements(int i, int j, int k, Element voice, int minimumDelay) throws NumberFormatException {
        Element rest = addElementToDocument(this.document, "rest");

        String eventRef = "Instrument_" + i + "_voice0_measure" + j + "_ev" + k;
        setAttributeOfElement(rest, "event_ref", eventRef);
        appendChildToElement(voice, rest);

        // TODO gestire caso EMPTY dell'optional
        Optional<Element> optionalEvent = this.eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
        Element event = optionalEvent.get();

        double randomNote = this.randomizer.getRandomNote(metreInNumbers[0], instruments.get(i).getNotesMap());

        createDurationElement(i, randomNote, rest, k, event, minimumDelay);
    }

    private void createDurationElement(int i, double randomNote, Element rest, int k, Element event, int minimumDelay) throws NumberFormatException {
        if (randomNote == 1) {
            Element duration = addDurationElement(metreInNumbers, rest);
            addEventAttributes(i, k, event, minimumDelay, duration);

        } else {
            Element duration = addDurationElement(instruments.get(i).getNotesMap(), randomNote, rest);
            addEventAttributes(i, k, event, minimumDelay, duration);
        }
    }

    private void createChordElements(int i, int j, int k, Element voice, int notesInAChord, int minimumDelay, boolean areIrregularGroupsPresent, int octavesNumber, Map<Integer, String> pitchesMap, List<Integer> randomPitches) throws NumberFormatException {
        Element chord = addElementToDocument(this.document, "chord");

        String eventRef = "Instrument_" + i + "_voice0_measure" + j + "_ev" + k;
        setAttributeOfElement(chord, "event_ref", eventRef);
        appendChildToElement(voice, chord);

        // TODO gestire caso EMPTY dell'optional
        Optional<Element> optionalEvent = this.eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
        Element event = optionalEvent.get();

        double randomNote = this.randomizer.getRandomNote(notesInAChord, instruments.get(i).getNotesMap());

        int irregularGroup = this.randomizer.getRandomIrregularGroup(instruments.get(i).getIrregularGroupsMap());

        createDurationElement(i, randomNote, chord, k, event, minimumDelay, areIrregularGroupsPresent, irregularGroup);

        createNoteheadElements(notesInAChord, chord, octavesNumber, pitchesMap, randomPitches, k);
    }

    private void createNoteheadElements(int notesInAChord, Element chord, int octavesNumber, Map<Integer, String> pitchesMap, List<Integer> randomPitches, int k) {
        for (int h = 1; h <= notesInAChord; h++) {
            Element notehead = addNoteheadElement(chord);

            Element pitch = addPitchElement(octavesNumber, pitchesMap, randomPitches, k, notehead);

            if (pitchesMap.get(randomPitches.get(k)).length() > 1) {
                setAttributeOfElement(pitch, "actual_accidental", "sharp");

                addPrintedAccidentalsElement(notehead);

                addSharpElement(notehead);
            }
        }
    }

    private void createDurationElement(int i, double randomNote, Element chord, int k, Element event, int minimumDelay, boolean areIrregularGroupsPresent, int irregularGroup) throws NumberFormatException {
        Element duration = addElementToDocument(this.document, "duration");
        if (randomNote == 1) {
            setAttributeOfElement(duration, "den", "" + metreInNumbers[1]);
            setAttributeOfElement(duration, "num", "" + metreInNumbers[0]);
            appendChildToElement(chord, duration);

            addEventAttributes(i, k, event, minimumDelay, duration);

            additionForIrregularGroupsPresence(i, areIrregularGroupsPresent, irregularGroup, duration);

        } else {
            setAttributeOfElement(duration, "den", "" + instruments.get(i).getNotesMap().get(randomNote)[0]);
            setAttributeOfElement(duration, "num", "" + 1);
            appendChildToElement(chord, duration);

            addEventAttributes(i, k, event, minimumDelay, duration);

            additionForIrregularGroupsPresence(i, areIrregularGroupsPresent, irregularGroup, randomNote, duration);
        }
    }

    private int computeNotesNumberInAMeasure(int notesNumber, int eventsNumber) {
        int notesNumberInAMeasure = notesNumber / measuresNumber;
        if (notesNumberInAMeasure > eventsNumber) {
            notesNumberInAMeasure = eventsNumber;
        }
        return notesNumberInAMeasure;
    }

    private void createVoiceListContainer(Element part, int i) {
        Element voiceList = addVoiceListElement(part);

        addVoiceItemElement(i, voiceList);
    }

    private List<Character> createNotesAndRestsList(int eventsNumber, int notesNumberInAMeasure, int restsNumberInAMeasure) {
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
        return notesAndRests;
    }

    private void addStaffListComponents(int i, Element staffList, List<Character> clefs, List<Integer> clefsSteps) {
        Element staff = addStaffElement(i, staffList);

        Element timeSignature = addTimeSignatureElement(i, staff);

        addTimeIndicationElement(metreInNumbers, timeSignature);

        addClefElement(i, clefs, clefsSteps, staff);
    }

    private void createSpineContainer(Element logic, int instrumentsNumber) {
        Element spine = addSpineElement(logic);

        defineFirstEvents(instrumentsNumber, "Instrument_", "_voice0_measure1_ev0", spine);
        defineFirstEvents(instrumentsNumber, "TimeSignature_Instrument_", "_1", spine);
        defineFirstEvents(instrumentsNumber, "Clef_Instrument_", "_1", spine);

        createEvents(measuresNumber, instrumentsNumber, spine);
    }

    private Element createGeneralLayer(String creator, double version, String title, String authorName) throws DOMException {
        Element ieee1599 = addIeee1599Element(creator, version);
        Element general = addGeneralElement(ieee1599);
        Element description = addDescriptionElement(general);
        addMainTitleElement(title, description);
        addAuthorElement(authorName, description);
        return ieee1599;
    }

    public Document getDocument() {
        return this.document;
    }

    private void additionForIrregularGroupsPresence(int i, boolean areIrregularGroupsPresent, int irregularGroup, double randomNote, Element duration) {
        if (areIrregularGroupsPresent) {
            if (this.randomizer.getRandomBoolean()) {
                addTupletRatioElement(irregularGroup, metreInNumbers, instruments.get(i).getIrregularGroupsMap(), instruments.get(i).getNotesMap(), randomNote, duration);
            }
        }
    }

    private void additionForIrregularGroupsPresence(int i, boolean areIrregularGroupsPresent, int irregularGroup, Element duration) {
        if (areIrregularGroupsPresent) {
            if (this.randomizer.getRandomBoolean()) {
                addTupletRatioElement(irregularGroup, metreInNumbers, instruments.get(i).getIrregularGroupsMap(), duration);
            }
        }
    }

    private void addEventAttributes(int i, int k, Element event, int minimumDelay, Element duration) throws NumberFormatException {
        if (k > 0) {
            setAttributeOfElement(event, "timing", "" + minimumDelay * (instruments.get(i).getMinDuration()[0] / Integer.parseInt(duration.getAttribute("den"))));
            setAttributeOfElement(event, "hpos", "" + minimumDelay * (instruments.get(i).getMinDuration()[0] / Integer.parseInt(duration.getAttribute("den"))));
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
        int randomOctave = this.randomizer.getRandomInteger(0, octavesNumber);
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
        setAttributeOfElement(clef, "shape", "" + this.randomizer.getRandomCharFromList(clefs));
        setAttributeOfElement(clef, "staff_step", "" + this.randomizer.getRandomIntegerFromList(clefsSteps));
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

    private void createEvents(int measuresNumber, int instrumentsNumber, Element spine) {
        for (int j = 1; j <= measuresNumber; j++) {
            List<Integer> randomInstruments = this.randomizer.getRandomNonRepeatingIntegers(instrumentsNumber, 1, instrumentsNumber);
            if (this.areFirstEventsDefined) {
                for (int i = 0; i < randomInstruments.size(); i++) {
                    int eventsNumber = this.randomizer.getRandomInteger(1, instruments.get(i).getMaxNumberOfEvents());
                    for (int k = 1; k < eventsNumber; k++) {
                        defineOtherEvents(i, j, k, randomInstruments, spine);
                    }
                }
                this.areFirstEventsDefined = false;
            } else {
                for (int i = 0; i < randomInstruments.size(); i++) {
                    int eventsNumber = this.randomizer.getRandomInteger(1, instruments.get(i).getMaxNumberOfEvents());
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
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            this.document = docBuilder.newDocument();

        } catch (ParserConfigurationException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
