package first_assignment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PecoraF_a1 {
	
	private static final String CIPHER_TEXT = "KWSVVSYXKSBOKRKBNRKDKXNKNBEXUKBOKDKLKBGROXDROIQODDROSBLOOBC"
			+ "DROIXYDSMOKPVISXOKMRWEQDROWSVVSYXKSBOZYVSDOVIKCUCDROLKBDOXN"
			+ "OBPYBKXYDROBLOOBDROXZBYMOONCDYCSZSDDRORKBNRKDCZSVVCYEDTECDO"
			+ "XYEQRDYQODBSNYPDROPVIKXNAEKPPCDROBOCDSDCXYGDRONBEXUCDEBXROC"
			+ "DSMUCRSCRKXNSXDYDROLOOBQBKLCDROPVILIDROGSXQCKXNCRYEDCCZSDSD"
			+ "YEDCZSDSDYEDBOKNOBCNSQOCDPOLBEKBIDGYDRYECKXNDOX";
	
	private static final String VIGENERE_CIPHER_TEXT = "VPTHPDQVSAVVGEPZMEVMCAEKKDTIPFUADTXYGLPF"
			+ "WNGZTSSEIMGVJKGVTUHZPOLPXYOIVUMWKKTUXVXM"
			+ "CPRXUWUJSITCVHXVFXXUOJMQTZXYGPJUXZPOHLEJ"
			+ "QVLHWFXMGDMKJPDBRUUICKKLPAEBXRYINMSIUQMT"
			+ "SEVPHALVXQCLCRTLHDIIGJJZCRIIXUEJVPTDICNW"
			+ "GNEEKHTKJRTUTYWKTMPAIUVPTPVMKVTZEEFBWLQF"
			+ "TMAHGBCLPPWZEIAUIZIPQVVJJCGYMVFBDKSKJMEY"
			+ "YEKVVALVAAWVYCFPPCIUQVTPREQDTTFVT";
	
	private static final double[] FREQUENCY_OF_ENGLISH_LETTER = {
            0.082, 0.015, 0.028, 0.043, 0.127, 0.022, 0.020, 0.061, 0.070, 0.002,
            0.008, 0.040, 0.024, 0.067, 0.075, 0.019, 0.001, 0.060, 0.063, 0.091,
            0.028, 0.010, 0.023, 0.001, 0.020, 0.001
    };
	
	public static void main(String[] args) {
		try {
			// I could have also done it assuming that E is the char more used in the English language
			// assignment 1:
			System.out.println("------------------------------------------------------------ASSIGNMENT 1------------------------------------------------------------");
	        double[] frequencyOfLettersCipherText = getFrequenciesOfLetters(CIPHER_TEXT);
	        int bestShift = findBestShiftKey(frequencyOfLettersCipherText);
	        String decryptedMessage = decrypt(CIPHER_TEXT, bestShift);
	        System.out.println("Recovered Key: " + bestShift);
	        System.out.println("Decrypted Message: " + decryptedMessage);
			
			// assignment 2:
	        System.out.println("------------------------------------------------------------ASSIGNMENT 2------------------------------------------------------------");
	        
	        // I want to first find the frequency of each letter in the string and store in a map
	        Map<Character, Integer> frequencies = new HashMap<>();
	        // At first all the letters have been put in the map with the value 0
	        frequencies = initializeLetterFrequencies();
	        // then we add the frequency
	        frequencies = calculateLetterFrequencies(VIGENERE_CIPHER_TEXT);
	        int totalLetters = VIGENERE_CIPHER_TEXT.length();
	        // I'll now calculate the index of coincidence using the formula:
	        double ic = calculateIC(frequencies, totalLetters);
	        // while testing I noticed that the value is 0.043765872040632425 which makes sense
	        // now I have to divide the string in substrings of length 1 - 10 and check for a value that goes close to 0/65
	        int keySize = analyzeKeyLength(VIGENERE_CIPHER_TEXT);
	        // I'll now get the key (which I noticed it is CIPHER)
	        String key = recoverVigenereKey(VIGENERE_CIPHER_TEXT, keySize);
	        System.out.println("The encrypted text is: \n" + VIGENERE_CIPHER_TEXT + "\n");
	        // and I can finally decrypt
	        String decryptedBidenere = decryptVigenere(VIGENERE_CIPHER_TEXT, key);
	        System.out.println("Thanks to the key size " + keySize + ", which helped us finding out that the key is " + key + ", we can now decrypt the text: \n" + decryptedBidenere);
	        	        
	        // from tis we can notice that length 6 has a IC of 0.06397450126639916 which means that the key is 6
	        // assignment 3
	        System.out.println("------------------------------------------------------------ASSIGNMENT 3------------------------------------------------------------");
	        // encrypting and printing the text that is inside plaintext.txt
	        encryptBytewise("C:\\Users\\fabio\\Desktop\\cryptography-csc768\\assignment_1\\first_assignment\\src\\first_assignment\\plaintext", "C:\\Users\\fabio\\Desktop\\cryptography-csc768\\assignment_1\\first_assignment\\src\\first_assignment\\key.txt", "C:\\Users\\fabio\\Desktop\\cryptography-csc768\\assignment_1\\first_assignment\\src\\first_assignment\\ciphertext.txt");
			System.out.println("Encrypted Ciphertext:");
			printFileContent("C:\\Users\\fabio\\Desktop\\cryptography-csc768\\assignment_1\\first_assignment\\src\\first_assignment\\ciphertext.txt");
			// decryptying and printing the text that has been encrypted
			decryptBytewise("C:\\Users\\fabio\\Desktop\\cryptography-csc768\\assignment_1\\first_assignment\\src\\first_assignment\\ciphertext.txt", "C:\\Users\\fabio\\Desktop\\cryptography-csc768\\assignment_1\\first_assignment\\src\\first_assignment\\key.txt", "C:\\Users\\fabio\\Desktop\\cryptography-csc768\\assignment_1\\first_assignment\\src\\first_assignment\\decrypted_plaintext.txt");
			System.out.println("Decrypted Plaintext:");
			printFileContent("C:\\Users\\fabio\\Desktop\\cryptography-csc768\\assignment_1\\first_assignment\\src\\first_assignment\\decrypted_plaintext.txt");
				        
		} catch (IOException e) {
	        System.err.println("Error reading or writing to a file: " + e.getMessage());
	        e.printStackTrace();
	    } catch (NumberFormatException e) {
	        System.err.println("Error parsing a number: " + e.getMessage());
	        e.printStackTrace();
	    } catch (Exception e) {
	        System.err.println("An unexpected error occurred: " + e.getMessage());
	        e.printStackTrace();
	    }
		
	}
	
	// Assignment 1 methods:
	
	public static double[] getFrequenciesOfLetters(String CIPHER_TEXT) {
		double[] frequency = new double[26];
		int numOfLetters = 0;
		for(int i = 0; i < CIPHER_TEXT.length(); i++) {
			frequency[CIPHER_TEXT.charAt(i) - 'A']++;
			numOfLetters++;
		}
		
		for (int j = 0; j < frequency.length; j++) {
			frequency[j] /= numOfLetters;
        }
		return frequency;
	}
	
	public static double calculateChiSquared(double[] observed, double[] expected) {
	    double chiSquared = 0.0;
	    for (int i = 0; i < 26; i++) {
	        double difference = observed[i] - expected[i];
	        chiSquared += difference * difference / expected[i];
	    }
	    return chiSquared;
	}
	 
    public static int findBestShiftKey(double[] cipherFreq) {
        double minChiSquared = Double.MAX_VALUE;
        int bestKey = 0;

        for (int key = 0; key < 26; key++) {
            double[] shiftedFreq = new double[26];
            for (int i = 0; i < 26; i++) {
                shiftedFreq[i] = cipherFreq[(i + key) % 26];
            }
            double chiSquared = calculateChiSquared(shiftedFreq, FREQUENCY_OF_ENGLISH_LETTER);

            if (chiSquared < minChiSquared) {
                minChiSquared = chiSquared;
                bestKey = key;
            }
        }

        return bestKey;
    }
    
    public static String decrypt(String cipherText, int key) {
        String plainText = ""; 

        for (char ch : cipherText.toCharArray()) {
            if (Character.isLetter(ch)) {
                char base;
                if (Character.isUpperCase(ch)) {
                    base = 'A';
                } else {
                    base = 'a';
                }

                // Decrypt the character
                char decryptedChar = (char) ((ch - base - key + 26) % 26 + base);
                plainText += decryptedChar; 
            } else {
                plainText += ch;
            }
        }

        return plainText;
    }
    
    // Assignment 2 methods:
    
    public static Map<Character, Integer> initializeLetterFrequencies() {
        Map<Character, Integer> frequencies = new HashMap<>();
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            frequencies.put(letter, 0);
        }

        return frequencies;
    }
    
    public static Map<Character, Integer> calculateLetterFrequencies(String text) {
        Map<Character, Integer> frequencies = new HashMap<>();

        for (char letter = 'A'; letter <= 'Z'; letter++) {
            frequencies.put(letter, 0);
        }

        for (char c : text.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                frequencies.put(c, frequencies.get(c) + 1);
            }
        }
        return frequencies;
    }
    
    public static double calculateIC(Map<Character, Integer> frequencies, int totalLetters) {
        double ic = 0.0;
        for (int frequency : frequencies.values()) {
            ic += frequency * (frequency - 1);
        }
        return ic / (totalLetters * (totalLetters - 1));
    }
    
    // we have a frequency method, but this is for the substrings
    public static Map<Character, Integer> letterFrequencies(String substring) {
        Map<Character, Integer> frequencies = new HashMap<>();
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            frequencies.put(letter, 0);
        }
        
        for (char c : substring.toCharArray()) {
            if (Character.isLetter(c)) {
                frequencies.put(c, frequencies.get(c) + 1);
            }
        }
        return frequencies;
    }
    
    public static int analyzeKeyLength(String ciphertext) {
        int bestKeyLength = 1;
        double bestIC = 0.0;

        for (int keyLength = 1; keyLength <= 10; keyLength++) {
            StringBuilder[] substrings = new StringBuilder[keyLength];
            for (int i = 0; i < keyLength; i++) {
                substrings[i] = new StringBuilder();
            }

            for (int i = 0; i < ciphertext.length(); i++) {
                if (Character.isLetter(ciphertext.charAt(i))) { // Ignore non-letters
                    substrings[i % keyLength].append(ciphertext.charAt(i));
                }
            }

            double totalIC = 0;
            for (StringBuilder substring : substrings) {
                Map<Character, Integer> frequencies = letterFrequencies(substring.toString());
                int totalLetters = substring.length();
                if (totalLetters > 1) { 
                    totalIC += calculateIC(frequencies, totalLetters);
                }
            }

            double averageIC = totalIC / keyLength;

            // Check if this is the closest to 0.6
            if (Math.abs(averageIC - 0.06) < Math.abs(bestIC - 0.06)) {
                bestIC = averageIC;
                bestKeyLength = keyLength;
            }
        }

        return bestKeyLength;
    }
    
    public static String recoverVigenereKey(String ciphertext, int keyLength) {
        StringBuilder keyword = new StringBuilder();


        StringBuilder[] substrings = new StringBuilder[keyLength];
        for (int i = 0; i < keyLength; i++) {
            substrings[i] = new StringBuilder();
        }

        for (int i = 0; i < ciphertext.length(); i++) {
            if (Character.isLetter(ciphertext.charAt(i))) {
                substrings[i % keyLength].append(ciphertext.charAt(i));
            }
        }

 
        for (StringBuilder substring : substrings) {
            double[] frequency = getFrequenciesOfLetters(substring.toString());
            int bestShift = findBestShiftKey(frequency);
           
            keyword.append((char) (bestShift + 'A'));
        }

        return keyword.toString();
    }

    public static String decryptVigenere(String ciphertext, String keyword) {
        StringBuilder decryptedText = new StringBuilder();
        int keywordLength = keyword.length();
        int keywordIndex = 0;

        for (char c : ciphertext.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = 'A';
                int shift = keyword.charAt(keywordIndex % keywordLength) - base;
                char decryptedChar = (char) ((c - base - shift + 26) % 26 + base);
                decryptedText.append(decryptedChar);

                keywordIndex++;
            } else {
                decryptedText.append(c);
            }
        }

        return decryptedText.toString();
    }

    
    // Assignment 3 methods:
    // Encrypts plaintext using a bytewise shift cipher
    
    private static void encryptBytewise(String plaintextFile, String keyFile, String ciphertextFile) throws IOException {
        String plaintext = readTextFile(plaintextFile);
        // Read key
        int key = Integer.parseInt(readTextFile(keyFile), 16);
        
        StringBuilder ciphertext = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            int shifted = (c + key) % 256; 
            ciphertext.append(String.format("%02X", shifted));
        }
        
        writeTextFile(ciphertextFile, ciphertext.toString());
    }

    // Decrypts ciphertext using a bytewise shift cipher
    private static void decryptBytewise(String ciphertextFile, String keyFile, String decryptedTextFile) throws IOException {
 
        String ciphertext = readTextFile(ciphertextFile);
        
        int key = Integer.parseInt(readTextFile(keyFile), 16);

        StringBuilder plaintext = new StringBuilder();
        for (int i = 0; i < ciphertext.length(); i += 2) {
            int byteValue = Integer.parseInt(ciphertext.substring(i, i + 2), 16);
            int shifted = (byteValue - key + 256) % 256; // Reverse the shift
            plaintext.append((char) shifted);
        }

        writeTextFile(decryptedTextFile, plaintext.toString());
    }


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


}
