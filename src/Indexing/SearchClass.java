package Indexing;

import CSVFiles.Newspaper;
import SearchExceptions.InvalidQueryException;

import java.util.*;

import static CSVFiles.FileManager.*;
import static CSVFiles.NewspapersManager.getArticleTexts;
import static Indexing.BM25.mainScoreMapFile;
import static Indexing.BM25.sortMapByValue;
import static Indexing.BooleanRetrieval.*;
import static Indexing.Indexer.mainDictionaryFile;
import static Indexing.PhraseQueries.getPhraseArticles;
import static Transformation.Tokenizer.*;

public class SearchClass {
    public static String searchText = "";
    public static ArrayList<String> searchWords;
    public static String queryCase = "OR";
    private static TreeMap<String, TreeMap<String, LinkedHashSet<String>>> dictionary;
    private static TreeMap<String, TreeMap<String, Double>> scoresMap;
    private static ArrayList<Newspaper> newspapers;
    public static int resultsLimit;

    //Create a user input Scanner
    public static void userInput(ArrayList<Newspaper> newspapers, boolean testMode, int resultsLimit) {
        SearchClass.newspapers = newspapers;
        SearchClass.resultsLimit = resultsLimit;
        SearchClass.dictionary = readDictionaryFile(mainDictionaryFile);
        SearchClass.scoresMap = readScoresFile(mainScoreMapFile);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter your search:     type \"exit\" for exit");
            searchText = scanner.nextLine();
            if (searchText.equals("exit")) break;
            else if (searchText.equals("")) continue;
            long startTime = System.currentTimeMillis();
            try {
                System.out.println(fetchResults(testMode));
            } catch (InvalidQueryException e) {
                System.out.println("Invalid Query");
            } catch (IndexOutOfBoundsException e2) {
                System.out.println("No Result");
            }
            long endTime = System.currentTimeMillis();
            if (testMode != true)
                System.out.println((double) (endTime - startTime) / 1000 + "Sec\n\n");  //divide by 1000000 to get milliseconds.
            queryCase = "OR";
        }
    }

    //combine all searching steps and get results ready for output
    private static String fetchResults(boolean testMode)
            throws InvalidQueryException, IndexOutOfBoundsException {
        processQueryWords();
        checkQueryCase();
        Map<String, Double> resultArticles = getResultArticles();
        String searchOutput = userOutput(resultArticles, testMode);
        if (searchOutput.equals("")) throw new IndexOutOfBoundsException();
        return searchOutput;
    }

    //prepare results of a query
    private static String userOutput(Map<String, Double> resultArticles, boolean testMode) {
        String searchOutput = "";
        if (testMode != true)
            searchOutput += "Total Articles Found : " + resultArticles.size() + "\n\n";
        int resultCount = 0;
        for (String article : resultArticles.keySet()) {
            if (resultCount >= resultsLimit)
                break;
            String newspaperName = article.split(",")[0];
            int articleNumber = Integer.parseInt(article.split(",")[1]);
            Newspaper newspaper = null;
            switch (newspaperName) {
                case ("DeutscheWelle"):
                    newspaper = newspapers.get(0);
                    break;
                case ("NewYorkTimes"):
                    newspaper = newspapers.get(1);
                    break;
                case ("TimesOfIndia"):
                    newspaper = newspapers.get(2);
                    break;
                case ("TheGlobeAndMail"):
                    newspaper = newspapers.get(3);
                    break;
                case ("TheGuardian"):
                    newspaper = newspapers.get(4);
                    break;
                default:
                    break;
            }
            String[] articleTexts = getArticleTexts(articleNumber, newspaper);
            String articleScore = Double.toString(resultArticles.get(article));
            if (testMode != true)
                searchOutput += userMode(newspaperName, articleTexts, articleScore, resultCount);
            else
                searchOutput += performanceMode(articleTexts) + "\n";
            resultCount++;
        }
        return searchOutput;
    }

    //get results in a summarized mode for testing
    private static String performanceMode(String[] articleTexts) {
        String articleResult = "";
        try {
            articleResult += articleTexts[4] + "|";                      //Headline
            articleResult += articleTexts[1];                            //URL
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return articleResult;
    }

    //get results in a full detailed mode
    private static String userMode(String newspaperName, String[] articleTexts, String articleScore, int resultCount) {
        String articleResult = "";
        try {
            articleResult += (resultCount + 1) + ")Newspaper:   " + newspaperName + "\n";   //Newspaper Name
            articleResult += "Headline:      " + articleTexts[4] + "\n";                    //Headline
            articleResult += "URL:           " + articleTexts[1] + "\n";                    //URL
            articleResult += "Snippets:      " + getSnippets(articleTexts[3]) + "\n";       //text
            articleResult += "Categories:    " + articleTexts[6] + "\n";                    //categories
            articleResult += "Author:        " + articleTexts[2] + "\n";                    //Author
            articleResult += "Release Date:  " + articleTexts[5] + "\n";                    //time
            articleResult += "Rank:           " + articleScore + "\n";                      //Score
            articleResult += "_________________________________________________________________________ \n";
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return articleResult.replaceAll("\"", "");
    }

    //get the search query prepared before searching
    private static void processQueryWords() {
        ArrayList<String> words = new ArrayList<String>();
        for (String word : splitTextToWords(searchText)) {
            if (word.length() > 1 && word.length() < 15) {
                if (!word.equals("AND") && !word.equals("NOT") && !word.equals("OR"))
                    word = (isAbbreviation(word)) ? "A-" + word : wordStem(word.toLowerCase());
                words.add(word);
            }
        }
        setSearchWords(words);
    }

    //check the cases for a given Query
    private static void checkQueryCase() throws InvalidQueryException {
        if (searchWords.contains("AND")) {
            if (searchWords.size() != 3)
                throw new InvalidQueryException();
            else {
                queryCase = "AND";
                searchWords.remove(searchWords.indexOf(queryCase));
            }
        } else if (searchWords.contains("NOT")) {
            if (searchWords.size() != 3)
                throw new InvalidQueryException();
            else {
                queryCase = "NOT";
                searchWords.remove(searchWords.indexOf(queryCase));
            }
        } else if (searchText.charAt(0) == '\"' && searchText.charAt(searchText.length() - 1) == '\"') {
            searchText = searchText.replaceAll("\"", "");
            queryCase = "Phrase";
        } else {
            queryCase = "OR";
            if (searchWords.contains("OR"))
                searchWords.remove(searchWords.indexOf(queryCase));
        }
    }

    //get related Articles with their Scores
    private static Map<String, Double> getResultArticles()
            throws InvalidQueryException, IndexOutOfBoundsException {
        Map<String, Double> resultArticles = new TreeMap<>();
        switch (queryCase) {
            case ("NOT"):
                Map<String, Double> toEditMap = scoresMap.get(searchWords.get(0));
                Map<String, Double> toRemoveMap = scoresMap.get(searchWords.get(1));
                if (toEditMap != null && !toEditMap.isEmpty() &&
                        toRemoveMap != null && !toRemoveMap.isEmpty()) {
                    resultArticles = sortMapByValue(notOp(toEditMap, toRemoveMap));
                } else
                    throw new InvalidQueryException();
                break;
            case ("AND"):
                Map<String, Double> Map1 = scoresMap.get(searchWords.get(0));
                Map<String, Double> Map2 = scoresMap.get(searchWords.get(1));
                if (Map1 != null && !Map1.isEmpty() &&
                        Map2 != null && !Map2.isEmpty()) {
                    resultArticles = sortMapByValue(andOp(Map1, Map2));
                } else
                    throw new InvalidQueryException();
                break;
            case ("OR"):
                ArrayList<Map<String, Double>> maps = new ArrayList<>();
                for (String word : searchWords) {
                    if (scoresMap.containsKey(word))
                        maps.add(scoresMap.get(word));
                }
                resultArticles = sortMapByValue(orOp(maps));
                break;
            case ("Phrase"):
                TreeMap<String, TreeMap<String, LinkedHashSet<String>>> phraseMaps = new TreeMap<>();
                for (String word : searchWords) {
                    if (dictionary.containsKey(word))
                        phraseMaps.put(word, dictionary.get(word));
                }
                TreeMap<String, LinkedHashSet<String>> phraseLocations =
                        getPhraseArticles(searchWords, phraseMaps);
                ArrayList<Map<String, Double>> phraseMaps2 = new ArrayList<>();
                for (String word : searchWords) {
                    if (scoresMap.containsKey(word))
                        phraseMaps2.add(scoresMap.get(word));
                }
                Map<String, Double> phraseLocations2 = orOp(phraseMaps2);
                Map<String, Double> finalPhraseMap = new TreeMap<>();
                for (String article : phraseLocations2.keySet()) {

                    if (phraseLocations.containsKey(article))
                        finalPhraseMap.put(article, phraseLocations2.get(article));

                }
                resultArticles = sortMapByValue(finalPhraseMap);
                break;
            default:
                throw new InvalidQueryException();
        }
        return resultArticles;
    }

    //returns a text of snippets gathered from the Article's main text
    public static String getSnippets(String text) {
        String snippetText = "";
        if (text != null) {
            if (queryCase.equals("Phrase")) {
                snippetText = getSentence(text, searchText);
            } else {
                HashSet<String> snippets = new HashSet<>();
                for (String word : searchWords)
                    snippets.add(getSentence(text, word));
                for (String snippet : snippets) {
                    if (snippet != null)
                        snippetText += (snippet + ". |**| ");
                }
            }
        }
        return snippetText;
    }

    //Separate search text to words
    public static void setSearchWords(ArrayList<String> searchWords) {
        SearchClass.searchWords = searchWords;
    }

}
