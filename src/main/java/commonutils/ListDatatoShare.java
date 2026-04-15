package commonutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ListDatatoShare {

    /**This method used to store dynamic list object which can be used for next set of API .*/

    private static Map<String, List<List<Map<String, Object>>>> dataMap = new ConcurrentHashMap<>();

    public static void addList(String key, List<Map<String, Object>> list) {
        if (key == null || list == null) {
            throw new IllegalArgumentException("Key or list cannot be null");
        }

        List<List<Map<String, Object>>> dataList = dataMap.computeIfAbsent(key, k -> new ArrayList<>());
        dataList.add(list);

        /***Logging for debugging **/
        System.out.println("Added list for key: " + key + ", dataList size: " + dataList.size());
    }

    public static List<List<Map<String, Object>>> getAllLists(String key) {
        return dataMap.getOrDefault(key, new ArrayList<>());
    }

    public static void clearAllLists(String key) {
        dataMap.remove(key);
    }


}