package scoring;
import java.util.ArrayList;
import utility.MyTokenizer;
import visitor.CustomCatchClause;
import core.CodeFragment;

public class HandlerCodeScore {

	ArrayList<CodeFragment> Fragments;
	public HandlerCodeScore(ArrayList<CodeFragment> Fragments) {
		this.Fragments = Fragments;
	}
	
	protected double getHandlerStatementCount(CodeFragment targetFragment) {
		// code for getting the average number of statements in handler
		int total_lines = 0;
		double avg_lines=0;
		ArrayList<CustomCatchClause> handlers = targetFragment.handlers;
		for (CustomCatchClause myclause : handlers) {
			String formattedCode = MyTokenizer.format_the_code(myclause.catchBlock);
			String lines[] = formattedCode.split("\n");
			total_lines += lines.length;
		}
		if (handlers.size() > 0) {
			avg_lines = (double) total_lines / handlers.size();
		}
		// returning average no of line/statement in the handler
		return avg_lines;
	}

	protected int getHandlerCount(CodeFragment targetFragment) {
		// returning the number of handlers
		return targetFragment.handlers.size();
	}
	
	protected double getHandlerCodeRatio(CodeFragment targetFragment)
	{
		double handler2CodeRatio=0;
		String formattedCode=MyTokenizer.format_the_code(targetFragment.CompleteCode);
		//String lines[]=formattedCode.split("\n");
		String lines[]=formattedCode.split("\\;");
		double handlercodes=targetFragment.StatementCountScore*targetFragment.HandlerCountScore;
		handler2CodeRatio=handlercodes/lines.length;
		return handler2CodeRatio;
	}
	

	public ArrayList<CodeFragment> collectExceptionHandlerQualityScores() {
		// code for handling exception handler scores
		try {
			for (CodeFragment targetFragment : this.Fragments) {
				//double readability = getReadabilityScore(targetFragment);
				double stmtCount = getHandlerStatementCount(targetFragment);
				double handlerCount = getHandlerCount(targetFragment);
				//targetFragment.ReadabilityScore = readability;
				targetFragment.StatementCountScore = stmtCount;
				targetFragment.HandlerCountScore = handlerCount;
				double handler2codeRatio=getHandlerCodeRatio(targetFragment);
				targetFragment.HandlerToCodeRatio=handler2codeRatio;
			}
		} catch (Exception exc) {
			// handle the exception
		}
		return this.Fragments;
	}
}
