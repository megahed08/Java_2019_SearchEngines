package CSVFiles;

import java.io.File;

import static CSVFiles.NewspapersManager.articlesCount;

public class Newspaper {
    String name;
    int articleCount;
    int wordCount;
    File file;
    File wordCountFile;
    File dictionary;

    public Newspaper(String name, int wordCount) {
        this.name = name;
        this.wordCount = wordCount;
        this.file = new File("MyFiles\\" + this.getName() + "_Articles.csv");
        articleCount = articlesCount(this);
        this.wordCountFile = new File("MyFiles\\Stems\\WordsCount_" + this.getName() + ".ser");
        this.dictionary = new File("MyFiles\\Stems\\Dictionary_" + this.getName() + ".ser");
    }

    public String getName() {
        return name;
    }

    public int getArticleCount() {
        return articleCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public File getFile() {
        return file;
    }

    public File getWordCountFile() {
        return wordCountFile;
    }

    public File getDictionary() {
        return dictionary;
    }
}
