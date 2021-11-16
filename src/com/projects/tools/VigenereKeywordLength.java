package com.projects.tools;

import java.util.ArrayList;

public class VigenereKeywordLength {
    public static int numOfSequences = 10;

    /**
     * getSequences takes a cyphertext string and breaks it up into a certeain
     * number of sequences based on probable keylengths.
     * 
     * @param cypherText
     * @return
     */
    public ArrayList<ArrayList<String>> getSequences(String cypherText) {
        ArrayList<ArrayList<String>> sequences = new ArrayList<ArrayList<String>>();

        for (int i = 2; i <= numOfSequences; i++) {

            // initialize arraylist with strings
            ArrayList<String> sequence = new ArrayList<String>();
            for (int j = 0; j < i; j++) {
                sequence.add("");
            }

            for (int j = 0; j < cypherText.length(); j++) {
                // first index
                int sequenceIndex = (j) % i;
                String currentSequence = sequence.get(sequenceIndex);
                currentSequence = currentSequence + cypherText.charAt(j);
                sequence.set(sequenceIndex, currentSequence);
            }

            sequences.add(sequence);
        }
        return sequences;
    }

    /**
     * returns the avg Index of Coincidence for each prospect key length
     * 
     * @param sequence
     * @return
     */
    public double getAvgIC(ArrayList<String> sequence) {

        double avgic = 0;

        for (int i = 0; i < sequence.size(); i++) {
            avgic += indexOfCoincidence.getIC(indexOfCoincidence.getCharFreq(sequence.get(i)), sequence.get(i));
        }

        avgic = avgic / sequence.size();

        return avgic;
    }

    /**
     * return the most probable key lengths based on average
     * 
     * @param avgICValues
     * @return
     */
    public ArrayList<Integer> getProbableKeyLengths(double[] avgICValues) {
        ArrayList<Integer> probableKeyLengths = new ArrayList<Integer>();
        double total = 0;
        double percentDiffCutoff = .1; // cutt off for percent difference required to be a probable key

        // get the avg IC
        for (int i = 0; i < avgICValues.length; i++) {
            total += avgICValues[i];
        }
        double avgIC = total / avgICValues.length;

        // determine the percent difference between each value and the average
        for (int i = 0; i < avgICValues.length; i++) {
            double percentDiff = (avgICValues[i] - avgIC) / avgIC;

            if (percentDiff >= percentDiffCutoff) {
                probableKeyLengths.add(i + 2);
            }
        }

        return probableKeyLengths;
    }
}
