package commonutils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonfetchUtil {
    public static List<JsonNode> extractSortObjects(String jsonResponse) {
        List<JsonNode> sortObjects = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            traverseAndCollectSortObjects(root, sortObjects);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sortObjects;
    }

    private static void traverseAndCollectSortObjects(JsonNode node, List<JsonNode> sortObjects) {
        if (node.isArray()) {
            for (JsonNode item : node) {
                if (item.has("sort") && !item.get("sort").isNull()) {
                    sortObjects.add(item.get("sort"));
                }
                if (item.has("items")) {
                    traverseAndCollectSortObjects(item.get("items"), sortObjects);
                }
            }
        } else if (node.isObject()) {
            if (node.has("sort") && !node.get("sort").isNull()) {
                sortObjects.add(node.get("sort"));
            }
            if (node.has("items")) {
                traverseAndCollectSortObjects(node.get("items"), sortObjects);
            }
        }
    }
}
