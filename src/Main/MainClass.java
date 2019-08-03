package Main;

import CSVFiles.Newspaper;

import java.util.ArrayList;


import static CSVFiles.NewspapersManager.ArticlesCounter;
import static CSVFiles.NewspapersManager.createNPs;
import static Crawling.Crawler.UpdateArticlesCrawler;
import static Indexing.BM25.dictionaryScoring;
import static Indexing.Indexer.indexer;
import static Indexing.SearchClass.userInput;

public class MainClass {


    public static void main(String[] args) {
        System.out.println("--SHARKS SEARCH ENGINE--");
        //UpdateArticlesCrawler();
        System.out.println("Newspapers Indexed:\n   -DeutscheWelle\n   -NewYorkTimes\n" +
                "   -TimesOfIndia\n   -TheGlobeAndMail\n   -TheGuardian");
        ArrayList<Newspaper> newspapers = createNPs();
        System.out.println("Total number of Articles: "+ ArticlesCounter +"\n");
        System.out.println("Getting Ready . . .(please wait ~20 sec)\n");
        //upadateDictionary(newspapers);
        userInput(newspapers, true, 10);
    }

    public static void upadateDictionary(ArrayList<Newspaper> newspapers) {
        System.out.println("Updating your Dictionary");
        long startTime = System.currentTimeMillis();
        indexer(newspapers);
        long endTime = System.currentTimeMillis();
        System.out.println((double) (endTime - startTime) / 1000 + "Sec\n");
        startTime = System.currentTimeMillis();
        dictionaryScoring();
        endTime = System.currentTimeMillis();
        System.out.println((double) (endTime - startTime) / 1000 + "Sec\n");
        System.out.println("done_________________________________________________");
    }
}
