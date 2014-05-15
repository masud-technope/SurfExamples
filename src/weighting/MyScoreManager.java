package weighting;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import misc.ScoreLoader;
import structural.InputDocProcessor;
import utility.ContextCodeLoader;
import utility.ExceptionKeyLoader;
import core.CodeFragment;
import core.ScoreWeights;
import core.StaticData;

public class MyScoreManager {
	
	
	int exceptionID;
	String exceptionName;
	String contextCode;
	CodeFragment queryFragment;
	ArrayList<CodeFragment> Fragments;
	
	public MyScoreManager(int exceptionID)
	{
	    //initialization
		this.exceptionID=exceptionID;
		this.exceptionName=ExceptionKeyLoader.getExceptionName(exceptionID);
		this.contextCode=ContextCodeLoader.loadContextCode(exceptionID);
		this.queryFragment=new InputDocProcessor(this.exceptionName, this.contextCode).extractInputDocumentInfo();	
		this.Fragments=new ScoreLoader(exceptionID).loadDifferentScores();
	}
	
	/*double matchedObj=targetFragment.CodeObjectMatchScore*queryFragment.codeObjectMap.keySet().size();
	if(matchedObj>0){
	targetFragment.FieldMatchScore=targetFragment.FieldMatchScore/matchedObj;
	targetFragment.MethodMatchScore=targetFragment.MethodMatchScore/matchedObj;
	}
	if(queryFragment.dependencies.size()>0)
	targetFragment.DependencyMatchScore=targetFragment.DependencyMatchScore/queryFragment.dependencies.size();
	
	targetFragment.StructuralSimilarityScore=targetFragment.FieldMatchScore+
			targetFragment.MethodMatchScore+targetFragment.DependencyMatchScore;
	//targetFragment.total_lexical_structural_readability_handlerquality_score=targetFragment.StructuralSimilarityScore;
	 */	
	
	protected CodeFragment finalizeStructuralScore(CodeFragment targetFragment)
	{
		// code for finalizing the structural scores

		targetFragment.StructuralSimilarityScore = ScoreWeights.CodeObjectFieldMatchWeight
				* targetFragment.FieldMatchScore;
		targetFragment.StructuralSimilarityScore += ScoreWeights.CodeObjectMethodMatchWegiht
				* targetFragment.MethodMatchScore;
		targetFragment.StructuralSimilarityScore += ScoreWeights.CodeObjectMatchWeight
				* targetFragment.CodeObjectMatchScore;
		targetFragment.StructuralSimilarityScore += ScoreWeights.CodeObjectDependencyWegiht
				* targetFragment.DependencyMatchScore;

		return targetFragment;
	}
	
	protected CodeFragment finalizeContentScore(CodeFragment targetFragment)
	{
		targetFragment.LexicalSimilarityScore=targetFragment.CodeCloneScore*ScoreWeights.CloneWeight+
				targetFragment.CosineSimilarityScore * ScoreWeights.CosineSimWeight;
		return targetFragment;
	}
	
	protected CodeFragment finalizeMiscScore(CodeFragment targetFragment)
	{
		targetFragment.HandlerQualityScore=targetFragment.ReadabilityScore*ScoreWeights.ReadabilityWeight;
		targetFragment.HandlerQualityScore+=targetFragment.StatementCountScore*ScoreWeights.AvgStmtWeight;
		targetFragment.HandlerQualityScore+=targetFragment.HandlerToCodeRatio*ScoreWeights.H2cRatioWeight;
		return targetFragment;
	}
	
	protected ArrayList<CodeFragment> normalizeSubtotalScores(ArrayList<CodeFragment> fragments)
	{
		//code for normalizing the subtotal scores
		try{
			double max_content_score=0;
			double max_struct_score=0;
			double max_quality_score=0;
			for(CodeFragment fragment:fragments){
				if(fragment.LexicalSimilarityScore>max_content_score)
					max_content_score=fragment.LexicalSimilarityScore;
				if(fragment.StructuralSimilarityScore>max_struct_score)
					max_struct_score=fragment.StructuralSimilarityScore;
				if(fragment.HandlerQualityScore>max_quality_score)
					max_quality_score=fragment.HandlerQualityScore;
			}
			//now normalize
			for(CodeFragment fragment:fragments){
				fragment.LexicalSimilarityScore=fragment.LexicalSimilarityScore/max_content_score;
				fragment.StructuralSimilarityScore=fragment.StructuralSimilarityScore/max_struct_score;
				fragment.HandlerQualityScore=fragment.HandlerQualityScore/max_quality_score;
			}
		}catch(Exception exc){
			//handle the exception
		}
		return fragments;
	}
  
    protected ArrayList<CodeFragment> calculateTotalScores(ArrayList<CodeFragment> fragments)
    {
    	//code for saving the total scores
    	try{
    		for(CodeFragment cf:fragments){
    		cf.total_lexical_structural_readability_handlerquality_score=
    				cf.LexicalSimilarityScore * ScoreWeights.LexicalWeight+
    				cf.StructuralSimilarityScore * ScoreWeights.StructuralWeight+
    				cf.HandlerQualityScore*ScoreWeights.QualityWeight;
    		}
    	}catch(Exception exc){
    		//handle the exception
    	}
    	return fragments;
    }
	
	protected void computeFinalScores()
	{
		//code for computing the final scores
		for(CodeFragment cfragment:Fragments){
			cfragment=finalizeContentScore(cfragment);
			cfragment=finalizeStructuralScore(cfragment);
			cfragment=finalizeMiscScore(cfragment);
		}
		//normalize the scores
		this.Fragments=normalizeSubtotalScores(this.Fragments);
		//calculate total scores
		this.Fragments=calculateTotalScores(this.Fragments);
		//now sort the items
		ArrayList<CodeFragment> sorted=sortItems(Fragments);
		saveSortedResults(sorted);
	}
	
	protected ArrayList<CodeFragment> sortItems(ArrayList<CodeFragment> items)
	{
		Collections.sort(items, new Comparator<CodeFragment>() {
			public int compare(CodeFragment c1, CodeFragment c2){
				Double v1=new Double(c1.total_lexical_structural_readability_handlerquality_score);
				Double v2=new Double(c2.total_lexical_structural_readability_handlerquality_score);
				return v2.compareTo(v1);
			}
		});
		//return sorted items
		return items;
	}
	
	protected void saveSortedResults(ArrayList<CodeFragment> sorted)
	{
		//code for saving the sorted results
		try{
			String outFile=StaticData.Surf_Data_Base+"/results/"+this.exceptionID+".txt";
			FileWriter fwriter=new FileWriter(new File(outFile));
			fwriter.write("FID\tSFile\tScore\n");
			for(CodeFragment frag:sorted){
				String line=frag.FragmentID+"\t"+frag.sourceFileID+"\t"+frag.total_lexical_structural_readability_handlerquality_score;
				fwriter.write(line+"\n");
			}
			fwriter.close();
			System.out.println("Saved items:"+sorted.size());
		}catch(Exception exc){
			//handle the exception
		}
	}
	
	public static void main(String[] args){
		for(int i=1;i<=150;i++){
		int exceptionID=i;
		if(new File(StaticData.Surf_Data_Base+"/querycodes/"+i+".txt").exists()==false){
			continue;
		}
		MyScoreManager manager=new MyScoreManager(exceptionID);
		manager.computeFinalScores();
		}
	}
	
	
	
	

}
