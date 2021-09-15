package com.ieee1599generator;

import java.util.List;
import java.util.Map;

/**
 * FormatterBuilder is the class implementing the Builder pattern for the
 * Formatter class
 *
 * @author Federica Paoli', id: 961887, e-mail:
 * federica.paoli1@studenti.unimi.it
 */
public class FormatterBuilder {

    /**
     * The seed for the random object in order to replicate the same random
     * values for several app executions
     */
    private long seed;
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
    /**
     * The map of irregular groups
     */
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
        this.docVersion = docVersion;
        return this;
    }

    public FormatterBuilder title(String title) {
        this.title = title;
        return this;
    }

    public FormatterBuilder author(String author) {
        this.author = author;
        return this;
    }

    public FormatterBuilder instrumentsNumber(int instrumentsNumber) {
        this.instrumentsNumber = instrumentsNumber;
        return this;
    }

    public FormatterBuilder instruments(List<Instrument> instruments) {
        this.instruments = instruments;
        return this;
    }

    public FormatterBuilder clefs(List<Character> clefs) {
        this.clefs = clefs;
        return this;
    }

    public FormatterBuilder clefsSteps(List<Integer> clefsSteps) {
        this.clefsSteps = clefsSteps;
        return this;
    }

    public FormatterBuilder accidentalMap(Map<String, Integer> accidentalMap) {
        this.accidentalMap = accidentalMap;
        return this;
    }

    public FormatterBuilder pitchesMap(Map<Integer, Character> pitchesMap) {
        this.pitchesMap = pitchesMap;
        return this;
    }

    public FormatterBuilder octavesNumber(int octavesNumber) {
        this.octavesNumber = octavesNumber;
        return this;
    }

    public FormatterBuilder metreInNumbers(int[] metreInNumbers) {
        this.metreInNumbers = metreInNumbers;
        return this;
    }

    public FormatterBuilder measuresNumber(int measuresNumber) {
        this.measuresNumber = measuresNumber;
        return this;
    }
    
    public FormatterBuilder irregularGroupsMap(Map<Integer, Integer> irregularGroupsMap) {
        this.irregularGroupsMap = this.irregularGroupsMap;
        return this;
    }

    public Formatter build() {
        return new Formatter(seed, creator, docVersion, title, author, instrumentsNumber, instruments, clefs, clefsSteps, accidentalMap, pitchesMap, octavesNumber, metreInNumbers, measuresNumber, irregularGroupsMap);
    }

}
