package scoring;

import java.util.ArrayList;
import utility.CodeDownloader;
import core.CodeFile;
import core.CodeFragment;
import extractor.SourceCodeElementExtractor;

public class ScoreCalculator implements Runnable {

	ArrayList<CodeFragment> Fragments;
	ArrayList<CodeFile> codeFiles;
	CodeFragment queryFragment;
	String contextCode;
	String exceptionName;
	CodeDownloader downloader;
	public ScoreCalculator(ArrayList<CodeFile> codeFiles, String contextCode, String exceptionName, CodeFragment queryFragment)
	{
		//initialization
		this.codeFiles=codeFiles;
		this.contextCode=contextCode;
		this.exceptionName=exceptionName;
		this.queryFragment=queryFragment;
		//creating downloader object
		this.downloader=new CodeDownloader(this.codeFiles);
	}
	
	public ScoreCalculator(String contextCode, String exceptionName, CodeFragment queryFragment, ArrayList<CodeFragment> codeFragments)
	{
		//initialization
		this.Fragments=codeFragments;
		this.contextCode=contextCode;
		this.exceptionName=exceptionName;
		this.queryFragment=queryFragment;
		//code fragments already downloaded
	}
	
	public ArrayList<CodeFragment> getComputedResults()
	{
		//return computed results
		return this.Fragments;
	}
	
	protected void calculateIntermediateResults()
	{
		//code for calculating intermediate results
		try{
			//downloading the code
			
			/*****temporary commented****/
			this.codeFiles=downloader.downloadCodeContents();
			//extract the code fragments
			SourceCodeElementExtractor sourceExtractor=new SourceCodeElementExtractor(exceptionName, codeFiles);
			this.Fragments=sourceExtractor.collectCodeFragments();
			//System.out.println("Query elements:"+this.queryFragment.codeObjectMap.size());
			/*****temporary commented*******/
			
			//now perform other operation
			//lexical similarity scores
			try{
				LexicalMatchScore lexMatcher=new LexicalMatchScore(contextCode, this.Fragments);
				this.Fragments=lexMatcher.collectLexicalMatchScore();
			}catch(Exception exc){
				System.err.println("Failed to calculate lexical similarity scores");
				exc.printStackTrace();
			}
			//structural match score
			try{
				StructuralMatchScore structMatcher=new StructuralMatchScore(queryFragment, this.Fragments);
				this.Fragments=structMatcher.collectStructuralScores();
			}catch(Exception exc){
				System.err.println("Failed to calculate the structural similarity");
				exc.printStackTrace();
			}
			//code quality scores
			try{
				CodeQualityScore qualityScore=new CodeQualityScore(this.Fragments);
				this.Fragments=qualityScore.collectCodeQualityScores();
			}catch(Exception exc){
				System.err.println("Failed to calculate the code quality (readability)");
				exc.printStackTrace();
			}
			//handler code scores
			try{
				HandlerCodeScore hcodeScore=new HandlerCodeScore(this.Fragments);
				this.Fragments=hcodeScore.collectExceptionHandlerQualityScores();
			}catch(Exception exc){
				System.err.println("Failed to calculate the handler code scores");
				exc.printStackTrace();
			}
		}catch(Exception exc){
			//handle the exception
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		calculateIntermediateResults();
	}

}
