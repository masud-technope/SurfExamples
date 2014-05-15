package weighting;

import indexing.CFIndexManager;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import core.CodeFragment;
import core.StaticData;
import scoring.LexicalMatchScore;
import utility.ContextCodeLoader;
public class MyContentWeightManager {
	

	public MyContentWeightManager()
	{
		//default constructor
	}

	protected void collectContentScores()
	{
		//code for collecting the content similarity scores
		for(int i=1;i<=150;i++){
		int exceptionID=i;
		String contextCode=ContextCodeLoader.loadContextCode(exceptionID);
		if(contextCode.isEmpty())continue; //avoid non-existent exception
		ArrayList<CodeFragment> codeFragments=CFIndexManager.readCodeFragment(exceptionID);
		LexicalMatchScore lexical=new LexicalMatchScore(contextCode, codeFragments);
		codeFragments=lexical.collectLexicalMatchScore();
		saveContentScores(exceptionID, codeFragments);
		}
	}
	
	protected void saveContentScores(int exceptionID, ArrayList<CodeFragment> Fragments)
	{
		// code for saving the content scores
		String filePath = StaticData.Surf_Data_Base + "/weights/content/" + exceptionID+ ".txt";
		try {
			FileWriter fwriter = new FileWriter(new File(filePath));
			int counter = 0;
			for (CodeFragment fragment : Fragments) {
				String line = fragment.FragmentID + "\t" + fragment.CodeCloneScore + "\t"
						+ fragment.CosineSimilarityScore;
				fwriter.write(line + "\n");
				counter++;
			}
			fwriter.close();
			System.out.println("Content score saved for "+exceptionID);
		} catch (Exception exc) {
			// handle the exception
		}
	}
	
	public static void main(String[] args){
		new MyContentWeightManager().collectContentScores();
	}
}
