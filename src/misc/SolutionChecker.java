package misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import core.CodeFragment;
import core.ECodeFragment;
import core.StaticData;

public class SolutionChecker {
	
	public static boolean matchSolutionCode(int exceptionID, int solFragID, int candFragID){
		boolean matched=false;
		try{
			String solFile=StaticData.Surf_Data_Base+"/solution/solfiles/"+exceptionID+"/"+solFragID+".txt";
			//String candidate=StaticData.Surf_Data_Base+"/hIndex/"+exceptionID+"/"+candFragID+".ser";
			String candidate=StaticData.Surf_Data_Base+"/hIndexAll/"+candFragID+".ser";
			String completeCode1=getFileContent(solFile);
			String completeCode2=getSerializedContent2(candidate);
			if(completeCode1.contains(completeCode2))
				matched=true;
			else matched=false;
		}catch(Exception exc){
			//hanld the exception
		}
		return matched;
	}
	public static boolean matchmySolutionCode(int exceptionID, int solFragID, int candFragID){
		boolean matched=false;
		try{
			String solFile=StaticData.Surf_Data_Base+"/solution/solfiles/"+exceptionID+"/"+solFragID+".txt";
			String candidate=StaticData.Surf_Data_Base+"/fragmentsIndex/"+exceptionID+"/"+candFragID+".ser";
			//String candidate=StaticData.Surf_Data_Base+"/fragmentsIndexAll/"+candFragID+".ser";
			String completeCode1=getFileContent(solFile);
			String completeCode2=getSerializedContent1(candidate);
			if(completeCode1.contains(completeCode2))
				matched=true;
			else matched=false;
		}catch(Exception exc){
			//hanld the exception
		}
		return matched;
	}
	
	
	
	static String getFileContent(String solFile) {
		String content = new String();
		try {
			File f = new File(solFile);
			Scanner scanner = new Scanner(f);
			while (scanner.hasNext()) {
				content += scanner.nextLine() + "\n";
			}
			scanner.close();
		} catch (Exception exc) {
			// handle it
		}
		return content;
	}
	
	static String getSerializedContent1(String fileName)
	{
		String completeCode=new String();
		try {
			FileInputStream fis=new FileInputStream(new File(fileName));
			ObjectInputStream ois=new ObjectInputStream(fis);
			CodeFragment ecf=(CodeFragment)ois.readObject();
			completeCode=ecf.CompleteCode;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return completeCode;	
	}
	
	static String getSerializedContent2(String fileName)
	{
		String completeCode=new String();
		try {
			FileInputStream fis=new FileInputStream(new File(fileName));
			ObjectInputStream ois=new ObjectInputStream(fis);
			ECodeFragment ecf=(ECodeFragment)ois.readObject();
			completeCode=ecf.CompleteCode;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return completeCode;	
	}
}
