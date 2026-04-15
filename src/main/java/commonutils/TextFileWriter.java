package commonutils;

import java.io.FileWriter;
import java.io.IOException;
/**
 * 
 * @author komal bharti
 *
 */
public class TextFileWriter {

	/**
	 * fileWriter method used for writing run time value in the file using file
	 * writer function
	 * 
	 * @throws IOException
	 */
	

	public static void fileWriter(String FilePath, String data) throws IOException {
		try {
			FileWriter fw = new FileWriter(FilePath);
			fw.write(data);
			fw.close();

		} catch (Exception e) {
			System.out.println("unable");
		}

		
	}

}