package compare.heuristic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import misc.SolutionChecker;
import utility.SolutionLoader;
import core.StaticData;

public class HeuResultAnalyzer {
	
	
	HashMap<Integer, ArrayList<Integer>> heuSolList;
	int maxLimit=0;
	double indiv_precision=0;
	double ffp_total=0;
	double receiprocal_total=0;
	public HeuResultAnalyzer(int maxLimit)
	{
		//default constructor
		heuSolList=new HashMap<>();
		this.maxLimit=maxLimit;
		//load the computed solutions
		this.loadHeuSolutions();
	}
	
	protected void loadHeuSolutions() {
		// code for loading the heuristic solutions
		String fileName = StaticData.Surf_Data_Base + "/existing/strathcona";
		for (int i = 1; i <= 150; i++) {
			if (new File(StaticData.Surf_Data_Base + "/querycodes/" + i
					+ ".txt").exists() == false) {
				continue;
			}
			String sFileName = fileName + "/" + i + ".txt";
			File f = new File(sFileName);
			try {
				Scanner scanner = new Scanner(f);
				ArrayList<Integer> solList = new ArrayList<>();
				scanner.nextLine(); // omit the first line
				int itemcount = 0;
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					int ID = Integer.parseInt(line.split("\\s+")[0].trim());
					solList.add(ID);
					itemcount++;
					if (itemcount == this.maxLimit)
						break;
				}
				scanner.close();
				heuSolList.put(i, solList);
			} catch (Exception exc) {
				// handle it
			}
		}
	}
	
	protected boolean isMatched(int exceptionID, int orgSolID, int candFragID)
	{
		return SolutionChecker.matchSolutionCode(exceptionID, orgSolID, candFragID);
	}
	
	protected int getTotalMatch(int exceptionID, ArrayList<Integer> sols,
			ArrayList<Integer> candidates) {
		int matchcount = 0;
		for (Integer key : sols) {
			for (Integer key2 : candidates) {
				/*if(key.intValue()==key2.intValue()){
					matchcount++;
					break;
				}*/
				if (SolutionChecker.matchSolutionCode(exceptionID,
						key.intValue(), key2.intValue())) {
					matchcount++;
					break;
				}
			}
			if(matchcount>0){
				indiv_precision=(double)matchcount/maxLimit;
			}
		}
		return matchcount;
	}
	
	
	
	protected double getAveragePrecision(int exceptionID,
			ArrayList<Integer> sols, ArrayList<Integer> candidates) {
		double avgPrecision = 0;
		double precision = 0;
		int matched = 0;
		int ffp_individual=-1;
		double receip_individual=-1;
		for (int i = 0; i < candidates.size(); i++) {
			int candidateID = candidates.get(i).intValue();
			int found=0;
			for (Integer key2 : sols) {
				/*if(key2.intValue()==candidateID){
					matched++;
					precision += (matched / (i + 1));
					break;
				}*/
				if (SolutionChecker.matchSolutionCode(exceptionID,
						key2.intValue(), candidateID)) {
					matched++;
					precision += (matched / (i + 1));
					if(receip_individual<0)receip_individual=1/(i+1);
					found=1;
					break;
				}
			}
			if(found==0){
				if(ffp_individual<0)ffp_individual=(i+1);
			}
			
		}
		
		//now add the measure to global vars
				ffp_total+=ffp_individual;
				receiprocal_total+=receip_individual;
		
		if (matched > 0)
			avgPrecision = precision /matched;
		return avgPrecision;
	}
	

	public void getSolutionMatchCount()
	{
		//code for getting solution match count
		int matchcount=0;
		double total_avg_precision=0;
		double total_precision=0;
		int foundcases=0;
		for(Integer key:this.heuSolList.keySet()){
			try{
			
				ArrayList<Integer> solutions=SolutionLoader.getSolutionIDs(key.intValue());
				ArrayList<Integer> tempList=this.heuSolList.get(key);
				int matched=getTotalMatch(key.intValue(), solutions, tempList);
				if(matched>0)
					{
					System.out.println(key);
					foundcases++;
					total_precision+=indiv_precision;
					indiv_precision=0;
					}
				matchcount+=matched;
				total_avg_precision+=getAveragePrecision(key.intValue(), solutions, tempList);
			}catch(Exception exc){}
		}
		System.out.println("Total matched found:"+matchcount);
		System.out.println("Solved cases:"+foundcases);
		System.out.println("Mean Average precision:"+total_avg_precision/65);
		System.out.println("Average precision:"+total_precision/65);
		System.out.println("Total Recall:"+(double)matchcount/176);
		System.out.println("Mean FFPP:"+ffp_total/65);
		System.out.println("Mean RR:"+receiprocal_total/65);
	}
	
	public static void main(String[] args){
		int maxLimit=15;
		HeuResultAnalyzer analyzer=new HeuResultAnalyzer(maxLimit);
		analyzer.getSolutionMatchCount();
	}
}
