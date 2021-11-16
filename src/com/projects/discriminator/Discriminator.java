package com.projects.discriminator;

import java.util.ArrayList;

/**
 * Discriminator
 */
public class Discriminator {

    public static final String CODE_ENIGMA = "enigma";

    public String Discriminate(ArrayList<Integer> keyLengths) {
        if (keyLengths.size() > 0) {
            // return nothing
            return "";
        } else {
            return CODE_ENIGMA;
        }
    }
}