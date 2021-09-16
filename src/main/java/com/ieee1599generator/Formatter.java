package com.ieee1599generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
    private Map<String, Float> accidentalMap;
    /**
     * The map of all notes
     */
    private Map<Float, List<String>> allNotesMap;
    /**
     * The metre converted from a string into numbers
     */
    private int[] metreInNumbers;
    /**
     * The number of available measures
     */
    private int measuresNumber;
    /**
     * The map of irregular groups
     */
    private Map<Integer, Integer> irregularGroupsMap;

    public Formatter(long seed, String creator, double docVersion, String title, String author, int instrumentsNumber, List<Instrument> instruments, List<Character> clefs, List<Integer> clefsSteps, Map<String, Float> accidentalMap, Map<Float, List<String>> allNotesMap, int[] metreInNumbers, int measuresNumber, Map<Integer, Integer> irregularGroupsMap) {
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
        clefs.forEach(clef -> LOGGER.log(Level.INFO, "" + clef));

        this.clefsSteps = clefsSteps;
        LOGGER.log(Level.INFO, "Clefs steps:");
        clefsSteps.forEach(clefStep -> LOGGER.log(Level.INFO, "" + clefStep));

        this.accidentalMap = accidentalMap;
        LOGGER.log(Level.INFO, "Accidentals map: " + mapAsString(this.accidentalMap));

        this.allNotesMap = allNotesMap;
        LOGGER.log(Level.INFO, "All notes map: " + mapAsString(this.allNotesMap));

        this.metreInNumbers = metreInNumbers;
        LOGGER.log(Level.INFO, "Metre: " + metreInNumbers[0] + ":" + metreInNumbers[1]);

        this.measuresNumber = measuresNumber;
        LOGGER.log(Level.INFO, "Measures number: " + this.measuresNumber);

        this.irregularGroupsMap = irregularGroupsMap;
        //LOGGER.log(Level.INFO, "Irregular groups map: " + mapAsString(this.irregularGroupsMap));
    }

    /**
     * <p>
     * format is the method that formats the IEEE1599 document
     * </p>
     *
     * @throws ParserConfigurationException if there is a configuration error
     * for the DocumentBuilderFactory class
     */
    public void format() throws ParserConfigurationException {

        LOGGER.log(Level.INFO, "Create document");
        createDocument();

        LOGGER.log(Level.INFO, "Create general layer");
        Element ieee1599 = createGeneralLayer();

        LOGGER.log(Level.INFO, "Create logic layer");
        createLogicLayer(ieee1599);
    }

    /**
     * <p>
     * createLogicLayer is the method that add logic layer elements to the
     * IEEE1599 document
     * </p>
     *
     * @param ieee1599 the element whose logic layer is to be added
     */
    private void createLogicLayer(Element ieee1599) {
        Element logic = addElementAndReturnIt(ieee1599, "logic");

        LOGGER.log(Level.INFO, "Create spine container");
        createSpineContainer(logic);

        LOGGER.log(Level.INFO, "Create los container");
        createLosContainer(logic);
    }

    /**
     * <p>
     * createLosContainer is the method that adds los container elements to the
     * logic layer of the IEEE1599 document
     * </p>
     *
     * @param logic the element to be created and whose events are to be added
     * as attributes
     */
    private void createLosContainer(Element logic) {
        Element los = addElementAndReturnIt(logic, "los");

        Element staffList = addElementAndReturnIt(los, "staff_list");

        for (int i = 0; i < instrumentsNumber; i++) {
            LOGGER.log(Level.INFO, "INSTRUMENT" + i);

            addStaffListComponents(i, staffList);

            LOGGER.log(Level.INFO, "Create part element");
            createPartElement(i, los);
        }
    }

    /**
     * <p>
     * createPartElement is the method that creates the part element
     * </p>
     *
     * @param i the index of the instruments
     * @param los the element whose child is to be appended
     *
     */
    private void createPartElement(int i, Element los) {
        Element part = addElementAndSetOneAttributeReturningTheElement(los, "part", "id", "Instrument_" + i);

        LOGGER.log(Level.INFO, "Create voice list container");
        createVoiceListContainer(part, i);

        LOGGER.log(Level.INFO, "Create measure elements");
        createMeasureElements(part, i);
    }

    /**
     * <p>
     * createMeasureElements is the method that creates the measure elements
     * </p>
     *
     * @param part the element whose child is to be appended
     * @param i the index of the instruments
     *
     */
    private void createMeasureElements(Element part, int i) {
        for (int j = 1; j <= measuresNumber; j++) {
            LOGGER.log(Level.INFO, "MEASURE" + j);

            Element measure = addElementAndSetOneAttributeReturningTheElement(part, "measure", "number", "" + j);

            LOGGER.log(Level.INFO, "Create voice element");
            createVoiceElement(i, measure, j);
        }
    }

    /**
     * <p>
     * createVoiceElement is the method that creates the voice elements
     * </p>
     *
     * @param i the index of the instruments
     * @param measure the element whose child is to be appended
     * @param j the index of the measures
     *
     */
    private void createVoiceElement(int i, Element measure, int j) {
        LOGGER.log(Level.INFO, "Add voice element");
        Element voice = addElementAndSetOneAttributeReturningTheElement(measure, "voice", "voice_item_ref", "Instrument_" + (i + 1) + "_0_voice");

        String attributeString = "Instrument_" + (i + 1) + "_voice0_measure" + j + "_";
        // number of events concerning the actual instrument in the actual measure
        int eventsNumber = (int) this.eventsList.stream().filter(e -> e.getAttribute("id").contains(attributeString)).count();
        LOGGER.log(Level.INFO, "Events number: " + eventsNumber);

        // random number of notes played by the actual instrument
        int notesNumber = this.randomizer.getRandomInteger(1, this.instruments.get(i).getMaxNumberOfPlayedNotes());
        LOGGER.log(Level.INFO, "Notes number: " + notesNumber);

        int notesNumberInAMeasure = computeNotesNumberInAMeasure(notesNumber, eventsNumber);
        LOGGER.log(Level.INFO, "Notes number in a measure: " + notesNumberInAMeasure);

        int restsNumberInAMeasure = eventsNumber - notesNumberInAMeasure;
        LOGGER.log(Level.INFO, "Rests number: " + restsNumberInAMeasure);

        List<Character> notesAndRests = createNotesAndRestsList(eventsNumber, notesNumberInAMeasure, restsNumberInAMeasure);
        this.randomizer.shuffleList(notesAndRests);
        LOGGER.log(Level.INFO, "Notes and rests:");
        notesAndRests.forEach(noteOrRest -> LOGGER.log(Level.INFO, "" + noteOrRest));

        float minHeight = 0f;
        float maxHeight = 0f;
        for (Map.Entry<Float, List<String>> e : this.allNotesMap.entrySet()) {
            if (e.getValue().contains(this.instruments.get(i).getMinHeight().replaceAll("\\d",""))) {
                minHeight = e.getKey();
            }
        }

        for (Map.Entry<Float, List<String>> e : this.allNotesMap.entrySet()) {
            if (e.getValue().contains(this.instruments.get(i).getMaxHeight().replaceAll("\\d",""))) {
                maxHeight = e.getKey();
            }
        }
        float randomPitch = this.randomizer.getRandomFloat(minHeight, maxHeight);
        LOGGER.log(Level.INFO, "Random pitch: " + randomPitch);

        // random number of notes in a chord
        int notesInAChord = this.randomizer.getRandomInteger(1, this.instruments.get(i).getMaxNumberOfNotesInAChord());
        LOGGER.log(Level.INFO, "Notes in a chord: " + notesInAChord);

        // list of notes represented as decimals
        List<Double> notesMapKeysList = new ArrayList<Double>(this.instruments.get(i).getNotesMap().keySet());

        // combinations with repetition of notes, in groups of notes and rests number, whose sum equals 1 and, therefore, it fills the measure
        List<Double> correctNotesAndRests = Generator
                .combination(notesMapKeysList)
                .multi(notesAndRests.size())
                .stream()
                .filter(d -> d.stream().reduce(0.0, Double::sum) == 1.0)
                .findFirst()
                .orElse(new ArrayList<Double>());

        this.randomizer.shuffleList(correctNotesAndRests);
        LOGGER.log(Level.INFO, "Correct notes and rests:");
        correctNotesAndRests.forEach(correctNoteOrRest -> LOGGER.log(Level.INFO, "" + correctNoteOrRest));

        for (int k = 0; k < notesAndRests.size(); k++) {

            if (notesAndRests.get(k) == 'N') {
                LOGGER.log(Level.INFO, "Create chord elements");
                createChordElements(i, j, k, voice, notesInAChord, correctNotesAndRests, randomPitch);

            } else {
                LOGGER.log(Level.INFO, "Create rest elements");
                createRestElements(i, j, k, voice, correctNotesAndRests);
            }
        }
    }

    /**
     * <p>
     * createRestElements is the method that creates the rest elements
     * </p>
     *
     * @param i the index of the instruments number
     * @param j the index of the measures number
     * @param k the index of the notes and rests number
     * @param voice the element whose child is to be appended
     * @param correctNotesAndRests the correct notes and rests list
     *
     * @throws NoSuchElementException if there is a configuration error
     */
    private void createRestElements(int i, int j, int k, Element voice, List<Double> correctNotesAndRests) {
        String eventRef = "Instrument_" + (i + 1) + "_voice0_measure" + j + "_ev" + k;

        // there is only one event that corresponds to the actual instrument in the actual measure, so if everything has been correctly configured, the Optional can never be empty
        Optional<Element> optionalEvent = this.eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
        if (optionalEvent.isEmpty()) {
            throw new NoSuchElementException("The event corresponding to the instrument " + (i + 1) + " in the measure " + j + " does not exist.");
        }
        Element event = optionalEvent.get();
        LOGGER.log(Level.INFO, "Event: " + event);

        if (!correctNotesAndRests.isEmpty()) {
            LOGGER.log(Level.INFO, "Add rest element");
            Element rest = addElementAndSetOneAttributeReturningTheElement(voice, "rest", "event_ref", eventRef);

            double randomNote = correctNotesAndRests.remove(0);
            LOGGER.log(Level.INFO, "Random note: " + randomNote);

            createRestDurationElement(i, randomNote, rest, k, event);
        } else {
            LOGGER.log(Level.INFO, "Add duration element");
            Element duration = addElementToDocument(this.document, "duration");
            setAttributeOfElement(duration, "den", "");
            setAttributeOfElement(duration, "num", "");

            LOGGER.log(Level.INFO, "Add event attributes of duration element");
            addEventAttributes(i, k, event, duration);
        }
    }

    /**
     * <p>
     * createRestDurationElement is the method that creates the duration element
     * for the rest one
     * </p>
     *
     * @param i the index of the instruments number
     * @param randomNote the index of the notes and rests number
     * @param rest the element whose child is to be appended
     * @param k the index of notes and rests number
     * @param event the element whose attributes are to be added
     *
     */
    private void createRestDurationElement(int i, double randomNote, Element rest, int k, Element event) {
        if (randomNote == 1) {
            LOGGER.log(Level.INFO, "Add duration element");
            Element duration = addElementAndSetTwoAttributesReturningTheElement(rest, "duration", "den", "" + metreInNumbers[1], "num", "" + metreInNumbers[0]);

            LOGGER.log(Level.INFO, "Add event attributes of duration element");
            addEventAttributes(i, k, event, duration);

        } else {
            LOGGER.log(Level.INFO, "Add duration element");
            Element duration = addElementAndSetTwoAttributesReturningTheElement(rest, "duration", "den", "" + this.instruments.get(i).getNotesMap().get(randomNote)[0], "num", "" + 1);

            LOGGER.log(Level.INFO, "Add event attributes of duration element");
            addEventAttributes(i, k, event, duration);
        }
    }

    /**
     * <p>
     * createChordElements is the method that creates the chord elements
     * </p>
     *
     * @param i the index of the instruments number
     * @param j the index of the measures number
     * @param k the index of the notes and rests number
     * @param voice the element whose child is to be appended
     * @param notesInAChord the number of notes in a chord
     * @param correctNotesAndRests the correct notes and rests list
     * @param randomPitches the random pitches list
     *
     * @throws NoSuchElementException if there is a configuration error
     */
    private void createChordElements(int i, int j, int k, Element voice, int notesInAChord, List<Double> correctNotesAndRests, float randomPitch) {
        String eventRef = "Instrument_" + (i + 1) + "_voice0_measure" + j + "_ev" + k;

        // there is only one event that corresponds to the actual instrument in the actual measure, so if everything has been correctly configured, the Optional can never be empty
        Optional<Element> optionalEvent = this.eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
        if (optionalEvent.isEmpty()) {
            throw new NoSuchElementException("The event corresponding to the instrument " + (i + 1) + " in the measure " + j + " does not exist.");
        }
        Element event = optionalEvent.get();
        LOGGER.log(Level.INFO, "Event: " + event);

        if (!correctNotesAndRests.isEmpty()) {
            LOGGER.log(Level.INFO, "Add chord element");
            Element chord = addElementAndSetOneAttributeReturningTheElement(voice, "chord", "event_ref", eventRef);

            double randomNote = correctNotesAndRests.remove(0);
            LOGGER.log(Level.INFO, "Random note: " + randomNote);

            // random irregular group from the map
            int irregularGroup = this.randomizer.getRandomIntFromMap(this.irregularGroupsMap);
            LOGGER.log(Level.INFO, "Irregular group: " + irregularGroup);

            createChordDurationElement(i, randomNote, chord, k, event, irregularGroup);

            createNoteheadElements(notesInAChord, chord, randomPitch, i);
        } else {
            LOGGER.log(Level.INFO, "Add duration element");
            Element duration = addElementToDocument(this.document, "duration");
            setAttributeOfElement(duration, "den", "");
            setAttributeOfElement(duration, "num", "");

            LOGGER.log(Level.INFO, "Add event attributes of duration element");
            addEventAttributes(i, k, event, duration);
        }
    }

    /**
     * <p>
     * createNoteheadElements is the method that creates the notehead element
     * </p>
     *
     * @param notesInAChord the number of notes in a chord
     * @param chord the element whose child is to be appended
     * @param randomPitches the random pitches list
     * @param k the index of notes and rests number
     *
     */
    private void createNoteheadElements(int notesInAChord, Element chord, float randomPitch, int i) {
        for (int n = 1; n <= notesInAChord; n++) {
            LOGGER.log(Level.INFO, "Add notehead element");
            Element notehead = addElementAndReturnIt(chord, "notehead");

            addPitchElement(randomPitch, notehead, i);
        }
    }

    /**
     * <p>
     * createChordDurationElement is the method that creates the duration
     * element for the chord one
     * </p>
     *
     * @param i the index of the instruments number
     * @param randomNote the index of the notes and rests number
     * @param chord the element whose child is to be appended
     * @param k the index of notes and rests number
     * @param event the element whose attributes are to be added
     * @param irregularGroup the random pitches list
     *
     */
    private void createChordDurationElement(int i, double randomNote, Element chord, int k, Element event, int irregularGroup) {
        if (randomNote == 1) {
            LOGGER.log(Level.INFO, "Add duration element");
            Element duration = addElementAndSetTwoAttributesReturningTheElement(chord, "duration", "den", "" + metreInNumbers[1], "num", "" + metreInNumbers[0]);

            LOGGER.log(Level.INFO, "Add event attributes of duration element");
            addEventAttributes(i, k, event, duration);

            additionForIrregularGroupsPresence(this.instruments.get(i).getAreIrregularGroupsPresent(), duration, "tuplet_ratio", "enter_num", "" + irregularGroup, "enter_den", "" + this.metreInNumbers[1] * this.irregularGroupsMap.get(irregularGroup), "in_num", "" + 1, "in_den", "" + this.metreInNumbers[1]);

        } else {
            LOGGER.log(Level.INFO, "Add duration element");
            Element duration = addElementAndSetTwoAttributesReturningTheElement(chord, "duration", "den", "" + instruments.get(i).getNotesMap().get(randomNote)[0], "num", "" + 1);

            LOGGER.log(Level.INFO, "Add event attributes of duration element");
            addEventAttributes(i, k, event, duration);

            additionForIrregularGroupsPresence(this.instruments.get(i).getAreIrregularGroupsPresent(), duration, "tuplet_ratio", "enter_num", "" + irregularGroup, "enter_den", "" + this.metreInNumbers[1] * this.irregularGroupsMap.get(irregularGroup), "in_num", "" + 1, "in_den", "" + this.instruments.get(i).getNotesMap().get(randomNote)[0]);
        }
    }

    /**
     * <p>
     * computeNotesNumberInAMeasure is the method that computes the total number
     * of notes in a measure
     * </p>
     *
     * @param notesNumber the total number of notes
     * @param eventsNumber the total number of events
     *
     * @return the number of notes in a measure
     */
    private int computeNotesNumberInAMeasure(int notesNumber, int eventsNumber) {
        int notesNumberInAMeasure = notesNumber / this.measuresNumber;
        if (notesNumberInAMeasure > eventsNumber) {
            notesNumberInAMeasure = eventsNumber;
        }
        return notesNumberInAMeasure;
    }

    /**
     * <p>
     * createVoiceListContainer is the method that creates the voice_list
     * container
     * </p>
     *
     * @param part the element whose voice_list container is to be appended
     * @param i the index of the instruments
     *
     */
    private void createVoiceListContainer(Element part, int i) {
        Element voiceList = addElementAndReturnIt(part, "voice_list");

        addElementAndSetTwoAttributes(voiceList, "voice_item", "id", "Instrument_" + (i + 1) + "_0_voice", "staff_ref", "Instrument_" + (i + 1) + "_staff");
    }

    /**
     * <p>
     * createNotesAndRestsList is the method that creates a unique list of notes
     * and rests
     * </p>
     *
     * @param eventsNumber the total number of events
     * @param notesNumberInAMeasure the total number of notes
     * @param restsNumberInAMeasure the total number of rests
     *
     * @return the list of characters containing notes (denoted with 'N') and
     * rests (denoted with 'R')
     */
    private List<Character> createNotesAndRestsList(int eventsNumber, int notesNumberInAMeasure, int restsNumberInAMeasure) {
        List<Character> notesAndRests = new ArrayList<>();
        int countNotes = 1, countRests = 1;
        for (int e = 0; e < eventsNumber; e++) {
            while (countNotes < notesNumberInAMeasure) {
                notesAndRests.add('N');
                countNotes++;
            }
            while (countRests < restsNumberInAMeasure) {
                notesAndRests.add('R');
                countRests++;
            }
        }
        return notesAndRests;
    }

    /**
     * <p>
     * addStaffListComponents is the method that adds staff_list components
     * </p>
     *
     * @param i the index of the instruments
     * @param staffList the element whose components are to be added
     *
     */
    private void addStaffListComponents(int i, Element staffList) {
        Element staff = addElementAndSetTwoAttributesReturningTheElement(staffList, "staff", "id", "Instrument_" + (i + 1) + "_staff", "line_number", "" + 5);

        Element timeSignature = addElementAndSetOneAttributeReturningTheElement(staff, "time_signature", "event_ref", "TimeSignature_Instrument_" + (i + 1) + "_1");

        addElementAndSetTwoAttributes(timeSignature, "time_indication", "den", "" + this.metreInNumbers[1], "num", "" + this.metreInNumbers[0]);

        addElementAndSetFourAttributes(staff, "clef", "event_ref", "Clef_Instrument_" + (i + 1) + "_1", "shape", "" + this.randomizer.getRandomElementFromList(this.clefs), "staff_step", "" + this.randomizer.getRandomElementFromList(this.clefsSteps), "octave_num", "" + 0);

    }

    /**
     * <p>
     * createSpineContainer is the method that adds spine container elements to
     * the logic layer of the IEEE1599 document
     * </p>
     *
     * @param logic the element to be created and whose events are to be added
     * as attributes
     */
    private void createSpineContainer(Element logic) {
        Element spine = addElementAndReturnIt(logic, "spine");

        defineFirstEvents(spine, "Instrument_", "_voice0_measure1_ev0");
        defineFirstEvents(spine, "TimeSignature_Instrument_", "_1");
        defineFirstEvents(spine, "Clef_Instrument_", "_1");

        createEvents(spine);
    }

    /**
     * <p>
     * createGeneralLayer is the method that adds general layer elements to the
     * IEEE1599 document
     * </p>
     *
     * @return the first Element of the General layer
     */
    private Element createGeneralLayer() {
        Element ieee1599 = addIeee1599Element(this.creator, this.docVersion);

        Element general = addElementAndReturnIt(ieee1599, "general");

        Element description = addElementAndReturnIt(general, "description");

        addElementAndSetTextContext(description, "main_title", this.title);
        addElementAndSetTextContext(description, "author", this.author);

        return ieee1599;
    }

    /**
     * <p>
     * additionForIrregularGroupsPresence is the method that adds the irregular
     * groups element
     * </p>
     *
     * @param areIrregularGroupsPresent the boolean that indicates the presence
     * or absece of the irregular groups
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
    private void additionForIrregularGroupsPresence(boolean areIrregularGroupsPresent, Element element, String childName, String firstAttributeName, String firstAttributeValue, String secondAttributeName, String secondAttributeValue, String thirdAttributeName, String thirdAttributeValue, String fourthAttributeName, String fourthAttributeValue) {

        if (areIrregularGroupsPresent) {
            LOGGER.log(Level.INFO, "Add irregular group elements");
            if (this.randomizer.getRandomBoolean()) {
                addElementAndSetFourAttributes(element, childName, firstAttributeName, firstAttributeValue, secondAttributeName, secondAttributeValue, thirdAttributeName, thirdAttributeValue, fourthAttributeName, fourthAttributeValue);

            }
        }
    }

    /**
     * <p>
     * addEventAttributes is the method that adds attributes of an event
     * </p>
     *
     * @param i the index i of the instruments
     * @param k the index k of the notes and rests
     * @param event the element whose attributes are to be added
     * @param duration the element to use to control the presence or absence of
     * the attribute "den" to assign the corresponding "timing" and "hpos"
     *
     */
    private void addEventAttributes(int i, int k, Element event, Element duration) {
        if (k > 0 && duration.getAttribute("den") != "") {
            setAttributeOfElement(event, "timing", "" + this.instruments.get(i).getMinimumDelay() * (instruments.get(i).getMinDuration()[1] / Integer.parseInt(duration.getAttribute("den"))));
            setAttributeOfElement(event, "hpos", "" + this.instruments.get(i).getMinimumDelay() * (instruments.get(i).getMinDuration()[1] / Integer.parseInt(duration.getAttribute("den"))));
        } else {
            setAttributeOfElement(event, "timing", "" + 0);
            setAttributeOfElement(event, "hpos", "" + 0);
        }
    }

    private void addPitchElement(float randomPitch, Element notehead, int i) {

        String randomAccidental = this.randomizer.getRandomStringFromMap(accidentalMap);

        LOGGER.log(Level.INFO, "Add pitch element");
        Element pitch = addElementToDocument(this.document, "pitch");
        int randomOctave = this.randomizer.getRandomInteger(Integer.parseInt(this.instruments.get(i).getMinHeight().replaceAll("[^\\d-]", "")), Integer.parseInt(this.instruments.get(i).getMaxHeight().replaceAll("[^\\d-]", "")));

        randomPitch += this.accidentalMap.get(randomAccidental);
        randomPitch %= 12;
        if (randomPitch < 0) {
            randomPitch += 12;
        }

        float maxHeightPitch = 0f;
        for (Map.Entry<Float, List<String>> e : this.allNotesMap.entrySet()) {
             LOGGER.log(Level.INFO, "get value " + e.getValue());
            if (e.getValue().contains(this.instruments.get(i).getMaxHeight().replaceAll("\\d","").trim())) {
                maxHeightPitch = e.getKey();
                break;
            }
        }
        if (randomPitch > maxHeightPitch) {
            randomOctave = this.randomizer.getRandomInteger(this.instruments.get(i).getMinHeight().charAt(this.instruments.get(i).getMinHeight().length() - 1), (int) (randomPitch - maxHeightPitch));
        }

        setAttributeOfElement(pitch, "actual_accidental", "" + randomAccidental);
        setAttributeOfElement(pitch, "octave", "" + randomOctave);
        setAttributeOfElement(pitch, "step", "" + this.randomizer.getRandomElementFromList(this.allNotesMap.get(randomPitch)));

        appendChildToElement(notehead, pitch);

        addElement(notehead, "printed_accidentals");
        addElement(notehead, randomAccidental);

    }

    /**
     * <p>
     * createEvents is the method that creates the events for each random
     * instrument
     * </p>
     *
     * @param spine the element whose children are to be appended
     *
     */
    private void createEvents(Element spine) {
        for (int j = 1; j <= measuresNumber; j++) {

            List<Integer> randomInstruments = this.randomizer.getRandomNonRepeatingIntegers(instrumentsNumber, 1, instrumentsNumber);

            if (this.areFirstEventsDefined) {

                defineOtherEvents(1, j, randomInstruments, spine);

                this.areFirstEventsDefined = false;

            } else {

                defineOtherEvents(0, j, randomInstruments, spine);
            }
        }
    }

    /**
     * <p>
     * defineOtherEvents is the method that defines the other events of the
     * IEEE1599 document
     * </p>
     *
     * @param kIndex the number to give to index k
     * @param j the index j of the number of measures
     * @param randomInstruments the list of random instruments
     * @param spine the element whose child is to be appended
     *
     */
    private void defineOtherEvents(int kIndex, int j, List<Integer> randomInstruments, Element spine) {
        for (int i = 0; i < randomInstruments.size(); i++) {

            int eventsNumber = this.randomizer.getRandomInteger(1, instruments.get(i).getMaxNumberOfEvents());

            for (int k = kIndex; k < eventsNumber; k++) {

                Element event = addElementAndSetOneAttributeReturningTheElement(spine, "event", "id", "Instrument_" + randomInstruments.get(i) + "_voice0_measure" + j + "_ev" + k);

                this.eventsList.add(event);
            }
        }
    }

    /**
     * <p>
     * defineFirstEvents is the method that defines the first events of the
     * IEEE1599 document
     * </p>
     *
     * @param spine the element whose child is to be appended
     * @param firstPartOfTheFirstAttributeValue the string constituting the
     * first part of the first attribute value
     * @param secondPartOfTheFirstAttributeValue the string constituting the
     * second part of the first attribute value
     *
     */
    private void defineFirstEvents(Element spine, String firstPartOfTheFirstAttributeValue, String secondPartOfTheFirstAttributeValue) {
        for (int i = 1; i <= instrumentsNumber; i++) {

            Element event = addElementAndSetThreeAttributesReturningTheElement(spine, "event", "id", firstPartOfTheFirstAttributeValue + i + secondPartOfTheFirstAttributeValue, "timing", "" + 0, "hpos", "" + 0);

            this.eventsList.add(event);

            this.areFirstEventsDefined = true;
        }
    }

    /**
     * <p>
     * addElementAndSetFourAttributes is the method that adds a child element to
     * the input element setting four attributes of the child element
     * </p>
     *
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
    private void addElementAndSetFourAttributes(Element element, String childName, String firstAttributeName, String firstAttributeValue, String secondAttributeName, String secondAttributeValue, String thirdAttributeName, String thirdAttributeValue, String fourthAttributeName, String fourthAttributeValue) {
        Element child = addElementToDocument(this.document, childName);

        setAttributeOfElement(child, firstAttributeName, firstAttributeValue);
        setAttributeOfElement(child, secondAttributeName, secondAttributeValue);
        setAttributeOfElement(child, thirdAttributeName, thirdAttributeValue);
        setAttributeOfElement(child, fourthAttributeName, fourthAttributeValue);

        appendChildToElement(element, child);
    }

    /**
     * <p>
     * addElementAndSetThreeAttributesReturningTheElement is the method that
     * adds a child element to the input element setting three attributes of the
     * child element
     * </p>
     *
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
    private Element addElementAndSetThreeAttributesReturningTheElement(Element element, String childName, String firstAttributeName, String firstAttributeValue, String secondAttributeName, String secondAttributeValue, String thirdAttributeName, String thirdAttributeValue) {
        Element child = addElementToDocument(this.document, childName);

        setAttributeOfElement(child, firstAttributeName, firstAttributeValue);
        setAttributeOfElement(child, secondAttributeName, secondAttributeValue);
        setAttributeOfElement(child, thirdAttributeName, thirdAttributeValue);

        appendChildToElement(element, child);

        return child;
    }

    /**
     * <p>
     * addElementAndSetTwoAttributes is the method that adds a child element to
     * the input element setting two attributes of the child element
     * </p>
     *
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
    private void addElementAndSetTwoAttributes(Element element, String childName, String firstAttributeName, String firstAttributeValue, String secondAttributeName, String secondAttributeValue) {
        Element child = addElementToDocument(this.document, childName);

        setAttributeOfElement(child, firstAttributeName, firstAttributeValue);
        setAttributeOfElement(child, secondAttributeName, secondAttributeValue);

        appendChildToElement(element, child);
    }

    /**
     * <p>
     * addElementAndSetTwoAttributesReturningTheElement is the method that adds
     * a child element to the input element setting two attributes of the child
     * element
     * </p>
     *
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
    private Element addElementAndSetTwoAttributesReturningTheElement(Element element, String childName, String firstAttributeName, String firstAttributeValue, String secondAttributeName, String secondAttributeValue) {
        Element child = addElementToDocument(this.document, childName);

        setAttributeOfElement(child, firstAttributeName, firstAttributeValue);
        setAttributeOfElement(child, secondAttributeName, secondAttributeValue);

        appendChildToElement(element, child);

        return child;
    }

    /**
     * <p>
     * addElementAndSetOneAttributeReturningTheElement is the method that adds a
     * child element to the input element setting an attribute of the child
     * element
     * </p>
     *
     * @param element the element whose child is to be appended
     * @param childName the string constituting the name of the child
     * @param attributeName the string constituting the attribute name
     * @param attributeValue the string constituting the attribute value
     *
     * @return the added Element
     */
    private Element addElementAndSetOneAttributeReturningTheElement(Element element, String childName, String attributeName, String attributeValue) {
        Element child = addElementToDocument(this.document, childName);

        setAttributeOfElement(child, attributeName, attributeValue);

        appendChildToElement(element, child);

        return child;
    }

    /**
     * <p>
     * addElementAndSetTextContext is the method that adds a child element to
     * the input element setting the text content of the child element
     * </p>
     *
     * @param element the element whose child is to be appended
     * @param childName the string constituting the name of the child
     * @param textContent the string constituting the text content
     */
    private void addElementAndSetTextContext(Element element, String childName, String textContent) {
        Element child = addElementToDocument(this.document, childName);

        setTextContentOfElement(child, textContent);

        appendChildToElement(element, child);
    }

    /**
     * <p>
     * addElement is the method that adds a child element to the input element
     * </p>
     *
     * @param element the element whose child is to be appended
     * @param childName the string constituting the name of the child
     *
     */
    private void addElement(Element element, String childName) {
        Element child = addElementToDocument(this.document, childName);

        appendChildToElement(element, child);
    }

    /**
     * <p>
     * addElementAndReturnIt is the method that adds a child element to the
     * input element and returns it
     * </p>
     *
     * @param element the element whose child is to be appended
     * @param childName the string constituting the name of the child
     *
     * @return the added child element
     */
    private Element addElementAndReturnIt(Element element, String childName) {
        Element child = addElementToDocument(this.document, childName);

        appendChildToElement(element, child);

        return child;
    }

    /**
     * <p>
     * addIeee1599Element is the method that adds the IEEE1599 element of the
     * general layer to the IEEE1599 document
     * </p>
     *
     * @param creator the value of the creator attribute of the ieee1599 element
     * @param version the value of the version attribute of the ieee1599 element
     *
     * @return the added Element
     */
    private Element addIeee1599Element(String creator, double version) {
        Element ieee1599 = addElementToDocument(this.document, "ieee1599");

        setAttributeOfElement(ieee1599, "creator", creator);
        setAttributeOfElement(ieee1599, "version", "" + version);

        this.document.appendChild(ieee1599);
        return ieee1599;
    }

    /**
     * <p>
     * createDocument is the method that initializes the document field
     * </p>
     *
     * @throws ParserConfigurationException if there is a configuration error
     * for the DocumentBuilderFactory class
     */
    private void createDocument() throws ParserConfigurationException {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            this.document = docBuilder.newDocument();
        } catch (ParserConfigurationException ex) {
            LOGGER.log(Level.SEVERE, ex.getClass() + "The document cannot be created due to a configuration error.");
            throw new ParserConfigurationException("The document cannot be created due to a configuration error.");
        }
    }

    /**
     * <p>
     * addElementToDocument is the method that adds an element to the IEEE1599
     * document
     * </p>
     *
     * @param document the document whose element is to be created
     * @param elementName the element constituting the element to be added to
     * the document
     *
     * @return the added Element
     */
    private Element addElementToDocument(Document document, String elementName) {
        return document.createElement(elementName);
    }

    /**
     * <p>
     * appendChildToElement is the method that appends a child to and element of
     * the IEEE1599 document
     * </p>
     *
     * @param firstElement the element whose child is to be appended
     * @param secondElement the element constituting the child of the first
     * element
     */
    private void appendChildToElement(Element firstElement, Element secondElement) {
        firstElement.appendChild(secondElement);
    }

    /**
     * <p>
     * setAttributeOfElement is the method that sets an attribute of an element
     * of the IEEE1599 document
     * </p>
     *
     * @param element the element whose attribute is to be set
     * @param attributeName the string constituting the attribute name
     * @param attributeValue the string constituting the attribute value
     */
    private void setAttributeOfElement(Element element, String attributeName, String attributeValue) {
        element.setAttribute(attributeName, attributeValue);
    }

    /**
     * <p>
     * setTextContentOfElement is the method that sets the text content of an
     * element of the IEEE1599 document
     * </p>
     *
     * @param element the element whose text is to be set
     * @param string the string constituting the text content
     */
    private void setTextContentOfElement(Element element, String string) {
        element.setTextContent(string);
    }

    private <T, R> String mapAsString(Map<T, R> map) {
        String mapAsString = map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        return mapAsString;
    }

    public Document getDocument() {
        return this.document;
    }
}
