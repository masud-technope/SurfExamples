package weighting;

import indexing.CFIndexManager;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import scoring.ResultScoreManager;
import scoring.ScoreCalculator;
import structural.InputDocProcessor;
import utility.ContextCodeLoader;
import utility.ExceptionKeyLoader;
import core.CodeFragment;
import core.StaticData;

public class MyLocalResultMaker {
	
	int exceptionID;
	String contextCode;
	String exceptionName;
	CodeFragment queryFragment;
	ArrayList<CodeFragment> Fragments;
	
	public MyLocalResultMaker(int exceptionID)
	{
		this.exceptionID=exceptionID;
		this.exceptionName=ExceptionKeyLoader.getExceptionName(exceptionID);
		this.contextCode=ContextCodeLoader.loadContextCode(exceptionID);
		//query code fragment info
		this.queryFragment=collectQueryCodeFragment();
		this.Fragments=CFIndexManager.readAllFragments();
	}
	
	protected CodeFragment collectQueryCodeFragment()
	{
		InputDocProcessor inputProcessor=new InputDocProcessor(exceptionName, contextCode);
		return inputProcessor.extractInputDocumentInfo();
	}
	
	protected ArrayList<CodeFragment> performParallelScoreCalculation() {
		// master result list
		ArrayList<CodeFragment> masterList = new ArrayList<>();
		// now perform the parallel task on the search operations
		int number_of_processors = 10;// Runtime.getRuntime().availableProcessors();
		// step size
		int stepSize = 0;

		double _stepsize = (double) this.Fragments.size()/ number_of_processors;

		stepSize = (int) Math.ceil(_stepsize);
		if (stepSize <= 1) {
			stepSize = this.Fragments.size() % number_of_processors;
			ScoreCalculator scal = new ScoreCalculator(
					this.contextCode,this.exceptionName, this.queryFragment,this.Fragments);
			masterList.addAll(scal.getComputedResults());
			return masterList;
		}

		ArrayList<Thread> myThreads = new ArrayList<Thread>();
		ArrayList<ScoreCalculator> scals = new ArrayList<ScoreCalculator>();
		System.out.println("Processors found:" + number_of_processors);

		// log.info("Processors found"+number_of_processors);

		// parallelize the score computation
		for (int i = 0; i < number_of_processors; i++) {
			ArrayList<CodeFragment> tempList = provide_segmented_links(
					this.Fragments, i * stepSize, stepSize);
			ScoreCalculator scal = new ScoreCalculator(
					this.contextCode, this.exceptionName, this.queryFragment,tempList);
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
	
	 protected ArrayList<CodeFragment> provide_segmented_links(ArrayList<CodeFragment> totalLinks,int index,int stepSize)
		{
			// code for providing segmented links
			ArrayList<CodeFragment> tempList = new ArrayList<CodeFragment>();
			int endIndex = index + stepSize;
			if (endIndex > totalLinks.size())
				endIndex = totalLinks.size();
			for (int i = index; i < endIndex; i++) {
				CodeFragment result = totalLinks.get(i);
				tempList.add(result);
			}
			System.out.println("Thread #" + index + ": URL index:" + index + " to "
					+ (endIndex - 1));
			// returning temporary list
			return tempList;
		}

	 public ArrayList<CodeFragment> provideFinalRankedExamples()
		{
			//code for preparing the final results
			ArrayList<CodeFragment> finalColls=null;
			try{
				long start=System.currentTimeMillis();
				ArrayList<CodeFragment> masterList=performParallelScoreCalculation();
				System.out.println("Results collected:"+masterList.size());
				//showTheCode(masterList);
				ResultScoreManager manager=new ResultScoreManager(masterList);
				finalColls=manager.provideFinalResults();
				long end=System.currentTimeMillis();
				//System.out.println("Time required:"+(end-start)/1000.0);
				saveSortedResults(finalColls);
			}catch(Exception exc){
				//handle the exception
			}
			return finalColls;
		}
	 
	 protected void saveSortedResults(ArrayList<CodeFragment> sorted)
		{
			//code for saving the sorted results
			try{
				String outFile=StaticData.Surf_Data_Base+"/results/"+this.exceptionID+".txt";
				FileWriter fwriter=new FileWriter(new File(outFile));
				fwriter.write("FID\tSFile\tScore\n");
				int count=0;
				for(CodeFragment frag:sorted){
					String line=frag.FragmentID+"\t"+frag.sourceFileID+"\t"+frag.total_lexical_structural_readability_handlerquality_score;
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
//		 for(int i=12;i<=150;i++){
//			 if(new File(StaticData.Surf_Data_Base+"/querycodes/"+i+".txt").exists()==false){
//					continue;
//			 }
			 int exceptionID=38;//i;
			 MyLocalResultMaker maker=new MyLocalResultMaker(exceptionID);
			 maker.provideFinalRankedExamples();
			 //System.out.println("Done:"+i);
		 //}
	 }
}
