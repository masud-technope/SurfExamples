package core;

import indexing.CFIndexManager;
import java.util.ArrayList;
import scoring.ResultScoreManager;
import scoring.ScoreCalculator;
import structural.InputDocProcessor;
import utility.ContextCodeLoader;
import utility.ExceptionKeyLoader;

public class SurfExampleProviderLocal {

	String searchQuery;
	String contextCode;
	String exceptionName;
	CodeFragment queryFragment;
	ArrayList<CodeFragment> Fragments;
	String queryException;
	int targetExceptionLine;
	int testCaseNum = 0;

	public SurfExampleProviderLocal(int testCaseNum, String queryException,
			int targetExceptionLine, String contextCode) {
		this.queryException = queryException;
		this.testCaseNum = testCaseNum;
		this.targetExceptionLine = targetExceptionLine;
		// extracting the exception name
		this.contextCode = contextCode;
		// query code fragment info
		this.queryFragment = collectQueryCodeFragment();
		this.Fragments = new ArrayList<>();
	}

	protected CodeFragment collectQueryCodeFragment() {
		InputDocProcessor inputProcessor = new InputDocProcessor(
				queryException, contextCode, this.targetExceptionLine);
		return inputProcessor.extractInputDocumentInfo();
	}

	protected ArrayList<CodeFragment> performParallelScoreCalculation() {
		// master result list
		ArrayList<CodeFragment> masterList = new ArrayList<CodeFragment>();
		// load the primary fragment list from local repository
		this.Fragments = CFIndexManager.readCodeFragment(this.testCaseNum);

		// now perform the parallel task on the search operations
		int number_of_processors = 10;// Runtime.getRuntime().availableProcessors();
		// step size
		int stepSize = 0;

		double _stepsize = (double) this.Fragments.size()
				/ number_of_processors;

		stepSize = (int) Math.ceil(_stepsize);
		if (stepSize <= 1) {
			stepSize = this.Fragments.size() % number_of_processors;
			ScoreCalculator scal = new ScoreCalculator(this.contextCode,
					this.exceptionName, this.queryFragment, this.Fragments);
			masterList.addAll(scal.getComputedResults());
			return masterList;
		}

		ArrayList<Thread> myThreads = new ArrayList<Thread>();
		ArrayList<ScoreCalculator> scals = new ArrayList<ScoreCalculator>();
		System.out.println("Processors found:" + number_of_processors);

		// parallelize the score computation
		for (int i = 0; i < number_of_processors; i++) {
			ArrayList<CodeFragment> tempList = provide_segmented_links(
					this.Fragments, i * stepSize, stepSize);
			ScoreCalculator scal = new ScoreCalculator(this.contextCode,
					this.exceptionName, this.queryFragment, tempList);

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

	protected ArrayList<CodeFragment> provide_segmented_links(
			ArrayList<CodeFragment> totalLinks, int index, int stepSize) {
		// code for providing segmented links
		ArrayList<CodeFragment> tempList = new ArrayList<>();
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

	public ArrayList<CodeFragment> provideFinalRankedExamples() {
		// code for preparing the final results
		ArrayList<CodeFragment> finalColls = null;
		try {
			long start = System.currentTimeMillis();
			ArrayList<CodeFragment> masterList = performParallelScoreCalculation();
			// System.out.println("Results collected:"+masterList.size());
			// showTheCode(masterList);
			ResultScoreManager manager = new ResultScoreManager(masterList,
					this.queryException);
			finalColls = manager.provideFinalResults();
			long end = System.currentTimeMillis();
			System.out.println("Time required:" + (end - start) / 1000.0);
			System.out.println("Final score calculated successfully.");
		} catch (Exception exc) {
			// handle the exception
			exc.printStackTrace();
		}
		return finalColls;
	}

	protected void showTheCode(ArrayList<CodeFragment> codeFragments) {
		// code for saving the code
		try {
			for (CodeFragment cfragment : codeFragments) {
				System.out.println(cfragment.CompleteCode);
			}
		} catch (Exception exc) {
		}
	}

	public static void main(String[] args) {
		// code for testing
		int exceptionID = 87;
		int exceptionLineNumber = 31;
		String codecontext = ContextCodeLoader.loadContextCode(exceptionID);
		String queryException = ExceptionKeyLoader
				.getExceptionName(exceptionID);
		SurfExampleProviderLocal provider = new SurfExampleProviderLocal(
				exceptionID, queryException, exceptionLineNumber, codecontext);
		ArrayList<CodeFragment> results = provider.provideFinalRankedExamples();
		System.out.println("Results found:" + results.size());
		int count = 0;
		for (CodeFragment cf : results) {
			// System.out.println(MyTokenizer.format_the_code(cf.CompleteCode)+
			// "\n=================\n"
			// +"Structural:"+cf.StructuralSimilarityScore+"\t"+"Lexical:"+cf.LexicalSimilarityScore+"\t"+"Quality:"+cf.HandlerQualityScore+"\n");
			System.out.println("Fragment ID:" + cf.FragmentID);
			System.out.println("Structural:" + cf.StructuralSimilarityScore
					+ "\t" + "Lexical:" + cf.LexicalSimilarityScore + "\t"
					+ "Quality:" + cf.HandlerQualityScore);
			System.out.println("AOM=" + cf.CodeObjectMatchScore + ", FAM="
					+ cf.FieldMatchScore + ", MIM=" + cf.MethodMatchScore
					+ ", DDM=" + cf.DependencyMatchScore + ", CL="
					+ cf.CodeCloneScore + ", CS=" + cf.CosineSimilarityScore
					+ ", R=" + cf.ReadabilityScore + ", AHA="
					+ cf.HandlerCountScore + ", HCR=" + cf.HandlerQualityScore);
			System.out.println("------------------------------------");
			count++;
			if (count == 15)
				break;
		}
	}
}
