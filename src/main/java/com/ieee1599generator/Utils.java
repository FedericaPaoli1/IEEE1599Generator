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

    private final Random random;

    public Utils(int seed) {
        this.random = new Random(seed);
    }

    protected int getRandomInteger(int min, int max) {
        return this.random.nextInt((max - min) + 1) + min;
    }

    protected int getRandomIntegerFromList(List<Integer> ints) {
        return ints.get(getRandomInteger(0, ints.size() - 1));
    }

    protected char getRandomCharFromList(List<Character> chars) {
        return chars.get(getRandomInteger(0, chars.size() - 1));
    }

    protected List<Integer> getRandomNonRepeatingIntegers(int size, int min, int max) {
        List<Integer> numbers = new ArrayList();
        while (numbers.size() < size) {
            int randomNumber = this.random.nextInt((max - min) + 1) + min;
            //Check for duplicate values
            if (!numbers.contains(randomNumber)) {
                numbers.add(randomNumber);
            }
        }
        return numbers;
    }

    protected List<Character> getRandomNonRepeatingChars(List<Character> chars) {
        List<Character> randomizedChars = new ArrayList();
        int min = 0;
        int max = chars.size() - 1;
        while (randomizedChars.size() < chars.size()) {
            int randomIndex = this.random.nextInt((max - min) + 1) + min;
            char c = chars.get(randomIndex);

            //Check for duplicate values
            if (!randomizedChars.contains(c)) {
                randomizedChars.add(c);
            }
        }
        return randomizedChars;
    }

    protected double getRandomNote(Map<Double, int[]> inputMap) {
        Double[] keySetArray = inputMap.keySet().toArray(new Double[inputMap.keySet().size()]);
        int randomIndex = this.random.nextInt(inputMap.keySet().size());
        return keySetArray[randomIndex];
    }

    protected int getRandomIrregularGroup(Map<Integer, Integer> inputMap) {
        Integer[] keySetArray = inputMap.keySet().toArray(new Integer[inputMap.keySet().size()]);
        int randomIndex = this.random.nextInt(inputMap.keySet().size());
        return keySetArray[randomIndex];
    }

    protected boolean getRandomBoolean() {
        return this.random.nextBoolean();
    }

}
