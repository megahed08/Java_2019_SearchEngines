package CSVFiles;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

import static Transformation.Tokenizer.splitTextToWords;

public class NewspapersManager {
    public static long ArticlesCounter = 0;
    public static long WordsCounter = 0;

    //get a certain line data in CSV-File, output as String
    public static String getArticle(long articleNumber, Newspaper newspaper) {
        String line = "";
        try {
            BufferedReader CSV_Scanner = new BufferedReader(new FileReader(newspaper.getFile()));
            for (long counter = articleNumber; counter > 0; counter--)
                line = CSV_Scanner.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    //Split a CSV-File line into Texts, output as list of Strings
    public static String[] getArticleTexts(int articleNumber, Newspaper newspaper) {
        String article = "";
        article = getArticle(articleNumber, newspaper);
        String[] split_Line = article.split("(?<=\\\")\\|(?=\\\")");
        return split_Line;
    }

    //returns number of lines "Articles" in a Newspaper
    public static int articlesCount(Newspaper newspaper) {
        try {
            return (int) Files.lines((newspaper.getFile()).toPath()).count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //import all newspapers with their properties and update the total WordsCount and ArticlesCount
    public static ArrayList<Newspaper> createNPs() {
        ArrayList<Newspaper> newspapers = new ArrayList<Newspaper>();

        Newspaper DW = new Newspaper("DeutscheWelle", 220074);
        newspapers.add(DW);
        ArticlesCounter += DW.articleCount;
        WordsCounter += DW.wordCount;

        Newspaper NYT = new Newspaper("NewYorkTimes", 1792033);
        newspapers.add(NYT);
        ArticlesCounter += NYT.articleCount;
        WordsCounter += NYT.wordCount;

        Newspaper TOI = new Newspaper("TimesOfIndia", 487108);
        newspapers.add(TOI);
        ArticlesCounter += TOI.articleCount;
        WordsCounter += TOI.wordCount;

        Newspaper TGAM = new Newspaper("TheGlobeAndMail", 421385);
        newspapers.add(TGAM);
        ArticlesCounter += TGAM.articleCount;
        WordsCounter += TGAM.wordCount;

        Newspaper TG = new Newspaper("TheGuardian", 2813884);
        newspapers.add(TG);
        ArticlesCounter += TG.articleCount;
        WordsCounter += TG.wordCount;

        return newspapers;
    }
}
