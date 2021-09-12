package com.ieee1599generator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author federica
 */
public class Initializer {

    private static final Logger LOGGER = Logger.getLogger(Initializer.class.getName());

    private List<Instrument> instruments = new ArrayList<>();
    private int[] metreInNumbers;
    private int measuresNumber;

    public Initializer(long trackLength, String metre, int bpm) {
        LOGGER.log(Level.INFO, "Length of the track: " + trackLength);
        LOGGER.log(Level.INFO, "Metre: " + metre);
        LOGGER.log(Level.INFO, "BPM: " + bpm);
        this.metreInNumbers = new int[]{Integer.parseInt(String.valueOf(metre.charAt(0))), Integer.parseInt(String.valueOf(metre.charAt(2)))};
        this.measuresNumber = computeMeasuresNumber(bpm, trackLength, this.metreInNumbers);
    }

    // da chiamare tante volte quanti sono gli strumenti musicali
    public void initializeInstrumentsParams(int maxNumberOfPlayedNotes, int[] minDuration, int[] maxDuration, int minHeight, int maxHeight, int maxNumberOfNotesInAChord, boolean areIrregularGroupsPresent, int minimumDelay) {
        LOGGER.log(Level.INFO, "Minimum duration of musical figures: " + Arrays.toString(minDuration));
        LOGGER.log(Level.INFO, "Maximum duration of musical figures: " + Arrays.toString(maxDuration));

        Map<Double, int[]> notesMap = new TreeMap<>();
        for (int i = maxDuration[0]; i <= minDuration[0]; i *= 2) {
            notesMap.put((double) 1 / i, new int[]{i, 1});
        }
        int maxNumberOfEvents = (int) ((((double) 1 / this.metreInNumbers[1]) / ((double) minDuration[1] / minDuration[0])) * this.metreInNumbers[0]);
        Map<Integer, Integer> irregularGroupsMap = new TreeMap<>();
        if (this.metreInNumbers[0] % 3 == 0 && this.metreInNumbers[1] % 2 == 0) {   // time signature is compound
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

        instruments.add(new Instrument(maxNumberOfPlayedNotes, minDuration, maxDuration, minHeight, maxHeight, maxNumberOfNotesInAChord, areIrregularGroupsPresent, minimumDelay, notesMap, maxNumberOfEvents, irregularGroupsMap));

    }

    private int computeMeasuresNumber(int bpm, long pieceLength, int[] metre) {
        double bps = (double) bpm / 60;
        int beatsNumber = metre[0];
        double oneBeatLength = 1 / bps;
        return (int) (pieceLength / (oneBeatLength * beatsNumber));
    }

    public List<Instrument> getInstruments() {
        return this.instruments;
    }

    public int[] getMetreInNumbers() {
        return this.metreInNumbers;
    }

    public int getMeasuresNumber() {
        return this.measuresNumber;
    }

}
