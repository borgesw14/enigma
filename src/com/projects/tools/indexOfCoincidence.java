package com.projects.tools;

import java.util.Scanner;

public class indexOfCoincidence {

    /**
     * A method to count the occurances of each letter in a cypher-text
     * 
     * @param cypherString
     */
    public static double[] getCharFreq(String cypherString) {
        double pArr[] = new double[26];
        for (int i = 0; i < pArr.length; i++) {
            pArr[i] = 0;
        }

        // Count individial char
        for (int i = 0; i < cypherString.length(); i++) {
            pArr[cypherString.charAt(i) - 'a']++;
        }

        return pArr;

    }

    /**
     * Calculates the Index of Coincidence
     * 
     * @param pArr
     * @param cypherString
     * @return
     */
    public static double getIC(double[] pArr, String cypherString) {
        double ic = 0;
        // calculate IC
        for (int i = 0; i < pArr.length; i++) {
            // ic += (pArr[i]/pArr.length) * ((pArr[i] - 1)/(pArr.length));
            ic += (pArr[i] * (pArr[i] - 1)) / (cypherString.length() * (cypherString.length() - 1));

        }
        return ic;
    }

    /**
     * 
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String cypherText = sc.nextLine();
        sc.close();

        cypherText.toLowerCase();
        double pArr[] = getCharFreq(cypherText);
        double ic = getIC(pArr, cypherText);

        System.out.println(ic);
    }
}
