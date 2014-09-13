package weighting;

import indexing.CFIndexManager;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import scoring.CodeQualityScore;
import scoring.HandlerCodeScore;
import utility.ContextCodeLoader;
import core.CodeFragment;
import core.StaticData;

public class MyCQEHScoreManager {

	public MyCQEHScoreManager() {
		// default constructor
	}

	public void collectCQAndCHScores() {
		// collect code quality and exception handler code scores
//		for (int i = 1; i <= 150; i++) {
//			int exceptionID = i;
//			String contextCode = ContextCodeLoader.loadContextCode(exceptionID);
//			if (contextCode.isEmpty())
//				continue; // avoid non-existent exception
			ArrayList<CodeFragment> codeFragments = CFIndexManager
					.readAllFragments();// readCodeFragment(exceptionID);
			CodeQualityScore qualityScore = new CodeQualityScore(codeFragments);
			codeFragments = qualityScore
					.collectCodeQualityScores();
			HandlerCodeScore handlerScore = new HandlerCodeScore(codeFragments);
			codeFragments = handlerScore.collectExceptionHandlerQualityScores();
			//save the scores
			for (int i = 1; i <= 150; i++){
				int exceptionID = i;
				String contextCode = ContextCodeLoader.loadContextCode(exceptionID);
				if (contextCode.isEmpty())
		    	continue; // avoid non
				saveCQCEHScores(exceptionID, codeFragments);
			}
		//}
	}

	protected void saveCQCEHScores(int exceptionID,
			ArrayList<CodeFragment> Fragments) {
		// code for saving the scores
		String filePath = StaticData.Surf_Data_Base + "/weights/quality/"
				+ exceptionID + ".txt";
		try {
			FileWriter fwriter = new FileWriter(new File(filePath));
			int counter = 0;
			for (CodeFragment fragment : Fragments) {
				String line = fragment.FragmentID + "\t" + fragment.ReadabilityScore + "\t"
						+ fragment.StatementCountScore + "\t"
						+ fragment.HandlerToCodeRatio;
				fwriter.write(line + "\n");
				counter++;
			}
			fwriter.close();
			System.out.println("Quality score saved for " + exceptionID);
		} catch (Exception exc) {
			// handle the exception
		}
	}
	public static void main(String[] args) {
		new MyCQEHScoreManager().collectCQAndCHScores();

	}
}
