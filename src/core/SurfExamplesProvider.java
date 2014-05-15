package core;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import scoring.ResultScoreManager;
import scoring.ScoreCalculator;
import structural.InputDocProcessor;
import utility.CodeDownloader;

public class SurfExamplesProvider {
	
	
	String searchQuery;
	String contextCode;
	String exceptionName;
	CodeFragment queryFragment;
	ArrayList<CodeFragment> Fragments;
	ArrayList<CodeFile> CodeFiles;
	SurfExampleSearch search;
	
	public SurfExamplesProvider(String searchQuery, String contextCode)
	{
		this.searchQuery=searchQuery;
		this.exceptionName=this.searchQuery;
		this.contextCode=contextCode;
		//query code fragment info
		this.queryFragment=collectQueryCodeFragment();
		this.Fragments=new ArrayList<>();
		this.CodeFiles=new ArrayList<>();
		this.search=new SurfExampleSearch(this.searchQuery);
	}
	
	protected CodeFragment collectQueryCodeFragment()
	{
		InputDocProcessor inputProcessor=new InputDocProcessor(exceptionName, contextCode);
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
			t.setPriority(Thread.NORM_PRIORITY);
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
			ResultScoreManager manager=new ResultScoreManager(masterList);
			finalColls=manager.provideFinalResults();
			long end=System.currentTimeMillis();
			System.out.println("Time required:"+(end-start)/1000.0);
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
		String searchQuery="java.nio.channels.ClosedChannelException";
		String contextCode="";
		SurfExamplesProvider provider=new SurfExamplesProvider(searchQuery, contextCode);
		provider.provideFinalRankedExamples();
		//provider.saveCodeContents(provider.search.CodeFiles);
	}
}
