package com.ieee1599generator;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author federica
 */
public class FormatterBuilder {

    private long seed;
    private String creator;
    private double docVersion;
    private String title;
    private String author;
    private int instrumentsNumber;
    private int maxNumberOfPlayedNotes;
    private int[] minDuration;
    private int minHeight;
    private int maxHeight;
    private int maxNumberOfNotesInAChord;
    private boolean areIrregularGroupsPresent;
    private int minimumDelay;
    private List<Character> clefs;
    private List<Integer> clefsSteps;
    private Map<Integer, String> pitchesMap;
    private int octavesNumber;
    private int[] metreInNumbers;
    private int measuresNumber;
    private Map<Double, int[]> notesMap;
    private int maxNumberOfEvents;
    private Map<Integer, Integer> irregularGroupsMap;

    private FormatterBuilder() {
    }

    public static FormatterBuilder newBuilder() {
        return new FormatterBuilder();
    }
    
    public FormatterBuilder seed(long seed) {
        this.seed = seed;
        return this;
    }

    public FormatterBuilder creator(String creator) {
        this.creator = creator;
        return this;
    }

    public FormatterBuilder docVersion(double docVersion) {
        this.docVersion = this.docVersion;
        return this;
    }

    public FormatterBuilder title(String title) {
        this.title = this.title;
        return this;
    }

    public FormatterBuilder author(String author) {
        this.author = this.author;
        return this;
    }

    public FormatterBuilder instrumentsNumber(int instrumentsNumber) {
        this.instrumentsNumber = this.instrumentsNumber;
        return this;
    }

    public FormatterBuilder maxNumberOfPlayedNotes(int maxNumberOfPlayedNotes) {
        this.maxNumberOfPlayedNotes = this.maxNumberOfPlayedNotes;
        return this;
    }

    public FormatterBuilder minDuration(int[] minDuration) {
        this.minDuration = this.minDuration;
        return this;
    }

    public FormatterBuilder minHeight(int minHeight) {
        this.minHeight = this.minHeight;
        return this;
    }

    public FormatterBuilder maxHeight(int maxHeight) {
        this.maxHeight = this.maxHeight;
        return this;
    }

    public FormatterBuilder maxNumberOfNotesInAChord(int maxNumberOfNotesInAChord) {
        this.maxNumberOfNotesInAChord = this.maxNumberOfNotesInAChord;
        return this;
    }

    public FormatterBuilder areIrregularGroupsPresent(boolean areIrregularGroupsPresent) {
        this.areIrregularGroupsPresent = this.areIrregularGroupsPresent;
        return this;
    }

    public FormatterBuilder minimumDelay(int minimumDelay) {
        this.minimumDelay = this.minimumDelay;
        return this;
    }

    public FormatterBuilder clefs(List<Character> clefs) {
        this.clefs = this.clefs;
        return this;
    }

    public FormatterBuilder clefsSteps(List<Integer> clefsSteps) {
        this.clefsSteps = this.clefsSteps;
        return this;
    }

    public FormatterBuilder pitchesMap(Map<Integer, String> pitchesMap) {
        this.pitchesMap = this.pitchesMap;
        return this;
    }

    public FormatterBuilder octavesNumber(int octavesNumber) {
        this.octavesNumber = this.octavesNumber;
        return this;
    }

    public FormatterBuilder metreInNumbers(int[] metreInNumbers) {
        this.metreInNumbers = this.metreInNumbers;
        return this;
    }

    public FormatterBuilder measuresNumber(int measuresNumber) {
        this.measuresNumber = this.measuresNumber;
        return this;
    }

    public FormatterBuilder notesMap(Map<Double, int[]> notesMap) {
        this.notesMap = this.notesMap;
        return this;
    }

    public FormatterBuilder maxNumberOfEvents(int maxNumberOfEvents) {
        this.maxNumberOfEvents = this.maxNumberOfEvents;
        return this;
    }

    public FormatterBuilder irregularGroupsMap(Map<Integer, Integer> irregularGroupsMap) {
        this.irregularGroupsMap = this.irregularGroupsMap;
        return this;
    }

    public Formatter build() {
        return new Formatter(seed, creator, docVersion, title, author, instrumentsNumber, maxNumberOfPlayedNotes, minDuration, minHeight, maxHeight, maxNumberOfNotesInAChord, areIrregularGroupsPresent, minimumDelay, clefs, clefsSteps, pitchesMap, octavesNumber, metreInNumbers, measuresNumber, notesMap, maxNumberOfEvents, irregularGroupsMap);
    }

}
