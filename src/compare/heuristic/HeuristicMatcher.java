package compare.heuristic;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import core.ECodeFragment;
import core.ScoreWeights;
import core.StaticData;
import utility.ContextCodeLoader;
import utility.ExceptionKeyLoader;

public class HeuristicMatcher {
	
	int exceptionID;
	String exceptionName;
	ArrayList<ECodeFragment> Fragments;
	String contextCode;
	ECodeFragment queryFragment;
	public HeuristicMatcher(int exceptionID)
	{
		//initialization
		this.exceptionID=exceptionID;
		this.exceptionName=ExceptionKeyLoader.getExceptionName(exceptionID);
		this.contextCode=ContextCodeLoader.loadContextCode(exceptionID);
		this.queryFragment=new HInputDocProcessor(this.exceptionName, this.contextCode).extractInputDocumentInfo();
		this.Fragments=HCFIndexManager.readAllFragments();
	}

	protected int getMethodMatchCoutn(ECodeFragment targetFragment)
	{
		int matchedMethodcall=0;
		ArrayList<String> qmethod=queryFragment.methodCalled;
		ArrayList<String> tmethods=targetFragment.methodCalled;
		for(String str:qmethod){
			if(tmethods.contains(str.trim()))
				matchedMethodcall++;
		}
		return matchedMethodcall;
	}
	
	protected int getMatchedVariableCount(ECodeFragment targetFragment)
	{
		int matchedVarType=0;
		ArrayList<String> qVarTypes=queryFragment.variableTypes;
		ArrayList<String> tVarTypes=targetFragment.variableTypes;
		for(String str:qVarTypes){
			if(tVarTypes.contains(str.trim()))
				matchedVarType++;
		}
		return matchedVarType;
	}
	
	protected int getHandledMatchCount(ECodeFragment targetFragment)
	{
		int handledMatched=0;
		ArrayList<String> handled=targetFragment.handles;
		if(handled.contains(this.exceptionName))handledMatched=1;
		else handledMatched=0;
		return handledMatched;
	}
	
	protected double collectWeightedScores(int methodcount, int varTypecount, int handlecount)
	{
		//code for calculating the scores
		double weightedMethodMatch=methodcount*ScoreWeights.CallsWeight;
		double weigthedVarTypeMatch=varTypecount*ScoreWeights.UsesWeight;
		double weightedHandleMatch=handlecount*ScoreWeights.HandlesWeight;
		double total=weightedMethodMatch+weigthedVarTypeMatch+weightedHandleMatch;
		double queryItems=queryFragment.methodCalled.size()+queryFragment.variableTypes.size()+1;
		return total/queryItems;
	}
	
	
	protected void computerHeuristicScores()
	{
		//code for computing the heuristic scores
		try{
			for(ECodeFragment fragment:this.Fragments){
				int methodMatch=getMethodMatchCoutn(fragment);
				int varTypeMatch=getMatchedVariableCount(fragment);
				int handlecount=getHandledMatchCount(fragment);
				double weightedScore=collectWeightedScores(methodMatch, varTypeMatch, handlecount);
				fragment.totalHeuristicScore=weightedScore;
			}
			ArrayList<ECodeFragment> sorted=sortItems(Fragments);
			saveSortedResults(sorted);
			
		}catch(Exception exc){
			//handle the exception
		}
	}
	
	protected ArrayList<ECodeFragment> sortItems(ArrayList<ECodeFragment> items)
	{
		Collections.sort(items, new Comparator<ECodeFragment>() {
			public int compare(ECodeFragment c1, ECodeFragment c2){
				Double v1=new Double(c1.totalHeuristicScore);
				Double v2=new Double(c2.totalHeuristicScore);
				return v2.compareTo(v1);
			}
		});
		//return sorted items
		return items;
	}
	
	protected void saveSortedResults(ArrayList<ECodeFragment> sorted)
	{
		//code for saving the sorted results
		//save max 20 results
		try{
			String outFile=StaticData.Surf_Data_Base+"/existing/heuristic/"+this.exceptionID+".txt";
			FileWriter fwriter=new FileWriter(new File(outFile));
			fwriter.write("FID\tSFile\tScore\n");
			int count=0;
			for(ECodeFragment frag:sorted){
				String line=frag.FragmentID+"\t"+frag.sourceFileID+"\t"+frag.totalHeuristicScore;
				fwriter.write(line+"\n");
				count++;
				if(count==20)break;
			}
			fwriter.close();
			System.out.println("Saved items:"+sorted.size());
		}catch(Exception exc){
			//handle the exception
		}
	}
	
	
	public static void main(String[] args){
		for(int i=1;i<=150;i++){
		if(new File(StaticData.Surf_Data_Base+"/querycodes/"+i+".txt").exists()==false){
			continue;
		}
		int exceptionID=i;
		HeuristicMatcher matcher=new HeuristicMatcher(exceptionID);
		matcher.computerHeuristicScores();
		System.out.println("Done: "+i);
		}
	}
}
