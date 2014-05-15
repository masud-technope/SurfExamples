package core;

import java.util.ArrayList;

import utility.ContextCodeLoader;

public class DemoClient {
	public static void main(String[] args){
	
		String query="SocketException Socket";
		String fileName=StaticData.Surf_Data_Base+"/ccontext/35.java";
		int exceptionID=35;
		String contextCode=ContextCodeLoader.loadContextCode(exceptionID);
		SurfExamplesProvider provider=new SurfExamplesProvider(query, contextCode);
		ArrayList<CodeFragment> results=provider.provideFinalRankedExamples();
		System.out.println("Results found:"+results.size());
	}
}
