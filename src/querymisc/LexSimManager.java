package querymisc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import lexical.LCS;
import utility.MyTokenizer;
import core.CodeFragment;
import core.StaticData;

public class LexSimManager {
	
	int exceptionID;
	String contextCode;
	ArrayList<CodeFragment> fragments;
	public LexSimManager(int exceptionID)
	{
		//initialization
		this.exceptionID=exceptionID;
		this.contextCode=ContextCodeLoader.loadContextCode(exceptionID);
		this.fragments=new ArrayList<>();
	}
	
	protected String getContent(File f){
		String content=new String();
		try{
			Scanner scanner=new Scanner(f);
			while(scanner.hasNext())
			{
				content+=scanner.nextLine()+"\n";
			}
			scanner.close();
		}catch(Exception exc){
		}
		return content;
	}
	
	protected double getCodeCloneScore(String fragmentCode) {
		// code for getting context match score
		MyTokenizer cand_tokenizer = new MyTokenizer(fragmentCode);
		ArrayList<String> cand_tokens = cand_tokenizer.tokenize_code_item();
		MyTokenizer tokenizer = new MyTokenizer(this.contextCode);
		ArrayList<String> contextCodeTokens = tokenizer.tokenize_code_item();
		
		LCS lcsmaker = new LCS(contextCodeTokens, cand_tokens);
		ArrayList<String> lcs = lcsmaker.getLCS_Dynamic(
				contextCodeTokens.size(), cand_tokens.size());
		// System.out.println("lcs: "+lcs.size());
		// now perform the normalization
		double normalized_matching_score = 0;
		if (lcs.size() == 0)
			return 0;
		else
			normalized_matching_score = (lcs.size() * 1.0)
					/ contextCodeTokens.size();
		return normalized_matching_score;
	}
	
	protected void measureLexicalSimilarity()
	{
		//code for matching the lexical similarity
		try{
			String folderName=StaticData.Surf_Data_Base+"/fragments/"+this.exceptionID;
			File f=new File(folderName);
			File[] files=f.listFiles();
			for(File f2:files){
				String completecode=getContent(f2);
				CodeFragment cfragment=new CodeFragment();
				cfragment.CompleteCode=completecode;
				cfragment.FragmentID=Integer.parseInt(f2.getName().split("\\.+")[0].trim());
				this.fragments.add(cfragment);
			}
			
			for(CodeFragment cfragment:this.fragments){
				//perform cosine similarity
				double clonescore=getCodeCloneScore(cfragment.CompleteCode);
				cfragment.CodeCloneScore=clonescore;
			}
			
			//sort the items
			ArrayList<CodeFragment> sorted=sortItems(this.fragments);
			for(CodeFragment cf:sorted){
				System.out.println(cf.FragmentID+"\t"+cf.CodeCloneScore);
			}
			
		}catch(Exception exc){
			//handle the exception
			exc.printStackTrace();
		}
	}
	protected ArrayList<CodeFragment> sortItems(ArrayList<CodeFragment> items)
	{
		Collections.sort(items, new Comparator<CodeFragment>() {
			public int compare(CodeFragment c1, CodeFragment c2){
				Double v1=new Double(c1.CodeCloneScore);
				Double v2=new Double(c2.CodeCloneScore);
				return v2.compareTo(v1);
			}
		});
		//return sorted items
		return items;
	}
	public static void main(String[] args){
		int exceptionID=49;
		LexSimManager manager=new LexSimManager(exceptionID);
		manager.measureLexicalSimilarity();
	}
}