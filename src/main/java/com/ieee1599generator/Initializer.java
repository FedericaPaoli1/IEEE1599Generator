package com.ieee1599generator;

import java.time.Duration;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author federica
 */
public class Initializer {

    private final String pieceLength;
    private final int[] metreInNumbers;
    private final long seconds;
    private final int measuresNumber;
    private Map<Double, int[]> notesMap = new TreeMap<>();
    private final double noteValueForAMeasure;
    private final int[] minDuration;
    private final int[] maxDuration;
    private final int maxNumberOfEvents;
    private Map<Integer, Integer> irregularGroupsMap = new TreeMap<>();
    private final boolean isTheTimeSignatureCompound;

    public Initializer(double trackLength, String metre, int bpm, int[] minDuration, int[] maxDuration) {
        this.pieceLength = "PT" + trackLength + "M";
        this.metreInNumbers = new int[]{Integer.parseInt(String.valueOf(metre.charAt(0))), Integer.parseInt(String.valueOf(metre.charAt(2)))};
        this.seconds = Duration.parse(this.pieceLength).getSeconds();
        this.measuresNumber = getMeasuresNumber(bpm, this.seconds, this.metreInNumbers);
        this.notesMap = new TreeMap<>();
        for (int i = maxDuration[0]; i <= minDuration[0]; i *= 2) {
            this.notesMap.put((double) 1 / i, new int[]{i, 1});
        }
        this.noteValueForAMeasure = (double) 1 / this.metreInNumbers[1];
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.maxNumberOfEvents = (int) ((((double) 1 / this.metreInNumbers[1]) / ((double) minDuration[1] / minDuration[0])) * this.metreInNumbers[0]);
        this.irregularGroupsMap = new TreeMap<>();
        this.isTheTimeSignatureCompound = this.metreInNumbers[0] % 3 == 0 && this.metreInNumbers[1] % 2 == 0;
        if (this.isTheTimeSignatureCompound) {
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

    private int getMeasuresNumber(int bpm, long pieceLength, int[] metre) {
        double bps = (double) bpm / 60;
        int beatsNumber = metre[0];
        double oneBeatLength = 1 / bps;
        return (int) (pieceLength / (oneBeatLength * beatsNumber));
    }

    public String getPieceLength() {
        return this.pieceLength;
    }

    public int[] getMetreInNumbers() {
        return this.metreInNumbers;
    }

    public long getSeconds() {
        return this.seconds;
    }

    public int getMeasuresNumber() {
        return this.measuresNumber;
    }

    public Map<Double, int[]> getNotesMap() {
        return this.notesMap;
    }

    public double getNoteValueForAMeasure() {
        return this.noteValueForAMeasure;
    }

    public int[] getMinDuration() {
        return this.minDuration;
    }

    public int[] getMaxDuration() {
        return this.maxDuration;
    }

    public int getMaxNumberOfEvents() {
        return this.maxNumberOfEvents;
    }

    public Map<Integer, Integer> getIrregularGroupsMap() {
        return this.irregularGroupsMap;
    }

    public boolean getIsTheTimeSignatureCompound() {
        return this.isTheTimeSignatureCompound;
    }

}