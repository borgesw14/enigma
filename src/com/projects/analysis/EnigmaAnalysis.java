package com.projects.analysis;

import java.util.*;

import com.projects.Main;
import com.projects.analysis.fitness.FitnessFunction;
import com.projects.enigma.Enigma;
import com.projects.enigma.Plugboard;

public class EnigmaAnalysis {
    public enum AvailableRotors {
        THREE, FIVE, ELEVEN, THREEG
    }

    /**
     * Finds the best rotor configurations and puts them in an order based on best fitness
     * 
     * @param ciphertext
     * @param rotors
     * @param plugboard
     * @param requiredKeys
     * @param f
     * @return
     */
    public static ScoredEnigmaKey[] findRotorConfiguration(char[] ciphertext, AvailableRotors rotors, String plugboard,
            int requiredKeys, FitnessFunction f) {
        String[] availableRotorList;

        switch (rotors) {
        case THREE:
            availableRotorList = new String[] { "I", "II", "III" };
            break;
        case FIVE:
            availableRotorList = new String[] { "I", "II", "III", "IV", "V" };
            break;
        case THREEG:
            availableRotorList = new String[] {"IG","IIG","IIIG"};
            break;
        case ELEVEN:
        default:
            availableRotorList = new String[] { "I", "II", "III", "IV", "V", "VI", "VII", "VIII","IG","IIG","IIIG" };
            break;
        }

        String[] optimalRotors;
        int[] optimalPositions;
        List<ScoredEnigmaKey> keySet = new ArrayList<>();

        if(Main.machineModel.equalsIgnoreCase("abwehr")){
            for (String rotor1 : availableRotorList) {
                for (String rotor2 : availableRotorList) {
                    if (rotor1.equals(rotor2))
                        continue;
                    for (String rotor3 : availableRotorList) {
                        if (rotor1.equals(rotor3) || rotor2.equals(rotor3))
                            continue;
                        System.out.println(rotor1 + " " + rotor2 + " " + rotor3);

                        float maxFitness = -1e30f;
                        EnigmaKey bestKey = null;
                        for (int i = 0; i < 26; i++) {
                            for (int j = 0; j < 26; j++) {
                                    for (int k = 0; k < 26; k++) {
                                            Enigma e = new Enigma(new String[] { rotor1, rotor2, rotor3 }, "G",0,
                                                    new int[] { i, j, k }, new int[] { 0, 0, 0 }, plugboard);
                                            char[] decryption = e.encrypt(ciphertext);
                                            float fitness = f.score(decryption);
                                            if (fitness > maxFitness) {
                                                maxFitness = fitness;
                                                optimalRotors = new String[] { e.leftRotor.getName(), e.middleRotor.getName(),
                                                        e.rightRotor.getName() };
                                                optimalPositions = new int[] { i, j, k };
                                                bestKey = new EnigmaKey(optimalRotors, optimalPositions, null, plugboard);
                                    }
                                }
                            }
                        }

                        keySet.add(new ScoredEnigmaKey(bestKey, maxFitness));
                    }
                }
            }

            // Sort keys by best performing (highest fitness score)
            keySet.sort(Collections.reverseOrder());
            return keySet.stream().sorted(Collections.reverseOrder()).limit(requiredKeys).toArray(ScoredEnigmaKey[]::new);
        
        }
        else{
            for (String rotor1 : availableRotorList) {
                for (String rotor2 : availableRotorList) {
                    if (rotor1.equals(rotor2))
                        continue;
                    for (String rotor3 : availableRotorList) {
                        if (rotor1.equals(rotor3) || rotor2.equals(rotor3))
                            continue;
                        System.out.println(rotor1 + " " + rotor2 + " " + rotor3);

                        float maxFitness = -1e30f;
                        EnigmaKey bestKey = null;
                        for (int i = 0; i < 26; i++) {
                            for (int j = 0; j < 26; j++) {
                                for (int k = 0; k < 26; k++) {
                                    Enigma e = new Enigma(new String[] { rotor1, rotor2, rotor3 }, "B",
                                            new int[] { i, j, k }, new int[] { 0, 0, 0 }, plugboard);
                                    char[] decryption = e.encrypt(ciphertext);
                                    float fitness = f.score(decryption);
                                    if (fitness > maxFitness) {
                                        maxFitness = fitness;
                                        optimalRotors = new String[] { e.leftRotor.getName(), e.middleRotor.getName(),
                                                e.rightRotor.getName() };
                                        optimalPositions = new int[] { i, j, k };
                                        bestKey = new EnigmaKey(optimalRotors, optimalPositions, null, plugboard);
                                    }
                                }
                            }
                        }

                        keySet.add(new ScoredEnigmaKey(bestKey, maxFitness));
                    }
                }
            }

            // Sort keys by best performing (highest fitness score)
            keySet.sort(Collections.reverseOrder());
            return keySet.stream().sorted(Collections.reverseOrder()).limit(requiredKeys).toArray(ScoredEnigmaKey[]::new);
        }
    }

    /**
     * Calculates the best fitness based on the rotors and ring settings
     * 
     * @param key
     * @param ciphertext
     * @param f
     * @return
     */
    public static ScoredEnigmaKey findRingSettings(EnigmaKey key, char[] ciphertext, FitnessFunction f) {
        if(Main.machineModel.equalsIgnoreCase("Abwehr"))
        {
                EnigmaKey newKey = new EnigmaKey(key);

                int rightRotorIndex = 2, middleRotorIndex = 1;

                // Optimise right rotor
                int optimalIndex = EnigmaAnalysis.findRingSetting(newKey, ciphertext, rightRotorIndex, f);
                newKey.rings[rightRotorIndex] = optimalIndex;
                newKey.indicators[rightRotorIndex] = (newKey.indicators[rightRotorIndex] + optimalIndex) % 26;

                // Optimise middle rotor
                optimalIndex = EnigmaAnalysis.findRingSetting(newKey, ciphertext, middleRotorIndex, f);
                newKey.rings[middleRotorIndex] = optimalIndex;
                newKey.indicators[middleRotorIndex] = (newKey.indicators[middleRotorIndex] + optimalIndex) % 26;

                // Calculate fitness and return scored key
                Enigma e = new Enigma(newKey);
                char[] decryption = e.encrypt(ciphertext);
                return new ScoredEnigmaKey(newKey, f.score(decryption));
        }
        else
            {
                EnigmaKey newKey = new EnigmaKey(key);

                int rightRotorIndex = 2, middleRotorIndex = 1;

                // Optimise right rotor
                int optimalIndex = EnigmaAnalysis.findRingSetting(newKey, ciphertext, rightRotorIndex, f);
                newKey.rings[rightRotorIndex] = optimalIndex;
                newKey.indicators[rightRotorIndex] = (newKey.indicators[rightRotorIndex] + optimalIndex) % 26;

                // Optimise middle rotor
                optimalIndex = EnigmaAnalysis.findRingSetting(newKey, ciphertext, middleRotorIndex, f);
                newKey.rings[middleRotorIndex] = optimalIndex;
                newKey.indicators[middleRotorIndex] = (newKey.indicators[middleRotorIndex] + optimalIndex) % 26;

                // Calculate fitness and return scored key
                Enigma e = new Enigma(newKey);
                char[] decryption = e.encrypt(ciphertext);
                return new ScoredEnigmaKey(newKey, f.score(decryption));
            }
    }

    /**
     * Finds and calculates different ring settings
     * 
     * @param key
     * @param ciphertext
     * @param rotor
     * @param f
     * @return
     */
    public static int findRingSetting(EnigmaKey key, char[] ciphertext, int rotor, FitnessFunction f) {
        String[] rotors = key.rotors;
        int[] originalIndicators = key.indicators;
        int[] originalRingSettings = key.rings != null ? key.rings : new int[] { 0, 0, 0 };
        String plugboard = key.plugboard;
        int optimalRingSetting = 0;

        if(Main.machineModel.equalsIgnoreCase("Abwehr")){
            float maxFitness = -1e30f;
            for (int i = 0; i < 26; i++) {
                int[] currentStartingPositions = Arrays.copyOf(originalIndicators, 3);
                int[] currentRingSettings = Arrays.copyOf(originalRingSettings, 3);

                currentStartingPositions[rotor] = Math.floorMod(currentStartingPositions[rotor] + i, 26);
                currentRingSettings[rotor] = i;

                Enigma e = new Enigma(rotors, "G",0, currentStartingPositions, currentRingSettings, plugboard);
                char[] decryption = e.encrypt(ciphertext);
                float fitness = f.score(decryption);
                if (fitness > maxFitness) {
                    maxFitness = fitness;
                    optimalRingSetting = i;
                }
            }
            return optimalRingSetting;
        }
        else{
            float maxFitness = -1e30f;
            for (int i = 0; i < 26; i++) {
                int[] currentStartingPositions = Arrays.copyOf(originalIndicators, 3);
                int[] currentRingSettings = Arrays.copyOf(originalRingSettings, 3);

                currentStartingPositions[rotor] = Math.floorMod(currentStartingPositions[rotor] + i, 26);
                currentRingSettings[rotor] = i;

                Enigma e = new Enigma(rotors, "B", currentStartingPositions, currentRingSettings, plugboard);
                char[] decryption = e.encrypt(ciphertext);
                float fitness = f.score(decryption);
                if (fitness > maxFitness) {
                    maxFitness = fitness;
                    optimalRingSetting = i;
                }
            }
            return optimalRingSetting;

        }
    }

    //this method definetly needs work for the correct setting to be found.
    public static ScoredEnigmaKey findReflectorSettings(EnigmaKey key, char[] ciphertext, FitnessFunction f) {
        
                EnigmaKey newKey = new EnigmaKey(key);

                int optimalIndex = EnigmaAnalysis.findReflectorSetting(newKey, ciphertext, f);
                newKey.reflectorPos = optimalIndex;

                // Calculate fitness and return scored key
                Enigma e = new Enigma(newKey);
                char[] decryption = e.encrypt(ciphertext);
                return new ScoredEnigmaKey(newKey, f.score(decryption));
    }
    
    //cycles through different reflector positions and returns a scored enigma key based on fitness function.
    public static int findReflectorSetting(EnigmaKey key, char[] ciphertext, FitnessFunction f)
    {
        String[] rotors = key.rotors;
        int[] originalIndicators = key.indicators;
        int[] originalRingSettings = key.rings != null ? key.rings : new int[] { 0, 0, 0 };
        String plugboard = key.plugboard;
        int originalReflectorPosition = key.reflectorPos;
        int optimalReflectorPosition = 0;
        
        float maxFitness = -1e30f;
            for (int i = 0; i < 26; i++) 
            {
                Enigma e = new Enigma(rotors, "G", i, originalIndicators, originalRingSettings, plugboard);
                char[] decryption = e.encrypt(ciphertext);
                float fitness = f.score(decryption);
                if (fitness > maxFitness) {
                    maxFitness = fitness;
                    optimalReflectorPosition = i;
                }

            }
            return optimalReflectorPosition;
    }





    /**
     * Plug board
     * 
     * @param key
     * @param ciphertext
     * @param f
     * @return
     */
    public static String findPlug(EnigmaKey key, char[] ciphertext, FitnessFunction f) {
        Set<Integer> unpluggedCharacters = Plugboard.getUnpluggedCharacters(key.plugboard);
        // Set<Integer> charCount = new HashSet<Integer>();

        EnigmaKey currentKey = new EnigmaKey(key);
        String originalPlugs = currentKey.plugboard;
        String optimalPlugSetting = "";
        float maxFitness = -1e30f;
        for (int i : unpluggedCharacters) {
            for (int j : unpluggedCharacters) {
                if (i >= j)
                    continue;

                String plug = "" + (char) (i + 65) + (char) (j + 65);
                currentKey.plugboard = originalPlugs.isEmpty() ? plug : originalPlugs + " " + plug;

                Enigma e = new Enigma(currentKey);
                char[] decryption = e.encrypt(ciphertext);
                float fitness = f.score(decryption);
                if (fitness > maxFitness) {
                    maxFitness = fitness;
                    optimalPlugSetting = plug;
                }
            }
        }

        return optimalPlugSetting;
    }

    /**
     * Finds different plug board settings
     * 
     * @param key
     * @param maxPlugs
     * @param ciphertext
     * @param f
     * @return
     */
    public static ScoredEnigmaKey findPlugs(EnigmaKey key, int maxPlugs, char[] ciphertext, FitnessFunction f) {
        EnigmaKey currentKey = new EnigmaKey(key);
        String plugs = "";
        // String findPlug(EnigmaKey key, char[] ciphertext, FitnessFunction f) {
        for (int i = 0; i < maxPlugs; i++) {
            currentKey.plugboard = plugs;
            String nextPlug = findPlug(currentKey, ciphertext, f);
            plugs = plugs.isEmpty() ? nextPlug : plugs + " " + nextPlug;
        }

        currentKey.plugboard = plugs;
        // Calculate fitness and return scored key
        Enigma e = new Enigma(currentKey);
        char[] decryption = e.encrypt(ciphertext);
        return new ScoredEnigmaKey(currentKey, f.score(decryption));
    }
}
