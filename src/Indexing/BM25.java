package Indexing;

import java.io.File;
import java.util.*;

import static CSVFiles.FileManager.*;
import static CSVFiles.NewspapersManager.*;
import static Indexing.Indexer.mainDictionaryFile;
import static Indexing.Indexer.mainWordCountMapFile;

public class BM25 {
                                          //Tuning factors
    private static double k = 1.2;        //k = 1.75 or 1.2
    private static double b = 0.75;       //b = 0.75
    public static File mainScoreMapFile = new File("MyFiles\\Stems\\mainScoreMap.ser");

    //create a Map of scores for each word
    public static void dictionaryScoring() {
        System.out.println("Start Scoring . . . ");
        TreeMap<String, TreeMap<String, LinkedHashSet<String>>> mainDictionary =
                readDictionaryFile( mainDictionaryFile);
        TreeMap<String, String> wordCountMap = readWordCountMapFile(mainWordCountMapFile);
        TreeMap<String, Map<String, Double>> scoresMap = new TreeMap<>();
        for (String word : mainDictionary.keySet())
            scoresMap.put(word, getWordScores(mainDictionary.get(word), wordCountMap));
        storeNestedMapFile(scoresMap, mainScoreMapFile);
    }

    //sort a Map by value in descending order
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
            result.put(entry.getKey(), entry.getValue());
        return result;
    }

    //grab  scores of all articles of a given word
    private static Map<String, Double> getWordScores(TreeMap<String, LinkedHashSet<String>> wordLocations,
            TreeMap<String, String> wordCountMap) {
        TreeMap<String, Double> wordScores = new TreeMap<>();
        for (String article : wordLocations.keySet()) {
            double score = computeBM25Score(get_tf(wordLocations, article),
                    get_df(wordLocations), get_N(), get_dl(article, wordCountMap), get_avdl());
            wordScores.put(article, Math.abs(score));
        }
        return sortMapByValue(wordScores);
    }

    //compute a score of a word using BM25 Formula
    private static double computeBM25Score(double tf, double df, double N, double dl, double avdl) {
        double tfx = tf * (k + 1) / (k * (1 - b + (b * dl / avdl)) + tf);
        double BM25Score = tfx * ((Math.log(N / df)) / Math.log(2));
        return BM25Score;
    }

    //df     : number of articles containing a word, numbers are powers of 2
    private static double get_df(TreeMap<String, LinkedHashSet<String>> wordLocations) {
        int df = 0;
        if (wordLocations!= null)
            df = wordLocations.size();
        return (double) df;
    }


    //tf     : word number of occurrence in an article
    private static double get_tf(TreeMap<String, LinkedHashSet<String>> wordLocations, String articleNumber) {
        int tf = 0;
        if (wordLocations.containsKey(articleNumber))
            tf = wordLocations.get(articleNumber).size();
        return (double) tf;
    }

    //dl     : article length
    private static double get_dl(String article, TreeMap<String, String> wordCountMap) {
        return Double.parseDouble(wordCountMap.get(article));
    }

    //avdl   : avarage article length in corpus
    private static double get_avdl() {
        return WordsCounter / ArticlesCounter;
    }

    //N = total number of articles
    private static double get_N() {
        return ArticlesCounter;
    }
}
