package commonutils;

import java.util.LinkedHashMap;

/*This class will be used to store all the values that are required to be set as global variables, environment variables and others in a map*/
public abstract class DataToShare {

    private DataToShare(){};

    private static LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();

    public static void setValue(String key, Object value){
        dataMap.put(key, value);
    }

    public static Object getValue(String key){
        return dataMap.get(key);
    }

    public static void flushMapData(){
        dataMap.clear();
    }

    public static void putIfAbsent(String key, Object value){ dataMap.putIfAbsent(key, value); } //this method will be used to set key as environment variable if it is not present as one
}
