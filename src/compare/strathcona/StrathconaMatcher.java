package compare.strathcona;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import utility.ContextCodeLoader;
import utility.ExceptionKeyLoader;
import compare.heuristic.HCFIndexManager;
import compare.heuristic.HInputDocProcessor;
import core.ECodeFragment;
import core.StaticData;

public class StrathconaMatcher {
	
	int exceptionID;
	String exceptionName;
	ArrayList<ECodeFragment> Fragments;
	String contextCode;
	ECodeFragment queryFragment;
	final int MAXLIMIT=10;

	public StrathconaMatcher(int exceptionID) {
		// initialization
		this.exceptionID = exceptionID;
		this.exceptionName = ExceptionKeyLoader.getExceptionName(exceptionID);
		this.contextCode = ContextCodeLoader.loadContextCode(exceptionID);
		this.queryFragment = new HInputDocProcessor(this.exceptionName,this.contextCode).extractInputDocumentInfo();
		this.Fragments = HCFIndexManager.readAllFragments();
	}
	
	protected ArrayList<ECodeFragment> getMethodCountMatch(ArrayList<ECodeFragment> fragments)
	{
		//code for getting method count matches
		for(ECodeFragment fragment:fragments){
			ArrayList<String> methodcalled=fragment.methodCalled;
			for(String method:methodcalled){
				if(queryFragment.methodCalled.contains(method)){
					fragment.matchedMethod++;
				}
			}
		}
		ArrayList<ECodeFragment> list1=new ArrayList<>();
		list1.addAll(fragments);
		
		Collections.sort(list1,new Comparator<ECodeFragment>() {
			@Override
			public int compare(ECodeFragment o1, ECodeFragment o2) {
				// TODO Auto-generated method stub
				Double v1=o1.matchedMethod;
				Double v2=o2.matchedMethod;
				return v2.compareTo(v1);
			}
		});
		//return sorted object
		return list1;
	}
	
	protected ArrayList<ECodeFragment> getMethodMatchRatio(ArrayList<ECodeFragment> fragments)
	{
		//code for getting method count matches
		for(ECodeFragment fragment:fragments){
			ArrayList<String> methodcalled=fragment.methodCalled;
			for(String method:methodcalled){
				if(queryFragment.methodCalled.contains(method)){
					fragment.matchedMethod++;
				}
			}
			fragment.normmatchedMethod=fragment.matchedMethod/fragment.methodCalled.size();
			//if(fragment.normmatchedMethod<0.4){ //discarding less than norm methods less than .4
			//	fragments.remove(fragment);
			//}
		}
		
		ArrayList<ECodeFragment> list2=new ArrayList<>();
		list2.addAll(fragments);
		
		Collections.sort(list2,new Comparator<ECodeFragment>() {
			@Override
			public int compare(ECodeFragment o1, ECodeFragment o2) {
				// TODO Auto-generated method stub
				Double v1=o1.normmatchedMethod;
				Double v2=o2.normmatchedMethod;
				return v2.compareTo(v1);
			}
		});
		//return sorted object
		return list2;
	}
	
	protected ArrayList<ECodeFragment> getUsesCountMatch(ArrayList<ECodeFragment> fragments)
	{
		//code for getting method count matches
		for(ECodeFragment fragment:fragments){
			ArrayList<String> usedVars=fragment.variableTypes;
			for(String variable:usedVars){
				if(queryFragment.variableTypes.contains(variable)){
					fragment.matchedVariableTypes++;
				}
			}
		}
		
		ArrayList<ECodeFragment> list3=new ArrayList<>();
		list3.addAll(fragments);
		
		Collections.sort(list3,new Comparator<ECodeFragment>() {
			@Override
			public int compare(ECodeFragment o1, ECodeFragment o2) {
				// TODO Auto-generated method stub
				Double v1=o1.matchedVariableTypes;
				Double v2=o2.matchedVariableTypes;
				return v2.compareTo(v1);
			}
		});
		//return sorted object
		return list3;
	}
	
	protected void calculateResultantExamples() {
		// code for getting the final results
		ArrayList<ECodeFragment> list1 = getMethodCountMatch(this.Fragments);
		ArrayList<ECodeFragment> list2 = getMethodMatchRatio(this.Fragments);
		ArrayList<ECodeFragment> list3 = getUsesCountMatch(this.Fragments);
		HashMap<Integer, Integer> freqcount = new HashMap<>();
		int allowedLimit=list1.size()<MAXLIMIT?list1.size():MAXLIMIT;
		for (int i = 0; i < allowedLimit; i++) {
			// extracting the IDs
			int item1 = list1.get(i).FragmentID;
			int item2 = list2.get(i).FragmentID;
			int item3 = list3.get(i).FragmentID;
			if (freqcount.containsKey(item1)) {
				int count = freqcount.get(item1);
				count++;
				freqcount.put(item1, count);
			} else
				freqcount.put(item1, 1);

			if (freqcount.containsKey(item2)) {
				int count = freqcount.get(item2);
				count++;
				freqcount.put(item2, count);
			} else
				freqcount.put(item1, 1);

			if (freqcount.containsKey(item3)) {
				int count = freqcount.get(item3);
				count++;
				freqcount.put(item3, count);
			} else
				freqcount.put(item3, 1);
		}
		// now sort the Hash Map
		List<Entry<Integer, Integer>> masterlist = new LinkedList<>(freqcount.entrySet());
		Collections.sort(masterlist,new Comparator<Map.Entry<Integer, Integer>>() {
					@Override
					public int compare(Entry<Integer, Integer> o1,
							Entry<Integer, Integer> o2) {
						// TODO Auto-generated method stub
						Integer v2 = o2.getValue();
						Integer v1 = o1.getValue();
						return v2.compareTo(v1);
					}
				});
		// now save the results
		String fileName = StaticData.Surf_Data_Base + "/existing/strathcona/"
				+ exceptionID + ".txt";
		try {
			FileWriter fwriter = new FileWriter(new File(fileName));
			for (Map.Entry<Integer, Integer> entry : masterlist) {
				fwriter.write(entry.getKey() + "\t" + entry.getValue()+"\n");
			}
			fwriter.close();
		} catch (Exception exc) {

		}
	}
	
	public static void main(String[] args) {
		for (int i = 1; i <= 150; i++) {
			int exceptionID = i;
			if (new File(StaticData.Surf_Data_Base + "/querycodes/" + i
					+ ".txt").exists() == false) {
				continue;
			}
			StrathconaMatcher matcher = new StrathconaMatcher(exceptionID);
			matcher.calculateResultantExamples();
			System.out.println("Done:"+i);
		}
	}
}
