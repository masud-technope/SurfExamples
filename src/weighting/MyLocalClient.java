package weighting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import utility.ExceptionKeyLoader;
import core.CodeFile;
import core.CodeFragment;
import core.StaticData;
import extractor.SourceCodeElementExtractor;
import misc.CodeFileLoader;

public class MyLocalClient {

	/**
	 * @param args
	 */
	int exceptionID;
	public MyLocalClient(int exceptionID)
	{
		//initialization
		this.exceptionID=exceptionID;
	}
	
	protected void extractSaveCodeFragments()
	{
		//code for adding and save code fragments
		ArrayList<CodeFile> codeFiles=CodeFileLoader.loadCodeFiles(exceptionID);
		String exceptionName=ExceptionKeyLoader.getExceptionName(exceptionID);
		SourceCodeElementExtractor extractor=new SourceCodeElementExtractor(exceptionName, codeFiles);
		ArrayList<CodeFragment> codeFragments=extractor.collectCodeFragments();
		
		String indexFolder=StaticData.Surf_Data_Base+"/fragmentsIndex/"+exceptionID;
		File findex=new File(indexFolder);
		if(!findex.exists())findex.mkdir();
		String fragFolder=StaticData.Surf_Data_Base+"/fragments/"+exceptionID;
		File ffrag=new File(fragFolder);
		if(!ffrag.exists())ffrag.mkdir();
		
		saveFragments(codeFragments);
		System.out.println(exceptionID+": Extracted and saved:"+codeFragments.size());
	}
	
	protected void saveFragments(ArrayList<CodeFragment> fragments) {
		// code for saving the fragments
		for (CodeFragment cfragment : fragments) {
			int id = cfragment.FragmentID;
			String code = cfragment.CompleteCode;
			String fragFileName = StaticData.Surf_Data_Base + "/fragments/"
					+ exceptionID + "/" + id + ".txt";
			String indexFileName = StaticData.Surf_Data_Base
					+ "/fragmentsIndex/" + exceptionID + "/" + id + ".ser";
			saveSingleFragment(fragFileName, code);
			saveSingleObject(indexFileName, cfragment);
		}
	}
	protected void saveSingleFragment(String fileName, String content){
		//code for saving the code
		try {
			FileWriter fileWriter=new FileWriter(new File(fileName));
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Failed for "+fileName);
		}
	}
	protected void saveSingleObject(String fileName, CodeFragment cfragment){
		//code for saving the object
		try {
			ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(new File(fileName)));
			oos.writeObject(cfragment);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Failed for :"+fileName);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(int i=3;i<=150;i++){
		int exceptionID=i;
		if(new File(StaticData.Surf_Data_Base+"/querycodes/"+i+".txt").exists()==false){
			continue;
		}
		MyLocalClient client=new MyLocalClient(exceptionID);
		client.extractSaveCodeFragments();
		}
	}

}
