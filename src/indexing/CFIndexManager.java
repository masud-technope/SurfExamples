package indexing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import utility.ExceptionKeyLoader;
import core.CodeFile;
import core.CodeFragment;
import core.StaticData;
import extractor.SourceCodeElementExtractor;

public class CFIndexManager {

	public CFIndexManager()
	{
		//default constructor
	}
	
	protected void developCodeFragmentIndex(int exceptionID, String exceptionName)
	{
		//code for developing the code fragment Index
		ArrayList<CodeFile> codeFiles=collectCodeFiles(exceptionID, exceptionName);
		SourceCodeElementExtractor extractor = new SourceCodeElementExtractor(
				exceptionName, codeFiles);
		ArrayList<CodeFragment> codeFragments = extractor
				.collectCodeFragments();
		int saved=saveCodeFragmentIndex(exceptionID, codeFragments);
		saveCodeFragments(exceptionID, codeFragments);
	}
	
	protected int saveCodeFragmentIndex(int exceptionID,
			ArrayList<CodeFragment> Fragments) {
		// code for saving the code fragment index
		int completed=0;
		try {
			String fragmentFolder = StaticData.Surf_Data_Base + "/fragmentsIndex/"
					+ exceptionID;
			File fDir = new File(fragmentFolder);
			if (!fDir.exists())
				fDir.mkdir();
			int count = 0;
			for (CodeFragment codeFragment : Fragments) {
				String outFile = fragmentFolder + "/" + count + ".ser";
				FileOutputStream fstream = new FileOutputStream(new File(
						outFile));
				ObjectOutputStream ostream = new ObjectOutputStream(fstream);
				ostream.writeObject(codeFragment);
				ostream.close();
				count++;
			}
			if(count>0)completed=1;
			System.out.println("Exception ID:" + exceptionID
					+ " Fragments saved:" + count);
		} catch (Exception exc) {
			// handle the exception
			exc.printStackTrace();
		}
		return completed;
	}
	protected void saveCodeFragments(int exceptionID,
			ArrayList<CodeFragment> Fragments)
	{
		String folder=StaticData.Surf_Data_Base+"/fragments/"+exceptionID;
		File fdir=new File(folder);
		if(!fdir.exists())fdir.mkdir();
		
		for(CodeFragment fragment:Fragments){
			String fileID=StaticData.Surf_Data_Base+"/fragments/"+exceptionID+"/"+fragment.FragmentID+".txt";
			File f2=new File(fileID);
			try {
				FileWriter fwriter=new FileWriter(f2);
				fwriter.write(fragment.CompleteCode);
				fwriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	protected ArrayList<CodeFile> collectCodeFiles(int exceptionID,
			String exceptionName) {
		// code for collecting code files
		ArrayList<CodeFile> codeFiles = new ArrayList<>();
		String codeFolder = StaticData.Surf_Data_Base + "/codes/" + exceptionID;
		File f = new File(codeFolder);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File f2 : files) {
				try {
					Scanner scanner = new Scanner(f2);
					String completeCode = new String();
					while (scanner.hasNext()) {
						String line = scanner.nextLine();
						completeCode += line + "\n";
					}
					scanner.close();
					CodeFile cfile = new CodeFile();
					cfile.ExceptionName = exceptionName;
					cfile.CompleteCode = completeCode;
					cfile.localFileName=f2.getName();
					codeFiles.add(cfile);
				} catch (Exception exc) {
				}
			}
		}
		// returning the code files
		return codeFiles;
	}
	
	public static ArrayList<CodeFragment> readCodeFragment(int exceptionID)
 {
		// code for reading the code fragment saved as a serialized object
		ArrayList<CodeFragment> Fragments = new ArrayList<>();
		try {
			String folderPath = StaticData.Surf_Data_Base + "/fragmentsIndex/"
					+ exceptionID;
			File fDir = new File(folderPath);
			int filecount = fDir.list().length;
			for (int i = 0; i < filecount; i++) {
				String serFile = folderPath + "/" + i + ".ser";
				FileInputStream fstream = new FileInputStream(new File(serFile));
				ObjectInputStream oistream = new ObjectInputStream(fstream);
				CodeFragment cfragment = (CodeFragment) oistream.readObject();
				// storing the fragment extracted
				Fragments.add(cfragment);
			}
			System.out.println("Exception ID:"+exceptionID+" Fragments recovered:"+Fragments.size());
		} catch (Exception exc) {
			// handle the exception
		}
		return Fragments;
	}
	
	public static ArrayList<CodeFragment> readAllFragments()
	{
		ArrayList<CodeFragment> Fragments = new ArrayList<>();
		try {
			String folderPath = StaticData.Surf_Data_Base + "/fragmentsIndexAll";
			File fDir = new File(folderPath);
			int filecount = fDir.list().length;
			for (int i = 0; i < filecount; i++) {
				String serFile = folderPath + "/" + i + ".ser";
				FileInputStream fstream = new FileInputStream(new File(serFile));
				ObjectInputStream oistream = new ObjectInputStream(fstream);
				CodeFragment cfragment = (CodeFragment) oistream.readObject();
				// storing the fragment extracted
				//updating the fragment ID
				cfragment.FragmentID=i;
				Fragments.add(cfragment);
			}
			//System.out.println("Exception ID:"+exceptionID+" Fragments recovered:"+Fragments.size());
		} catch (Exception exc) {
			// handle the exception
		}
		return Fragments;
	}
	
	
	public static int getFileCount(int exceptionID){
		//returning the number of fragments
		String folderPath = StaticData.Surf_Data_Base + "/fragmentsIndex/"
				+ exceptionID;
		File fdir=new File(folderPath);
		return fdir.list().length;
	}
	
	public static int getLocalRepoFileCount()
	{
		String folderPath = StaticData.Surf_Data_Base + "/fragmentsIndexAll";
		File fdir=new File(folderPath);
		return fdir.list().length;
	}
	
	
	public static void main(String[] args){
		
		for(int i=1;i<=150;i++){
		if(new File(StaticData.Surf_Data_Base+"/querycodes/"+i+".txt").exists()==false){
			continue;
		}
		CFIndexManager manager=new CFIndexManager();
		int exceptionID=i;
		String exceptionName=ExceptionKeyLoader.getExceptionName(exceptionID);
		System.out.println("Current exception:"+exceptionName);
		manager.developCodeFragmentIndex(exceptionID, exceptionName);
		//manager.readCodeFragment(exceptionID);
		}
		
	}
}
