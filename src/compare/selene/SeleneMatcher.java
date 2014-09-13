package compare.selene;

import indexing.CFIndexManager;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import lexical.CosineSimilarityMeasure;
import utility.ContextCodeLoader;
import utility.ExceptionKeyLoader;
import core.CodeFragment;
import core.StaticData;

public class SeleneMatcher {
	
	int exceptionID;
	String exceptionName;
	ArrayList<CodeFragment> Fragments;
	String contextCode;
	CodeFragment queryFragment;
	
	public SeleneMatcher(int exceptionID)
	{
		this.exceptionID = exceptionID;
		this.exceptionName = ExceptionKeyLoader.getExceptionName(exceptionID);
		this.contextCode = ContextCodeLoader.loadContextCode(exceptionID);
		this.Fragments = CFIndexManager.readAllFragments();
	}
	
	protected void calculateResults()
	{
		//code for preparing the results
		for(CodeFragment cfragment:this.Fragments){
			CosineSimilarityMeasure measure=new CosineSimilarityMeasure(this.contextCode, cfragment.CompleteCode);
			cfragment.CosineSimilarityScore=measure.get_cosine_similarity_score(true);
		}
		ArrayList<CodeFragment> list1=new ArrayList<>();
		list1.addAll(this.Fragments);
		
		Collections.sort(list1,new Comparator<CodeFragment>() {
			@Override
			public int compare(CodeFragment o1, CodeFragment o2) {
				// TODO Auto-generated method stub
				Double v1=o1.CosineSimilarityScore;
				Double v2=o2.CosineSimilarityScore;
				return v2.compareTo(v1);
			}
		});
		this.saveSortedResults(list1);
	}
	
	
	protected void saveSortedResults(ArrayList<CodeFragment> sorted)
	{
		//code for saving the sorted results
		//save max 20 results
		try{
			String outFile=StaticData.Surf_Data_Base+"/existing/selene/"+this.exceptionID+".txt";
			FileWriter fwriter=new FileWriter(new File(outFile));
			fwriter.write("FID\tSFile\tScore\n");
			int count=0;
			for(CodeFragment frag:sorted){
				String line=frag.FragmentID+"\t"+frag.sourceFileID+"\t"+frag.CosineSimilarityScore;
				fwriter.write(line+"\n");
				count++;
				if(count==30)break;
			}
			fwriter.close();
			System.out.println("Saved items:"+sorted.size());
		}catch(Exception exc){
			//handle the exception
		}
	}
	
	public static void main(String[] args)
	{
		for(int i=1;i<=150;i++){
			if(new File(StaticData.Surf_Data_Base+"/querycodes/"+i+".txt").exists()==false){
				continue;
			}
			int exceptionID=i;
			SeleneMatcher matcher=new SeleneMatcher(exceptionID);
			matcher.calculateResults();
			System.out.println("Done: "+i);
			}
	}
}
