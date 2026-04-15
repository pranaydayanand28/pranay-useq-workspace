package commonutils;

import lombok.SneakyThrows;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadJSON {

    static JSONParser parser = new JSONParser();

    @SneakyThrows
    public static String readJsonAndGetAsString(String filePath, String key){
        Object obj= parser.parse(new FileReader(filePath));
        JSONObject json = (JSONObject) obj;
        return (String)json.get(key);
    }
    @SneakyThrows
    public static boolean readJsonAndGetAsBoolean(String filePath, String key){
         Object obj= parser.parse(new FileReader(filePath));
         JSONObject json = (JSONObject) obj;
        return (Boolean)json.get(key);
    }
    @SneakyThrows
    public static int readJsonAndGetAsInteger(String filePath, String key){
        Object obj= parser.parse(new FileReader(filePath));
        JSONObject json = (JSONObject) obj;
        return (Integer)json.get(key);
    }
    @SneakyThrows
    public static double readJsonAndGetAsDouble(String filePath, String key){
        Object obj= parser.parse(new FileReader(filePath));
        JSONObject json = (JSONObject) obj;
        return (Double)json.get(key);
    }

    @SneakyThrows
    public static String readJsonFileAsString(String filePath){
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}
