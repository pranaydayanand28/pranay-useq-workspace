package commonutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
/**
 * @author komal bharti
 */
public class TextFileReader {
	/**
	 * This method will read text file from specified path .
	 */

	public static String fileReader(String Filepath) throws IOException {
		File Fileloccs = new File(Filepath);
		FileInputStream fr = new FileInputStream(Fileloccs);
		byte[] data = new byte[(int) Fileloccs.length()];
		fr.read(data);
		String fetchedData = new String(data, "UTF-8");
		return fetchedData;
	}
}
