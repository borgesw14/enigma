package com.demo.crypto.enigma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import com.demo.crypto.enigma.discriminator.Discriminator;
import com.demo.crypto.enigma.tools.VigenereCracker;
import com.demo.crypto.enigma.tools.VigenereDecoder;
import com.demo.crypto.enigma.tools.VigenereKeywordLength;
import com.demo.crypto.enigma.model.EnigmaMachine;
import com.demo.crypto.enigma.model.SteckerCable;
import com.demo.crypto.enigma.util.ConfigurationUtil;
import com.demo.crypto.enigma.util.SteckerCombinationTracker;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class Driver {
    private static final String senderEmail = "codeenigma2021@gmail.com";// change with your sender email
    private static final String senderPassword = "zzzzclgypbsekhtp";// change with your sender password
    public ArrayList<Character> encodedMsg = new ArrayList<Character>();
    public ArrayList<String> testKeys = new ArrayList<String>();
    public String startPlainText = "";

    /**
     * 
     * 
     * @param args
     * @throws IOException
     * @throws InterruptedException
     * @throws MessagingException
     */
    public static void main(String[] args) throws IOException, InterruptedException, MessagingException {
        String fileInPath = "";
        String fileOutName = "";
        String DesiredOut = "";
        String decoded = "";

        VigenereDecoder iDecoder = new VigenereDecoder();
        VigenereCracker vc = new VigenereCracker();
        VigenereKeywordLength vkl = new VigenereKeywordLength();
        Discriminator discriminator = new Discriminator();
        Scanner usrIn = new Scanner(System.in);
        Driver driver = new Driver();

        // read file in
        fileInPath = args[0];
        reader(fileInPath, driver);

        // set file out
        DesiredOut = args[1];

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // String startPlainText = getStartPlainText(reader);

        char[] initialPositions = getInitialPositions(reader);

        List<SteckerCable> steckeredPairs = getSteckeredPairs(reader);

        EnigmaMachine enigmaMachine = new EnigmaMachine(initialPositions, steckeredPairs);

        String cipherText;

        System.out.println("Encrypt with Enigma?(Y, N)");
        while (true) {
            System.out.print("  => ");
            String input = reader.readLine().trim();

            if (input.equals("Y")) {
                System.out.println("\nEncrypting the chosen plain text with rotor positions "
                        + Arrays.toString(initialPositions) + " and steckered pairs: " + steckeredPairs);
                for (int timer = 0; timer < driver.startPlainText.length(); timer++) {
                    try {
                        System.out.print("* ");
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                cipherText = enigmaMachine.encrypt(driver.startPlainText);
                break;
            } else {
                cipherText = driver.startPlainText;
                break;
            }
        }

        // get key lengths

        ArrayList<ArrayList<String>> sequences = vkl.getSequences(cipherText.toLowerCase());
        double avgICValues[] = new double[VigenereKeywordLength.numOfSequences - 1];

        for (int i = 0; i < sequences.size(); i++) {
            avgICValues[i] = vkl.getAvgIC(sequences.get(i));
        }

        ArrayList<Integer> probableKeylengths = vkl.getProbableKeyLengths(avgICValues);
        // Run discriminator
        String result = discriminator.Discriminate(probableKeylengths);
        if (result.equals(Discriminator.CODE_ENIGMA)) {
            System.out.println("\nReciepient Email Address: ");

            System.out.print("  => ");
            String emailinput = reader.readLine().trim();

            System.out.println("\n\nWe encrypted your plain text to " + cipherText
                    + ". Now, let's see if we can decrypt it back by re-discovering the rotor and stecker settings (these settings are Enigma's encryption 'key').");

            int threadCount = getThreadCount(reader);
            System.out.println("\nHere we go ...\n");

            String endPlainText;

            List<EnigmaBreaker> enigmaBreakers = new ArrayList<>();
            for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
                EnigmaBreaker enigmaBreaker = new EnigmaBreaker(cipherText, steckeredPairs.size(), threadIndex,
                        threadCount);
                enigmaBreakers.add(enigmaBreaker);
                enigmaBreaker.start();
            }

            char[] solvedPositions = null;
            List<SteckerCable> solvedSteckeredPairs = null;

            while (true) {
                Thread.sleep(4000);

                EnigmaBreaker stoppedEnigmaBreaker = null;
                boolean stillRunning = false;
                for (EnigmaBreaker enigmaBreaker : enigmaBreakers) {
                    if (enigmaBreaker.getSolvedPositions() != null) {
                        stoppedEnigmaBreaker = enigmaBreaker;
                        break;
                    }
                }

                if (stoppedEnigmaBreaker == null) {
                    for (EnigmaBreaker enigmaBreaker : enigmaBreakers) {
                        if (enigmaBreaker.isAlive()) {
                            stillRunning = true;
                            break;
                        }
                    }
                }

                if (stoppedEnigmaBreaker != null) {
                    System.out.println(
                            stoppedEnigmaBreaker + " has returned with results! interrupting other threads now.\n");

                    for (EnigmaBreaker enigmaBreaker : enigmaBreakers) // first interrupt any remaining live threads
                        enigmaBreaker.interrupt();

                    // then pull the solved positions info from the thread that returned
                    // successfully
                    solvedPositions = stoppedEnigmaBreaker.getSolvedPositions();
                    solvedSteckeredPairs = stoppedEnigmaBreaker.getSolvedSteckeredPairs();
                    break;
                } else if (!stillRunning) {
                    break;
                }
            }

            if (solvedPositions == null) {
                System.out.println(
                        "\nno solution found! perhaps this sample message matches multiple cribs, and the breaker chose the wrong one?\n\n");
                Driver.sendAsHtml(emailinput, "No solution found!",
                        "<h2>Enigma No Solution! Perhaps this sample message matches multiple cribs, and the breaker chose the wrong one?</h2>");
            } else {
                enigmaMachine.setRotors(solvedPositions);
                enigmaMachine.setSteckers(solvedSteckeredPairs);
                endPlainText = enigmaMachine.decrypt(cipherText);
                System.out.println("End plain text: " + endPlainText + "\n\n");
                fileOutName = DesiredOut;
                if (!fileOutName.contains(".txt"))
                    fileOutName = fileOutName + ".txt";
                // fileWrite(driver.testKeys.get(i), decoded, "result/" + fileOutName + "key" +
                // (i + 1));
                fileWrite("Enigma", endPlainText, "src/main/java/com/demo/crypto/result/" + fileOutName);
                Driver.sendAsHtml(emailinput, "Solution found!", "<p>End plain text: " + endPlainText + "\n</p>");
            }

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
                driver.testKeys = keys;
                for (int i = 0; i < driver.testKeys.size(); i++) {
                    decoded = iDecoder.decoder(driver.encodedMsg, driver.testKeys.get(i));
                    fileOutName = DesiredOut;
                    // check if file name has .txt typing if not, add .txt
                    if (!fileOutName.contains(".txt"))
                        fileOutName = fileOutName + ".txt";
                    // fileWrite(driver.testKeys.get(i), decoded, "result/" + fileOutName + "key" +
                    // (i + 1));
                    fileWrite(driver.testKeys.get(i), decoded, "src/main/java/com/demo/crypto/result/" + fileOutName);
                }
            }

            usrIn.close();

        }

    }

    public static void reader(String filePath, Driver driver) {
        File f = new File(filePath); // Creation of File Descriptor for input file
        FileReader fr;
        try {
            fr = new FileReader(f); // Creation of File Reader object
            BufferedReader br = new BufferedReader(fr); // Creation of BufferedReader object
            int c = 0;
            try {
                while ((c = br.read()) != -1) // Read char by Char
                {
                    char character = (char) c; // converting integer to char

                    if (Character.isLetter(character)) {
                        driver.encodedMsg.add(Character.toLowerCase(character));
                        driver.startPlainText += character;
                    } else
                        continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(driver.startPlainText);

    }

    public static void fileWrite(String key, String plainText, String fileOutPath) {

        try {
            File myObj = new File(fileOutPath);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter(fileOutPath);
            myWriter.write("KEY:" + key + "\n" + "\n"); // adds key to top of file
            myWriter.write(plainText); // writes out decoded text
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    /**
     * Capture the plain text (in German) sample to use for the simulation, either
     * from user input for a set of preset options.
     *
     * @param reader
     * @return a plain text String of the text to be encoded.
     * @throws IOException
     */
    private static String getStartPlainText(final BufferedReader reader) throws IOException {
        String startPlainText = null;

        // final File sampleMessageDirectory = new
        // File("src/main/resources/sample/message");
        // final File[] sampleMessageFiles = sampleMessageDirectory.listFiles();

        final List<Properties> sampleMessages = new ArrayList<>();

        System.out.println("First enter a plain text message, or select a number to choose one of these WWII samples:");

        // for (int index = 0; index < sampleMessageFiles.length; index++) {
        // FileInputStream sampleMessage = new
        // FileInputStream(sampleMessageFiles[index]);
        // Properties properties = new Properties();
        // properties.load(sampleMessage);
        // sampleMessages.add(properties);

        // System.out.println(" " + (index + 1) + ". " +
        // properties.getProperty("plain_text"));
        // System.out.println(" (translation: " +
        // properties.getProperty("translation"));
        // }

        while (true) {
            System.out.print("  => ");
            String input = reader.readLine().trim();

            if (StringUtils.isNotBlank(input)) {
                int sampleMessageChoice = -1;

                if (input.length() == 1) {
                    try {
                        sampleMessageChoice = Integer.parseInt(input);
                        Validate.isTrue(sampleMessageChoice > 0);
                        Validate.isTrue(sampleMessageChoice <= sampleMessages.size());
                        startPlainText = sampleMessages.get(sampleMessageChoice - 1).getProperty("plain_text");
                    } catch (IllegalArgumentException exception) {
                    }
                } else {
                    startPlainText = input;
                }
            }

            if (startPlainText == null) {
                System.out.println(
                        "Please enter a plain text message, or select a number to choose one of the above samples:");
            } else {
                break;
            }
        }

        return startPlainText;
    }

    private static char[] getInitialPositions(final BufferedReader reader) throws IOException {
        char[] initialPositions;

        while (true) {
            System.out.println(
                    "Now, enter a sequence of starting rotor positions, from left rotor to right. for example: ARH. Or, press enter for random positions: ");
            System.out.print("  => ");
            String input = reader.readLine().trim();

            if (StringUtils.isBlank(input)) {
                initialPositions = ConfigurationUtil.generateRandomRotorPositions();
                System.out.println(
                        "Randomly chosen rotor positions to encrypt with: " + Arrays.toString(initialPositions));
                break;
            } else {
                try {
                    initialPositions = ConfigurationUtil.getPositionsFromString(input);
                    System.out.println(
                            "Using your chosen rotor positions to encrypt with: " + Arrays.toString(initialPositions));
                    break;
                } catch (IllegalArgumentException illegalArgumentException) {
                    System.out.println(illegalArgumentException.getMessage());
                }
            }
        }

        return initialPositions;
    }

    private static List<SteckerCable> getSteckeredPairs(final BufferedReader reader) throws IOException {
        List<SteckerCable> steckeredPairs;

        while (true) {
            System.out.println(
                    "Finally, enter a number of stecker cables to use, 0 through 10. Or press enter to use the default ("
                            + SteckerCombinationTracker.DEFAULT_STECKER_PAIR_COUNT + ").");
            System.out.println(
                    " *tip: 0-2 pairs cracks in minutes, 3-5 pairs cracks in hours, 6-10 pairs cracks in days!");
            System.out.print("  => ");
            String input = reader.readLine().trim();
            if (StringUtils.isBlank(input)) {
                System.out.println("Using default of " + SteckerCombinationTracker.DEFAULT_STECKER_PAIR_COUNT
                        + " stecker cables.");
                steckeredPairs = ConfigurationUtil
                        .generateRandomSteckers(SteckerCombinationTracker.DEFAULT_STECKER_PAIR_COUNT);
                break;
            } else {
                try {
                    steckeredPairs = ConfigurationUtil.generateRandomSteckers(input);
                    System.out.println("Using your chosen stecker count: " + input);
                    break;
                } catch (IllegalArgumentException illegalArgumentException) {
                    System.out.println(illegalArgumentException.getMessage());
                }
            }
        }

        return steckeredPairs;
    }

    private static int getThreadCount(final BufferedReader reader) throws IOException {
        int threadCount = 1;

        while (true) {
            System.out.println(
                    "Enter the number of parallel threads you'd like to use for for breaking the cipher; 1, 2, 4, or 8. Or press enter to use the default (1). ");
            System.out.println(" *tip: use one thread per CPU core for fastest cipher break.");
            System.out.print("  => ");
            String input = reader.readLine().trim();
            if (StringUtils.isBlank(input)) {
                System.out.println("Using default of 1 thread.");
                break;
            } else {
                try {
                    threadCount = ConfigurationUtil.validateThreadCount(input);
                    System.out.println("Using your chosen thread count: " + threadCount);
                    break;
                } catch (IllegalArgumentException illegalArgumentException) {
                    System.out.println(illegalArgumentException.getMessage());
                }
            }
        }

        return threadCount;
    }

    public static void sendAsHtml(String to, String title, String html) throws MessagingException {
        System.out.println("Sending email to " + to);

        Session session = createSession();

        // create message using session
        MimeMessage message = new MimeMessage(session);
        prepareEmailMessage(message, to, title, html);

        // sending message
        Transport.send(message);
        System.out.println("Done");
    }

    private static void prepareEmailMessage(MimeMessage message, String to, String title, String html)
            throws MessagingException {
        message.setContent(html, "text/html; charset=utf-8");
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(title);
    }

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");// Outgoing server requires authentication
        props.put("mail.smtp.starttls.enable", "true");// TLS must be activated
        props.put("mail.smtp.host", "smtp.gmail.com"); // Outgoing server (SMTP) - change it to your SMTP server
        props.put("mail.smtp.port", "587");// Outgoing port

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        return session;
    }
}
