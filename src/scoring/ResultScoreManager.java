package scoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		
		public ArrayList<CodeFragment> provideFinalResults()
		{
			//code for computing the final scores
			for(CodeFragment cfragment:Fragments){
				cfragment=finalizeContentScore(cfragment);
				cfragment=finalizeStructuralScore(cfragment);
				cfragment=finalizeMiscScore(cfragment);
			}
			//normalize the scores
			//this.Fragments=normalizeSubtotalScores(this.Fragments);
			//calculate total scores
			this.Fragments=calculateTotalScores(this.Fragments);
			//now sort the items
			ArrayList<CodeFragment> sorted=sortItems(Fragments);
			return sorted;
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
