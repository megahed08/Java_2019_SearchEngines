package CSVFiles;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;

public class FileManager {
    //store a treeMap in a file
    public static void storeMapFile(Map map, File destination) {
        try {
            FileOutputStream fos = new FileOutputStream(destination);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //store a Nested TreeMap in a file
    public static void storeNestedMapFile(Map map, File destination) {
        try (ObjectOutput objectOutputStream = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(destination, false)))) {
            objectOutputStream.writeObject(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Read a Dictionary to a treeMap
    public static TreeMap readDictionaryFile(File file) {
        TreeMap tree = null;
        try (ObjectInput objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            tree = (TreeMap<String, TreeMap<String, LinkedHashSet<String>>>) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tree;
    }

    //Read a WordCountingMapFile to a treeMap
    public static TreeMap readWordCountMapFile(File file) {
        TreeMap tree = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            tree = (TreeMap<String, String>) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tree;
    }

    //Read a ScoresFile to a treeMap
    public static TreeMap readScoresFile(File file) {
        TreeMap tree = null;
        try (ObjectInput objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            tree = (TreeMap<String, TreeMap<String, Double>>) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tree;
    }
}
