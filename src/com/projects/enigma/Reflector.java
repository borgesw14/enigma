package com.projects.enigma;

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

    /**
     * creates an abwehr enigma reflector sorting its initial position
     * 
     * @param Position
     * @return
     */
    public static Reflector CreateG(int Position) {
            return new Reflector("RULQMZJSYGOCETKWDAHNBXPVIF", Position);
        
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
        {
            return encipher(c, reflectorPosition, 0, this.forwardWiring);
        }
        else
            return this.forwardWiring[c];
    }

    /**
     * advances reflector position by 1
     */
    public void turnover() {
        //shiftEncoding(this.forwardWiring);
            this.reflectorPosition = (this.reflectorPosition + 1) % 26;
    }

    /**
     * returns character 
     * 
     * @return
     */
    public int getPosition(){
        return this.reflectorPosition;
    }

    protected static int encipher(int k, int pos, int ring, int[] mapping) {
        int shift = pos - ring;
        return (mapping[(k + shift + 26) % 26] - shift + 26) % 26;
    }

    /**
     * array shifter in case it comes in handy in the future for stepping
     */
    /*
    protected void shiftEncoding(int[] forwardWiring)
    {
        int[] shiftedArray = forwardWiring;
        int lastIndex  = shiftedArray[shiftedArray.length-1];



        for(int i = shiftedArray.length-2; i >= 0 ; i--){
            shiftedArray[i+1] = shiftedArray[i]; 
        }

        shiftedArray[0] = lastIndex;

        this.forwardWiring = shiftedArray;

    }
    */
}
