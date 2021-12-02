package com.projects.enigma;

import com.projects.Main;
import com.projects.analysis.EnigmaKey;

public class Enigma {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
    // A B C D E F G H I J K  L  M  N  O  P  Q  R  S  T  U  V  W  X  Y  Z
    public Rotor leftRotor;
    public Rotor middleRotor;
    public Rotor rightRotor;

    public Reflector reflector;

    public Plugboard plugboard;

    public EnigmaKey tempKey;

    /**
     * Sets rotor positions by creating new rotors and settings
     * 
     * @param rotors
     * @param reflector
     * @param rotorPositions
     * @param ringSettings
     * @param plugboardConnections
     */

    public Enigma(String[] rotors, String reflector, int[] rotorPositions, int[] ringSettings,
            String plugboardConnections) {
        this.leftRotor = Rotor.Create(rotors[0], rotorPositions[0], ringSettings[0]);
        this.middleRotor = Rotor.Create(rotors[1], rotorPositions[1], ringSettings[1]);
        this.rightRotor = Rotor.Create(rotors[2], rotorPositions[2], ringSettings[2]);
        this.reflector = Reflector.Create(reflector);
        this.plugboard = new Plugboard(plugboardConnections);
    }

    /**
     * creates the settings and sets the positions
     * 
     * @param rotors
     * @param reflector
     * @param reflectorPosition
     * @param rotorPositions
     * @param ringSettings
     * @param plugboardConnections
     */
    public Enigma(String[] rotors, String reflector, int reflectorPosition, int[] rotorPositions, int[] ringSettings, String plugboardConnections)
    {
        this.leftRotor = Rotor.Create(rotors[0], rotorPositions[0], ringSettings[0]);
        this.middleRotor = Rotor.Create(rotors[1], rotorPositions[1], ringSettings[1]);
        this.rightRotor = Rotor.Create(rotors[2], rotorPositions[2], ringSettings[2]);
        this.reflector = Reflector.CreateG(reflectorPosition);
        this.plugboard = new Plugboard(plugboardConnections);
    }

    public Enigma(EnigmaKey key)
    {
        this(key.rotors, "B", key.indicators, key.rings, key.plugboard);

    }
    
    

/**
 * turns rotors over when we reach a notch, M3 contains a double-step
 * turns rotors over when we reach a notch, Abwehr does not contain a double-step but reflector steps when the left most rotor reaches a notch
 */
    public void rotate(){
        if(Main.machineModel.equalsIgnoreCase("M3")){
            if(middleRotor.isAtNotch()){
                middleRotor.turnover();
                leftRotor.turnover();
            }
            else if(rightRotor.isAtNotch())
            {
                middleRotor.turnover();
            }

            rightRotor.turnover();
        }
        else if(Main.machineModel.equalsIgnoreCase("Abwehr")){
            if(middleRotor.isAtNotch()){
                leftRotor.turnover();
            }
            else if(rightRotor.isAtNotch())
            {
                middleRotor.turnover();
            }
            else if(leftRotor.isAtNotch()){
                reflector.turnover();
            }

            rightRotor.turnover();
        }
    }
    

    /**
     * This is the encrypt method and it helps tell the rotors to rotate based on settings.
     * 
     * @param c
     * @return
     */
    public int encrypt(int c) {
        rotate();

        // Plugboard in
        c = this.plugboard.forward(c);

        // Right to left
        int c1 = rightRotor.forward(c);
        int c2 = middleRotor.forward(c1);
        int c3 = leftRotor.forward(c2);

        // Reflector
        int c4 = reflector.forward(c3);

        // Left to right
        int c5 = leftRotor.backward(c4);
        int c6 = middleRotor.backward(c5);
        int c7 = rightRotor.backward(c6);

        // Plugboard out
        c7 = plugboard.forward(c7);

        return c7;
    }

    /**
     * 
     * 
     * @param c
     * @return
     */
    public char encrypt(char c) {
        return (char) (this.encrypt(c - 65) + 65);
    }

    /**
     * 
     * 
     * @param input
     * @return
     */
    public char[] encrypt(char[] input) {
        char[] output = new char[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = this.encrypt(input[i]);
        }
        return output;
    }
}
