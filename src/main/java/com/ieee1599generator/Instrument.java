package com.ieee1599generator;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author federica
 */
public class Instrument {

    private int maxNumberOfPlayedNotes;
    private int[] minDuration;
    private int[] maxDuration;
    private int minHeight;
    private int maxHeight;
    private int maxNumberOfNotesInAChord;
    private boolean areIrregularGroupsPresent;
    private int minimumDelay;
    private Map<Double, int[]> notesMap = new TreeMap<>();
    private int maxNumberOfEvents;
    private Map<Integer, Integer> irregularGroupsMap = new TreeMap<>();

    public Instrument(int maxNumberOfPlayedNotes, int[] minDuration, int[] maxDuration, int minHeight, int maxHeight, int maxNumberOfNotesInAChord, boolean areIrregularGroupsPresent, int minimumDelay, Map<Double, int[]> notesMap, int maxNumberOfEvents, Map<Integer, Integer> irregularGroupsMap) {
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
        this.irregularGroupsMap = irregularGroupsMap;
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
    
    public Map<Integer, Integer> getIrregularGroupsMap() {
        return this.irregularGroupsMap;
    }

}
