/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;
import java.util.Arrays;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;

/**
 * @author ehab
 */
public class Test {

    public static void main(String[] args) throws IOException {
        Index5 index = new Index5();
        String files = "tmp11/rl/collection/"; // Change it to your collection directory

        File file = new File(files);
        String[] fileList = file.list(); // List files in the directory

        fileList = index.sort(fileList); // Sort files if necessary
        index.N = fileList.length;

        // Build both regular and bi-word indexes
        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = files + fileList[i];
        }
        index.buildIndex(fileList); // Existing method to build a standard index
        index.buildBiWordIndex(fileList); // New method to build a bi-word index
        index.store("index"); // Store the index, possibly enhancing to differentiate between indexes
        index.printDictionary(); // Print dictionary for verification

        // Interactive search handling
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String phrase = "";


        do {
            System.out.println("Print search phrase: ");
            phrase = in.readLine();
            String[] words = phrase.split("\\W+");
            boolean ok = true;

            // Check if all words in the phrase exist in the index
            for (String word : words) {
                if (!index.index.containsKey(word)) {
                    ok = false ;
                    break;
                }
            }

            if (ok) {
                // Check if phrase is a bi-word query
                if (phrase.startsWith("\"") && phrase.endsWith("\"")) {
                    phrase = phrase.substring(1, phrase.length() - 1);
                    System.out.println("Bi-word Search Result = \n" + index.searchPositional(phrase));
                }
                else if (!phrase.contains("\"")) {
                    // Use existing single word search method
                    System.out.println("Boolean result = \n" + index.find_24_01(phrase));
                }
                else {
                    int firstQuoteIndex = phrase.indexOf('\"');
                    int secondQuoteIndex = phrase.indexOf('\"', firstQuoteIndex + 1);
                    String biWord = phrase.substring(firstQuoteIndex, secondQuoteIndex + 1);
                    phrase = phrase.replace(biWord, "").replace(" ", "");
                    biWord = biWord.substring(1, biWord.length() - 1).replace(" ", "_");
                    String[] biWordResults = index.searchBiWord(biWord).split("\n");
                    String[] singleWordResults = index.find_24_01(phrase).split("\n");
                    System.out.println("Boolean result = \n");
                    for (String word : biWordResults) {
                        if (Arrays.asList(singleWordResults).contains(word)) {
                            System.out.println(word);
                        }
                    }
                }

            }


            else {
                System.out.println("This Phrase does not exist! Please try again :)");
            }
        }

        while (!phrase.isEmpty());

    }
}