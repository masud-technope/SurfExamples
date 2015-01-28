package core;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import scoring.ResultScoreManager;
import scoring.ScoreCalculator;
import structural.InputDocProcessor;
import utility.CodeDownloader;
import utility.ContextCodeLoader;
import utility.MyTokenizer;
import utility.RegexMatcher;

public class SurfExamplesProvider {
	
	
	String searchQuery;
	String contextCode;
	String exceptionName;
	CodeFragment queryFragment;
	ArrayList<CodeFragment> Fragments;
	ArrayList<CodeFile> CodeFiles;
	SurfExampleSearch search;
	String queryException;
	int targetExceptionLine;
	
	public SurfExamplesProvider(String queryException, String searchQuery, String contextCode, int targetExceptionLine)
	{
		//initialization
		this.queryException=queryException;
		this.searchQuery=searchQuery;
		this.targetExceptionLine=targetExceptionLine;
		//extracting the exception name
		this.exceptionName=RegexMatcher.extractExceptionName(this.searchQuery);
		this.contextCode=contextCode;
		//query code fragment info
		this.queryFragment=collectQueryCodeFragment();
		this.Fragments=new ArrayList<>();
		this.CodeFiles=new ArrayList<>();
		this.search=new SurfExampleSearch(this.searchQuery);
	}
	
	protected CodeFragment collectQueryCodeFragment()
	{
		InputDocProcessor inputProcessor=new InputDocProcessor(queryException, contextCode, this.targetExceptionLine);
		return inputProcessor.extractInputDocumentInfo();
	}
	
	protected ArrayList<CodeFragment> performParallelScoreCalculation(SurfExampleSearch search) {
		// master result list
		ArrayList<CodeFragment> masterList = new ArrayList<CodeFragment>();
		// now perform the parallel task on the search operations
		int number_of_processors = 10;// Runtime.getRuntime().availableProcessors();
		// step size
		int stepSize = 0;

		double _stepsize = (double) search.CodeFiles.size()/ number_of_processors;

		stepSize = (int) Math.ceil(_stepsize);
		if (stepSize <= 1) {
			stepSize = search.CodeFiles.size() % number_of_processors;
			ScoreCalculator scal = new ScoreCalculator(search.CodeFiles,
					this.contextCode,this.exceptionName, this.queryFragment);
			masterList.addAll(scal.getComputedResults());
			return masterList;
		}

		ArrayList<Thread> myThreads = new ArrayList<Thread>();
		ArrayList<ScoreCalculator> scals = new ArrayList<ScoreCalculator>();
		System.out.println("Processors found:" + number_of_processors);

		// log.info("Processors found"+number_of_processors);

		// parallelize the score computation
		for (int i = 0; i < number_of_processors; i++) {
			ArrayList<CodeFile> tempList = provide_segmented_links(
					search.CodeFiles, i * stepSize, stepSize);
			ScoreCalculator scal = new ScoreCalculator(tempList,
					this.contextCode, this.exceptionName, this.queryFragment);
			Runnable runnable = scal;
			Thread t = new Thread(runnable);
			t.setName("Thread: #" + i);
			myThreads.add(t);
			scals.add(scal);
			t.setPriority(Thread.MAX_PRIORITY);
			t.start();
			System.out.println("Starting thread:" + t.getName());
		}

		// checking the thread status and collecting results
		int running = number_of_processors;
		while (running > 0) {
			for (int k = 0; k < myThreads.size(); k++) {
				Thread t1 = myThreads.get(k);
				if (!t1.isAlive()) {
					ScoreCalculator scal1 = (ScoreCalculator) scals.get(k);
					masterList.addAll(scal1.getComputedResults());
					myThreads.remove(k);
					scals.remove(k);
					running--;
					System.out.println("Completed Thread :" + k + " "
							+ t1.getName());
					System.out.println("Threads remaining....");
					for (Thread t2 : myThreads)
						System.out.println(t2.getName());
				}
			}
		}
		System.out.println("Just finished the itermediate calculation");
		return masterList;
	}
	
    protected ArrayList<CodeFile> provide_segmented_links(ArrayList<CodeFile> totalLinks,int index,int stepSize)
	{
		// code for providing segmented links
		ArrayList<CodeFile> tempList = new ArrayList<CodeFile>();
		int endIndex = index + stepSize;
		if (endIndex > totalLinks.size())
			endIndex = totalLinks.size();
		for (int i = index; i < endIndex; i++) {
			CodeFile result = totalLinks.get(i);
			tempList.add(result);
		}
		System.out.println("Thread #" + index + ": URL index:" + index + " to "
				+ (endIndex - 1));
		// returning temporary list
		return tempList;
	}
    
	protected void showTheCode(ArrayList<CodeFragment> codeFragments)
	{
		//code for saving the code
		try{
			for(CodeFragment cfragment:codeFragments){
				System.out.println(cfragment.CompleteCode);
			}
		}catch(Exception exc){
		}
	}
    
	public ArrayList<CodeFragment> provideFinalRankedExamples()
	{
		//code for preparing the final results
		ArrayList<CodeFragment> finalColls=null;
		try{
			long start=System.currentTimeMillis();
			ArrayList<CodeFragment> masterList=performParallelScoreCalculation(this.search);
			//System.out.println("Results collected:"+masterList.size());
			//showTheCode(masterList);
			ResultScoreManager manager=new ResultScoreManager(masterList, this.queryException);
			finalColls=manager.provideFinalResults();
			long end=System.currentTimeMillis();
			//System.out.println("Time required:"+(end-start)/1000.0);
			System.out.println("Final score calculated successfully.");
		}catch(Exception exc){
			//handle the exception
		}
		return finalColls;
	}
	
	protected void saveCodeContents(ArrayList<CodeFile> codeFiles) {
		// code for showing the code contents
		try {
			CodeDownloader downloader=new CodeDownloader(codeFiles);
			codeFiles=downloader.downloadCodeContents();
			int count = 0;
			for (CodeFile codeFile : codeFiles) {
				String fileURL = StaticData.Surf_Data_Base + "/codes/" + count
						+ ".java";
				File f = new File(fileURL);
				FileWriter fwriter = new FileWriter(f);
				fwriter.write(codeFile.CompleteCode);
				fwriter.close();
				count++;
			}
		} catch (Exception exc) {
			// handle the exception
		}
	}
	
	public static void main(String[] args){
		//code for testing
		String searchQuery="" +
				"IOException  ArrayList FileInputStream"; 
		int exceptionID=200;
		int exceptionLineNumber=16;
		String codecontext = ContextCodeLoader.loadContextCode(exceptionID);
		String queryException="IOException";
		SurfExamplesProvider provider=new SurfExamplesProvider(queryException, searchQuery, codecontext,exceptionLineNumber);
		ArrayList<CodeFragment> results=provider.provideFinalRankedExamples();
		System.out.println("Results found:"+results.size());
		int count=0;
		for(CodeFragment cf:results){
			System.out.println(MyTokenizer.format_the_code(cf.CompleteCode)+"\n=================\n");//+"Structural:"+cf.StructuralSimilarityScore+"\t"+"Lexical:"+cf.LexicalSimilarityScore+"\t"+"Quality:"+cf.HandlerQualityScore+"\n");
			System.out.println("AOM="+cf.CodeObjectMatchScore+", FAM="+cf.FieldMatchScore+", MIM="+cf.MethodMatchScore+", DDM="+cf.DependencyMatchScore+", CL="+cf.CodeCloneScore+", CS="+cf.CosineSimilarityScore+", R="+cf.ReadabilityScore+", AHA="+cf.HandlerCountScore+", HCR="+cf.HandlerQualityScore);
			count++;
			if(count==15)break;
		}
	}
}
