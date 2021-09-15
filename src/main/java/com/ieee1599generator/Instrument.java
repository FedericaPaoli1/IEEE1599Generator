package com.ieee1599generator;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Istrument is the class representing a musical instrument
 *
 * @author Federica Paoli', id: 961887, e-mail:
 * federica.paoli1@studenti.unimi.it
 */
public class Instrument {

    /**
     * The maximum number of played notes
     */
    private final int maxNumberOfPlayedNotes;
    /**
     * The minimum duration of musical figures
     */
    private final int[] minDuration;
    /**
     * The maximum duration of musical figures
     */
    private final int[] maxDuration;
    /**
     * The minimum height of musical figures
     */
    private final int minHeight;
    /**
     * The maximum height of musical figures
     */
    private final int maxHeight;
    /**
     * The maximum number of notes in a chord
     */
    private final int maxNumberOfNotesInAChord;
    /**
     * The presence or absence of irregular groups
     */
    private final boolean areIrregularGroupsPresent;
    /**
     * The minimum delay, expressed in VTU, after which the next note will sound
     */
    private final int minimumDelay;
    /**
     * The map of the musical instrument notes
     */
    private Map<Double, int[]> notesMap = new TreeMap<>();
    /**
     * The maximum number of the events available for the musical instrument
     */
    private final int maxNumberOfEvents;
    /**
     * The map of the irregular groups
     */
    private Map<Integer, Integer> irregularGroupsMap = new TreeMap<>();

    public Instrument(int maxNumberOfPlayedNotes, int[] minDuration, int[] maxDuration, int minHeight, int maxHeight, int maxNumberOfNotesInAChord, boolean areIrregularGroupsPresent, int minimumDelay, Map<Double, int[]> notesMap, int maxNumberOfEvents) {
        this.maxNumberOfPlayedNotes = maxNumberOfPlayedNotes;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.maxNumberOfNotesInAChord = maxNumberOfNotesInAChord;
        this.areIrregularGroupsPresent = areIrregularGroupsPresent;
        this.minimumDelay = minimumDelay;
        this.notesMap = notesMap;
        this.maxNumberOfEvents = maxNumberOfEvents;
    }

    public int getMaxNumberOfPlayedNotes() {
        return this.maxNumberOfPlayedNotes;
    }

    public int[] getMinDuration() {
        return this.minDuration;
    }

    public int[] getMaxDuration() {
        return this.maxDuration;
    }

    public int getMinHeight() {
        return this.minHeight;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public int getMaxNumberOfNotesInAChord() {
        return this.maxNumberOfNotesInAChord;
    }

    public boolean getAreIrregularGroupsPresent() {
        return this.areIrregularGroupsPresent;
    }

    public int getMinimumDelay() {
        return this.minimumDelay;
    }

    public Map<Double, int[]> getNotesMap() {
        return this.notesMap;
    }

    public int getMaxNumberOfEvents() {
        return this.maxNumberOfEvents;
    }

    private <T, R> String mapAsString(Map<T, R> map) {
        String mapAsString = map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        return mapAsString;
    }

    @Override
    public String toString() {
        return "Maximum number of played notes: " + this.maxNumberOfPlayedNotes
                + ", minimum duration: " + this.minDuration[1] + "/" + minDuration[0]
                + ", maximum duration: " + this.maxDuration[1] + "/" + maxDuration[0]
                + ", minimum height: " + this.minHeight
                + ", maximum height: " + this.maxHeight
                + ", maximum numbers of notes in a chord: " + this.maxNumberOfNotesInAChord
                + ", are irregular groups present? " + this.areIrregularGroupsPresent
                + ", minimum delay: " + this.minimumDelay
                + ", notes map:\n" + mapAsString(this.notesMap)
                + "\nmaximum number of events: " + this.maxNumberOfEvents;
    }

}
