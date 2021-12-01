package com.projects.analysis.fitness;

// import java.io.*;
// import java.nio.charset.StandardCharsets;
// import java.util.Arrays;
// import java.util.stream.Stream;

public abstract class FitnessFunction {
    protected final float epsilon = 3e-10f;
    public static final String ENG = "english";
    public static final String GER = "german";

    public float score(char[] text) {
        return 0f;
    }
}
