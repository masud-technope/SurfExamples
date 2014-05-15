package weighting;

import indexing.CFIndexManager;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import scoring.StructuralMatchScore;
import structural.InputDocProcessor;
import utility.ContextCodeLoader;
import utility.ExceptionKeyLoader;
import core.CodeFragment;
import core.StaticData;

public class MyStructureWeightManager {
	
	public MyStructureWeightManager()
	{
		//default constructor
	}
	
	protected void collectStructureScores()
	{
		//code for collecting the content similarity scores
		for(int i=1;i<=150;i++){
		int exceptionID=i;
		String contextCode=ContextCodeLoader.loadContextCode(exceptionID);
		if(contextCode.isEmpty())continue; //avoid non-existent exception
		ArrayList<CodeFragment> codeFragments=CFIndexManager.readCodeFragment(exceptionID);
		String exceptionName=ExceptionKeyLoader.getExceptionName(exceptionID);
		InputDocProcessor inputProcessor=new InputDocProcessor(exceptionName, contextCode);
		CodeFragment queryFragment=inputProcessor.extractInputDocumentInfo();
		StructuralMatchScore structural=new StructuralMatchScore(queryFragment, codeFragments);
		codeFragments=structural.collectStructuralScores();
		saveStructureScores(exceptionID, codeFragments);
		}
	}
	
	protected void saveStructureScores(int exceptionID, ArrayList<CodeFragment> Fragments)
	{
		// code for saving the content scores
		String filePath = StaticData.Surf_Data_Base + "/weights/structure/" + exceptionID+ ".txt";
		try {
			FileWriter fwriter = new FileWriter(new File(filePath));
			int counter = 0;
			for (CodeFragment fragment : Fragments) {
				String line = fragment.FragmentID + "\t" + fragment.FieldMatchScore + "\t"
						+ fragment.MethodMatchScore+"\t"+fragment.CodeObjectMatchScore+"\t"+
						fragment.DependencyMatchScore;
				fwriter.write(line + "\n");
				counter++;
			}
			fwriter.close();
			System.out.println("Structure score saved for "+exceptionID);
		} catch (Exception exc) {
			// handle the exception
		}
	}
	
	public static void main(String[] args){
		new MyStructureWeightManager().collectStructureScores();	
	}
}
