package misc;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import utility.ExceptionKeyLoader;
import core.CodeFile;
import core.StaticData;

public class CodeFileLoader {
	
	public static ArrayList<CodeFile> loadCodeFiles(int exceptionID)
	{
		ArrayList<CodeFile> codeFiles=new ArrayList<>();
		String folderName=StaticData.Surf_Data_Base+"/codes/"+exceptionID;
		File f=new File(folderName);
		File[] files=f.listFiles();
		for(File f2:files){
			try {
				Scanner scanner = new Scanner(f2);
				String completeCode = new String();
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					completeCode += line + "\n";
				}
				CodeFile cfile = new CodeFile();
				cfile.ExceptionName = ExceptionKeyLoader.getExceptionName(exceptionID);
				cfile.CompleteCode = completeCode;
				codeFiles.add(cfile);
			} catch (Exception exc) {
			}
		}
		return codeFiles;
	}

}
