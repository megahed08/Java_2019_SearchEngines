package Indexing;

import CSVFiles.Newspaper;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.TreeMap;

import static CSVFiles.FileManager.*;
import static CSVFiles.NewspapersManager.getArticleTexts;
import static Transformation.Tokenizer.*;

public class Indexer {
    public static File mainDictionaryFile = new File("MyFiles\\Stems\\mainDictionary.ser");
    public static File mainWordCountMapFile = new File("MyFiles\\Stems\\mainWordCountMap.ser");

    //loop on all newspapers, index and merging
    public static void indexer(ArrayList<Newspaper> newspapers) {
        System.out.println("Start Indexing . . . ");
        for (Newspaper newspaper : newspapers)
            newspaperIndexer(newspaper);
        TreeMap<String, TreeMap<String, LinkedHashSet<String>>> mainDictionary = new TreeMap<>();
        System.out.println("Merging Dictionaries . . . ");
        for (Newspaper newspaper : newspapers)
            mainDictionary = mergeDictionary(mainDictionary, newspaper);
        storeNestedMapFile(mainDictionary, mainDictionaryFile);
        TreeMap<String, String> mainWordCountMap = new TreeMap<>();
        System.out.println("Merging WordCountMaps . . . ");
        for (Newspaper newspaper : newspapers)
            mainWordCountMap = mergeWordCountMap(mainWordCountMap, newspaper);
        storeNestedMapFile(mainWordCountMap, mainWordCountMapFile);
    }

    //Merging a Newspaper's dictionary to the main Dictionary
    private static TreeMap<String, String> mergeWordCountMap(
            TreeMap<String, String> mainWordCountMap,
            Newspaper newspaper) {
        System.out.println("    Merging " + newspaper.getName());
        TreeMap<String, String> wordCountMap = readWordCountMapFile(newspaper.getWordCountFile());
        for (String articleNumber : wordCountMap.keySet())
            mainWordCountMap.put(newspaper.getName() + "," + articleNumber, wordCountMap.get(articleNumber));
        return mainWordCountMap;
    }

    //Merging a Newspaper's dictionary to the main Dictionary
    private static TreeMap<String, TreeMap<String, LinkedHashSet<String>>> mergeDictionary(
            TreeMap<String, TreeMap<String, LinkedHashSet<String>>> mainDictionary,
            Newspaper newspaper) {
        System.out.println("    Merging " + newspaper.getName());
        TreeMap<String, TreeMap<String, LinkedHashSet<String>>> dictionary = readDictionaryFile(newspaper.getDictionary());
        for (String word : dictionary.keySet()) {
            TreeMap<String, LinkedHashSet<String>> locations = dictionary.get(word);
            for (String articleNumber : locations.keySet()) {
                TreeMap<String, LinkedHashSet<String>> newLocation = new TreeMap<>();
                if (mainDictionary.containsKey(word))
                    newLocation = mainDictionary.get(word);
                newLocation.put(newspaper.getName() + "," + articleNumber, locations.get(articleNumber));
                mainDictionary.put(word, newLocation);
            }
        }
        return mainDictionary;
    }

    //index a whole Newspaper
    public static void newspaperIndexer(Newspaper newspaper) {
        System.out.println("    Indexing " + newspaper.getName());
        int newspaperWordCount = 0;
        TreeMap<String, TreeMap<String, LinkedHashSet<String>>> dictionary = new TreeMap<>();
        TreeMap<String, String> articlesWordsCountMap = new TreeMap<>();
        for (int articleNumber = 1; articleNumber <= newspaper.getArticleCount(); articleNumber++) {
            String[] Texts = getArticleTexts(articleNumber, newspaper);
            int articleWordCount = 0;
            for (int textNumber = 0; textNumber < Texts.length; textNumber++) {
                if (textNumber == 0 || textNumber == 1 || textNumber == 5 || textNumber > 6)
                    continue;
                String[] words = splitTextToWords(Texts[textNumber]);
                String textLocation = textNumber + ",";

                for (int wordNumber = 0; wordNumber < words.length; wordNumber++) {
                    String word = words[wordNumber];
                    String wordLocation = textLocation + wordNumber;
                    if (!isAbbreviation(word) && word.length() < 15) {
                        word = wordStem(word).toLowerCase();
                        articleWordCount++;
                        newspaperWordCount++;
                        dictionary = wordIndexer(dictionary, word, Integer.toString(articleNumber), wordLocation);
                    }
                }
            }
            articlesWordsCountMap.put(Integer.toString(articleNumber), Integer.toString(articleWordCount));
        }
        storeNestedMapFile(dictionary, newspaper.getDictionary());
        newspaper.setWordCount(newspaperWordCount);
        storeMapFile(articlesWordsCountMap, newspaper.getWordCountFile());
    }

    //index a new word to the dictionary
    private static TreeMap<String, TreeMap<String, LinkedHashSet<String>>> wordIndexer(
            TreeMap<String, TreeMap<String, LinkedHashSet<String>>> dictionary,
            String word, String articleNumber, String location) {
        TreeMap<String, LinkedHashSet<String>> wordMap = new TreeMap<>();
        LinkedHashSet<String> locations = new LinkedHashSet<>();
        if (dictionary.containsKey(word)) {
            wordMap = dictionary.get(word);
            if (wordMap.containsKey(articleNumber))
                locations = wordMap.get(articleNumber);
        }
        locations.add(location);
        wordMap.put(articleNumber, locations);
        dictionary.put(word, wordMap);
        return dictionary;
    }
}
