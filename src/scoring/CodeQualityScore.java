package scoring;

import java.util.ArrayList;
import core.CodeFragment;

public class CodeQualityScore {

	ArrayList<CodeFragment> Fragments;

	public CodeQualityScore(ArrayList<CodeFragment> Fragments) {
		// initialization
		this.Fragments = Fragments;
	}

	public double getReadabilityScore(CodeFragment codeFragment) {
		// code for getting readability score
		double readability = 0;
		try {
			readability = raykernel.apps.readability.eval.Main
					.getReadability(codeFragment.CompleteCode);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return readability;
	}

	public ArrayList<CodeFragment> collectCodeQualityScores() {
		// code for collecting code quality score
		try {
			for (CodeFragment cfragment : this.Fragments) {
				double readability = getReadabilityScore(cfragment);
				cfragment.ReadabilityScore = readability;
			}
		} catch (Exception exc) {
			// handle the exception
		}
		return this.Fragments;
	}

}
