package com.multiassetsearch;

import io.restassured.path.json.JsonPath;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class multiAssetUtils {

    // Remaps the filter configurations by extracting nested filter and sort configs
    public static Map<String, Object> remapFilterConfig(Map<String, Object> filters) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                if (nestedMap.containsKey("filterConfig")) {
                    result.put(key, nestedMap.get("filterConfig"));
                }
                if (nestedMap.containsKey("sortConfig")) {
                    result.put(key, nestedMap.get("sortConfig"));
                }
                if (nestedMap.containsKey("subItemsFilter")) {
                    result.putAll(remapFilterConfig((Map<String, Object>) nestedMap.get("subItemsFilter")));
                } else {
                    result.putAll(remapFilterConfig(nestedMap));
                }
            }
        }
        return result;
    }

    // Processes the filter configurations from a JSONPath and builds a structured map
    public static Map<String, Object> processFilterConfigs(JsonPath jsonPath) {
        List<Map<String, Object>> filterConfigs = jsonPath.getList("data.filterConfig");
        Map<String, Object> smallcaseFilters = new HashMap<>();
        for (Map<String, Object> filterConfig : filterConfigs) {
            String filterId = (String) filterConfig.get("id");
            List<Map<String, Object>> items = (List<Map<String, Object>>) filterConfig.get("items");
            Map<String, Object> filterItems = new HashMap<>();
            buildFilterItems(items, filterItems);
            if (!filterItems.isEmpty()) {
                smallcaseFilters.put(filterId, filterItems);
            }
        }
        return smallcaseFilters;
    }

    // Recursively builds filter items and handles nested sub-items
    public static void buildFilterItems(List<Map<String, Object>> items, Map<String, Object> filterItems) {
        for (Map<String, Object> item : items) {
            String itemId = (String) item.get("id");
            List<Map<String, Object>> filters = (List<Map<String, Object>>) item.get("filter");
            Map<String, Object> itemDetails = new HashMap<>();
            itemDetails.put("filterConfig", filters);
            List<Map<String, Object>> subItems = (List<Map<String, Object>>) item.get("subItems");
            if (subItems != null && !subItems.isEmpty()) {
                Map<String, Object> subItemsMap = new HashMap<>();
                buildFilterItems(subItems, subItemsMap);
                itemDetails.put("subItemsFilter", subItemsMap);
            }
            filterItems.put(itemId, itemDetails);
        }
    }

    // Processes sort configurations from a JSONPath and builds a structured map for sorts
    public static Map<String, Object> processSortConfigs(JsonPath jsonPath) {
        List<Map<String, Object>> sortConfigs = jsonPath.getList("data.sortConfig");
        Map<String, Object> stockSorts = new HashMap<>();
        for (Map<String, Object> sortConfig : sortConfigs) {
            String sortId = (String) sortConfig.get("id");
            String type = (String) sortConfig.get("type");
            Map<String, Object> newSortConfig = new HashMap<>();
            if ("single-toggle".equals(type)) {
                newSortConfig.put("sortConfig", Collections.singletonList(sortConfig.get("sort")));
            } else if ("multi-toggle-selector".equals(type)) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) sortConfig.get("items");
                for (Map<String, Object> item : items) {
                    String itemId = (String) item.get("id");
                    Map<String, Object> sortItem = (Map<String, Object>) item.get("sort");
                    Map<String, Object> itemConfig = new HashMap<>();
                    List<Map<String, Object>> itemSortConfigList = Collections.singletonList(sortItem);
                    itemConfig.put("sortConfig", itemSortConfigList);
                    newSortConfig.put(itemId, itemConfig);
                }
            }
            stockSorts.put(sortId, newSortConfig);
        }
        return stockSorts;
    }

}
