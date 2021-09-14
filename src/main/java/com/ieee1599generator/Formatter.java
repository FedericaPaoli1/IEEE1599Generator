package com.ieee1599generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.paukov.combinatorics3.Generator;

/**
 * Formatter is the class that formats the entire IEEE1599 document
 *
 * @author Federica Paoli', id: 961887, e-mail:
 * federica.paoli1@studenti.unimi.it
 */
public class Formatter {

    private static final Logger LOGGER = Logger.getLogger(Formatter.class.getName());

    /**
     * The randomizer object
     */
    private final Randomizer randomizer;
    /**
     * The document object
     */
    private Document document;
    /**
     * The list of all events of the document
     */
    private final List<Element> eventsList = new ArrayList<>();
    /**
     * The definition or non-definition of the first events in the document
     */
    private boolean areFirstEventsDefined = false;
    /**
     * The document creator name
     */
    private String creator;
    /**
     * The document version
     */
    private double docVersion;
    /**
     * The document title name
     */
    private String title;
    /**
     * The document author name
     */
    private String author;
    /**
     * The number of musical instruments
     */
    private int instrumentsNumber;
    /**
     * The list of musical instruments, each with its own parameters
     */
    private List<Instrument> instruments;
    /**
     * The list of clefs
     */
    private List<Character> clefs;
    /**
     * The list of clefs steps
     */
    private List<Integer> clefsSteps;
    /**
     * The map of accidentals
     */
    private Map<String, Integer> accidentalMap;
    /**
     * The map of pitches
     */
    private Map<Integer, Character> pitchesMap;
    /**
     * The number of available octaves
     */
    private int octavesNumber;
    /**
     * The metre converted from a string into numbers
     */
    private int[] metreInNumbers;
    /**
     * The number of available measures
     */
    private int measuresNumber;

    public Formatter(long seed, String creator, double docVersion, String title, String author, int instrumentsNumber, List<Instrument> instruments, List<Character> clefs, List<Integer> clefsSteps, Map<String, Integer> accidentalMap, Map<Integer, Character> pitchesMap, int octavesNumber, int[] metreInNumbers, int measuresNumber) {
        LOGGER.log(Level.INFO, "Inputs");
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
        for (int i = 0; i < this.instrumentsNumber; i++) {
            LOGGER.log(Level.INFO, "Instrument " + i + ": " + this.instruments.get(i).toString());
        }
        this.clefs = clefs;
        LOGGER.log(Level.INFO, "Clefs:");
        for (Character c : clefs) {
            LOGGER.log(Level.INFO, "" + c);
        }
        this.clefsSteps = clefsSteps;
        LOGGER.log(Level.INFO, "Clefs steps:");
        for (Integer i : clefsSteps) {
            LOGGER.log(Level.INFO, "" + i);
        }
        this.accidentalMap = accidentalMap;
        LOGGER.log(Level.INFO, "Accidentals map: " + mapAsString(this.accidentalMap));
        this.pitchesMap = pitchesMap;
        LOGGER.log(Level.INFO, "Pitches map: " + mapAsString(this.pitchesMap));
        this.octavesNumber = octavesNumber;
        LOGGER.log(Level.INFO, "Octaves number: " + this.octavesNumber);
        this.metreInNumbers = metreInNumbers;
        LOGGER.log(Level.INFO, "Metre: " + metreInNumbers[0] + ":" + metreInNumbers[1]);
        this.measuresNumber = measuresNumber;
        LOGGER.log(Level.INFO, "Measures number: " + this.measuresNumber);
    }

    public void format() {

        //LOGGER.log(Level.INFO, "Create document");
        createDocument();

        //LOGGER.log(Level.INFO, "Create general layer");
        Element ieee1599 = createGeneralLayer();

        //LOGGER.log(Level.INFO, "Create logic layer");
        createLogicLayer(ieee1599);
    }

    private void createLogicLayer(Element ieee1599) throws NumberFormatException {
        Element logic = addLogicElement(ieee1599);

        //LOGGER.log(Level.INFO, "Create spine container");
        createSpineContainer(logic);

        //LOGGER.log(Level.INFO, "Create los container");
        createLosContainer(logic);
    }

    private void createLosContainer(Element logic) throws NumberFormatException {
        Element los = addLosElement(logic);

        Element staffList = addStaffListElement(los);

        for (int i = 0; i < instrumentsNumber; i++) {
            //LOGGER.log(Level.INFO, "INSTRUMENT {0}", i);

            addStaffListComponents(i, staffList);

            //LOGGER.log(Level.INFO, "Create part element");
            createPartElement(i, los);
        }
    }

    private void createPartElement(int i, Element los) throws NumberFormatException {
        Element part = addPartElement(i, los);

        //LOGGER.log(Level.INFO, "Create voice list container");
        createVoiceListContainer(part, i);

        //LOGGER.log(Level.INFO, "Create measure elements");
        createMeasureElements(part, i);
    }

    private void createMeasureElements(Element part, int i) throws NumberFormatException {
        for (int j = 1; j <= measuresNumber; j++) {
            //LOGGER.log(Level.INFO, "MEASURE {0}", j);

            Element measure = addMeasureElement(j, part);

            //LOGGER.log(Level.INFO, "Create voice element");
            createVoiceElement(i, measure, j);
        }
    }

    private void createVoiceElement(int i, Element measure, int j) throws NumberFormatException {
        //LOGGER.log(Level.INFO, "Add voice element");
        Element voice = addVoiceElement(i, measure);

        String attributeString = "Instrument_" + (i + 1) + "_voice0_measure" + j + "_";
        int eventsNumber = (int) this.eventsList.stream().filter(e -> e.getAttribute("id").contains(attributeString)).count();
        //LOGGER.log(Level.INFO, "Events number: {0}", eventsNumber);

        int notesNumber = this.randomizer.getRandomInteger(1, instruments.get(i).getMaxNumberOfPlayedNotes());
        //LOGGER.log(Level.INFO, "Notes number: {0}", notesNumber);

        int notesNumberInAMeasure = computeNotesNumberInAMeasure(notesNumber, eventsNumber);
        //LOGGER.log(Level.INFO, "Notes number in a measure: {0}", notesNumberInAMeasure);

        int restsNumberInAMeasure = eventsNumber - notesNumberInAMeasure;
        //LOGGER.log(Level.INFO, "Rests number: {0}", restsNumberInAMeasure);

        List<Character> notesAndRests = createNotesAndRestsList(eventsNumber, notesNumberInAMeasure, restsNumberInAMeasure);

        //Collections.shuffle(notesAndRests);
        this.randomizer.shuffleList(notesAndRests);

        /*notesAndRests.forEach(c -> {
        System.out.println(c);
        });*/
        List<Integer> randomPitches = this.randomizer.getRandomIntegers(notesAndRests.size(), instruments.get(i).getMinHeight(), instruments.get(i).getMaxHeight());

        int notesInAChord = this.randomizer.getRandomInteger(1, instruments.get(i).getMaxNumberOfNotesInAChord());
        //LOGGER.log(Level.INFO, "Notes in a chord: {0}", notesInAChord);

        /*randomPitches.forEach(p -> {
            System.out.println(p);
        });*/
        List<Double> notesMapKeysList = new ArrayList<Double>(instruments.get(i).getNotesMap().keySet());

        List<Double> correctNotesAndRests = Generator
                .combination(notesMapKeysList)
                .multi(notesAndRests.size())
                .stream()
                .filter(d -> d.stream().reduce(0.0, Double::sum) == 1.0)
                .findFirst()
                .orElse(new ArrayList<Double>());

        //Collections.shuffle(correctNotesAndRests);
        this.randomizer.shuffleList(correctNotesAndRests);

        for (int k = 0; k < notesAndRests.size(); k++) {

            if (notesAndRests.get(k) == 'N') {
                // LOGGER.log(Level.INFO, "Create chord elements");
                createChordElements(i, j, k, voice, notesInAChord, correctNotesAndRests, randomPitches);

            } else {
                //  LOGGER.log(Level.INFO, "Create rest elements");
                createRestElements(i, j, k, voice, correctNotesAndRests);
            }
        }
    }

    private void createRestElements(int i, int j, int k, Element voice, List<Double> correctNotesAndRests) throws NumberFormatException {

        String eventRef = "Instrument_" + (i + 1) + "_voice0_measure" + j + "_ev" + k;
        // TODO gestire caso EMPTY dell'optional
        Optional<Element> optionalEvent = this.eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
        Element event = optionalEvent.get();

        if (!correctNotesAndRests.isEmpty()) {
            Element rest = addElementToDocument(this.document, "rest");

            setAttributeOfElement(rest, "event_ref", eventRef);
            appendChildToElement(voice, rest);

            // LOGGER.log(Level.INFO, "Get random note");
            //double randomNote = this.randomizer.getRandomNote(instruments.get(i).getNotesMap());
            double randomNote = correctNotesAndRests.remove(0);
            createDurationElement(i, randomNote, rest, k, event);
        } else {
            Element duration = addElementToDocument(this.document, "duration");
            setAttributeOfElement(duration, "den", "");
            setAttributeOfElement(duration, "num", "");

            addEventAttributes(i, k, event, duration);
        }
    }

    private void createDurationElement(int i, double randomNote, Element rest, int k, Element event) throws NumberFormatException {
        if (randomNote == 1) {
            Element duration = addDurationElement(rest);
            addEventAttributes(i, k, event, duration);

        } else {
            Element duration = addDurationElement(instruments.get(i).getNotesMap(), randomNote, rest);
            addEventAttributes(i, k, event, duration);
        }
    }

    private void createChordElements(int i, int j, int k, Element voice, int notesInAChord, List<Double> correctNotesAndRests, List<Integer> randomPitches) throws NumberFormatException {
        String eventRef = "Instrument_" + (i + 1) + "_voice0_measure" + j + "_ev" + k;

        // TODO gestire caso EMPTY dell'optional
        Optional<Element> optionalEvent = this.eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
        Element event = optionalEvent.get();

        if (!correctNotesAndRests.isEmpty()) {
            Element chord = addElementToDocument(this.document, "chord");

            setAttributeOfElement(chord, "event_ref", eventRef);
            appendChildToElement(voice, chord);

            //double randomNote = this.randomizer.getRandomNote(instruments.get(i).getNotesMap());
            double randomNote = correctNotesAndRests.remove(0);
            int irregularGroup = this.randomizer.getRandomIntFromMap(instruments.get(i).getIrregularGroupsMap());

            createDurationElement(i, randomNote, chord, k, event, irregularGroup);

            createNoteheadElements(notesInAChord, chord, randomPitches, k);
        } else {
            Element duration = addElementToDocument(this.document, "duration");
            setAttributeOfElement(duration, "den", "");
            setAttributeOfElement(duration, "num", "");

            addEventAttributes(i, k, event, duration);
        }
    }

    private void createNoteheadElements(int notesInAChord, Element chord, List<Integer> randomPitches, int k) {
        for (int h = 1; h <= notesInAChord; h++) {
            Element notehead = addNoteheadElement(chord);

            addPitchElement(randomPitches, k, notehead);
        }
    }

    private void createDurationElement(int i, double randomNote, Element chord, int k, Element event, int irregularGroup) throws NumberFormatException {
        Element duration = addElementToDocument(this.document, "duration");
        if (randomNote == 1) {
            setAttributeOfElement(duration, "den", "" + metreInNumbers[1]);
            setAttributeOfElement(duration, "num", "" + metreInNumbers[0]);
            appendChildToElement(chord, duration);

            addEventAttributes(i, k, event, duration);

            additionForIrregularGroupsPresence(i, irregularGroup, duration);

        } else {
            setAttributeOfElement(duration, "den", "" + instruments.get(i).getNotesMap().get(randomNote)[0]);
            setAttributeOfElement(duration, "num", "" + 1);
            appendChildToElement(chord, duration);

            addEventAttributes(i, k, event, duration);

            additionForIrregularGroupsPresence(i, irregularGroup, randomNote, duration);
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

    private void addStaffListComponents(int i, Element staffList) {
        Element staff = addStaffElement(i, staffList);

        Element timeSignature = addTimeSignatureElement(i, staff);

        addTimeIndicationElement(timeSignature);

        addClefElement(i, staff);
    }

    private void createSpineContainer(Element logic) {
        Element spine = addSpineElement(logic);

        defineFirstEvents("Instrument_", "_voice0_measure1_ev0", spine);
        defineFirstEvents("TimeSignature_Instrument_", "_1", spine);
        defineFirstEvents("Clef_Instrument_", "_1", spine);

        createEvents(spine);
    }

    private Element createGeneralLayer() throws DOMException {
        Element ieee1599 = addIeee1599Element(creator, docVersion);
        Element general = addGeneralElement(ieee1599);
        Element description = addDescriptionElement(general);
        addMainTitleElement(description);
        addAuthorElement(description);
        return ieee1599;
    }

    public Document getDocument() {
        return this.document;
    }

    private void additionForIrregularGroupsPresence(int i, int irregularGroup, double randomNote, Element duration) {
        if (this.instruments.get(i).getAreIrregularGroupsPresent()) {
            if (this.randomizer.getRandomBoolean()) {
                addTupletRatioElement(irregularGroup, instruments.get(i).getIrregularGroupsMap(), instruments.get(i).getNotesMap(), randomNote, duration);
            }
        }
    }

    private void additionForIrregularGroupsPresence(int i, int irregularGroup, Element duration) {
        if (this.instruments.get(i).getAreIrregularGroupsPresent()) {
            if (this.randomizer.getRandomBoolean()) {
                addTupletRatioElement(irregularGroup, instruments.get(i).getIrregularGroupsMap(), duration);
            }
        }
    }

    private void addEventAttributes(int i, int k, Element event, Element duration) throws NumberFormatException {
        if (k > 0 && duration.getAttribute("den") != "") {
            setAttributeOfElement(event, "timing", "" + this.instruments.get(i).getMinimumDelay() * (instruments.get(i).getMinDuration()[0] / Integer.parseInt(duration.getAttribute("den"))));
            setAttributeOfElement(event, "hpos", "" + this.instruments.get(i).getMinimumDelay() * (instruments.get(i).getMinDuration()[0] / Integer.parseInt(duration.getAttribute("den"))));
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

    private Element addDurationElement(Element rest) {
        Element duration = addElementToDocument(this.document, "duration");
        setAttributeOfElement(duration, "den", "" + metreInNumbers[1]);
        setAttributeOfElement(duration, "num", "" + metreInNumbers[0]);
        appendChildToElement(rest, duration);
        return duration;
    }

    private void addAccidentalElement(String accidentalType, Element notehead) {
        Element accidental = addElementToDocument(this.document, accidentalType);
        appendChildToElement(notehead, accidental);
    }

    private void addPrintedAccidentalsElement(Element notehead) {
        Element printedAccidentals = addElementToDocument(this.document, "printed_accidentals");
        appendChildToElement(notehead, printedAccidentals);
    }

    private void addPitchElement(List<Integer> randomPitches, int k, Element notehead) {
        char randomPitch = pitchesMap.get(randomPitches.get(k));

        String randomAccidental = this.randomizer.getRandomStringFromMap(accidentalMap);

        Element pitch = addElementToDocument(this.document, "pitch");
        int randomOctave = this.randomizer.getRandomInteger(0, octavesNumber);

        setAttributeOfElement(pitch, "octave", "" + randomOctave);

        if (randomPitch == ' ') {
            int newPitchIndex = randomPitches.get(k) + accidentalMap.get(randomAccidental);

            if (newPitchIndex < 0) {
                newPitchIndex = 11 + accidentalMap.get(randomAccidental);
            }

            if (newPitchIndex > 11) {
                newPitchIndex = 0 + accidentalMap.get(randomAccidental);
            }

            randomPitch = pitchesMap.get(newPitchIndex);
            if (randomPitch == ' ') {
                if (this.randomizer.getRandomString(new String[]{"sharp", "flat"}).equals("sharp")) {
                    randomPitch = pitchesMap.get(newPitchIndex + 1);

                    setAttributeOfElement(pitch, "actual_accidental", "" + randomAccidental);

                    setAttributeOfElement(pitch, "step", "" + randomPitch + "-" + "sharp");

                    appendChildToElement(notehead, pitch);

                    addPrintedAccidentalsElement(notehead);

                    addAccidentalElement(randomAccidental, notehead);
                } else {
                    randomPitch = pitchesMap.get(newPitchIndex - 1);

                    setAttributeOfElement(pitch, "actual_accidental", "" + randomAccidental);

                    setAttributeOfElement(pitch, "step", "" + randomPitch + "-" + "flat");

                    appendChildToElement(notehead, pitch);

                    addPrintedAccidentalsElement(notehead);

                    addAccidentalElement(randomAccidental, notehead);
                }
            } else {
                setAttributeOfElement(pitch, "actual_accidental", "" + randomAccidental);

                setAttributeOfElement(pitch, "step", "" + randomPitch);

                appendChildToElement(notehead, pitch);

                addPrintedAccidentalsElement(notehead);

                addAccidentalElement(randomAccidental, notehead);

            }

        } else {
            setAttributeOfElement(pitch, "actual_accidental", "natural");

            setAttributeOfElement(pitch, "step", "" + randomPitch);

            appendChildToElement(notehead, pitch);

        }

    }

    private Element addNoteheadElement(Element chord) {
        Element notehead = addElementToDocument(this.document, "notehead");
        appendChildToElement(chord, notehead);
        return notehead;
    }

    private void addTupletRatioElement(int irregularGroup, Map<Integer, Integer> irregularGroupsMap, Map<Double, int[]> notesMap, double randomNote, Element duration) {
        Element tupletRatio = addElementToDocument(this.document, "tuplet_ratio");
        setAttributeOfElement(tupletRatio, "enter_num", "" + irregularGroup);
        setAttributeOfElement(tupletRatio, "enter_den", "" + metreInNumbers[1] * irregularGroupsMap.get(irregularGroup));
        setAttributeOfElement(tupletRatio, "in_num", "" + 1);
        setAttributeOfElement(tupletRatio, "in_den", "" + notesMap.get(randomNote)[0]);
        appendChildToElement(duration, tupletRatio);
    }

    private void addTupletRatioElement(int irregularGroup, Map<Integer, Integer> irregularGroupsMap, Element duration) {
        Element tupletRatio = addElementToDocument(this.document, "tuplet_ratio");
        setAttributeOfElement(tupletRatio, "enter_num", "" + irregularGroup);
        setAttributeOfElement(tupletRatio, "enter_den", "" + metreInNumbers[1] * irregularGroupsMap.get(irregularGroup));
        setAttributeOfElement(tupletRatio, "in_num", "" + 1);
        setAttributeOfElement(tupletRatio, "in_den", "" + metreInNumbers[1]);
        appendChildToElement(duration, tupletRatio);
    }

    private Element addVoiceElement(int i, Element measure) {
        Element voice = addElementToDocument(this.document, "voice");
        setAttributeOfElement(voice, "voice_item_ref", "Instrument_" + (i + 1) + "_0_voice");
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
        setAttributeOfElement(voiceItem, "id", "Instrument_" + (i + 1) + "_0_voice");
        setAttributeOfElement(voiceItem, "staff_ref", "Instrument_" + (i + 1) + "_staff");
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

    private void addClefElement(int i, Element staff) {
        Element clef = addElementToDocument(this.document, "clef");
        setAttributeOfElement(clef, "event_ref", "Clef_Instrument_" + (i + 1) + "_1");
        setAttributeOfElement(clef, "shape", "" + this.randomizer.getRandomElementFromList(clefs));
        setAttributeOfElement(clef, "staff_step", "" + this.randomizer.getRandomElementFromList(clefsSteps));
        setAttributeOfElement(clef, "octave_num", "" + 0);
        appendChildToElement(staff, clef);
    }

    private void addTimeIndicationElement(Element timeSignature) {
        Element timeIndication = addElementToDocument(this.document, "time_indication");
        setAttributeOfElement(timeIndication, "den", "" + metreInNumbers[1]);
        setAttributeOfElement(timeIndication, "num", "" + metreInNumbers[0]);
        appendChildToElement(timeSignature, timeIndication);
    }

    private Element addTimeSignatureElement(int i, Element staff) {
        Element timeSignature = addElementToDocument(this.document, "time_signature");
        setAttributeOfElement(timeSignature, "event_ref", "TimeSignature_Instrument_" + (i + 1) + "_1");
        appendChildToElement(staff, timeSignature);
        return timeSignature;
    }

    private Element addStaffElement(int i, Element staffList) {
        Element staff = addElementToDocument(this.document, "staff");
        setAttributeOfElement(staff, "id", "Instrument_" + (i + 1) + "_staff");
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

    private void addAuthorElement(Element description) {
        Element author = addElementToDocument(this.document, "author");
        setTextContentOfElement(author, this.author);
        appendChildToElement(description, author);
    }

    private void addMainTitleElement(Element description) {
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

    private void createEvents(Element spine) {
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

    private void defineFirstEvents(String firstPart, String secondPart, Element spine) {
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

    private <T, R> String mapAsString(Map<T, R> map) {
        String mapAsString = map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        return mapAsString;
    }
}
