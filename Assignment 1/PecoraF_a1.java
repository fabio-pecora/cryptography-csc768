package shiftCipherDecrypt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PecoraF_a1 {
	// assignment 1:
	
	
	// Frequency distribution of each letter in the English alphabet
    private static final double[] ENGLISH_FREQUENCIES = {
        0.082, 0.015, 0.028, 0.043, 0.13, 0.022, 0.02, 0.061, 0.07, 0.0015, 0.0077, 
        0.04, 0.024, 0.067, 0.075, 0.019, 0.00095, 0.06, 0.063, 0.091, 0.028, 0.0098, 
        0.024, 0.0015, 0.02, 0.00074
    };

    // This method counts the occurrence of each letter in the ciphertext
    // It helps us determine which letters are most frequent and guess the shift used
    private static Map<Character, Integer> calculateLetterFrequencies(String ciphertext) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : ciphertext.toCharArray()) {
            if (Character.isLetter(c)) {
                c = Character.toUpperCase(c);
                frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
            }
        }
        return frequencyMap;
    }

    // Decrypts the ciphertext using a specific shift value
    private static String decrypt(String ciphertext, int shift) {
        StringBuilder decryptedText = new StringBuilder();
        
        for (char currentChar : ciphertext.toCharArray()) {
            if (Character.isLetter(currentChar)) {
                char base = Character.isLowerCase(currentChar) ? 'a' : 'A';
                int shiftedPosition = (currentChar - base - shift + 26) % 26;
                char decryptedChar = (char) (base + shiftedPosition);
                decryptedText.append(decryptedChar);
            } else {
                decryptedText.append(currentChar);
            }
        }
        return decryptedText.toString();
    }

    // Calculates the chi-squared statistic for a given shift value
    // This helps us find the shift that results in the most readable text
    private static double computeChiSquared(String ciphertext, int shift) {
        String decryptedText = decrypt(ciphertext, shift);
        Map<Character, Integer> frequencyMap = calculateLetterFrequencies(decryptedText);
        int totalLetters = decryptedText.length();
        
        double chiSquaredValue = 0.0;
        for (char c = 'A'; c <= 'Z'; c++) {
            int observedCount = frequencyMap.getOrDefault(c, 0);
            double expectedCount = ENGLISH_FREQUENCIES[c - 'A'] * totalLetters;
            chiSquaredValue += Math.pow(observedCount - expectedCount, 2) / expectedCount;
        }
        return chiSquaredValue;
    }

    // Finds the best shift key by minimizing the chi-squared statistic
    private static int determineBestShift(String ciphertext) {
        double minimumChiSquared = Double.MAX_VALUE;
        int bestShift = 0;

        for (int shift = 0; shift < 26; shift++) {
            double chiSquaredValue = computeChiSquared(ciphertext, shift);
            if (chiSquaredValue < minimumChiSquared) {
                minimumChiSquared = chiSquaredValue;
                bestShift = shift;
            }
        }
        return bestShift;
    }    
    
    // assignment 2:
    public static String readCiphertextFromFile(String filepath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.replaceAll("\\s", "")); // Remove spaces
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // Function to estimate key length using Index of Coincidence
    public static int estimateKeyLength(String text) {
        int maxKeyLength = 10; // maximum key length to test
        double maxIC = 0;
        int bestLength = 1;

        for (int length = 1; length <= maxKeyLength; length++) {
            double ic = calculateIC(text, length);
            System.out.println("Key Length " + length + ": IC = " + ic); // Debugging IC value
            if (ic > maxIC) {
                maxIC = ic;
                bestLength = length;
            }
        }
        return bestLength;
    }

    public static double calculateIC(String text, int length) {
        String[] substrings = splitByKeyLength(text, length);
        double ic = 0;

        for (String substring : substrings) {
            Map<Character, Integer> freqMap = new HashMap<>();
            int totalChars = 0;

            for (char c : substring.toCharArray()) {
                if (Character.isLetter(c)) {
                    c = Character.toUpperCase(c);
                    freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
                    totalChars++;
                }
            }

            for (int count : freqMap.values()) {
                double freq = (double) count / totalChars;
                ic += freq * freq;
            }
        }

        return ic;
    }

    public static String[] splitByKeyLength(String text, int length) {
        String[] substrings = new String[length];
        for (int i = 0; i < length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < text.length(); j += length) {
                sb.append(text.charAt(j));
            }
            substrings[i] = sb.toString();
        }
        return substrings;
    }

    // Function to find the key using frequency analysis
    public static String findKey(String text, int keyLength) {
        StringBuilder key = new StringBuilder();
        String[] substrings = splitByKeyLength(text, keyLength);

        for (String substring : substrings) {
            char keyChar = findKeyChar(substring);
            System.out.println("Substring: " + substring + ", Key Char: " + keyChar); // Debugging key character
            key.append(keyChar);
        }

        return key.toString();
    }

    public static char findKeyChar(String text) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                c = Character.toUpperCase(c);
                freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
            }
        }

        char mostFrequent = 'A';
        int maxCount = 0;
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequent = entry.getKey();
            }
        }

        return (char) ((mostFrequent - 'E' + 26) % 26 + 'A');
    }

    // Function to decrypt the ciphertext using the key
    public static String decryptVigenere(String ciphertext, String key) {
        StringBuilder plaintext = new StringBuilder();
        key = key.toUpperCase();
        int keyLength = key.length();
        int keyIndex = 0;

        for (char c : ciphertext.toCharArray()) {
            if (Character.isLetter(c)) {
                char keyChar = key.charAt(keyIndex);
                char decryptedChar = (char) ((c - keyChar + 26) % 26 + 'A');
                plaintext.append(decryptedChar);
                keyIndex = (keyIndex + 1) % keyLength;
            } else {
                plaintext.append(c);
            }
        }

        return plaintext.toString();
    }
    
    
    // assignment 3 
    
 // Encrypts plaintext using a bytewise shift cipher
    private static void encryptBytewise(String plaintextFile, String keyFile, String ciphertextFile) throws IOException {
        // Read plaintext
        String plaintext = readTextFile(plaintextFile);
        // Read key
        int key = Integer.parseInt(readTextFile(keyFile), 16);
        
        StringBuilder ciphertext = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            int shifted = (c + key) % 256; // Assuming bytewise shift
            ciphertext.append(String.format("%02X", shifted));
        }
        
        writeTextFile(ciphertextFile, ciphertext.toString());
    }

    // Decrypts ciphertext using a bytewise shift cipher
    private static void decryptBytewise(String ciphertextFile, String keyFile, String decryptedTextFile) throws IOException {
        // Read ciphertext
        String ciphertext = readTextFile(ciphertextFile);
        // Read key
        int key = Integer.parseInt(readTextFile(keyFile), 16);

        StringBuilder plaintext = new StringBuilder();
        for (int i = 0; i < ciphertext.length(); i += 2) {
            int byteValue = Integer.parseInt(ciphertext.substring(i, i + 2), 16);
            int shifted = (byteValue - key + 256) % 256; // Reverse the shift
            plaintext.append((char) shifted);
        }

        writeTextFile(decryptedTextFile, plaintext.toString());
    }

    // Reads a text file and returns its content as a string
    private static String readTextFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

    // Writes a string to a text file
    private static void writeTextFile(String filename, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        }
    }
    
    private static String readFileContent(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
   
    private static void printFileContent(String filename) throws IOException {
        String content = readFileContent(filename);
        System.out.println(content);
    }
    public static void main(String[] args) {
        try {
            // Assignment 1
        	System.out.println("Assignment 1");
            String shiftCiphertext = readCiphertextFromFile("C:\\Users\\fabio\\eclipse-workspace\\shiftCipherDecrypt\\src\\shiftCipherDecrypt\\shift_ciphertext.txt");
            int bestShift = determineBestShift(shiftCiphertext);
            String decryptedShiftText = decrypt(shiftCiphertext, bestShift);
            System.out.println("Decrypted Shift Cipher Text: \n" + decryptedShiftText +"\n");

            // Assignment 2
            System.out.println("Assignment 2");

            // Read ciphertext from file
            String filepath = "C:\\Users\\fabio\\eclipse-workspace\\shiftCipherDecrypt\\src\\shiftCipherDecrypt\\vigenere_ciphertext.txt";
            String vigenereCiphertext = readCiphertextFromFile(filepath);

            // Estimate key length
            int keyLength = estimateKeyLength(vigenereCiphertext);
            System.out.println("Estimated key length for the Vigenère cipher: " + keyLength);

            // Find the key
            String vigenereKey = findKey(vigenereCiphertext, keyLength);
            System.out.println("Recovered key for the Vigenère cipher: " + vigenereKey);

            // Decrypt the ciphertext
            String decryptedVigenereText = decryptVigenere(vigenereCiphertext, vigenereKey);
            System.out.println("Decrypted Vigenère Cipher Text: \n" + decryptedVigenereText + "\n");
            
            
            
            // Assignment 3
            System.out.println("Assignment 3");
            encryptBytewise("C:\\Users\\fabio\\eclipse-workspace\\shiftCipherDecrypt\\src\\shiftCipherDecrypt\\plaintext.txt", "C:\\Users\\fabio\\eclipse-workspace\\shiftCipherDecrypt\\src\\shiftCipherDecrypt\\key.txt", "C:\\Users\\fabio\\eclipse-workspace\\shiftCipherDecrypt\\src\\shiftCipherDecrypt\\ciphertext.txt");
            System.out.println("Encrypted Ciphertext:");
            printFileContent("C:\\Users\\fabio\\eclipse-workspace\\shiftCipherDecrypt\\src\\shiftCipherDecrypt\\ciphertext.txt");
            decryptBytewise("C:\\Users\\fabio\\eclipse-workspace\\shiftCipherDecrypt\\src\\shiftCipherDecrypt\\ciphertext.txt", "C:\\Users\\fabio\\eclipse-workspace\\shiftCipherDecrypt\\src\\shiftCipherDecrypt\\key.txt", "C:\\Users\\fabio\\eclipse-workspace\\shiftCipherDecrypt\\src\\shiftCipherDecrypt\\decrypted_plaintext.txt");
            System.out.println("Decrypted Plaintext:");
            printFileContent("C:\\Users\\fabio\\eclipse-workspace\\shiftCipherDecrypt\\src\\shiftCipherDecrypt\\decrypted_plaintext.txt");
                        
        	} catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
