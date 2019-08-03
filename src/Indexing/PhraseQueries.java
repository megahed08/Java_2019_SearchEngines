package Indexing;

import SearchExceptions.InvalidQueryException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.TreeMap;

public class PhraseQueries {

    //find Articles of a Phrase Queries
    public static TreeMap<String, LinkedHashSet<String>> getPhraseArticles(ArrayList<String> words,
                                                                           TreeMap<String, TreeMap<String, LinkedHashSet<String>>> dictionary)
            throws InvalidQueryException {
        try {
            TreeMap<String, LinkedHashSet<String>> referenceWordLocations = dictionary.get(words.get(0));
            TreeMap<String, LinkedHashSet<String>> resultLocations = new TreeMap<>();
            for (int wordNumber = 1; wordNumber < words.size(); wordNumber++) {
                TreeMap<String, LinkedHashSet<String>> nextWordLocations = dictionary.get(words.get(wordNumber));
                for (String articleNumber : referenceWordLocations.keySet()) {
                    if (nextWordLocations.containsKey(articleNumber)) {
                        LinkedHashSet<String> referenceWordTexts = referenceWordLocations.get(articleNumber);
                        LinkedHashSet<String> nextWordTexts = nextWordLocations.get(articleNumber);
                        for (String referenceWordText : referenceWordTexts) {
                            String expectedLocation = referenceWordText.split(",")[0] + "," +
                                    (Integer.parseInt(referenceWordText.split(",")[1]) + 1);
                            if (nextWordTexts.contains(expectedLocation))
                                resultLocations = addLocation(articleNumber, expectedLocation, resultLocations);
                        }
                    }
                }
                referenceWordLocations.clear();
                referenceWordLocations.putAll(resultLocations);
                resultLocations.clear();
            }
            if (referenceWordLocations.isEmpty() || referenceWordLocations == null)
                throw new InvalidQueryException();
            return referenceWordLocations;
        } catch (NullPointerException e) {
            return null;
        }
    }

    //add a new location to the locations Map
    private static TreeMap<String, LinkedHashSet<String>> addLocation(String articleNumber, String expectedLocation,
                                                                      TreeMap<String, LinkedHashSet<String>> resultLocations) {
        LinkedHashSet<String> texts = new LinkedHashSet<>();
        if (resultLocations.containsKey(articleNumber))
            texts = resultLocations.get(articleNumber);
        texts.add(expectedLocation);
        resultLocations.put(articleNumber, texts);
        return resultLocations;
    }
}
