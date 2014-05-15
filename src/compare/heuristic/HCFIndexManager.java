package compare.heuristic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import utility.ExceptionKeyLoader;
import core.CodeFile;
import core.ECodeFragment;
import core.StaticData;

public class HCFIndexManager {
	
	
	public HCFIndexManager()
	{
		//default constructor
	}
	protected void developCodeFragmentIndex(int exceptionID,
			String exceptionName) {
		// code for developing the code fragment Index
		ArrayList<CodeFile> codeFiles = collectCodeFiles(exceptionID,
				exceptionName);
		HSourceCodeElementExtractor extractor = new HSourceCodeElementExtractor(
				exceptionName, codeFiles);
		ArrayList<ECodeFragment> codeFragments = extractor
				.collectCodeFragments();
		int saved = saveCodeFragmentIndex(exceptionID, codeFragments);
	}

	protected int saveCodeFragmentIndex(int exceptionID,
			ArrayList<ECodeFragment> Fragments) {
		// code for saving the code fragment index
		int completed=0;
		try {
			String fragmentFolder = StaticData.Surf_Data_Base + "/hIndex/"
					+ exceptionID;
			File fDir = new File(fragmentFolder);
			if (!fDir.exists())
				fDir.mkdir();
			int count = 0;
			for (ECodeFragment codeFragment : Fragments) {
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
	public static ArrayList<ECodeFragment> readCodeFragment(int exceptionID)
	 {
			// code for reading the code fragment saved as a serialized object
			ArrayList<ECodeFragment> Fragments = new ArrayList<>();
			try {
				String folderPath = StaticData.Surf_Data_Base + "/hIndex/"
						+ exceptionID;
				File fDir = new File(folderPath);
				int filecount = fDir.list().length;
				for (int i = 0; i < filecount; i++) {
					String serFile = folderPath + "/" + i + ".ser";
					FileInputStream fstream = new FileInputStream(new File(serFile));
					ObjectInputStream oistream = new ObjectInputStream(fstream);
					ECodeFragment cfragment = (ECodeFragment) oistream.readObject();
					//System.out.println(cfragment.FragmentID);
					// storing the fragment extracted
					Fragments.add(cfragment);
				}
				System.out.println("Exception ID:"+exceptionID+" Fragments recovered:"+Fragments.size());
			} catch (Exception exc) {
				// handle the exception
			}
			return Fragments;
		}
	
	public static ArrayList<ECodeFragment> readAllFragments()
	{
		//code for reading all code fragments
		ArrayList<ECodeFragment> Fragments = new ArrayList<>();
		try{
		String folderPath=StaticData.Surf_Data_Base+"/hIndexAll";
		File fDir = new File(folderPath);
		int filecount = fDir.list().length;
		for (int i = 0; i < filecount; i++) {
			String serFile = folderPath + "/" + i + ".ser";
			FileInputStream fstream = new FileInputStream(new File(serFile));
			ObjectInputStream oistream = new ObjectInputStream(fstream);
			ECodeFragment cfragment = (ECodeFragment) oistream.readObject();
			//updating the fragment
			cfragment.FragmentID=i;
			//System.out.println(cfragment.FragmentID);
			// storing the fragment extracted
			Fragments.add(cfragment);
		}
		}catch(Exception exc){
			exc.printStackTrace();
		}
		return Fragments;
	}
	
	
	public void save2CodeFragment(ArrayList<ECodeFragment> codeFragments, int exceptionID)
	{
		//code for saving the code fragments
		try{
			String excepFolder=StaticData.Surf_Data_Base+"/hfragments/"+exceptionID;
			File fDir=new File(excepFolder);
			if(!fDir.exists()){
				fDir.mkdir();
				//now save the code fragments
				for(ECodeFragment ecf:codeFragments){
					String fileName=excepFolder+"/"+ecf.FragmentID+".txt";
					FileWriter fwriter=new FileWriter(new File(fileName));
					fwriter.write(ecf.CompleteCode);
					fwriter.close();
				}
			}
			System.out.println("Code fragments saved:"+codeFragments.size());
			
		}catch(Exception exc){
			//handle the exception
		}
		
	}
	
	
public static void main(String[] args){
		
		for(int i=1;i<=150;i++){
		if(new File(StaticData.Surf_Data_Base+"/querycodes/"+i+".txt").exists()==false){
			continue;
		}
		HCFIndexManager manager=new HCFIndexManager();
		int exceptionID=i;
		String exceptionName=ExceptionKeyLoader.getExceptionName(exceptionID);
		System.out.println("Current exception:"+exceptionName);
		manager.developCodeFragmentIndex(exceptionID, exceptionName);
		//ArrayList<ECodeFragment> frags=manager.readCodeFragment(exceptionID);
		//manager.save2CodeFragment(frags, exceptionID);
		}	
	}
}
