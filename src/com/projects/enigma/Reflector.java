package com.projects.enigma;

import javax.swing.text.Position;

import com.projects.Main;

public class Reflector {
    protected int[] forwardWiring;
    protected int reflectorPosition;


    public Reflector(String encoding) {
        this.forwardWiring = decodeWiring(encoding);
    }

    public Reflector(String encoding, int position){
        this.forwardWiring = decodeWiring(encoding);
        this.reflectorPosition = position;
    }

    
    public static Reflector Create(String name) {
        switch (name) {
        case "B":
            return new Reflector("YRUHQSLDPXNGOKMIEBFZCWVJAT");
        case "C":
            return new Reflector("FVPJIAOYEDRZXWGCTKUQSBNMHL");
        case "G":
            return new Reflector("RULQMZJSYGOCETKWDAHNBXPVIF");
        default:
            return new Reflector("ZYXWVUTSRQPONMLKJIHGFEDCBA");
        }
    }

    public static Reflector CreateG(int Position) {
            return new Reflector("RULQMZJSYGOCETKWDAHNBXPVIF",Position);
        
        }

    protected static int[] decodeWiring(String encoding) {
        char[] charWiring = encoding.toCharArray();
        int[] wiring = new int[charWiring.length];
        for (int i = 0; i < charWiring.length; i++) {
            wiring[i] = charWiring[i] - 65;
        }
        return wiring;
    }

    public int forward(int c) {
        if(Main.machineModel.equalsIgnoreCase("Abwehr"))
            return encipher(c, this.reflectorPosition, 0, this.forwardWiring);
        else
            return this.forwardWiring[c];
    }

    public void turnover() {
        this.reflectorPosition = (this.reflectorPosition + 1) % 26;
    }

    protected static int encipher(int k, int pos, int ring, int[] mapping) {
        int shift = pos - ring;
        return (mapping[(k + shift + 26) % 26] - shift + 26) % 26;
    }

    public int getPosition(){
        return reflectorPosition;
    }

}
