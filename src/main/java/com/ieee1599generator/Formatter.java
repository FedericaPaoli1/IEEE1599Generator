package com.ieee1599generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Formats the entire IEEE1599 document
 *
 * @author Federica Paoli', id: 961887, e-mail:
 * federica.paoli1@studenti.unimi.it
 */
public class Formatter {

    private static final Logger logger = LogManager.getLogger(Formatter.class.getName());

    private static final int DEFAULT_LINES_NUMBER = 5;
    private static final int PITCHES_NUMBER = 12;

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
     * The map of random number of events for each instrument
     */
    private final Map<Integer, Integer[]> randomEventsPerInstrumentMap = new HashMap<>();
    /**
     * The document creator name
     */
    private final String creator;
    /**
     * The document version
     */
    private final double docVersion;
    /**
     * The document title name
     */
    private final String title;
    /**
     * The document author name
     */
    private final String author;
    /**
     * The number of musical instruments
     */
    private final int instrumentsNumber;
    /**
     * The list of musical instruments, each with its own parameters
     */
    private final List<Instrument> instruments;
    /**
     * The list of clefs
     */
    private final List<Character> clefs;
    /**
     * The list of clefs steps
     */
    private final List<Integer> clefsSteps;
    /**
     * The map of accidentals
     */
    private final Map<String, Float> accidentalMap;
    /**
     * The map of all notes
     */
    private final Map<Float, List<String>> allNotesMap;
    /**
     * The metre converted from a string into numbers
     */
    private final int[] metreInNumbers;
    /**
     * The number of available measures
     */
    private final int measuresNumber;
    /**
     * The map of irregular groups
     */
    private final Map<Integer, Integer> irregularGroupsMap;

    public Formatter(long seed, String creator, double docVersion, String title, String author, int instrumentsNumber, List<Instrument> instruments, List<Character> clefs, List<Integer> clefsSteps, Map<String, Float> accidentalMap, Map<Float, List<String>> allNotesMap, int[] metreInNumbers, int measuresNumber, Map<Integer, Integer> irregularGroupsMap) {

        Formatter.logger.info("Inputs");

        this.randomizer = new Randomizer(seed);
        Formatter.logger.info("Randomizer seed: " + seed);

        this.creator = creator;
        Formatter.logger.info("Creator: " + this.creator);

        this.docVersion = docVersion;
        Formatter.logger.info("Document version: " + this.docVersion);

        this.title = title;
        Formatter.logger.info("Title: " + this.title);

        this.author = author;
        Formatter.logger.info("Author: " + this.author);

        this.instrumentsNumber = instrumentsNumber;
        Formatter.logger.info("Number of instruments: " + this.instrumentsNumber);

        this.instruments = instruments;
        for (int i = 0; i < this.instrumentsNumber; i++) {
            Formatter.logger.info("Instrument " + (i + 1) + ": " + this.instruments.get(i).toString());
        }
        this.clefs = clefs;
        Formatter.logger.info("Clefs: " + clefs.stream().map(Object::toString).collect(Collectors.joining(", ")));

        this.clefsSteps = clefsSteps;
        Formatter.logger.info("Clefs steps: " + clefsSteps.stream().map(Object::toString).collect(Collectors.joining(", ")));

        this.accidentalMap = accidentalMap;
        Formatter.logger.info("Accidentals map: " + FormatterUtils.mapAsString(this.accidentalMap));

        this.allNotesMap = allNotesMap;
        Formatter.logger.info("All notes map: " + FormatterUtils.notesMapAsString(this.allNotesMap));

        this.metreInNumbers = metreInNumbers;
        Formatter.logger.info("Metre: " + metreInNumbers[0] + ":" + metreInNumbers[1]);

        this.measuresNumber = measuresNumber;
        Formatter.logger.info("Measures number: " + this.measuresNumber);

        this.irregularGroupsMap = irregularGroupsMap;
        Formatter.logger.info("Irregular groups map: " + FormatterUtils.mapAsString(this.irregularGroupsMap));
    }

    /**
     * <p>
     * formats the IEEE1599 document
     * </p>
     *
     * @throws ParserConfigurationException if there is a configuration error
     * for the DocumentBuilderFactory class
     */
    public void format() throws ParserConfigurationException {

        Formatter.logger.debug("Create document");
        this.document = FormatterUtils.createDocument();

        Formatter.logger.debug("Create general layer");
        Element ieee1599 = createGeneralLayer();

        Formatter.logger.debug("Create logic layer");
        createLogicLayer(ieee1599);
    }

    /**
     * <p>
     * adds logic layer elements to the IEEE1599 document
     * </p>
     *
     * @param ieee1599 the element whose logic layer is to be added
     */
    private void createLogicLayer(Element ieee1599) {
        Element logic = FormatterUtils.addElementAndReturnIt(this.document, ieee1599, "logic");

        Formatter.logger.debug("Create spine container");
        createSpineContainer(logic);

        Formatter.logger.debug("Create los container");
        createLosContainer(logic);
    }

    /**
     * <p>
     * adds los container elements to the logic layer of the IEEE1599 document
     * </p>
     *
     * @param logic the element to be created and whose events are to be added
     * as attributes
     */
    private void createLosContainer(Element logic) {
        Element los = FormatterUtils.addElementAndReturnIt(this.document, logic, "los");

        Element staffList = FormatterUtils.addElementAndReturnIt(this.document, los, "staff_list");

        for (int i = 0; i < this.instrumentsNumber; i++) {
            Formatter.logger.info("INSTRUMENT " + (i + 1));

            addStaffListComponents(i, staffList);

            Formatter.logger.debug("Create part element");
            createPartElement(i, los);
        }
    }

    /**
     * <p>
     * creates the part element
     * </p>
     *
     * @param i the index of the instruments number
     * @param los the element whose child is to be appended
     *
     */
    private void createPartElement(int i, Element los) {
        Element part = FormatterUtils.addElementAndSetOneAttributeReturningTheElement(this.document, los, "part", "id", "Instrument_" + i);

        Formatter.logger.debug("Create voice list container");
        createVoiceListContainer(part, i);

        Formatter.logger.debug("Create measure elements");
        createMeasureElements(part, i);
    }

    /**
     * <p>
     * creates the measure elements
     * </p>
     *
     * @param part the element whose child is to be appended
     * @param i the index of the instruments number
     *
     */
    private void createMeasureElements(Element part, int i) {

        // random number of notes played by the actual instrument
        int notesNumber = this.randomizer.getRandomInteger(1, this.instruments.get(i).getMaxNumberOfPlayedNotes());
        Formatter.logger.info("Notes number: " + notesNumber);

        if (notesNumber > this.randomEventsPerInstrumentMap.get(i)[0]) {
            notesNumber = this.randomEventsPerInstrumentMap.get(i)[0];
        }

        // random number of rests of the actual instrument
        int restsNumber = this.randomEventsPerInstrumentMap.get(i)[0] - notesNumber;
        Formatter.logger.info("Events number: " + this.randomEventsPerInstrumentMap.get(i)[0]);
        Formatter.logger.info("Rests number: " + restsNumber);

        List<Character> notesAndRests = createNotesAndRestsList(this.randomEventsPerInstrumentMap.get(i)[0], notesNumber, restsNumber);
        this.randomizer.shuffleList(notesAndRests);
        Formatter.logger.info("Total of notes and rests: " + notesAndRests.size());

        for (int j = 1; j <= this.measuresNumber; j++) {
            Formatter.logger.info("MEASURE " + j);

            Element measure = FormatterUtils.addElementAndSetOneAttributeReturningTheElement(this.document, part, "measure", "number", "" + j);

            Formatter.logger.debug("Create voice element");
            createVoiceElement(i, measure, j, notesAndRests);
        }
    }

    /**
     * <p>
     * creates the voice elements
     * </p>
     *
     * @param i the index of the instruments number
     * @param measure the element whose child is to be appended
     * @param j the index of the measures number
     *
     */
    private void createVoiceElement(int i, Element measure, int j, List<Character> notesAndRests) {
        Formatter.logger.debug("Add voice element");
        Element voice = FormatterUtils.addElementAndSetOneAttributeReturningTheElement(this.document, measure, "voice", "voice_item_ref", "Instrument_" + (i + 1) + "_0_voice");

        // pick from allNotesMap the corresponding float to the note name
        float minHeight = getFloatFromNoteName(this.instruments.get(i).getMinHeight().replaceAll("\\d", ""));
        float maxHeight = getFloatFromNoteName(this.instruments.get(i).getMaxHeight().replaceAll("\\d", ""));

        float randomPitch = this.randomizer.getRandomFloat(minHeight, maxHeight);
        Formatter.logger.info("Random pitch: " + randomPitch);

        // random number of notes in a chord
        int notesInAChord = this.randomizer.getRandomInteger(1, this.instruments.get(i).getMaxNumberOfNotesInAChord());
        Formatter.logger.info("Notes in a chord: " + notesInAChord);

        // list of notes represented as double
        List<Double> notesMapKeysList = new ArrayList<>(this.instruments.get(i).getNotesMap().keySet());

        int eventsNumberInAMeasure = this.randomEventsPerInstrumentMap.get(i)[1];
        Formatter.logger.info("Events number in a measure: " + eventsNumberInAMeasure);

        // compute a selection of the correct musical figures for the actual measure
        List<Double> correctNotesAndRests = selectCorrectNotesAndRests(i, eventsNumberInAMeasure, notesMapKeysList);

        for (int k = 0; k < eventsNumberInAMeasure; k++) {

            if (!notesAndRests.isEmpty()) {
                char actualCharacter = notesAndRests.remove(0);

                if (actualCharacter == 'N') {
                    Formatter.logger.debug("Create chord elements");
                    createChordElements(i, j, k, voice, notesInAChord, correctNotesAndRests, randomPitch);

                } else {
                    Formatter.logger.debug("Create rest elements");
                    createRestElements(i, j, k, voice, correctNotesAndRests);
                }
            }
            
        }
    }

    /**
     * <p>
     * selects the correct notes and rests
     * </p>
     *
     * @param i the index of the instruments number
     * @param eventsNumberInAMeasure the number of events in the actual measure
     * @param notesMapKeysList the list of notes represented as double
     *
     * @return the list of correct notes and rests
     */
    private List<Double> selectCorrectNotesAndRests(int i, int eventsNumberInAMeasure, List<Double> notesMapKeysList) {
        List<Double> correctNotesAndRests;
        double timesMinDurationInMeasure = ((double) this.metreInNumbers[0] / this.metreInNumbers[1]) * this.instruments.get(i).getMinDuration()[1];
        double timesMaxDurationInMeasure = ((double) this.metreInNumbers[0] / this.metreInNumbers[1]) * this.instruments.get(i).getMaxDuration()[1];
        if (timesMinDurationInMeasure < eventsNumberInAMeasure) {
            correctNotesAndRests = new ArrayList<>();
        } else if (timesMaxDurationInMeasure > eventsNumberInAMeasure) {
            correctNotesAndRests = new ArrayList<>();
        } else if (timesMinDurationInMeasure == eventsNumberInAMeasure) {
            correctNotesAndRests = new ArrayList<>(Collections.nCopies(eventsNumberInAMeasure, (double) this.instruments.get(i).getMinDuration()[0] / this.instruments.get(i).getMinDuration()[1]));
        } else if (timesMaxDurationInMeasure == eventsNumberInAMeasure) {
            correctNotesAndRests = new ArrayList<>(Collections.nCopies(eventsNumberInAMeasure, (double) this.instruments.get(i).getMaxDuration()[0] / this.instruments.get(i).getMaxDuration()[1]));

        } else {
            correctNotesAndRests = selectNotes(eventsNumberInAMeasure, notesMapKeysList, this.instruments.get(i).getMinDuration()[1]);
        }
        this.randomizer.shuffleList(correctNotesAndRests);
        Formatter.logger.info("Correct notes and rests: " + correctNotesAndRests.stream().map(Object::toString).collect(Collectors.joining(", ")));

        return correctNotesAndRests;
    }

    /**
     * <p>
     * gets the float, contained in the allNotesMap, corresponding to the note
     * name
     * </p>
     *
     * @param minOrMaxHeight the string representing the note name
     *
     * @return the height of the input note
     */
    private float getFloatFromNoteName(String minOrMaxHeight) {
        float height = 0f;
        for (Map.Entry<Float, List<String>> e : this.allNotesMap.entrySet()) {
            if (e.getValue().contains(minOrMaxHeight)) {
                height = e.getKey();
                break;
            }
        }
        return height;
    }

    /**
     * <p>
     * selects the correct musical figures for the actual measure
     * </p>
     *
     * @param eventsNumberInAMeasure the number of events in a measure
     * @param notesMapKeysList the list of notes represented as double
     * @param minDuration the minimum duration of the musical figures
     *
     * @return the list of the selected musical figures
     */
    private List<Double> selectNotes(int eventsNumberInAMeasure, List<Double> notesMapKeysList, int minDuration) {
        List<Double> correctNotes = new ArrayList<>();
        double remainingDuration = (double) (this.metreInNumbers[0]) / this.metreInNumbers[1];
        int remainingEvents = eventsNumberInAMeasure;
        int notesMapKeysListIndex = 0;

        while (remainingDuration > 0.001) {

            double noteKey = notesMapKeysList.get(notesMapKeysListIndex);
            // subtract from remainingDuration the duration of the note being considered
            remainingDuration -= noteKey;
            // subtract from remainingEvents an event considered as played
            remainingEvents--;

            if ((remainingDuration * minDuration) >= remainingEvents) {
                // case where one or more notes used manage to complete the measure
                correctNotes.add(noteKey);

            } else {
                // case where one or more of the notes used fail to complete the measure and the notesMapKeysListIndex, the remainingDuration and the remainingEvents are restored
                notesMapKeysListIndex++;
                remainingDuration += noteKey;
                remainingEvents++;
            }
        }

        return correctNotes;
    }

    /**
     * <p>
     * creates the rest elements
     * </p>
     *
     * @param i the index of the instruments number
     * @param j the index of the measures number
     * @param k the index of the events number in the actual measure
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
            Formatter.logger.error("The event " + k + " corresponding to the instrument " + (i + 1) + " in the measure " + j + " does not exist");
            throw new NoSuchElementException(" the event " + k + " corresponding to the instrument " + (i + 1) + " in the measure " + j + " does not exist");
        }
        Element event = optionalEvent.get();
        Formatter.logger.info("Event: " + eventRef);

        if (!correctNotesAndRests.isEmpty()) {
            Formatter.logger.debug("Add rest element");
            Element rest = FormatterUtils.addElementAndSetOneAttributeReturningTheElement(this.document, voice, "rest", "event_ref", eventRef);

            double randomNote = correctNotesAndRests.remove(0);
            Formatter.logger.info("Random note: " + randomNote);

            createRestDurationElement(i, randomNote, rest, k, event);
        } else {
            Formatter.logger.debug("Add duration element");
            Element duration = FormatterUtils.addElementToDocument(this.document, "duration");
            FormatterUtils.setAttributeOfElement(duration, "den", "");
            FormatterUtils.setAttributeOfElement(duration, "num", "");

            Formatter.logger.debug("Add event attributes of duration element");
            addEventAttributes(i, k, event, duration);
        }
    }

    /**
     * <p>
     * creates the duration element for the rest one
     * </p>
     *
     * @param i the index of the instruments number
     * @param randomNote the index of the notes and rests number
     * @param rest the element whose child is to be appended
     * @param k the index of the events number in the actual measure
     * @param event the element whose attributes are to be added
     *
     */
    private void createRestDurationElement(int i, double randomNote, Element rest, int k, Element event) {
        if (randomNote == 1) {
            Formatter.logger.debug("Add duration element");
            Element duration = FormatterUtils.addElementAndSetTwoAttributesReturningTheElement(this.document, rest, "duration", "den", "" + metreInNumbers[1], "num", "" + metreInNumbers[0]);

            Formatter.logger.debug("Add event attributes of duration element");
            addEventAttributes(i, k, event, duration);

        } else {
            Formatter.logger.debug("Add duration element");
            Element duration = FormatterUtils.addElementAndSetTwoAttributesReturningTheElement(this.document, rest, "duration", "den", "" + this.instruments.get(i).getNotesMap().get(randomNote)[1], "num", "" + 1);

            Formatter.logger.debug("Add event attributes of duration element");
            addEventAttributes(i, k, event, duration);
        }
    }

    /**
     * <p>
     * creates the chord elements
     * </p>
     *
     * @param i the index of the instruments number
     * @param j the index of the measures number
     * @param k the index of the events number in the actual measure
     * @param voice the element whose child is to be appended
     * @param notesInAChord the number of notes in a chord
     * @param correctNotesAndRests the correct notes and rests list
     * @param randomPitch the random pitch
     *
     * @throws NoSuchElementException if there is a configuration error
     */
    private void createChordElements(int i, int j, int k, Element voice, int notesInAChord, List<Double> correctNotesAndRests, float randomPitch) {
        String eventRef = "Instrument_" + (i + 1) + "_voice0_measure" + j + "_ev" + k;

        // there is only one event that corresponds to the actual instrument in the actual measure, so if everything has been correctly configured, the Optional can never be empty
        Optional<Element> optionalEvent = this.eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
        if (optionalEvent.isEmpty()) {
            Formatter.logger.error("The event " + k + " corresponding to the instrument " + (i + 1) + " in the measure " + j + " does not exist");
            throw new NoSuchElementException(" the event " + k + " corresponding to the instrument " + (i + 1) + " in the measure " + j + " does not exist");
        }
        Element event = optionalEvent.get();
        Formatter.logger.info("Event: " + eventRef);

        if (!correctNotesAndRests.isEmpty()) {
            Formatter.logger.debug("Add chord element");
            Element chord = FormatterUtils.addElementAndSetOneAttributeReturningTheElement(this.document, voice, "chord", "event_ref", eventRef);

            double randomNote = correctNotesAndRests.remove(0);
            Formatter.logger.info("Random note: " + randomNote);

            // random irregular group from the map
            int irregularGroup = this.randomizer.getRandomIntFromMap(this.irregularGroupsMap);
            Formatter.logger.info("Irregular group: " + irregularGroup + "/" + this.metreInNumbers[1] * this.irregularGroupsMap.get(irregularGroup));

            createChordDurationElement(i, randomNote, chord, k, event, irregularGroup);

            createNoteheadElements(notesInAChord, chord, randomPitch, i);
        } else {
            Formatter.logger.debug("Add duration element");
            Element duration = FormatterUtils.addElementToDocument(this.document, "duration");
            FormatterUtils.setAttributeOfElement(duration, "den", "");
            FormatterUtils.setAttributeOfElement(duration, "num", "");

            Formatter.logger.debug("Add event attributes of duration element");
            addEventAttributes(i, k, event, duration);
        }
    }

    /**
     * <p>
     * creates the notehead element
     * </p>
     *
     * @param notesInAChord the number of notes in a chord
     * @param chord the element whose child is to be appended
     * @param randomPitch the random pitch
     * @param i the index of the instruments number
     *
     */
    private void createNoteheadElements(int notesInAChord, Element chord, float randomPitch, int i) {
        for (int n = 1; n <= notesInAChord; n++) {
            Formatter.logger.debug("Add notehead element");
            Element notehead = FormatterUtils.addElementAndReturnIt(this.document, chord, "notehead");

            Formatter.logger.debug("Add pitch element");
            addPitchElement(randomPitch, notehead, i);
        }
    }

    /**
     * <p>
     * creates the duration element for the chord one
     * </p>
     *
     * @param i the index of the instruments number
     * @param randomNote the random note
     * @param chord the element whose child is to be appended
     * @param k the index of the events number in the actual measure
     * @param event the element whose attributes are to be added
     * @param irregularGroup the irregular group
     *
     */
    private void createChordDurationElement(int i, double randomNote, Element chord, int k, Element event, int irregularGroup) {
        if (randomNote == 1) {
            Formatter.logger.debug("Add duration element");
            Element duration = FormatterUtils.addElementAndSetTwoAttributesReturningTheElement(this.document, chord, "duration", "den", "" + metreInNumbers[1], "num", "" + metreInNumbers[0]);

            Formatter.logger.debug("Add event attributes of duration element");
            addEventAttributes(i, k, event, duration);

            additionForIrregularGroupsPresence(this.instruments.get(i).getAreIrregularGroupsPresent(), duration, "tuplet_ratio", "enter_num", "" + irregularGroup, "enter_den", "" + this.metreInNumbers[1] * this.irregularGroupsMap.get(irregularGroup), "in_num", "" + 1, "in_den", "" + this.metreInNumbers[1]);

        } else {
            Formatter.logger.debug("Add duration element");
            Element duration = FormatterUtils.addElementAndSetTwoAttributesReturningTheElement(this.document, chord, "duration", "den", "" + instruments.get(i).getNotesMap().get(randomNote)[1], "num", "" + 1);

            Formatter.logger.debug("Add event attributes of duration element");
            addEventAttributes(i, k, event, duration);

            additionForIrregularGroupsPresence(this.instruments.get(i).getAreIrregularGroupsPresent(), duration, "tuplet_ratio", "enter_num", "" + irregularGroup, "enter_den", "" + this.metreInNumbers[1] * this.irregularGroupsMap.get(irregularGroup), "in_num", "" + 1, "in_den", "" + this.instruments.get(i).getNotesMap().get(randomNote)[1]);
        }
    }

    /**
     * <p>
     * creates the voice_list container
     * </p>
     *
     * @param part the element whose voice_list container is to be appended
     * @param i the index of the instruments number
     *
     */
    private void createVoiceListContainer(Element part, int i) {
        Formatter.logger.debug("Add voice_list element");
        Element voiceList = FormatterUtils.addElementAndReturnIt(this.document, part, "voice_list");

        Formatter.logger.debug("Add voice_item element");
        FormatterUtils.addElementAndSetTwoAttributes(this.document, voiceList, "voice_item", "id", "Instrument_" + (i + 1) + "_0_voice", "staff_ref", "Instrument_" + (i + 1) + "_staff");
    }

    /**
     * <p>
     * creates a unique list of notes and rests and rests
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
        int countNotes = 0, countRests = 0;

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
     * adds staff_list components
     * </p>
     *
     * @param i the index of the instruments
     * @param staffList the element whose components are to be added
     *
     */
    private void addStaffListComponents(int i, Element staffList) {
        Formatter.logger.debug("Add staff element");
        Element staff = FormatterUtils.addElementAndSetTwoAttributesReturningTheElement(this.document, staffList, "staff", "id", "Instrument_" + (i + 1) + "_staff", "line_number", "" + DEFAULT_LINES_NUMBER);

        Formatter.logger.debug("Add time_signature element");
        Element timeSignature = FormatterUtils.addElementAndSetOneAttributeReturningTheElement(this.document, staff, "time_signature", "event_ref", "TimeSignature_Instrument_" + (i + 1) + "_1");

        Formatter.logger.debug("Add time_indication element");
        FormatterUtils.addElementAndSetTwoAttributes(this.document, timeSignature, "time_indication", "den", "" + this.metreInNumbers[1], "num", "" + this.metreInNumbers[0]);

        Formatter.logger.debug("Add clef element");
        FormatterUtils.addElementAndSetFourAttributes(this.document, staff, "clef", "event_ref", "Clef_Instrument_" + (i + 1) + "_1", "shape", "" + this.randomizer.getRandomElementFromList(this.clefs), "staff_step", "" + this.randomizer.getRandomElementFromList(this.clefsSteps), "octave_num", "" + 0);

    }

    /**
     * <p>
     * adds spine container elements to the logic layer of the IEEE1599 document
     * </p>
     *
     * @param logic the element to be created and whose events are to be added
     * as attributes
     */
    private void createSpineContainer(Element logic) {
        Formatter.logger.debug("Add spine element");
        Element spine = FormatterUtils.addElementAndReturnIt(this.document, logic, "spine");

        Formatter.logger.debug("Add the first events to the spine container");
        defineFirstEvents(spine, "Instrument_", "_voice0_measure1_ev0");
        defineFirstEvents(spine, "TimeSignature_Instrument_", "_1");
        defineFirstEvents(spine, "Clef_Instrument_", "_1");

        createEvents(spine);
    }

    /**
     * <p>
     * adds general layer elements to the IEEE1599 document
     * </p>
     *
     * @return the first Element of the General layer
     */
    private Element createGeneralLayer() {
        Formatter.logger.debug("Add ieee1599 element");
        Element ieee1599 = FormatterUtils.addIeee1599Element(this.document, this.creator, this.docVersion);

        Formatter.logger.debug("Add general element");
        Element general = FormatterUtils.addElementAndReturnIt(this.document, ieee1599, "general");

        Formatter.logger.debug("Add description element");
        Element description = FormatterUtils.addElementAndReturnIt(this.document, general, "description");

        Formatter.logger.debug("Add main_title element");
        FormatterUtils.addElementAndSetTextContext(this.document, description, "main_title", this.title);
        Formatter.logger.debug("Add author element");
        FormatterUtils.addElementAndSetTextContext(this.document, description, "author", this.author);

        return ieee1599;
    }

    /**
     * <p>
     * adds the irregular groups element
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
            Formatter.logger.debug("Add irregular group elements");
            if (this.randomizer.getRandomBoolean()) {
                FormatterUtils.addElementAndSetFourAttributes(this.document, element, childName, firstAttributeName, firstAttributeValue, secondAttributeName, secondAttributeValue, thirdAttributeName, thirdAttributeValue, fourthAttributeName, fourthAttributeValue);

            }
        }
    }

    /**
     * <p>
     * adds attributes of an event
     * </p>
     *
     * @param i the index i of the instruments number
     * @param k the index k of the events number in the actual measure
     * @param event the element whose attributes are to be added
     * @param duration the element to use to control the presence or absence of
     * the attribute "den" to assign the corresponding "timing" and "hpos"
     *
     */
    private void addEventAttributes(int i, int k, Element event, Element duration) {
        if (k > 0 && !"".equals(duration.getAttribute("den"))) {
            FormatterUtils.setAttributeOfElement(event, "timing", "" + this.instruments.get(i).getMinimumDelay() * (instruments.get(i).getMinDuration()[1] / Integer.parseInt(duration.getAttribute("den"))));
            FormatterUtils.setAttributeOfElement(event, "hpos", "" + this.instruments.get(i).getMinimumDelay() * (instruments.get(i).getMinDuration()[1] / Integer.parseInt(duration.getAttribute("den"))));
        } else {
            FormatterUtils.setAttributeOfElement(event, "timing", "" + 0);
            FormatterUtils.setAttributeOfElement(event, "hpos", "" + 0);
        }
    }

    /**
     * <p>
     * adds the pitch element computing it
     * </p>
     *
     * @param randomPitch the randomly selected pitch
     * @param notehead the element whose whose pitch element is to be added
     * @param i the index of the instruments number
     *
     */
    private void addPitchElement(float randomPitch, Element notehead, int i) {

        String randomAccidental = this.randomizer.getRandomStringFromMap(accidentalMap);

        Element pitch = FormatterUtils.addElementToDocument(this.document, "pitch");

        int randomOctave = this.randomizer.getRandomInteger(Integer.parseInt(this.instruments.get(i).getMinHeight().replaceAll("[^\\d-]", "")), Integer.parseInt(this.instruments.get(i).getMaxHeight().replaceAll("[^\\d-]", "")));

        randomPitch += this.accidentalMap.get(randomAccidental);
        randomPitch %= PITCHES_NUMBER;
        if (randomPitch < 0) {
            randomPitch += PITCHES_NUMBER;
        }

        // get from the allNotesMap the corresponding maximum height for the actual random pitch
        float maxHeightPitch = getFloatFromNoteName(this.instruments.get(i).getMaxHeight().replaceAll("\\d", ""));

        // change octave if the random pitch is higher than the input maximum height
        if (randomPitch > maxHeightPitch) {
            randomOctave += this.randomizer.getRandomInteger(Integer.parseInt(this.instruments.get(i).getMinHeight().replaceAll("[^\\d-]", "")), (int) (randomPitch - maxHeightPitch));
        }

        // get the true pitch from which the random one has the minimum distance
        float key = getMinimumDistancePitchKey(randomPitch);

        // get a random note name corresponding to the actual pitch
        String randomStringFromAllNotesMap = this.randomizer.getRandomElementFromList(this.allNotesMap.get(key));

        String actualAccidental;
        if (randomStringFromAllNotesMap.contains("_")) {
            actualAccidental = randomStringFromAllNotesMap.substring(2);
        } else {
            actualAccidental = "natural";
        }

        Formatter.logger.info("Pitch: " + key);

        FormatterUtils.setAttributeOfElement(pitch, "actual_accidental", "" + actualAccidental);
        Formatter.logger.info("Actual accidental: " + actualAccidental);

        FormatterUtils.setAttributeOfElement(pitch, "octave", "" + randomOctave);
        Formatter.logger.info("Octave: " + randomOctave);

        FormatterUtils.setAttributeOfElement(pitch, "step", "" + randomStringFromAllNotesMap.charAt(0));
        Formatter.logger.info("Note name: " + randomStringFromAllNotesMap.charAt(0));

        FormatterUtils.appendChildToElement(notehead, pitch);

        Formatter.logger.debug("Add printed_accidentals element");
        Element printedAccidentals = FormatterUtils.addElementAndReturnIt(this.document, notehead, "printed_accidentals");
        FormatterUtils.addElement(this.document, printedAccidentals, actualAccidental);

    }

    /**
     * <p>
     * gets the pitch key that has the minimum distance with the input random
     * pitch
     * </p>
     *
     * @param randomPitch the random pitch
     *
     * @return the key to get the pitch in the corresponding map
     */
    private float getMinimumDistancePitchKey(float randomPitch) {
        float minimumDistance = 0f;
        float key = 0f;

        for (Map.Entry<Float, List<String>> e : this.allNotesMap.entrySet()) {
            if (e.getKey() == 0f) {
                minimumDistance = Math.abs(e.getKey() - randomPitch);
                key = e.getKey();
            }
            if (Math.abs(e.getKey() - randomPitch) < minimumDistance) {
                minimumDistance = Math.abs(e.getKey() - randomPitch);
                key = e.getKey();
            }
        }
        return key;
    }

    /**
     * <p>
     * creates the events for each random instrument
     * </p>
     *
     * @param spine the element whose children are to be appended
     *
     */
    private void createEvents(Element spine) {
        List<Integer> randomInstruments = this.randomizer.getRandomNonRepeatingIntegers(instrumentsNumber, 0, instrumentsNumber - 1);

        Formatter.logger.debug("Add all the events to the spine container");
        defineOtherEvents(randomInstruments, spine);

    }

    /**
     * <p>
     * defines the other events of the IEEE1599 document
     * </p>
     *
     * @param randomInstruments the list of random instruments
     * @param spine the element whose child is to be appended
     *
     */
    private void defineOtherEvents(List<Integer> randomInstruments, Element spine) {
        for (int i = 0; i < randomInstruments.size(); i++) {

            // number of events concerning the actual instrument in the actual measure
            int eventsNumber = this.randomizer.getRandomInteger(1, this.instruments.get(randomInstruments.get(i)).getMaxNumberOfEvents());
            if (eventsNumber < this.measuresNumber) {
                eventsNumber = this.measuresNumber;
            }
            Formatter.logger.info("Events number for the instrument " + (i + 1) + " :" + eventsNumber);

            int eventsNumberInAMeasure = eventsNumber / this.measuresNumber;

            this.randomEventsPerInstrumentMap.put(randomInstruments.get(i), new Integer[]{eventsNumber, eventsNumberInAMeasure});

            int kIndex;

            for (int j = 1; j <= this.measuresNumber; j++) {

                // two indexes to differentiate differentiate between adding events after the first and adding events from 0 onwards
                if (j == 1) {
                    kIndex = 1;
                } else {
                    kIndex = 0;
                }

                for (int k = kIndex; k < eventsNumberInAMeasure; k++) {

                    Element event = FormatterUtils.addElementAndSetOneAttributeReturningTheElement(this.document, spine, "event", "id", "Instrument_" + (randomInstruments.get(i) + 1) + "_voice0_measure" + j + "_ev" + k);

                    this.eventsList.add(event);
                }
            }
        }
    }

    /**
     * <p>
     * defines the first events of the IEEE1599 document
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
        for (int i = 1; i <= this.instrumentsNumber; i++) {

            Element event = FormatterUtils.addElementAndSetThreeAttributesReturningTheElement(this.document, spine, "event", "id", firstPartOfTheFirstAttributeValue + i + secondPartOfTheFirstAttributeValue, "timing", "" + 0, "hpos", "" + 0);

            this.eventsList.add(event);
        }
    }

    public Document getDocument() {
        return this.document;
    }
}
