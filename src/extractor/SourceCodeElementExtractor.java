package extractor;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import structural.SourceDocumentProcessor;
import core.CodeFile;
import core.CodeFragment;
import core.ECodeFragment;
import core.StaticData;

public class SourceCodeElementExtractor {

	ArrayList<CodeFile> codeFiles;
	ArrayList<CodeFragment> Fragments;
	String exceptionName;
	
	public SourceCodeElementExtractor(String exceptionName, ArrayList<CodeFile> codeFiles)
	{
		//initialization
		this.codeFiles=codeFiles;
		this.Fragments=new ArrayList<>();
		this.exceptionName=exceptionName;
	}
	
	protected ArrayList<CodeFragment> updateCodepath(String rawURL, String htmlURL,String localFileName,  ArrayList<CodeFragment> fragments)
	{
		//update the fragment path
		for(CodeFragment fragment:fragments){
			fragment.rawFileURL=rawURL;
			fragment.htmlFileURL=htmlURL;
			fragment.sourceFileID=localFileName;
		}
		return fragments;
	}
	protected ArrayList<CodeFragment> addFragmentID(ArrayList<CodeFragment> fragments)
	{
		//code for adding a sequential ID to the fragments
		int ID=0;
		for(CodeFragment fragment:fragments){
			fragment.FragmentID=ID;
			ID++;
		}
		return fragments;
	}
	
	
	public ArrayList<CodeFragment> collectCodeFragments()
	{
		// code for collecting code fragments
		for (CodeFile codeFile : this.codeFiles) {
			SourceDocumentProcessor sourceProcessor = new SourceDocumentProcessor(
					exceptionName, codeFile.CompleteCode);
			ArrayList<CodeFragment> fragments = sourceProcessor
					.collectExtractedCodeFragments();
			if (fragments.size() > 0) {
				fragments = updateCodepath(codeFile.rawFileURL,
						codeFile.htmlFileURL,codeFile.localFileName, fragments);
				//accumulating the fragments
				this.Fragments.addAll(fragments);
			}
		}
		//adding the fragment ID
				this.Fragments=addFragmentID(this.Fragments);
		return this.Fragments;
	}
	
	protected void saveCodeFragments(int exceptionID, ArrayList<CodeFragment> codeFragments)
	{
		//code for saving the code fragments
		try{
			String fragmentFolder=StaticData.Surf_Data_Base+"/fragments/"+exceptionID;
			File fDir=new File(fragmentFolder);
			if(!fDir.exists())fDir.mkdir();
			int count=0;
			for (CodeFragment fragment : codeFragments) {
				FileWriter fwriter = new FileWriter(new File(fragmentFolder
						+ "/" + count + ".txt"));
				fwriter.write(fragment.CompleteCode);
				fwriter.close();
				count++;
			}
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		int exceptionID=141;
		String exceptionName = "SWTException";
		
		ArrayList<CodeFile> codeFiles = new ArrayList<>();
		String codeFolder = StaticData.Surf_Data_Base + "/codes/"+exceptionID;
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
					CodeFile cfile = new CodeFile();
					cfile.ExceptionName = exceptionName;
					cfile.CompleteCode = completeCode;
					codeFiles.add(cfile);
				} catch (Exception exc) {
				}
			}
			// now perform the extraction
			SourceCodeElementExtractor extractor = new SourceCodeElementExtractor(
					exceptionName, codeFiles);
			ArrayList<CodeFragment> codeFragments = extractor
					.collectCodeFragments();
			extractor.saveCodeFragments(exceptionID, codeFragments);
			System.out.println("Fragments collected:"+codeFragments.size());
		}
	}
}
