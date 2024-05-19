/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.log10;
import static java.lang.Math.sqrt;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ehab
 */
public class Index5 {

    //--------------------------------------------
    int N = 0;    // To store the total number of documents in the collection.
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index
    //--------------------------------------------

    /**
     * Constructor for the Index5 class.
     * Initializes the 'sources' and 'index' data structures.
     */
    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    /**
     * Setter method to set the total number of documents in the collection..
     */
    public void setN(int n) {
        N = n;  // Assign the value of 'n' to the class variable 'N'.
    }


    //---------------------------------------------

/**
 * Method to print the posting list represented by the given Posting object.
 * A posting list is a list of document IDs associated with a specific term in the inverted index.
 * p The Posting object representing the posting list.
 */
    public void printPostingList(Posting p) {
        // Iterator<Integer> it2 = hset.iterator();

        // Start printing the posting list.
        System.out.print("[");
        boolean first = true ;  // keep track of whether it's the first iteration to handle comma placement.
        while (p != null) {
            /// -4- **** complete here ****
            // fix get rid of the last comma

            if(!first){
                System.out.println(",");
            }

            else{
                first = false ;
            }
            System.out.print(p.docId) ;
            p = p.next;
        }
        System.out.println("]");
    }

    //---------------------------------------------

    /**
 * Method to print the entire dictionary along with their corresponding posting lists.
 * The dictionary contains terms as keys and their dictionary entries as values.
 * Each entry in the dictionary represents a term's document frequency and its posting list.
 * Additionally, it prints the total number of terms in the dictionary.
 */
    public void printDictionary() {
        // Obtain an iterator over the entries in the index (dictionary).
        Iterator it = index.entrySet().iterator() ;

        // Iterate through each entry in the dictionary.
        while (it.hasNext()) {
            // Get the next entry (term and its corresponding dictionary entry).
            Map.Entry pair = (Map.Entry) it.next();
            // Extract the dictionary entry from the pair.
            DictEntry dd = (DictEntry) pair.getValue();
            // Print the term along with its document frequency.
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
            // Print the posting list associated with the term.
            printPostingList(dd.pList);
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------

/**
 * Method to build the inverted index from the specified array of file names.
 * It reads each file line by line, processes each line to build the inverted index,
 * and updates the sources map with information about each source document.
 * files Array of file names to build the index from.
 */
    public void buildIndex(String[] files) {  // from disk not from the internet
        int fid = 0;  // file ID

        // iterates over each file specified in the files array.
        for (String fileName : files) {
            // opens each file using a BufferedReader, reads it line by line, and processes each line to build the inverted index.
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                // If the file is not already present in the sources map (which presumably stores information about each source document),
                // it adds a new entry to the sources map with the file ID fid.
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln;
                int flen = 0;
                // Read each line of the file.
                while ((ln = file.readLine()) != null) {
                    /// -2- **** complete here ****
                    flen += indexOneLine(ln, fid, flen);
                }

                // Update the length of the file in the sources map.
                sources.get(fid).length = flen;

            } catch (IOException e) {    // Handle the case where the file is not found.
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
        //   printDictionary();
    }

    //----------------------------------------------------------------------------

    /**
 * Method to index the words in a single line of text from a document.
 * It processes each word to build the inverted index and updates the document frequency and posting list accordingly.
 * ln The line of text to index.
 * fid The file ID of the document containing the line of text.
 * The length of the line after processing.
 */
    public int indexOneLine(String ln, int fid, int numOfPrevWords) {
        int flen = 0;

        String[] words = ln.split("\\W+");
        //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].toLowerCase();
            words[i] = stemWord(words[i]);
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(words[i])) {
                index.put(words[i], new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(words[i]).postingListContains(fid)) {
                index.get(words[i]).doc_freq += 1; //set doc freq to the number of doc that contain the term
                if (index.get(words[i]).pList == null) {
                    index.get(words[i]).pList = new Posting(fid);
                    index.get(words[i]).last = index.get(words[i]).pList;
                    index.get(words[i]).last.addPositions(numOfPrevWords + i + 1);
                } else {
                    index.get(words[i]).last.next = new Posting(fid);
                    index.get(words[i]).last = index.get(words[i]).last.next;
                    index.get(words[i]).last.addPositions(numOfPrevWords + i + 1);
                }
            } else {
                index.get(words[i]).last.dtf += 1;
                index.get(words[i]).last.addPositions(numOfPrevWords + i + 1);
            }
            //set the term_fteq in the collection
            index.get(words[i]).term_freq += 1;
            if (words[i].equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(words[i]).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }

//----------------------------------------------------------------------------

/**
 * Method to determine if a word is a stop word.
 * Stop words are common words that are often filtered out in text processing tasks.
 * word The word to check if it is a stop word.
 * True if the word is a stop word, false otherwise.
 */
    boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;

    }
//----------------------------------------------------------------------------
/**
 * Method to perform stemming on a word.
 * Stemming is the process of reducing words to their root form.
 * Currently, this method does not perform stemming and simply returns the original word.
 * word The word to stem.
 * The stemmed word.
 */
    String stemWord(String word) { //skip for now
        return word;
//        Stemmer s = new Stemmer();
//        s.addString(word);
//        s.stem();
//        return s.toString();
    }

    //----------------------------------------------------------------------------

    /**
 * Method to find the intersection of two posting lists.
 * The intersection of two posting lists contains the document IDs that appear in both lists.
 * pL1 The first posting list.
 * pL2 The second posting list.
 * return A new posting list containing the intersection of the input posting lists.
 */
    Posting intersect(Posting pL1, Posting pL2) {
///****  -1-   complete after each comment ****
//   INTERSECT ( p1 , p2 )
//          1  answer ← {}
        Posting answer = null;
        Posting last = null;
//      2 while p1  != NIL and p2  != NIL
        while(pL1 != null && pL2 != null){
            // 3- do if docID ( p 1 ) = docID ( p2 )
            if(pL1.docId == pL2.docId){
                // 4-   then ADD ( answer, docID ( p1 ))
                if (answer == null) {
                    answer = new Posting(pL1.docId) ;
                    last = answer;
                }

                else {
                    last.next = new Posting(pL1.docId) ;
                    last = last.next;
                }

                // 5- p1 ← next ( p1 )
                pL1 = pL1.next ;

                // 6- p2 ← next ( p2 )
                pL2 = pL2.next ;
            }

            // 7- else if docID(pL1) < docID(pL2)
            else if(pL1.docId < pL2.docId){
                // 8- then p1 ← next ( p1 )
                pL1 = pL1.next ;

            }

            else{
                // 9- else p2 ← next ( p2 )
                pL2 = pL2.next ;
            }
        }

//      10 return answer
        return answer;
    }


    /**
 * Method to perform a non-optimized search for a phrase containing any number of terms.
 * It finds documents containing all the terms in the given phrase using an intersection-based approach.
 * phrase The phrase to search for.
 * return A string containing the search results (document IDs, titles, and lengths) or "Not Found" if no matching documents are found.
 */
    public String find_24_01(String phrase) {
        String result = "";
        String[] words = phrase.split("\\s+");
        int len = words.length;

        // Initialize posting (null value)
        Posting posting = null;

        // Check if the first word exists
        if (index.containsKey(words[0].toLowerCase())) {
            posting = index.get(words[0].toLowerCase()).pList;
        }

        if (posting == null) {
            return result;
        }

        // Iterate through the rest of the words
        int i = 1;
        while (i < len) {
            String word = words[i].toLowerCase();
            // Check if the word exists in the index and the posting list is not null
            if (index.containsKey(word) && index.get(word).pList != null) {
                // Intersect the current posting list with the posting list of the current word
                posting = intersect(posting, index.get(word).pList);
            } else {
                // If the word doesn't exist in the index or the posting list is null, set posting to null and break the loop
                posting = null;
                break;
            }
            i++ ;
        }


        // If posting is not null after intersecting all words, generate the result
        if (posting != null) {
            while (posting != null) {
                result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
                posting = posting.next;
            }
        }
        return result;
    }


    //---------------------------------
/**
 * Method to sort an array of strings using bubble sort algorithm.
 * words The array of strings to be sorted.
 * return The sorted array of strings.
 */
    String[] sort(String[] words) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }

     //---------------------------------

     /**
 * Method to store the index and sources data into a file.
 * storageName The name of the storage file.
 */

    public void store(String storageName) {
        try {
            String pathToStorage = "tmp11/rl"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = " + entry.getValue().title + ", Value = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); //String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }

            // Write a separator to distinguish between sections in the storage file.
            wr.write("section2" + "\n");

            // Write index data into the storage file.
            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                //  System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    //    System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============EBD STORE=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//=========================================

/**
 * Method to check if a storage file exists.
 * storageName The name of the storage file.
 * return True if the storage file exists, false otherwise.
 */
    public boolean storageFileExists(String storageName){
        java.io.File f = new java.io.File("tmp11/rl"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;

    }
//----------------------------------------------------

/**
 * Method to create an empty storage file.
 * param storageName The name of the storage file.
 */
    public void createStore(String storageName) {
        try {
            String pathToStorage = "tmp11"+storageName;
            Writer wr = new FileWriter(pathToStorage);  // Create a writer to write data into the storage file.
            wr.write("end" + "\n");
            wr.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//----------------------------------------------------
     //load index from hard disk into memory

     /**
 * Method to load the index from the hard disk into memory.
 * It reads the index data from the specified storage file and populates the 'sources' and 'index' data structures accordingly.
 * storageName The name of the storage file containing the index data.
 * return The loaded index represented as a HashMap.
 */
    public HashMap<String, DictEntry> load(String storageName) {
        try {
            String pathToStorage = "tmp11/rl/"+storageName;
            sources = new HashMap<Integer, SourceRecord>();
            index = new HashMap<String, DictEntry>();
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));   // Open the storage file for reading.
            String ln = "";
            int flen = 0;

            // Read each line from the storage file.
            while ((ln = file.readLine()) != null) {
                // Check if the end of the first section is reached.
                if (ln.equalsIgnoreCase("section2")) {
                    break;
                }
                String[] ss = ln.split(","); // Split the line into fields.
                int fid = Integer.parseInt(ss[0]);
                try {
                    System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " [" + ss[4] + "]   " + ss[5].replace('~', ','));

                    SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                    //   System.out.println("**>>"+fid+" "+ ss[1]+" "+ ss[2]+" "+ ss[3]+" ["+ Double.parseDouble(ss[4])+ "]  \n"+ ss[5]);
                    sources.put(fid, sr);
                } catch (Exception e) {

                    System.out.println(fid + "  ERROR  " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Read the index entries and posting lists from the storage file.
            while ((ln = file.readLine()) != null) {
                //     System.out.println(ln);
                if (ln.equalsIgnoreCase("end")) {
                    break;
                }

                // Split the line into term information and posting list.
                String[] ss1 = ln.split(";");
                String[] ss1a = ss1[0].split(",");
                String[] ss1b = ss1[1].split(":");

                // Create a new DictEntry object for the term and add it to the index map.
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                String[] ss1bx;     // Process the posting list for the term.
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }
            System.out.println("============= END LOAD =============");
            //    printDictionary();
        } catch (Exception e) {
            e.printStackTrace();   // Handle any errors that occur during loading.
        }
        return index;
    }


    /**
     * Indexes a bi-word (pair of adjacent words) in the corpus,
     * handling stop words, stemming, and updating frequency counts.
     */

    public int indexBiWord(String bigram, int fileId, int position) {
        // Normalize bigram to lowercase
        bigram = bigram.toLowerCase();

        // Split bigram into individual words to check for stop words
        String[] words = bigram.split("\\W+");
        for (String word : words) {
            // Check if any word in the bigram is a stop word
            if (stopWord(word)) {
                return 0;  // Skip indexing if any word is a stop word
            }
        }

        // Stem each word in the bigram
        StringBuilder stemmedBigramBuilder = new StringBuilder();
        for (String word : words) {
            stemmedBigramBuilder.append(stemWord(word)).append(" ");
        }
        String stemmedBigram = stemmedBigramBuilder.toString().trim();  // Remove trailing space

        // Add stemmed bigram to index if absent
        if (!index.containsKey(stemmedBigram)) {
            index.put(stemmedBigram, new DictEntry());
        }

        // Add document id to the posting list
        DictEntry dictEntry = index.get(stemmedBigram);
        if (!dictEntry.postingListContains(fileId)) {
            // Increment document frequency if fileId not present in posting list
            dictEntry.doc_freq += 1;

            // Add new Posting node to posting list
            if (dictEntry.pList == null) {
                dictEntry.pList = new Posting(fileId);
                dictEntry.last = dictEntry.pList;
            }


            else {
                dictEntry.last.next = new Posting(fileId);
                dictEntry.last = dictEntry.last.next;
            }
        }


        else {
            // Increment term frequency in document if fileId already present in posting list
            dictEntry.last.dtf += 1;
        }

        // Increment total term frequency in the collection
        dictEntry.term_freq += 1;

        // Debug or log if bigram contains a specific word, e.g., "lattice"
        if (stemmedBigram.contains("lattice")) {
            System.out.println("  <<" + dictEntry.getPosting(1) + ">> " + bigram);
        }

        return 2;  // Return 2 because each bigram is made of two words
    }


    /**
     * Builds a bi-word index from the given array of file names.
     *
     * @param files An array of file names to be indexed.
     */
    public void buildBiWordIndex(String[] files) {
        int fileId = 0; // Initialize file identifier

        // Iterate through each file
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                // Check if the file is not already indexed and add it to sources if not
                if (!sources.containsKey(fileName)) {
                    sources.put(fileId, new SourceRecord(fileId, fileName, fileName, "notext")); // Assumes SourceRecord manages these details
                }

                String line;
                String prevWord = null; // Store the previous word for bi-word indexing
                int fileLength = 0; // Initialize file length in terms of words processed

                // Read each line from the file
                while ((line = file.readLine()) != null) {
                    String[] words = line.split("\\W+"); // Split the line into words

                    // Iterate through each word in the line
                    for (int i = 0; i < words.length; i++) {
                        if (prevWord != null) {
                            // Construct the bi-word
                            String biWord = prevWord + "_" + words[i];
                            // Index the bi-word and update file length by word count
                            fileLength += indexBiWord(biWord, fileId, i);
                        }
                        prevWord = words[i]; // Update the previous word for next iteration
                    }
                }

                // Update the source record with the file length
                sources.get(fileId).length = fileLength;

            } catch (IOException e) {
                // Handle file not found exception
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fileId++; // Increment file identifier for the next file
        }
    }



    /**
     * Searches for a bi-word phrase in the index and retrieves relevant documents.
     *
     * @param biWordPhrase The bi-word phrase to search for.
     * @return A string containing the search results.
     */
    public String searchBiWord(String biWordPhrase) {
        String result = "";

        // Normalize and prepare the bi-word phrase for lookup
        biWordPhrase = biWordPhrase.toLowerCase().replace(" ", "_");

        // Retrieve the posting list for the bi-word directly
        Posting posting = index.containsKey(biWordPhrase) ? index.get(biWordPhrase).pList : null;

        // If no postings, return an indication such as "No results found."
        if (posting == null) {
            return "No results found for '" + biWordPhrase.replace("_", " ") + "'.";
        }

        // Iterate through the postings and build the result string
        while (posting != null) {
            // Retrieve document title and length
            String docTitle = sources.get(posting.docId).title;
            int docLength = sources.get(posting.docId).length;

            // Append document information to result string
            result += "\t" + posting.docId + " - " + docTitle + " - " + docLength + "\n";

            // Move to the next posting
            posting = posting.next;
        }
        return result;
    }



    /**
     * Searches for a positional phrase in the index and retrieves relevant documents.
     *
     * @param positionalPhrase The positional phrase to search for.
     * @return A string containing the search results.
     */
    public String searchPositional(String positionalPhrase) {
        String result = "";

        // Normalize and prepare the positional phrase for lookup
        String[] positionalPhraseList = positionalPhrase.toLowerCase().split(" ");

        // Create a list to store posting lists for each word in the positional phrase
        List<Posting> postingList = new ArrayList<>();

        // Retrieve posting lists for each word in the positional phrase
        for (String phrase : positionalPhraseList) {
            if (index.containsKey(phrase)) {
                postingList.add(index.get(phrase).pList); // Add the posting list to the list
            }


            else {
                return "No results found for " + positionalPhrase; // Return if any word is not found in the index
            }
        }

        // If posting lists are found for all words in the positional phrase
        if (!postingList.isEmpty()) {
            Posting firstPosting = postingList.get(0); // Get the first posting list
            while (firstPosting != null) {
                for (int position : firstPosting.positions) {
                    boolean found = true;
                    // Check if the current position in the first posting list satisfies the positional phrase
                    for (int wordNum = 1; wordNum < postingList.size(); wordNum++) {
                        Posting currPosting = postingList.get(wordNum);

                        // Move to the posting list for the current word in the positional phrase
                        while (currPosting != null && currPosting.docId != firstPosting.docId) {
                            currPosting = currPosting.next;
                        }

                        // If the posting list is null or the position does not match, set found to false
                        if (currPosting == null || !currPosting.positions.contains(position + wordNum)) {
                            found = false;
                            break;
                        }
                    }
                    // If all words in the positional phrase are found at the correct positions, add document info to result
                    if (found) {
                        String docTitle = sources.get(firstPosting.docId).title;
                        int docLength = sources.get(firstPosting.docId).length;
                        result += "\t" + firstPosting.docId + " - " + docTitle + " - " + docLength + "\n";
                        break;
                    }
                }
                // Move to the next posting in the first posting list
                firstPosting = firstPosting.next;
            }
        }

        else {
            // Return if no posting lists are found for the words in the positional phrase
            return "No results found for " + positionalPhrase;
        }

        return result;
    }


}

//=====================================================================
