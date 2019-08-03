package Transformation;

import opennlp.tools.stemmer.PorterStemmer;

import java.util.regex.Pattern;

public class Tokenizer {

    //return a stemmed given word
    public static String wordStem(String word) {
        PorterStemmer stemmer = new PorterStemmer();
        return stemmer.stem(word);
    }

    //Split a String into an array of words
    public static String[] splitTextToWords(String text) {
        return text.split("\\W+");
    }

    //Get Sentence including a word
    private static final Pattern END_OF_SENTENCE = Pattern.compile("\\.\\s+");

    public static String getSentence(String text, String word) {
        final String lcWord = word.toLowerCase();
        return END_OF_SENTENCE.splitAsStream(text)
                .filter(s -> s.toLowerCase().contains(lcWord))
                .findAny()
                .orElse(null);
    }

    //check whether a word is an abbreviation
    public static boolean isAbbreviation(String word) {
        switch (word.length()) {
            case (2):
                return Character.isUpperCase(word.charAt(0))
                        && Character.isUpperCase(word.charAt(1));

            case (3):
                return Character.isUpperCase(word.charAt(0))
                        && (Character.isUpperCase(word.charAt(1))
                        || (Character.isUpperCase(word.charAt(2))));

            case (4):
                return Character.isUpperCase(word.charAt(0))
                        && ((Character.isUpperCase(word.charAt(1))
                        && (!Character.isUpperCase(word.charAt(2))
                        || !Character.isUpperCase(word.charAt(3))))
                        || (Character.isUpperCase(word.charAt(2))
                        && (!Character.isUpperCase(word.charAt(1))
                        || !Character.isUpperCase(word.charAt(3))))
                        || (Character.isUpperCase(word.charAt(3))
                        && (!Character.isUpperCase(word.charAt(1))
                        || !Character.isUpperCase(word.charAt(2)))));

            default:
                return false;
        }
    }
}
