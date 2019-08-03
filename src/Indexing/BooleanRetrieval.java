package Indexing;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class BooleanRetrieval {

    //AND operator
    public static Map<String, Double> andOp(Map<String, Double> Map1, Map<String, Double> Map2) {
        Map<String, Double> outMap = new TreeMap<>();
        outMap.putAll(Map1);
        for (String location : Map1.keySet()) {
            if (!Map2.containsKey(location))
                outMap.remove(location);
            else
                outMap.put(location, Map1.get(location) + Map2.get(location));
        }
        return outMap;
    }

    //NOT operator
    public static Map<String, Double> notOp(Map<String, Double> toEditMap, Map<String, Double> toRemoveMap) {
        for (String location : toRemoveMap.keySet())
            toEditMap.remove(location);
        return toEditMap;
    }

    //OR operator
    public static Map<String, Double> orOp(ArrayList<Map<String, Double>> maps) {
        TreeMap<String, Double> resultMap = new TreeMap<>();
        for (Map<String, Double> map : maps) {
            for (String article : map.keySet()) {
                if (!resultMap.containsKey(article))
                    resultMap.put(article, map.get(article));
                else
                    resultMap.put(article, resultMap.get(article) + map.get(article));
            }
        }
        return resultMap;
    }
}
