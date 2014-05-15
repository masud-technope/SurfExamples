package core;

import java.io.Serializable;
import java.util.ArrayList;

public class ECodeFragment implements Serializable{
	
	public int FragmentID=-1;
	public String ExceptionName;
	public String sourceFileID=new String();
	public String CompleteCode=new String();
	public ArrayList<String> variableTypes;
	public ArrayList<String> methodCalled;
	public ArrayList<String> handles;
	public int handledExceptionLevel=0;
	
	public ECodeFragment()
	{
		variableTypes=new ArrayList<>();
		methodCalled=new ArrayList<>();
		handles=new ArrayList<>();
	}
	//scores
	public double weightedHandleScore=0;
	public double weightedVariableTypeScore=0;
	public double weightedMethodCallScore=0;
	
	public double totalHeuristicScore=0;
	
	
	//Strathcona heuristic scores
	public double matchedMethod=0;
	public double normmatchedMethod=0;
	public double matchedUnmatchedmethodRatio=0;
	public double matchedVariableTypes=0;
	public double normmatchedVariableTypes=0;
	public int heuristicFrequency=0;
	public double normTotalScore=0;
	
}
