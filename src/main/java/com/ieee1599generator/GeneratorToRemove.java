package com.ieee1599generator;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author federica
 */
public class GeneratorToRemove {

    public int getMeasuresNumber(int bpm, long pieceLength, int[] metre) {
        double bps = (double) bpm / 60;
        System.out.println("bps: " + bps);
        int beatsNumber = metre[0];
        System.out.println("Number of beats: " + beatsNumber);
        double oneBeatLength = 1 / bps;
        System.out.println("oneBeatLength: " + oneBeatLength);
        return (int) (pieceLength / (oneBeatLength * beatsNumber));
    }

    public int getRandomInteger(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
    
    public int getRandomIntegerFromList(List<Integer> ints) {
        return ints.get(getRandomInteger(0, ints.size() - 1));
    }
    
    public char getRandomCharFromList(List<Character> chars) {
        return chars.get(getRandomInteger(0, chars.size() - 1));
    }

    public List<Integer> getRandomNonRepeatingIntegers(int size, int min, int max) {
        List<Integer> numbers = new ArrayList();
        Random random = new Random();
        while (numbers.size() < size) {
            int randomNumber = random.nextInt((max - min) + 1) + min;
            //Check for duplicate values
            if (!numbers.contains(randomNumber)) {
                numbers.add(randomNumber);
            }
        }

        return numbers;
    }

    public List<Character> getRandomNonRepeatingChars(List<Character> chars) {
        List<Character> randomizedChars = new ArrayList();
        int min = 0;
        int max = chars.size() - 1;
        Random random = new Random();
        while (randomizedChars.size() < chars.size()) {
            int randomIndex = random.nextInt((max - min) + 1) + min;
            char c = chars.get(randomIndex);
            //Check for duplicate values
            if (!randomizedChars.contains(c)) {
                randomizedChars.add(c);
                System.out.println(c);
            }
        }

        return randomizedChars;
    }


    public double getRandomNote(Map<Double, int[]> inputMap) {
        Double[] keySetArray = inputMap.keySet().toArray(new Double[inputMap.keySet().size()]);
        Random random = new Random();
        int randomIndex = random.nextInt(inputMap.keySet().size());
        return keySetArray[randomIndex];
    }

    public int getRandomIrregularGroup(Map<Integer, Integer> inputMap) {
        Integer[] keySetArray = inputMap.keySet().toArray(new Integer[inputMap.keySet().size()]);
        Random random = new Random();
        int randomIndex = random.nextInt(inputMap.keySet().size());
        return keySetArray[randomIndex];
    }

    public boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

    public static void main(String[] args) throws TransformerException {
        GeneratorToRemove g = new GeneratorToRemove();
        // parametri da chiedere all'utente a riga di comando
        String titleName = "Title";
        String authorName = "Author";
        int minutes = 2;
        int bpm = 108;
        String metre = 4 + ":" + 4;
        int instrumentsNumber = 5;
        int maxNumberOfPlayedNotes = 150;
        int[] minDuration = {8, 1};
        int[] maxDuration = {1, 1};
        int minHeight = 0;
        int maxHeight = 11;
        int maxNumberOfNotesInAChord = 3;
        boolean areIrregularGroupsPresent = true;
        int basicVTU = 256;
        //
        
        
        // Inizializzazione parametri per la generazione del documento XML
        String pieceLength = "PT" + minutes + "M";
        int[] metreInNumbers = {Integer.parseInt(String.valueOf(metre.charAt(0))), Integer.parseInt(String.valueOf(metre.charAt(2)))};
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

        int maxNumberOfEvents = (int) ((((double) 1 / metreInNumbers[1]) / ((double) minDuration[1] / minDuration[0])) * metreInNumbers[0]);
        System.out.println("Max number of events: " + maxNumberOfEvents);

        List<Element> eventsList = new ArrayList<>();

        List<Character> clefs = List.of('G', 'F', 'C');
        List<Integer> clefsSteps = List.of(2, 4, 6);

        Map<Integer, String> pitchesMap = new TreeMap<>();
        pitchesMap.put(0, "C");
        pitchesMap.put(1, "C-sharp");
        pitchesMap.put(2, "D");
        pitchesMap.put(3, "D-sharp");
        pitchesMap.put(4, "E");
        pitchesMap.put(5, "F");
        pitchesMap.put(6, "F-sharp");
        pitchesMap.put(7, "G");
        pitchesMap.put(8, "G-sharp");
        pitchesMap.put(9, "A");
        pitchesMap.put(10, "B-sharp");
        pitchesMap.put(11, "B");

        int octavesNumber = 10;

        Map<Integer, Integer> irregularGroupsMap = new TreeMap<>();
        boolean isTheTimeSignatureCompound = metreInNumbers[0] % 3 == 0 && metreInNumbers[1] % 2 == 0;

        if (isTheTimeSignatureCompound) {
            irregularGroupsMap.put(2, 1);
            irregularGroupsMap.put(4, 2);
            irregularGroupsMap.put(5, 2);
            irregularGroupsMap.put(7, 4);
            irregularGroupsMap.put(9, 4);
            irregularGroupsMap.put(11, 4);
            irregularGroupsMap.put(13, 4);
        } else {
            irregularGroupsMap.put(3, 2);
            irregularGroupsMap.put(5, 4);
            irregularGroupsMap.put(6, 4);
            irregularGroupsMap.put(7, 8);
            irregularGroupsMap.put(9, 8);
            irregularGroupsMap.put(11, 8);
            irregularGroupsMap.put(13, 8);
        }

        /* Printing notesMap
        System.out.println("Notes map keys: " + notesMap.keySet());
        for (int[] n : notesMap.values()) {
            System.out.println("Notes map values: " + n[0] + " " + n[1] + "\n");
        }
         */
        
        //
        
        // Creazione documento
        
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
                event.setAttribute("timing", "" + 0);
                event.setAttribute("hpos", "" + 0);
                spine.appendChild(event);
                eventsList.add(event);
                firstEventsDefined = true;
            }

            for (int i = 1; i <= instrumentsNumber; i++) {
                Element event = doc.createElement("event");
                event.setAttribute("id", "TimeSignature_Instrument_" + i + "_1");
                event.setAttribute("timing", "" + 0);
                event.setAttribute("hpos", "" + 0);
                spine.appendChild(event);
                eventsList.add(event);
            }

            for (int i = 1; i <= instrumentsNumber; i++) {
                Element event = doc.createElement("event");
                event.setAttribute("id", "Clef_Instrument_" + i + "_1");
                event.setAttribute("timing", "" + 0);
                event.setAttribute("hpos", "" + 0);
                spine.appendChild(event);
                eventsList.add(event);
            }

            for (int j = 1; j <= measuresNumber; j++) {
                //System.out.println("MEASURE " + j);
                List<Integer> randomInstruments = g.getRandomNonRepeatingIntegers(instrumentsNumber, 1, instrumentsNumber);
                if (firstEventsDefined) {
                    for (int i = 0; i < randomInstruments.size(); i++) {
                        int eventsNumber = g.getRandomInteger(1, maxNumberOfEvents);
                        //System.out.println("Events number for instrument " + randomInstruments.get(i) + " : " + eventsNumber);
                        for (int k = 1; k < eventsNumber; k++) {
                            Element event = doc.createElement("event");
                            event.setAttribute("id", "Instrument_" + randomInstruments.get(i) + "_voice0_measure" + j + "_ev" + k);
                            //event.setAttribute("timing", "null");
                            //event.setAttribute("hpos", "null");
                            spine.appendChild(event);
                            eventsList.add(event);
                        }
                    }
                    firstEventsDefined = false;
                } else {
                    for (int i = 0; i < randomInstruments.size(); i++) {
                        int eventsNumber = g.getRandomInteger(1, maxNumberOfEvents);
                        //System.out.println("Events number for instrument " + randomInstruments.get(i) + " : " + eventsNumber);
                        for (int k = 0; k < eventsNumber; k++) {
                            Element event = doc.createElement("event");
                            event.setAttribute("id", "Instrument_" + randomInstruments.get(i) + "_voice0_measure" + j + "_ev" + k);
                            //event.setAttribute("timing", "null");
                            //event.setAttribute("hpos", "null");
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
                System.out.println("INSTRUMENT " + i);
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

                Element clef = doc.createElement("clef");
                clef.setAttribute("event_ref", "Clef_Instrument_" + i + "_1");
                clef.setAttribute("shape", "" + g.getRandomCharFromList(clefs));
                clef.setAttribute("staff_step", "" + g.getRandomIntegerFromList(clefsSteps));
                clef.setAttribute("octave_num", "" + 0);
                staff.appendChild(clef);

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
                    System.out.println("MEASURE " + j);
                    Element measure = doc.createElement("measure");
                    measure.setAttribute("number", "" + j);
                    part.appendChild(measure);

                    Element voice = doc.createElement("voice");
                    voice.setAttribute("voice_item_ref", "Instrument_" + i + "_0_voice");
                    measure.appendChild(voice);

                    String s = "Instrument_" + i + "_voice0_measure" + j + "_";
                    int eventsNumber = (int) eventsList.stream().filter(e -> e.getAttribute("id").contains(s)).count();
                    System.out.println("Events number: " + eventsNumber);

                    int notesNumber = g.getRandomInteger(1, maxNumberOfPlayedNotes);
                    System.out.println("Notes number: " + notesNumber);
                    int notesNumberInAMeasure = notesNumber / measuresNumber;
                    if (notesNumberInAMeasure > eventsNumber) {
                        notesNumberInAMeasure = eventsNumber;
                    }
                    System.out.println("Notes number in a measure: " + notesNumberInAMeasure);

                    int restsNumberInAMeasure = eventsNumber - notesNumberInAMeasure;
                    System.out.println("Rests number: " + restsNumberInAMeasure);

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
                    List<Integer> randomPitches = g.getRandomNonRepeatingIntegers(notesAndRests.size(), minHeight, maxHeight);

                    int notesInAChord = g.getRandomInteger(1, maxNumberOfNotesInAChord);

                    System.out.println("Notes in a chord: " + notesInAChord);

                    /*randomPitches.forEach(p -> {
                        System.out.println(p);
                    });*/
                    for (int k = 0; k < notesAndRests.size(); k++) {

                        if (notesAndRests.get(k) == 'N') {
                            Element chord = doc.createElement("chord");

                            String eventRef = "Instrument_" + i + "_voice0_measure" + j + "_ev" + k;
                            chord.setAttribute("event_ref", eventRef);
                            voice.appendChild(chord);

                            //TODO riuscire a fare meglio quest'operazione: evitare Optional Empty
                            Optional<Element> optionalEvent = eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
                            Element event = optionalEvent.get();

                            double randomNote = g.getRandomNote(notesMap);
                            while (randomNote * notesInAChord > 1) {
                                randomNote = g.getRandomNote(notesMap);
                            }

                            int irregularGroup = g.getRandomIrregularGroup(irregularGroupsMap);

                            Element duration = doc.createElement("duration");
                            if (randomNote == 1) {
                                duration.setAttribute("den", "" + metreInNumbers[1]);
                                duration.setAttribute("num", "" + metreInNumbers[0]);
                                chord.appendChild(duration);

                                if (k > 0) {
                                    event.setAttribute("timing", "" + basicVTU * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                                    event.setAttribute("hpos", "" + basicVTU * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                                } else {
                                    event.setAttribute("timing", "" + 0);
                                    event.setAttribute("hpos", "" + 0);
                                }

                                if (areIrregularGroupsPresent) {
                                    if (g.getRandomBoolean()) {
                                        Element tupletRatio = doc.createElement("tuplet_ratio");
                                        tupletRatio.setAttribute("enter_num", "" + irregularGroup);
                                        tupletRatio.setAttribute("enter_den", "" + metreInNumbers[1] * irregularGroupsMap.get(irregularGroup));
                                        tupletRatio.setAttribute("in_num", "" + 1);
                                        tupletRatio.setAttribute("in_den", "" + metreInNumbers[1]);
                                        duration.appendChild(tupletRatio);
                                    }
                                }
                            } else {
                                duration.setAttribute("den", "" + notesMap.get(randomNote)[0]);
                                duration.setAttribute("num", "" + 1);
                                chord.appendChild(duration);

                                if (k > 0) {
                                    event.setAttribute("timing", "" + basicVTU * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                                    event.setAttribute("hpos", "" + basicVTU * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                                } else {
                                    event.setAttribute("timing", "" + 0);
                                    event.setAttribute("hpos", "" + 0);
                                }

                                if (areIrregularGroupsPresent) {
                                    if (g.getRandomBoolean()) {
                                        Element tupletRatio = doc.createElement("tuplet_ratio");
                                        tupletRatio.setAttribute("enter_num", "" + irregularGroup);
                                        tupletRatio.setAttribute("enter_den", "" + metreInNumbers[1] * irregularGroupsMap.get(irregularGroup));
                                        tupletRatio.setAttribute("in_num", "" + 1);
                                        tupletRatio.setAttribute("in_den", "" + notesMap.get(randomNote)[0]);
                                        duration.appendChild(tupletRatio);
                                    }
                                }
                            }

                            for (int h = 1; h <= notesInAChord; h++) {
                                Element notehead = doc.createElement("notehead");
                                chord.appendChild(notehead);

                                Element pitch = doc.createElement("pitch");
                                pitch.setAttribute("actual_accidental", "natural");
                                int randomOctave = g.getRandomInteger(0, octavesNumber);
                                pitch.setAttribute("octave", "" + randomOctave);
                                pitch.setAttribute("step", "" + pitchesMap.get(randomPitches.get(k)).charAt(0));
                                notehead.appendChild(pitch);

                                if (pitchesMap.get(randomPitches.get(k)).length() > 1) {
                                    pitch.setAttribute("actual_accidental", "sharp");
                                    Element printedAccidentals = doc.createElement("printed_accidentals");
                                    notehead.appendChild(printedAccidentals);
                                    Element sharp = doc.createElement("sharp");
                                    printedAccidentals.appendChild(sharp);
                                }
                            }

                        } else {
                            Element rest = doc.createElement("rest");

                            String eventRef = "Instrument_" + i + "_voice0_measure" + j + "_ev" + k;
                            rest.setAttribute("event_ref", eventRef);
                            voice.appendChild(rest);

                            //TODO riuscire a fare meglio quest'operazione: evitare Optional Empty
                            Optional<Element> optionalEvent = eventsList.stream().filter(e -> e.getAttribute("id").contains(eventRef)).findAny();
                            Element event = optionalEvent.get();

                            double randomNote = g.getRandomNote(notesMap);
                            while (randomNote * metreInNumbers[0] > 1) {
                                randomNote = g.getRandomNote(notesMap);
                            }

                            if (randomNote == 1) {
                                Element duration = doc.createElement("duration");
                                duration.setAttribute("den", "" + metreInNumbers[1]);
                                duration.setAttribute("num", "" + metreInNumbers[0]);
                                rest.appendChild(duration);

                                if (k > 0) {
                                    event.setAttribute("timing", "" + basicVTU * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                                    event.setAttribute("hpos", "" + basicVTU * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                                } else {
                                    event.setAttribute("timing", "" + 0);
                                    event.setAttribute("hpos", "" + 0);
                                }

                            } else {
                                Element duration = doc.createElement("duration");
                                duration.setAttribute("den", "" + notesMap.get(randomNote)[0]);
                                duration.setAttribute("num", "" + 1);
                                rest.appendChild(duration);

                                if (k > 0) {
                                    event.setAttribute("timing", "" + basicVTU * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                                    event.setAttribute("hpos", "" + basicVTU * (minDuration[0] / Integer.parseInt(duration.getAttribute("den"))));
                                } else {
                                    event.setAttribute("timing", "" + 0);
                                    event.setAttribute("hpos", "" + 0);
                                }

                            }
                        }

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
                Logger.getLogger(GeneratorToRemove.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GeneratorToRemove.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
