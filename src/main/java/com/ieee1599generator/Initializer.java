package com.ieee1599generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Initializes some of the input parameters
 *
 * @author Federica Paoli', id: 961887, e-mail:
 * federica.paoli1@studenti.unimi.it
 */
public class Initializer {

    /**
     * The list of musical instruments, each with its own parameters
     */
    private final List<Instrument> instruments = new ArrayList<>();
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
    private final Map<Integer, Integer> irregularGroupsMap = new HashMap<>();

    public Initializer(long trackLength, String metre, int bpm) {

        this.metreInNumbers = new int[]{Integer.parseInt(String.valueOf(metre.charAt(0))), Integer.parseInt(String.valueOf(metre.charAt(2)))};
        this.measuresNumber = computeMeasuresNumber(bpm, trackLength, this.metreInNumbers);

        // fill the map of the irregular groups
        if (this.metreInNumbers[0] % 3 == 0 && this.metreInNumbers[1] % 2 == 0) {   // check if time signature is compound
            this.irregularGroupsMap.put(2, 1);
            this.irregularGroupsMap.put(4, 2);
            this.irregularGroupsMap.put(5, 2);
            this.irregularGroupsMap.put(7, 4);
            this.irregularGroupsMap.put(9, 4);
            this.irregularGroupsMap.put(11, 4);
            this.irregularGroupsMap.put(13, 4);
        } else {
            this.irregularGroupsMap.put(3, 2);
            this.irregularGroupsMap.put(5, 4);
            this.irregularGroupsMap.put(6, 4);
            this.irregularGroupsMap.put(7, 8);
            this.irregularGroupsMap.put(9, 8);
            this.irregularGroupsMap.put(11, 8);
            this.irregularGroupsMap.put(13, 8);
        }

    }

    /**
     * <p>
     * initializes its input parameters, so that they can be used more easily
     * within the document to be created
     * </p>
     *
     * @param maxNumberOfPlayedNotes the maximum number of played notes
     * @param minDuration the array representing the minimum duration of musical
     * figures
     * @param maxDuration the array representing the maximum duration of musical
     * figures
     * @param minHeight the minimum height of musical figure
     * @param maxHeight the minimum height of musical figure
     * @param maxNumberOfNotesInAChord the maximum number of notes in a chord
     * @param areIrregularGroupsPresent the presence or absence of irregular
     * groups
     * @param minimumDelay the minimum delay, expressed in VTU, after which the
     * next note will sound
     */
    public void initializeInstrumentsParams(int maxNumberOfPlayedNotes, int[] minDuration, int[] maxDuration, String minHeight, String maxHeight, int maxNumberOfNotesInAChord, boolean areIrregularGroupsPresent, int minimumDelay) {

        // fill the map of the notes from from that of maximum duration to that of minimum duration
        Map<Double, int[]> notesMap = new HashMap<>();
        for (int i = maxDuration[1]; i <= minDuration[1]; i *= 2) {
            notesMap.put((double) 1 / i, new int[]{1, i});
        }

        // compute the maximum number of events
        int maxNumberOfEvents = (int) ((((double) 1 / this.metreInNumbers[1]) / ((double) minDuration[0] / minDuration[1])) * this.metreInNumbers[0]) * this.measuresNumber;

        // create a new instrument with the initialized parameters
        instruments.add(new Instrument(maxNumberOfPlayedNotes, minDuration, maxDuration, minHeight, maxHeight, maxNumberOfNotesInAChord, areIrregularGroupsPresent, minimumDelay, notesMap, maxNumberOfEvents));

    }

    /**
     * <p>
     * computes the number of measures
     * </p>
     *
     * @param bpm the time expressed in beats per minute (bpm)
     * @param trackLength the track length expressed in seconds
     * @param metre
     *
     * @return the number of measures
     */
    private int computeMeasuresNumber(int bpm, long trackLength, int[] metre) {
        double bps = (double) bpm / 60;
        int beatsNumber = metre[0];
        double oneBeatLength = 1 / bps;
        return (int) (trackLength / (oneBeatLength * beatsNumber));
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

    public Map<Integer, Integer> getIrregularGroupsMap() {
        return this.irregularGroupsMap;
    }

}
