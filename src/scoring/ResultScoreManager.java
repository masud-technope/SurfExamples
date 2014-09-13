package scoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import keyword.MyKeywordMaker;
import core.CodeFragment;
import core.ScoreWeights;


public class ResultScoreManager {
	
	ArrayList<CodeFragment> Fragments;
	public ResultScoreManager(ArrayList<CodeFragment> Fragments)
	{
		//initialization
		this.Fragments=Fragments;
	}
	
	public void calculate_relative_scores()
	{
		//code for calculating the relative scores	
	}
	
	protected CodeFragment finalizeStructuralScore(CodeFragment targetFragment)
	{
		// code for finalizing the structural scores

		try {
			targetFragment.StructuralSimilarityScore = ScoreWeights.CodeObjectFieldMatchWeight
					* targetFragment.FieldMatchScore;
		} catch (Exception e) {
			System.err.println("Failed to calculate FieldMatchScore score");
			e.printStackTrace();
		}
		try {
			targetFragment.StructuralSimilarityScore += ScoreWeights.CodeObjectMethodMatchWegiht
					* targetFragment.MethodMatchScore;
		} catch (Exception e) {
			System.err.println("Failed to calculate MethodMatchScore score");
			e.printStackTrace();
		}
		try {
			targetFragment.StructuralSimilarityScore += ScoreWeights.CodeObjectMatchWeight
					* targetFragment.CodeObjectMatchScore;
		} catch (Exception e) {
			System.err.println("Failed to calculate CodeObjectMatchScore score");
			e.printStackTrace();
		}
		try {
			targetFragment.StructuralSimilarityScore += ScoreWeights.CodeObjectDependencyWegiht
					* targetFragment.DependencyMatchScore;
		} catch (Exception e) {
			System.err.println("Failed to calculate DependencyMatchScore");
			e.printStackTrace();
		}
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
		try {
			targetFragment.HandlerQualityScore = targetFragment.ReadabilityScore
					* ScoreWeights.ReadabilityWeight;
		} catch (Exception e) {
			System.err.println("Failed to calculate ReadabilityScore");
			e.printStackTrace();
		}
		try {
			targetFragment.HandlerQualityScore += targetFragment.StatementCountScore
					* ScoreWeights.AvgStmtWeight;
		} catch (Exception e) {
			System.err.println("Failed to calculate StatementCountScore");
			e.printStackTrace();
		}
		try {
			targetFragment.HandlerQualityScore += targetFragment.HandlerToCodeRatio
					* ScoreWeights.H2cRatioWeight;
		} catch (Exception e) {
			System.err.println("Failed to calculate HandlerToCodeRatio");
			e.printStackTrace();
		}
		return targetFragment;
	}
	
	protected ArrayList<CodeFragment> normalizeSubtotalScores(ArrayList<CodeFragment> fragments)
	{
		//code for normalizing the sub total scores
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
				if(max_content_score>0)
				fragment.LexicalSimilarityScore=fragment.LexicalSimilarityScore/max_content_score;
				if(max_struct_score>0)
				fragment.StructuralSimilarityScore=fragment.StructuralSimilarityScore/max_struct_score;
				if(max_quality_score>0)
				fragment.HandlerQualityScore=fragment.HandlerQualityScore/max_quality_score;
			}
		}catch(Exception exc){
			//handle the exception
			exc.printStackTrace();
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
	    		exc.printStackTrace();
	    	}
	    	return fragments;
	    }
		
		public ArrayList<CodeFragment> provideFinalResults()
		{
			//code for computing the final scores
			for(CodeFragment cfragment:Fragments){
				cfragment=finalizeContentScore(cfragment);
				cfragment=finalizeStructuralScore(cfragment);
				//System.out.println("Structure objects:"+cfragment.codeObjectMap.size());
				cfragment=finalizeMiscScore(cfragment);
			}
			//normalize the scores
			ArrayList<CodeFragment> normalized=normalizeSubtotalScores(this.Fragments);
			//calculate total scores
			ArrayList<CodeFragment> totalCalc=calculateTotalScores(normalized);
			//label the items
			ArrayList<CodeFragment> labeledFrags=addkeywords(totalCalc);
			//now sort the items
			ArrayList<CodeFragment> sorted=sortItems(labeledFrags);
			
			sorted=normalizeTotalScores(sorted);
			
			return sorted;
		}
		
		protected ArrayList<CodeFragment> normalizeTotalScores(ArrayList<CodeFragment> sorted)
		{
			//normalizing the sorted scores
			double maxScore=0;
			for(CodeFragment cf:sorted){
				if(cf.total_lexical_structural_readability_handlerquality_score>maxScore)
					maxScore=cf.total_lexical_structural_readability_handlerquality_score;
			}
			//now normaize
			for(CodeFragment cf:sorted){
				cf.total_lexical_structural_readability_handlerquality_score=
						cf.total_lexical_structural_readability_handlerquality_score/maxScore;
			}
			return sorted;
		}
		
		
		protected ArrayList<CodeFragment> addkeywords(ArrayList<CodeFragment> items)
		{
			for(CodeFragment codeFragment:items){
				MyKeywordMaker maker=new MyKeywordMaker(codeFragment);
				codeFragment=maker.getLabeledResult();
			}
			return items;
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
		
}
