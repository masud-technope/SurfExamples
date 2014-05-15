package utility;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import core.StaticData;

public class ExceptionKeyLoader {

	public static HashMap<String, String> loadExceptions() {
		// code for loading the exception
		HashMap<String, String> exceptionMap = new HashMap<>();
		String excepList = StaticData.Surf_Data_Base + "/exceplist.txt";
		File f = new File(excepList);
		try {
			Scanner scanner = new Scanner(f);
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] parts = line.split("\\s+");
				String key = parts[0].trim();
				String exception = parts[1].trim();
				exceptionMap.put(key, exception);
			}
			scanner.close();
		} catch (Exception exc) {
			// handle the exception
		}
		return exceptionMap;
	}
	public static String getExceptionName(int exceptionID){
		// code for getting the exception name
		HashMap<String, String> exceptionMap = loadExceptions();
		String key = exceptionID + ".txt";
		return exceptionMap.get(key);
	}
	
	
}
