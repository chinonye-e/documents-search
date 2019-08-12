package search;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import util.LuceneReadIndexFromFile;
import util.LuceneWriteIndexFromFile;

/**
 * Searches for a given search term in the given documents and returns a map of the number of times the search term is found in each document, sorted from the highest number to the lowest.
 * The search method can be a simple string search, and indexed search or regular expression string matching.
 * @author CE017795
 *
 */
public class SearchDocument {

    private static final Random RAND = new Random();
    private static final String TEXTS_DIRECTORY = "./src/main/resources/sampleTexts/";
    private static final String INDEX_DIRECTORY = "./src/main/resources/indexedFiles/";
    
    public static void main(String [] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        try {
            Map<String, String> documentsByName = new HashMap<>();
            final String exitSignal = "end";
            
            System.out.println("Enter a search term: ");
            String searchTerm = scanner.nextLine();  // Read the search term/phrase
            
            if (!(searchTerm.equalsIgnoreCase(exitSignal))) {
                SearchDocument docSearch = new SearchDocument();
                documentsByName = docSearch.readFileAsString();
            }
            
            // pre-process the documents and index them
            (new LuceneWriteIndexFromFile()).createIndex(TEXTS_DIRECTORY, INDEX_DIRECTORY);
            
            while (!(searchTerm.equalsIgnoreCase(exitSignal))) {
                
                System.out.println("Select a search method, enter 1 for String Match, enter 2 for Regular Expression, and 3 for Indexed (does not return matches in documents but returns the order of relevance based on index): ");
                String searchMethod = scanner.nextLine();  // Read the preferred search method
                
                while (!(searchMethod.equals("1") || searchMethod.equals("2") || searchMethod.equals("3")) && !(searchMethod.equalsIgnoreCase(exitSignal))) {
                    System.out.println("Invalid method selection.");
                    System.out.println("Select a search method, enter 1 for String Match, enter 2 for Regular Expression, and 3 for Indexed (does not return matches in documents but returns the order of relevance based on index): ");
                    scanner.close();
                    scanner = new Scanner(System.in);
                    searchMethod = scanner.nextLine();
                }
                
                if (searchMethod.equalsIgnoreCase(exitSignal))
                    break;
                
                searchDocument(searchTerm, searchMethod, documentsByName);
                
                // then loop until exit
                System.out.println("Enter a search term or type end to exit the program: ");
                searchTerm = scanner.nextLine();  // Read the search term/phrase
            }
            
            System.out.println("Would you like to run the perfrmance test that performs two million searches? Yes or No");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                performanceSearch(documentsByName);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    /**
     * Searches the given documents for the specified search term, using the given search method
     * @param searchTerm the search term
     * @param searchMethod the search method
     * @param documentsByName the map of documents by the document name
     * @return the map of the number of times the search term was found, mapped by the document name, and sorted from most relevant to least relevant document
     * @throws Exception 
     */
    public static Map<String, Integer> searchDocument(final String searchTerm, final String searchMethod, final Map<String, String> documentsByName) throws Exception {
        Map<String, Integer> result = null;
        if (!(searchMethod.equals("1") || searchMethod.equals("2") || searchMethod.equals("3"))) {
            System.out.println("Invalid method selection.");
            System.out.println("Select a search method, enter 1 for String Match, enter 2 for Regular Expression, and 3 for Indexed (does not return matches in documents but returns the order of relevance based on index): ");
            return result;
        }
        
        long startTime = 0, endTime = 0, timeElapsed = 0;
        switch (searchMethod) {
            case "1":
                startTime = System.nanoTime();
                result = stringMatch(searchTerm, documentsByName);
                endTime = System.nanoTime();
                break;
            case "2":
                startTime = System.nanoTime();
                result = regexMatch(searchTerm, documentsByName);
                endTime = System.nanoTime();
                break;
            case "3":
                startTime = System.nanoTime();
                result = indexMatch(searchTerm, documentsByName);
                endTime = System.nanoTime();
                break;
            default:
                result = new HashMap<>();
        }
        timeElapsed = endTime - startTime;
        
        // sort result according to relevance
        final Map<String, Integer> sortedResult = result == null ? new HashMap<>() : result
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
        
        System.out.println("Search results:");
        for (Entry<String, Integer> entry : sortedResult.entrySet()) {
            if (searchMethod.equals("3"))
                System.out.println("\t" + entry.getKey());
            else
                System.out.println("\t" + entry.getKey() + " - " + entry.getValue() + " matches");
        }
        System.out.println("Elapsed time: " + timeElapsed / 1000000 + " ms");
        return sortedResult;
    }
    
    private static void performanceSearch(final Map<String, String> documentsByName) throws Exception {
        SearchDocument docSearch = new SearchDocument();
        docSearch.readFileAsString();
        
        long startTime = 0, endTime = 0, timeElapsed = 0;
        startTime = System.nanoTime();
        for (int i = 0; i < 2000000; i++) {
            final String searchTerm = generateRandomString();
            stringMatch(searchTerm, documentsByName);
        }
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        System.out.println("Simple Search took: " + timeElapsed / 1000000 + " ms");
        
//        startTime = System.nanoTime();
//        for (int i = 0; i < 2000000; i++) {
//            final String searchTerm = generateRandomString();
//            result = regexMatch(searchTerm);
//        }
//        endTime = System.nanoTime();
//        timeElapsed = endTime - startTime;
//        System.out.println("Regex Search took: " + timeElapsed / 1000000 + " ms");
        
//        startTime = System.nanoTime();
//        for (int i = 0; i < 2000000; i++) {
//            final String searchTerm = generateRandomString();
//            result = indexMatch(searchTerm);
//        }
//        endTime = System.nanoTime();
//        timeElapsed = endTime - startTime;
//        System.out.println("Indexed Search took: " + timeElapsed / 1000000 + " ms");
    }
    
    /**
     * Generates a random string of random lengths from the letters of the alphabets
     * @return the generated string
     */
    private static String generateRandomString() {
        int count = RAND.nextInt(7) + 2;
        final String alphabets = "abcdefghijklmnopqrstuvwxyz";
        final StringBuilder builder = new StringBuilder();
        while (count-- > 1) {
            builder.append(alphabets.charAt(RAND.nextInt(25)));
        }
        return builder.toString();
    }
    
    /**
     * Performs simple string matching
     * @param searchTerm the search term
     * @param documentsByName documents keyed by name
     * @throws Exception
     */
    private static Map<String, Integer> stringMatch(final String searchTerm, final Map<String, String> documentsByName) {
        final Map<String, Integer> result = new HashMap<>();
        
        for (final Entry<String, String> entry : documentsByName.entrySet()) {
            final String document = entry.getValue();
            int numberOfMatchings = 0;
            int j = 0;

            final int documentLength = document.length();
            if(documentLength < searchTerm.length()) {
                result.put(entry.getKey(), numberOfMatchings);
                continue;
            }
            
            for(int i = 0; i < documentLength; i++) {
                if(Character.toLowerCase(document.charAt(i)) == Character.toLowerCase(searchTerm.charAt(j)))
                    j++;
                else
                    j = 0;
                if(j == searchTerm.length()) {
                    numberOfMatchings++;
                    j = 0;
                }
            }
            result.put(entry.getKey(), numberOfMatchings);
        }
        return result;
    }
    
    /**
     * Performs Regular Expression string matching
     * @param searchTerm the search term
     * @param documentsByName documents keyed by name
     * @throws Exception
     */
    private static Map<String, Integer> regexMatch(final String searchTerm, final Map<String, String> documentsByName) {
        final String regex = Pattern.quote(searchTerm); // build a regex from the given search term
        final Map<String, Integer> result = new HashMap<>();
        
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE); // the pattern to search for
        
        for (final Entry<String, String> entry : documentsByName.entrySet()) {
            final Matcher matcher = pattern.matcher(entry.getValue());
            
            int count = 0;
            while (matcher.find())
                count++;
            
            result.put(entry.getKey(), count);
        }
        
        return result;
    }
    
    /**
     * Uses Lucene indexing to index the given documents for easier searches
     * @param searchTerm the search term
     * @param documentsByName documents keyed by name
     * @return
     * @throws Exception
     */
    private static Map<String, Integer> indexMatch(final String searchTerm, final Map<String, String> documentsByName) throws Exception {
        LuceneReadIndexFromFile readIndex = new LuceneReadIndexFromFile();
        final Map<String, Integer> result = readIndex.searchIndex(searchTerm, INDEX_DIRECTORY);
        for (Entry<String, String> entry : documentsByName.entrySet()) {
            if (!result.containsKey(entry.getKey()))
                result.put(entry.getKey(), 0);
        }
        return result;
    }

    private Map<String, String> readFileAsString()throws Exception {
        final List<String> documentNames = new ArrayList<>();
        final Map<String, String> documentsByName = new HashMap<>();
        
        documentNames.add("french_armed_forces.txt");
        documentNames.add("hitchhikers.txt");
        documentNames.add("warp_drive.txt");
        
        for (final String documentName : documentNames) {
            final String fileName = TEXTS_DIRECTORY + documentName;
            final Path path = Paths.get(fileName);
            documentsByName.put(documentName, (new String(Files.readAllBytes(path))).trim());
        }
        return documentsByName;
    }
}
