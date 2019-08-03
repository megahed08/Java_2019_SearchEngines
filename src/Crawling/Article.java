package Crawling;

public class Article {
    String url;
    String date;
    String author;
    String categories;
    String id;
    String text;
    String headline;

    public String toString() {
        String article = "";
        article += "\"" + id + "\"" + ", ";
        article += "\"" + url + "\"" + ", ";
        article += "\"" + author + "\"" + ", ";
        article += "\"" + text + "\"" + ", ";
        article += "\"" + headline + "\"" + ", ";
        article += "\"" + date + "\"" + ", ";
        article += "\"" + categories + "\"" + ", ";

        return article;
    }
}
