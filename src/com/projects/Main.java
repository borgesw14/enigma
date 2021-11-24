package com.projects;

import com.projects.analysis.EnigmaAnalysis;
// import com.projects.analysis.EnigmaKey;
import com.projects.analysis.ScoredEnigmaKey;
import com.projects.analysis.fitness.*;
import com.projects.enigma.Enigma;
import com.projects.discriminator.Discriminator;
import com.projects.tools.VigenereCracker;
import com.projects.tools.VigenereDecoder;
import com.projects.tools.VigenereKeywordLength;

import java.util.ArrayList;
import java.util.Scanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

	// TODO:
	/*
	 * We need to add the email notif,
	 * switch for german or english, german and english engrams need to be updated
	 */

	public static void main(String[] args) throws IOException{
		Scanner sc = new Scanner(System.in);

		VigenereDecoder vd = new VigenereDecoder();
		VigenereCracker vc = new VigenereCracker();
		VigenereKeywordLength vkl = new VigenereKeywordLength();
		Discriminator discriminator = new Discriminator();

		FitnessFunction ioc = new IoCFitness();
		FitnessFunction bigrams = new BigramFitness();
		FitnessFunction quadgrams = new QuadramFitness();

		final long startTime = System.currentTimeMillis();

		// For those interested, these were the original settings
		// II V III / 7 4 19 / 12 2 20 / AF TV KO BL RW

		Path filePath = Path.of(args[0]);

		String encipheredString = Files.readString(filePath);
		encipheredString = encipheredString.toUpperCase();

		System.out.println("Encrypted String: ");
	    System.out.println(encipheredString+ "\n");

		ArrayList<Character> encodedMsg = new ArrayList<Character>();

		for (Character character : encipheredString.toLowerCase().toCharArray()) {
			encodedMsg.add(character);
		}

		ArrayList<ArrayList<String>> sequences = vkl.getSequences(encipheredString.toLowerCase());
		double avgICValues[] = new double[VigenereKeywordLength.numOfSequences - 1];

		for (int i = 0; i < sequences.size(); i++) {
			avgICValues[i] = vkl.getAvgIC(sequences.get(i));
		}

		ArrayList<Integer> probableKeylengths = vkl.getProbableKeyLengths(avgICValues);
		// Run discriminator
		String result = discriminator.Discriminate(probableKeylengths);

		if (result.equals(Discriminator.CODE_ENIGMA)) {

			//M3(encipheredString, ioc, bigrams, quadgrams);
			//System.out.println("now we try to decrypt with the abwehr engima:\n");
			abwehr(encipheredString, ioc, bigrams, quadgrams);			

		} else if (!result.equals(Discriminator.CODE_ENIGMA)) {
			// Decipher for Vigenere cipher

			// for each probable key length add an arraylist of keys to keyList
			ArrayList<ArrayList<String>> keyList = new ArrayList<ArrayList<String>>();
			for (Integer integer : probableKeylengths) {
				for (int i = 0; i < sequences.size(); i++) {
					/*
					 * if the number of sequences matches the probable key length pass the sequences
					 * array to vc.getkeys and expect an array of probable keys in return
					 */
					if (sequences.get(i).size() == integer) {

						keyList.add(vc.getKeys(sequences.get(i)));
					}
				}
			}

			for (ArrayList<String> keys : keyList) {
				for (int i = 0; i < keys.size(); i++) {
					String decoded = vd.decoder(encodedMsg, keys.get(i));
					// System.out.println("Key: " + keys);
					// System.out.println(decoded);

					PrintOut(("Key: "+ keys + "\n"+ decoded), sc);
				}
			}
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime));
	}

	public static void PrintOut(String s, Scanner sc) throws IOException{
		System.out.println("Enter output path: ");
		Path path = Path.of(sc.nextLine());
		Files.writeString(path, s);
	}

	public static void M3(String encipheredString, FitnessFunction ioc, FitnessFunction bigrams, FitnessFunction quadgrams) throws IOException{
		char[] ciphertext = encipheredString.toCharArray();

			// Begin by finding the best combination of rotors and start positions (returns
			// top n)
			ScoredEnigmaKey[] rotorConfigurations = EnigmaAnalysis.findRotorConfiguration(ciphertext,
					EnigmaAnalysis.AvailableRotors.FIVE, "", 10, ioc);

			System.out.println("\nTop 10 rotor configurations:");
			for (ScoredEnigmaKey key : rotorConfigurations) {
				System.out.println(String.format("%s %s %s / %d %d %d / %f", key.rotors[0], key.rotors[1],
						key.rotors[2], key.indicators[0], key.indicators[1], key.indicators[2], key.getScore()));
			}
			System.out.println(String.format("Current decryption: %s\n",
					new String(new Enigma(rotorConfigurations[0]).encrypt(ciphertext))));

			// Next find the best ring settings for the best configuration (index 0)
			ScoredEnigmaKey rotorAndRingConfiguration = EnigmaAnalysis.findRingSettings(rotorConfigurations[0],
					ciphertext, bigrams);

			System.out.println(String.format("Best ring settings: %d %d %d", rotorAndRingConfiguration.rings[0],
					rotorAndRingConfiguration.rings[1], rotorAndRingConfiguration.rings[2]));
			System.out.println(String.format("Current decryption: %s\n",
					new String(new Enigma(rotorAndRingConfiguration).encrypt(ciphertext))));

			// Finally, perform hill climbing to find plugs one at a time
			ScoredEnigmaKey optimalKeyWithPlugs = EnigmaAnalysis.findPlugs(rotorAndRingConfiguration, 5, ciphertext,
					quadgrams);
			System.out.println(String.format("Best plugboard: %s", optimalKeyWithPlugs.plugboard));
			System.out.println(String.format("Final decryption: %s\n",
					new String(new Enigma(optimalKeyWithPlugs).encrypt(ciphertext))));
			PrintOut(new String(new Enigma(optimalKeyWithPlugs).encrypt(ciphertext)), new Scanner(System.in));
	}

	
	public static void abwehr(String encipheredString, FitnessFunction ioc, FitnessFunction bigrams, FitnessFunction quadgrams) throws IOException
	{
		char[] ciphertext = encipheredString.toCharArray();

			// Begin by finding the best combination of rotors and start positions (returns
			// top n)
			ScoredEnigmaKey[] rotorConfigurations = EnigmaAnalysis.findRotorConfiguration(ciphertext,
					EnigmaAnalysis.AvailableRotors.THREEG, "", 10, ioc);

			System.out.println("\nTop 10 rotor configurations:");
			for (ScoredEnigmaKey key : rotorConfigurations) {
				System.out.println(String.format("%s %s %s / %d %d %d / %f", key.rotors[0], key.rotors[1],
						key.rotors[2], key.indicators[0], key.indicators[1], key.indicators[2], key.getScore()));
			}
			System.out.println(String.format("Current decryption: %s\n",
					new String(new Enigma(rotorConfigurations[0]).encrypt(ciphertext))));

			// Next find the best ring settings for the best configuration (index 0)
			ScoredEnigmaKey rotorAndRingConfiguration = EnigmaAnalysis.findRingSettings(rotorConfigurations[0],
					ciphertext, bigrams);

			System.out.println(String.format("Best ring settings: %d %d %d", rotorAndRingConfiguration.rings[0],
			rotorAndRingConfiguration.rings[1], rotorAndRingConfiguration.rings[2]));
			System.out.println(String.format("Current decryption: %s\n",
					new String(new Enigma(rotorAndRingConfiguration).encrypt(ciphertext))));

			// Finally, perform hill climbing to find plugs one at a time
			ScoredEnigmaKey optimalKeyWithPlugs = EnigmaAnalysis.findPlugs(rotorAndRingConfiguration, 5, ciphertext,
					quadgrams);
			System.out.println(String.format("Best plugboard: %s", optimalKeyWithPlugs.plugboard));
			System.out.println(String.format("Final decryption: %s\n",
					new String(new Enigma(optimalKeyWithPlugs).encrypt(ciphertext))));
			PrintOut(new String(new Enigma(optimalKeyWithPlugs).encrypt(ciphertext)), new Scanner(System.in));
	}
}
