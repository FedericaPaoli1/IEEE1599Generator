package com.ieee1599generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Randomizer is the class that contains all the random methods
 *
 * @author Federica Paoli', id: 961887, e-mail:
 * federica.paoli1@studenti.unimi.it
 */
public class Randomizer {

    /**
     * The random object
     */
    private final Random random;

    public Randomizer(long seed) {
        this.random = new Random(seed);
    }

    /**
     * <p>
     * getRandomInteger
     * </p>
     *
     * @param min the minimum integer
     * @param max the maximum integer
     * @return a random integer in the range between the minimum and maximum
     * parameters
     */
    protected int getRandomInteger(int min, int max) {
        return this.random.nextInt((max - min) + 1) + min;
    }

    /**
     * <p>
     * getRandomFloat
     * </p>
     *
     * @param min the minimum float
     * @param max the maximum float
     * @return a random float in the range between the minimum and maximum
     * parameters
     */
    protected float getRandomFloat(float min, float max) {
        return min + this.random.nextFloat() * (max - min);
    }

    /**
     * <p>
     * getRandomString
     * </p>
     *
     * @param strings the array of strings
     * @return a random string from the array of strings parameter
     */
    protected String getRandomString(String[] strings) {
        return strings[this.random.nextInt(strings.length)];
    }

    /**
     * <p>
     * getRandomBoolean
     * </p>
     *
     * @return a random boolean
     */
    protected boolean getRandomBoolean() {
        return this.random.nextBoolean();
    }

    /**
     * <p>
     * getRandomElementFromList is a generic method that picks a random element
     * from the generic input list
     * </p>
     *
     * @param <T>
     * @param inputList the input list
     * @return a random element from the input list
     */
    protected <T> T getRandomElementFromList(List<T> inputList) {
        return inputList.get(getRandomInteger(0, inputList.size() - 1));
    }

    /* protected int getRandomIntegerFromList(List<Integer> ints) {
        return ints.get(getRandomInteger(0, ints.size() - 1));
    }

    protected char getRandomCharFromList(List<Character> chars) {
        return chars.get(getRandomInteger(0, chars.size() - 1));
    }*/
    /**
     * <p>
     * getRandomIntegers
     * </p>
     *
     * @param size the entire size
     * @param min the minimum integer
     * @param max the maximum integer
     * @return a random list of integers in the range between the minimum and
     * maximum
     */
    protected List<Integer> getRandomIntegers(int size, int min, int max) {
        List<Integer> numbers = new ArrayList();
        while (numbers.size() < size) {
            numbers.add(getRandomInteger(min, max));
        }
        return numbers;
    }

    /**
     * <p>
     * getRandomNonRepeatingIntegers
     * </p>
     *
     * @param size the entire size
     * @param min the minimum integer
     * @param max the maximum integer
     * @return a random list of non repeating integers in the range between the
     * minimum and maximum
     */
    protected List<Integer> getRandomNonRepeatingIntegers(int size, int min, int max) {
        List<Integer> numbers = new ArrayList();
        while (numbers.size() < size) {
            int randomNumber = getRandomInteger(min, max);
            //Check for duplicate values
            if (!numbers.contains(randomNumber)) {
                numbers.add(randomNumber);
            }
        }
        return numbers;
    }

    /**
     * <p>
     * getRandomNonRepeatingChars
     * </p>
     *
     * @param chars the list of characters
     * @return a random list of non repeating characters from the input list
     */
    protected List<Character> getRandomNonRepeatingChars(List<Character> chars) {
        List<Character> randomizedChars = new ArrayList();
        int min = 0;
        int max = chars.size() - 1;
        while (randomizedChars.size() < chars.size()) {
            int randomIndex = getRandomInteger(min, max);
            char c = chars.get(randomIndex);

            //Check for duplicate values
            if (!randomizedChars.contains(c)) {
                randomizedChars.add(c);
            }
        }
        return randomizedChars;
    }

    /**
     * <p>
     * shuffleList is a generic method that mixes the elements of the inputList
     * </p>
     *
     * @param <T>
     * @param inputList the input list
     */
    protected <T> void shuffleList(List<T> inputList) {
        Collections.shuffle(inputList, this.random);
    }

    /**
     * <p>
     * getRandomStringFromMap
     * </p>
     *
     * @param inputMap the input map
     * @return a random string from the input map
     */
    protected String getRandomStringFromMap(Map<String, Float> inputMap) {
        String[] keySetArray = inputMap.keySet().toArray(new String[inputMap.keySet().size()]);
        int randomIndex = this.random.nextInt(inputMap.keySet().size());
        return keySetArray[randomIndex];
    }

    /**
     * <p>
     * getRandomIntFromMap
     * </p>
     *
     * @param inputMap the input map
     * @return a random integer from the input map
     */
    protected int getRandomIntFromMap(Map<Integer, Integer> inputMap) {
        Integer[] keySetArray = inputMap.keySet().toArray(new Integer[inputMap.keySet().size()]);
        int randomIndex = this.random.nextInt(inputMap.keySet().size());
        return keySetArray[randomIndex];
    }
}
