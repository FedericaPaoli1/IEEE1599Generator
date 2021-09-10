package com.ieee1599generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author federica
 */
public class Utils {

    protected static int getMeasuresNumber(int bpm, long pieceLength, int[] metre) {
        double bps = (double) bpm / 60;
        int beatsNumber = metre[0];
        double oneBeatLength = 1 / bps;
        return (int) (pieceLength / (oneBeatLength * beatsNumber));
    }

    protected static int getRandomInteger(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    protected static int getRandomIntegerFromList(List<Integer> ints) {
        return ints.get(getRandomInteger(0, ints.size() - 1));
    }

    protected static char getRandomCharFromList(List<Character> chars) {
        return chars.get(getRandomInteger(0, chars.size() - 1));
    }

    protected static List<Integer> getRandomNonRepeatingIntegers(int size, int min, int max) {
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

    protected static List<Character> getRandomNonRepeatingChars(List<Character> chars) {
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

    protected static double getRandomNote(Map<Double, int[]> inputMap) {
        Double[] keySetArray = inputMap.keySet().toArray(new Double[inputMap.keySet().size()]);
        Random random = new Random();
        int randomIndex = random.nextInt(inputMap.keySet().size());
        return keySetArray[randomIndex];
    }

    protected static int getRandomIrregularGroup(Map<Integer, Integer> inputMap) {
        Integer[] keySetArray = inputMap.keySet().toArray(new Integer[inputMap.keySet().size()]);
        Random random = new Random();
        int randomIndex = random.nextInt(inputMap.keySet().size());
        return keySetArray[randomIndex];
    }

    protected static boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

}
