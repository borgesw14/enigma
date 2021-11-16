package com.projects.tools;

import java.util.ArrayList;
import java.util.Collections;

public class VigenereCracker {

    public static char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * character frequencies for the english language extracted from:
     * 
     * @author http://cs.wellesley.edu/~fturbak/codman/letterfreq.html
     */
    public static double[] charFreqEng = { 0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094,
            0.06966, 0.00153, 0.00772, 0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056,
            0.02758, 0.00978, 0.02360, 0.00150, 0.01974, 0.00074 };

    /**
     * generates all 26 possible ceaser ciphers from the sequence input: an
     * encrypted ceaser cipher sequence output: all 26 possible ciphers
     * 
     * @param sequence
     * @return
     */
    public String[] generateCeaserCiphers(String sequence) {
        String[] ceasersCiphers = new String[26];

        for (int i = 25; i >= 0; i--) {
            StringBuilder result = new StringBuilder();
            for (int j = 0; j < sequence.length(); j++) {
                {
                    char ch = (char) (((int) sequence.charAt(j) + i - 97) % 26 + 97);
                    result.append(ch);
                }
            }
            // System.out.println(result.toString());
            ceasersCiphers[(25 - i + 1) % 26] = result.toString();
        }
        return ceasersCiphers;
    }

    /**
     * A method to determine the chi-squared statistic of a string of text in
     * relation to english. input: a string of text output: chi-squared statistic
     * 
     * @param currentCipher
     * @return
     */
    public double getChiSqrd(String currentCipher) {
        double[] chisqrd = new double[26];
        int[] charFreqCipher = new int[26];
        double sum = 0;

        for (int i = 0; i < alphabet.length; i++) {
            for (int j = 0; j < currentCipher.length(); j++) {
                if (currentCipher.charAt(j) == (alphabet[i])) {
                    charFreqCipher[i] = charFreqCipher[i] + 1;
                }
            }
        }

        for (int i = 0; i < charFreqCipher.length; i++) {
            chisqrd[i] = Math.pow((charFreqCipher[i] - (charFreqEng[i] * currentCipher.length())), 2)
                    / (charFreqEng[i] * currentCipher.length());
        }

        for (double i : chisqrd) {
            sum += i;
        }

        return sum;
    }

    /**
     * A method to build a string repressenting a probable key from an array of
     * strings representing current probable strings
     * 
     * @param currentKeys
     * @param possibleChars
     * @return
     */
    public ArrayList<String> keyBuilder(ArrayList<String> currentKeys, ArrayList<Integer> possibleChars) {

        ArrayList<String> newCurrentKeys = new ArrayList<String>();
        for (int i = 0; i < possibleChars.size(); i++) {

            for (int j = 0; j < currentKeys.size(); j++) {
                newCurrentKeys.add(currentKeys.get(j) + Character.toString('a' + possibleChars.get(i)));
            }
            if (currentKeys.size() == 0) {
                newCurrentKeys.add(Character.toString('a' + possibleChars.get(i)));
            }
        }
        return newCurrentKeys;
    }

    /**
     * 
     * 
     * @param encodedMsg
     * @return
     */
    public String msgToString(ArrayList<Character> encodedMsg) {
        String eMsgString = "";
        for (int i = 0; i < encodedMsg.size(); i++) {
            eMsgString = eMsgString + encodedMsg.get(i);
        }
        return eMsgString;
    }

    /**
     * Method gets the lowest chi squared value
     * 
     * @param ciphers
     * @param chiStats
     * @return
     */
    public ArrayList<Integer> getLowestChiIndex(String[] ciphers,
            double[] chiStats /* ArrayList<Character> encodedMsg */) {
        /*
         * VigenereCracker mainCracker = new VigenereCracker(); String[] ciphers =
         * mainCracker.generateCeaserCiphers(msgToString(encodedMsg));
         * 
         * double[] chiStats = new double[26];
         */
        ArrayList<String> tempCiphers = new ArrayList<String>();
        ArrayList<Integer> originalCipherIndex = new ArrayList<Integer>();
        int count = 0;
        Collections.addAll(tempCiphers, ciphers);

        // sort chiStats
        for (int i = 0; i < chiStats.length - 1; i++)
            for (int j = 0; j < chiStats.length - i - 1; j++)
                if (chiStats[j] > chiStats[j + 1]) {
                    // swap arr[j+1] and arr[j]
                    double temp = chiStats[j];
                    chiStats[j] = chiStats[j + 1];
                    chiStats[j + 1] = temp;

                    String sTemp = ciphers[j];
                    ciphers[j] = ciphers[j + 1];
                    ciphers[j + 1] = sTemp;
                }

        while (originalCipherIndex.size() < 1) {
            originalCipherIndex.add(tempCiphers.indexOf(ciphers[count]));
            count++;
        }

        return originalCipherIndex;
    }

    /**
     * a method that accepts Arraylist of sequences and returns an arraylist of
     * possible keys
     *
     * @param sequences
     * @return
     */
    public ArrayList<String> getKeys(ArrayList<String> sequences) {
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<String[]> ciphers = new ArrayList<String[]>();
        ArrayList<double[]> chiStats = new ArrayList<double[]>();
        ArrayList<ArrayList<Integer>> possibleCharIndexs = new ArrayList<ArrayList<Integer>>();

        // generates the ceasers ciphers for each sequence
        for (String seq : sequences) {
            ciphers.add(generateCeaserCiphers(seq));
        }

        for (int i = 0; i < ciphers.size(); i++) {
            double[] currentChiStats = new double[ciphers.get(i).length];

            for (int j = 0; j < ciphers.get(i).length; j++) {
                currentChiStats[j] = getChiSqrd(ciphers.get(i)[j]);
            }
            /*
             * for (int j = 0; j < currentChiStats.length; j++) { System.out.println(j + " "
             * + ciphers.get(i)[j] + " " + currentChiStats[j]); }
             */
            chiStats.add(currentChiStats);
        }

        for (int i = 0; i < ciphers.size(); i++) {
            possibleCharIndexs.add(getLowestChiIndex(ciphers.get(i), chiStats.get(i)));
        }

        for (ArrayList<Integer> arrayList : possibleCharIndexs) {
            keys = new ArrayList<String>(keyBuilder(keys, arrayList));
        }

        return keys;
    }
}
