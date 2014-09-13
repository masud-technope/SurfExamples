package core;

import java.util.ArrayList;

import utility.ContextCodeLoader;

public class DemoClient {
	public static void main(String[] args){
	
		String query="SocketException Socket";
		String queryException="SocketException";
		//String fileName=StaticData.Surf_Data_Base+"/ccontext/35.txt";
		int exceptionID=35;
		String contextCode=ContextCodeLoader.loadContextCode(exceptionID);
		SurfExamplesProvider provider=new SurfExamplesProvider(queryException, query, contextCode,5);
		ArrayList<CodeFragment> results=provider.provideFinalRankedExamples();
		System.out.println("Results found:"+results.size());
		int count=0;
		for(CodeFragment cf:results){
			System.out.println("Structure: "+cf.StructuralSimilarityScore);
			count++;
			if(count==15)break;
		}
	}
}
