package compare.sourcerer;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import core.StaticData;
import app.LuceneResult;
import app.MySearchFiles;
import querymanager.MyQueryMaker;
import utility.ExceptionKeyLoader;

public class LuceneMatcher {
	
	
	int exceptionID;
	String searchQuery;
	String exceptionName;
	ArrayList<LuceneResult> LResults;
	
	public LuceneMatcher(int exceptionID)
	{
		this.exceptionID=exceptionID;
		this.exceptionName=ExceptionKeyLoader.getExceptionName(exceptionID);
		MyQueryMaker maker=new MyQueryMaker(this.exceptionID, exceptionName);
		this.searchQuery=maker.getGitHubSearchQuery();
		this.LResults=new ArrayList<>();
	}
	
	protected void calculateResults()
	{
		MySearchFiles searcher=new MySearchFiles(searchQuery, exceptionID, new String[]{});
		this.LResults=searcher.find_lucene_results();
		this.saveSortedResults(this.LResults);
		
	}
	protected void saveSortedResults(ArrayList<LuceneResult> sorted)
	{
		//code for saving the sorted results
		//save max 20 results
		try{
			String outFile=StaticData.Surf_Data_Base+"/existing/lucene/"+this.exceptionID+".txt";
			FileWriter fwriter=new FileWriter(new File(outFile));
			fwriter.write("FID\tFoID\tScore\n");
			int count=0;
			for(LuceneResult result:sorted){
				String line=result.fragmentID+"\t"+result.score;
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
	
	public static void main(String[] args){
		for(int i=1;i<=150;i++){
			if(new File(StaticData.Surf_Data_Base+"/querycodes/"+i+".txt").exists()==false){
				continue;
			}
			int exceptionID=i;
			LuceneMatcher matcher=new LuceneMatcher(exceptionID);
			matcher.calculateResults();
			System.out.println("Done: "+i);
			}
	}
	
	

}
