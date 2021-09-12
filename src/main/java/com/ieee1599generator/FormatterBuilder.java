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
    private List<Instrument> instruments;
    private List<Character> clefs;
    private List<Integer> clefsSteps;
    private Map<Integer, String> pitchesMap;
    private int octavesNumber;
    private int[] metreInNumbers;
    private int measuresNumber;

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

    public FormatterBuilder pitchesMap(Map<Integer, String> pitchesMap) {
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

    public Formatter build() {
        return new Formatter(seed, creator, docVersion, title, author, instrumentsNumber, instruments, clefs, clefsSteps, pitchesMap, octavesNumber, metreInNumbers, measuresNumber);
    }

}
