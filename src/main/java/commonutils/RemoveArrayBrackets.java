package commonutils;

public class RemoveArrayBrackets {

    /*** This method is used to  modify map or list where we want to make proper query json param  **/
    public static String removeArrayBrackets(String jsonArrayString) {
        /** Check if the string starts and ends with brackets **/
        if (jsonArrayString.startsWith("[") && jsonArrayString.endsWith("]")) {
            /** Remove the first and last character ([ and ]) **/
            return jsonArrayString.substring(1, jsonArrayString.length() - 1);
        } else {
            /*** Return original string if it doesn't start and end with brackets **/
            return jsonArrayString;
        }}
}
