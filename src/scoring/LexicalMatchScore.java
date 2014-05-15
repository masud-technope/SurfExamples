package scoring;

import java.util.ArrayList;
import lexical.CosineSimilarityMeasure;
import lexical.LCS;
import utility.MyTokenizer;
import core.CodeFragment;

public class LexicalMatchScore {

	ArrayList<CodeFragment> Fragments;
	String contextCode;
	ArrayList<String> contextCodeTokens;
	
	public LexicalMatchScore(String contextCode,
			ArrayList<CodeFragment> Fragments) {
		// initialization
		this.contextCode = contextCode;
		this.Fragments = Fragments;
		// tokenize context code
		tokenizConextCode();
	}

	protected void tokenizConextCode() {
		// tokenize the context code item
		MyTokenizer tokenizer = new MyTokenizer(this.contextCode);
		this.contextCodeTokens = tokenizer.tokenize_code_item();
	}

	protected double getCodeCloneScore(String fragmentCode) {
		// code for getting context match score
		MyTokenizer cand_tokenizer = new MyTokenizer(fragmentCode);
		ArrayList<String> cand_tokens = cand_tokenizer.tokenize_code_item();
		LCS lcsmaker = new LCS(this.contextCodeTokens, cand_tokens);
		ArrayList<String> lcs = lcsmaker.getLCS_Dynamic(
				this.contextCodeTokens.size(), cand_tokens.size());
		// System.out.println("lcs: "+lcs.size());
		// now perform the normalization
		double normalized_matching_score = 0;
		if (lcs.size() == 0)
			return 0;
		else
			normalized_matching_score = (lcs.size() * 1.0)
					/ this.contextCodeTokens.size();
		return normalized_matching_score;
	}
	
	protected double getCosineSimilarityScore(String fragmentCode){
		//code for getting cosine similarity scores
		double cossim_score = 0;
		try {
			CosineSimilarityMeasure cosMeasure=new CosineSimilarityMeasure(this.contextCode, fragmentCode);
			cossim_score=cosMeasure.get_cosine_similarity_score(true); //granularize the camel case tokens
		} catch (Exception exc) {
			// handle the exception
		}
		return cossim_score;
	}
	
	public ArrayList<CodeFragment> collectLexicalMatchScore() {
		// code for collecting lexical match scores
		try {
			for (CodeFragment fragment : this.Fragments) {
				double cloneScore = getCodeCloneScore(fragment.CompleteCode);
				fragment.CodeCloneScore = cloneScore;
				double cossimScore = getCosineSimilarityScore(fragment.CompleteCode);
				fragment.CosineSimilarityScore = cossimScore;
			}
		} catch (Exception exc) {
			// handle the exception
		}
		return this.Fragments;
	}	
}
