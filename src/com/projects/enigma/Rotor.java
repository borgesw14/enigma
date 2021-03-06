package com.projects.enigma;

public class Rotor {
    protected String name;
    protected int[] forwardWiring;
    protected int[] backwardWiring;

    protected int rotorPosition;
    protected int notchPosition;
    protected int ringSetting;
    protected int[] notchPositions;

    /**
     * rotor without arrays
     * 
     * @param name
     * @param encoding
     * @param rotorPosition
     * @param notchPosition
     * @param ringSetting
     */
    public Rotor(String name, String encoding, int rotorPosition, int notchPosition, int ringSetting) {
        this.name = name;
        this.forwardWiring = decodeWiring(encoding);
        this.backwardWiring = inverseWiring(this.forwardWiring);
        this.rotorPosition = rotorPosition;
        this.notchPosition = notchPosition;
        this.ringSetting = ringSetting;
    }

    /**
     * creates rotor with arrays to input settings for another method
     * 
     * @param name
     * @param encoding
     * @param rotorPosition
     * @param notchPositions
     * @param ringSetting
     */
    public Rotor(String name, String encoding, int rotorPosition, int[] notchPositions, int ringSetting) {
        this.name = name;
        this.forwardWiring = decodeWiring(encoding);
        this.backwardWiring = inverseWiring(this.forwardWiring);
        this.rotorPosition = rotorPosition;
        this.notchPositions = notchPositions;
        this.ringSetting = ringSetting;
    }

    /**
     * creates different rotors with the settings
     * 
     * @param name
     * @param rotorPosition
     * @param ringSetting
     * @return
     */
    public static Rotor Create(String name, int rotorPosition, int ringSetting) {
        int[] notches;
        switch (name) {
        case "I":
            return new Rotor("I", "EKMFLGDQVZNTOWYHXUSPAIBRCJ", rotorPosition, 16, ringSetting);
        case "II":
            return new Rotor("II", "AJDKSIRUXBLHWTMCQGZNPYFVOE", rotorPosition, 4, ringSetting);
        case "III":
            return new Rotor("III", "BDFHJLCPRTXVZNYEIWGAKMUSQO", rotorPosition, 21, ringSetting);
        case "IV":
            return new Rotor("IV", "ESOVPZJAYQUIRHXLNFTGKDCMWB", rotorPosition, 9, ringSetting);
        case "V":
            return new Rotor("V", "VZBRGITYUPSDNHLXAWMJQOFECK", rotorPosition, 25, ringSetting);
        case "VI":
            return new Rotor("VI", "JPGVOUMFYQBENHZRDKASXLICTW", rotorPosition, 0, ringSetting) {
                @Override
                public boolean isAtNotch() {
                    return this.rotorPosition == 12 || this.rotorPosition == 25;
                }
            };
        case "VII":
            return new Rotor("VII", "NZJHGRCXMYSWBOUFAIVLPEKQDT", rotorPosition, 0, ringSetting) {
                @Override
                public boolean isAtNotch() {
                    return this.rotorPosition == 12 || this.rotorPosition == 25;
                }
            };
        case "VIII":
            return new Rotor("VIII", "FKQHTLXOCBJSPDZRAMEWNIUYGV", rotorPosition, 0, ringSetting) {
                @Override
                public boolean isAtNotch() {
                    return this.rotorPosition == 12 || this.rotorPosition == 25;
                }
            };
        case "IG":
            notches = new int[] {0,2,3,4,7,8,9,10,12,13,14,16,18,19,22,23,24};
            return new Rotor("IG", "DMTWSILRUYQNKFEJCAZBPGXOHV", rotorPosition, notches, ringSetting){
                @Override
                public boolean isAtNotch(){
                    return this.rotorPosition == 0 || this.rotorPosition == 2 || this.rotorPosition == 3 || this.rotorPosition == 4 || this.rotorPosition == 7 || this.rotorPosition == 8 || this.rotorPosition == 9 || this.rotorPosition == 10 || this.rotorPosition == 12 || this.rotorPosition == 13 || this.rotorPosition == 14 || this.rotorPosition == 16 || this.rotorPosition == 18 || this.rotorPosition == 19 || this.rotorPosition == 22 || this.rotorPosition == 23 || this.rotorPosition == 24;
                }
            };
        case "IIG":
            notches = new int[] {0,1,3,6,7,8,10,11,13,14,16,18,21,22,24};
            return new Rotor("IIG", "HQZGPJTMOBLNCIFDYAWVEUSRKX", rotorPosition, notches, ringSetting){
                @Override
                public boolean isAtNotch(){
                    return this.rotorPosition == 0 || this.rotorPosition == 1 || this.rotorPosition == 3 || this.rotorPosition == 6 || this.rotorPosition == 7 || this.rotorPosition == 8 || this.rotorPosition == 10 || this.rotorPosition == 11 || this.rotorPosition == 13 || this.rotorPosition == 14 || this.rotorPosition == 16 || this.rotorPosition == 18 || this.rotorPosition == 21 || this.rotorPosition == 22 || this.rotorPosition == 24;
                }
            };
        case "IIIG":
            notches = new int[] {2,4,5,8,12,13,15,18,20,21,25};
            return new Rotor("IIIG", "UQNTLSZFMREHDPXKIBVYGJCWOA", rotorPosition, notches, ringSetting){
                @Override
                public boolean isAtNotch(){
                    return this.rotorPosition == 2 || this.rotorPosition == 4 || this.rotorPosition == 5 || this.rotorPosition == 8 || this.rotorPosition == 12 || this.rotorPosition == 13 || this.rotorPosition == 15 || this.rotorPosition == 18 || this.rotorPosition == 20 || this.rotorPosition == 21 || this.rotorPosition == 25;
                }
            };
        default:
            return new Rotor("Identity", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", rotorPosition, 0, ringSetting);
        }
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return rotorPosition;
    }

    /**
     * 
     * 
     * @param encoding
     * @return
     */
    protected static int[] decodeWiring(String encoding) {
        char[] charWiring = encoding.toCharArray();
        int[] wiring = new int[charWiring.length];
        for (int i = 0; i < charWiring.length; i++) {
            wiring[i] = charWiring[i] - 65;
        }
        return wiring;
    }

    /**
     * 
     * 
     * @param wiring
     * @return
     */
    protected static int[] inverseWiring(int[] wiring) {
        int[] inverse = new int[wiring.length];
        for (int i = 0; i < wiring.length; i++) {
            int forward = wiring[i];
            inverse[forward] = i;
        }
        return inverse;
    }

    /**
     * 
     * 
     * @param k
     * @param pos
     * @param ring
     * @param mapping
     * @return
     */
    protected static int encipher(int k, int pos, int ring, int[] mapping) {
        int shift = pos - ring;
        return (mapping[(k + shift + 26) % 26] - shift + 26) % 26;
    }

    /**
     * 
     * 
     * @param c
     * @return
     */
    public int forward(int c) {
        return encipher(c, this.rotorPosition, this.ringSetting, this.forwardWiring);
    }

    /**
     * 
     * 
     * @param c
     * @return
     */
    public int backward(int c) {
        return encipher(c, this.rotorPosition, this.ringSetting, this.backwardWiring);
    }

    /**
     * 
     * 
     * @return
     */
    public boolean isAtNotch() {
        return this.notchPosition == this.rotorPosition;
    }

    /**
     * 
     */
    public void turnover() {
        this.rotorPosition = (this.rotorPosition + 1) % 26;
    }

}
