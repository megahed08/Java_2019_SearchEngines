package Crawling;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import CSVFiles.Newspaper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static CSVFiles.NewspapersManager.articlesCount;
import static CSVFiles.NewspapersManager.getArticle;

public class Crawler {
    static File TGAM_Articles_CSV_File = new File("Articles.csv");
    private static long fileLength ;
    private static Boolean enoughFlag = false;
    private final static LocalDate initialSearchDate = LocalDate.of(2019, 05, 03);
    private static LocalDate lastUpdateDate;
    private static LocalDate searchDateTill = LocalDate.now();
    public static boolean isUpdated = true;

    //The Class calling method
    public static void UpdateArticlesCrawler(Newspaper newspaper) {
        //initialFileCreation();
        fileLength = articlesCount(newspaper);
        setLastUpdateDate(LocalDate.of(
                Integer.parseInt(getArticle(newspaper.getArticleCount(), newspaper).split("-")[0]),
                Integer.parseInt(getArticle(newspaper.getArticleCount(), newspaper).split("-")[1]),
                Integer.parseInt(getArticle(newspaper.getArticleCount(), newspaper).split("-")[2])));
        if (lastUpdateDate.isEqual(searchDateTill)) {
            setIsUpdated(false);
            return;
        }
        LinkedHashSet<Article> articles = getWorldArticles();
        fileCreation(articles);
    }

    //get every World Article
    private static LinkedHashSet<Article> getWorldArticles() {
        Document document;
        LinkedHashSet<Article> articles = new LinkedHashSet<Article>();
        int searchPage = 1;
        while (enoughFlag == false) {
            try {
                document = Jsoup.connect(pageLink(searchPage)).get(); //Get Document object after parsing the html from given url.
                Elements links = document.select("a[href]");
                for (Element link : links) {
                    if (isWorldLink(link)) {
                        Article article = articleDateCheck(link.attr("abs:href"));
                        if (article != null)
                            articles.add(article);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            searchPage++;
        }
        return flipLinkedHashSet(articles);
    }

    //Checks if an article matches the date conditions given its URL
    private static Article articleDateCheck(String articleURL) {
        Article article = crawlPage(articleURL);
        String articleDate = article.date.split("T")[0];
        if (LocalDate.parse(articleDate).isEqual(lastUpdateDate.minusDays(7)))
            setEnoughFlag(true);
        if ((LocalDate.parse(articleDate).isAfter(lastUpdateDate)
                && !LocalDate.parse(articleDate).isEqual(searchDateTill))
                || LocalDate.parse(articleDate).isEqual(lastUpdateDate))
            return article;
        else
            return null;
    }

    //create a new CSV_File's initial form
    private static void initialFileCreation() {
        String firstLine = "article_id, article_url, article_author, article_text, article_headline, publication_timestamp, article_categories ";
        try {
            PrintWriter pw = new PrintWriter(TGAM_Articles_CSV_File);
            pw.println(firstLine);
            pw.println(initialSearchDate);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //updates the CSV_File to include all new Articles
    private static void fileCreation(LinkedHashSet<Article> articles) {
        long printStartLine = fileLength;            //overwrite last stored updating date
        long articleID = fileLength - 1;
        try {
            RandomAccessFile f = new RandomAccessFile(TGAM_Articles_CSV_File, "rw"); //delete last line in the file
            long length = f.length() - 1;
            byte b;
            do {
                length -= 1;
                f.seek(length);
                b = f.readByte();
            } while (b != 10 && length > 0);
            if (length == 0) {
                f.setLength(length);
            } else {
                f.setLength(length + 1);
            }
            FileWriter fr = new FileWriter(TGAM_Articles_CSV_File, true);            //append new articles
            BufferedWriter br = new BufferedWriter(fr);
            PrintWriter pw = new PrintWriter(br);
            for (Article article : articles) {
                article.id = Long.toString(articleID);
                pw.println(article);
                articleID++;
            }
            pw.println(searchDateTill);
            pw.close();
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Get an Article object after parsing the html from given url.
    private static Article crawlPage(String url) {
        Document document;
        try {
            document = Jsoup.connect(url).get();
            Article article = new Article();
            article.date = document.select("meta[property=article:published_time]").attr("content");
            article.headline = document.select("meta[property=og:title]").attr("content");
            article.url = url;
            article.categories = document.select("meta[name=keywords]").attr("content");
            Elements bodyParts = document.getElementsByClass("c-article-body__text");
            StringBuffer text = new StringBuffer();
            for (Element body : bodyParts) {
                text.append(body.text()).append(" ");
            }
            article.text = text.toString();
            article.author = document.getElementsByClass("c-byline").text();
            return article;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //returns search Page URL
    private static String pageLink(int searchPage) {
        return "https://www.theglobeandmail.com/search/?q=world&mode=news&page=" + searchPage;
    }

    //check if a link is a World Link
    private static boolean isWorldLink(Element link) {
        String url = link.attr("abs:href");
        return url.startsWith("https://www.theglobeandmail.com/world/article");
    }

    //flip the set of Articles to be in order
    private static LinkedHashSet flipLinkedHashSet(LinkedHashSet set) {
        Object[] setToArray = set.toArray();
        LinkedHashSet<Article> flipSet = new LinkedHashSet<Article>();
        for (int i = setToArray.length - 1; i >= 0; i--) {
            flipSet.add((Article) setToArray[i]);
        }
        return flipSet;
    }

    //Setter for the stop crawling more Search pages
    public static void setEnoughFlag(Boolean enoughFlag) {
        Crawler.enoughFlag = enoughFlag;
    }

    //Setter for last updated day
    public static void setLastUpdateDate(LocalDate lastUpdateDate) {
        Crawler.lastUpdateDate = lastUpdateDate;
    }

    //Setter for isUpdated, set whether the CSV File has new updates
    public static void setIsUpdated(boolean isUpdated) {
        Crawler.isUpdated = isUpdated;
    }

}