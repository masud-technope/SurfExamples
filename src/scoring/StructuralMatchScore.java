package scoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import visitor.CodeObject;
import visitor.Dependence;
import weka.core.pmml.TargetMetaInfo;
import core.CodeFragment;
import core.ScoreWeights;

public class StructuralMatchScore {

	ArrayList<CodeFragment> Fragments;
	CodeFragment queryFragment;
	
	public StructuralMatchScore(CodeFragment queryFragment,ArrayList<CodeFragment> Fragments)
	{
		//initialization
		this.queryFragment=queryFragment;
		this.Fragments=Fragments;
	}
	
	protected int getFieldMatch(HashSet<String> queryFields, HashSet<String> targetFields)
	{ 
		// code for calculating field match
		int fieldcount = 0;
		for (String field : queryFields) {
			if (targetFields.contains(field))
				fieldcount++;
		}
		return fieldcount;
	}
	
	protected int getMethodMatch(HashSet<String> queryMethods, HashSet<String> targetMethods)
	{
		// code for calculating method match
		int methodcount = 0;
		for (String method : queryMethods) {
			if (targetMethods.contains(method)) {
				methodcount++;
			}
		}
		return methodcount++;
	}

	protected double getCodeObjectFieldMatchScore(CodeFragment targetFragment)
	{
		double fieldMatchScore = 0;
		try {
			HashMap<String, CodeObject> queryMap = queryFragment.codeObjectMap;
			HashMap<String, CodeObject> taregtMap = targetFragment.codeObjectMap;
			double totNormalizedFieldCount = 0;

			for (String key : queryMap.keySet()) {
				CodeObject queryCodeObject = queryMap.get(key);
				CodeObject targetCodeObject = getEquivalentCodeObject(key,
						taregtMap);
				if (targetCodeObject != null) {
					int fieldcount = getFieldMatch(queryCodeObject.fields,
							targetCodeObject.fields);
					double normFieldCount = queryCodeObject.fields.size() > 0 ? ((double) fieldcount / queryCodeObject.fields
							.size()) : 0;
					// accumulating normalized counts
					totNormalizedFieldCount += normFieldCount;
				}
			}
			fieldMatchScore = totNormalizedFieldCount;
		} catch (Exception exc) {
			// handle the exception
		}
		return fieldMatchScore;
	}
	
	
	protected double getCodeObjectMethodMatchScore(CodeFragment targetFragment)
	{
		//code for getting code object matching
		double methodMatchScore=0;
		try{
			HashMap<String, CodeObject> queryMap=queryFragment.codeObjectMap;
			HashMap<String, CodeObject> taregtMap=targetFragment.codeObjectMap;			
			double totNormalizedMethodCount=0;

			for(String key:queryMap.keySet()){
				CodeObject queryCodeObject=queryMap.get(key);
				CodeObject targetCodeObject=getEquivalentCodeObject(key, taregtMap);
				if(targetCodeObject!=null){
					int methodcount=getMethodMatch(queryCodeObject.methods, targetCodeObject.methods);
					double normMethCount=queryCodeObject.methods.size()>0?((double)methodcount/queryCodeObject.methods.size()):0;
					//accumulating normalized counts
					totNormalizedMethodCount+=normMethCount;
				}
			}
			methodMatchScore=totNormalizedMethodCount;
		}catch(Exception exc){
			//handle the exception
		}
		return methodMatchScore;
	}
	
	protected double getCodeObjectMatchScore(CodeFragment targetFragment)
	{
		//code for getting total object matching
		double objectMatchScore=0;
		try{
			HashMap<String, CodeObject> queryMap=queryFragment.codeObjectMap;
			HashMap<String, CodeObject> taregtMap=targetFragment.codeObjectMap;
			int totalMatch=0;
			for(String key:queryMap.keySet()){
				if(taregtMap.containsKey(key)){
					totalMatch++;
				}
			}
			//just consider the matched object
			objectMatchScore=totalMatch;
			/*if(queryMap.keySet().size()>0){
				objectMatchScore=((double)totalMatch/queryMap.keySet().size());
			}*/
		}catch(Exception exc){
			//handle the exception
		}
		return objectMatchScore;
	}
	
	
	protected double getDependencyMatchScore(CodeFragment targetFragment)
 {
		double dependencyMatchScore = 0;
		try {
			ArrayList<Dependence> queryDependencies = this.queryFragment.dependencies;
			ArrayList<Dependence> targetDependencies = targetFragment.dependencies;
			for (Dependence dep : queryDependencies) {
				for (Dependence tdep : targetDependencies) {
					// now compare the edge if they are same or equivalent
					double match = getEdgeMatchScore(dep, tdep);
					dependencyMatchScore += match;
				}
			}
		} catch (Exception exc) {
		}
		return dependencyMatchScore;
	}
	
	public ArrayList<CodeFragment> collectStructuralScores()
	{
		// code for collecting structural similarity scores
		for (CodeFragment targetFragment : this.Fragments) {
			double codeObjectFieldMatchScore = getCodeObjectFieldMatchScore(targetFragment);
			double codeObjectMethodMatchScore=getCodeObjectMethodMatchScore(targetFragment);
			double codeObjectMatchScore=getCodeObjectMatchScore(targetFragment);
			double dependencyMatchScore = getDependencyMatchScore(targetFragment);
			targetFragment.MethodMatchScore=codeObjectMethodMatchScore;
			targetFragment.FieldMatchScore=codeObjectFieldMatchScore;
			targetFragment.CodeObjectMatchScore=codeObjectMatchScore;
			targetFragment.DependencyMatchScore = dependencyMatchScore;
		}
		// returning computed results
		return this.Fragments;
	}
	
	
	protected CodeObject getEquivalentCodeObject(String qkey, HashMap<String, CodeObject> taregtMap)
 {
		// code for getting same object or super object
		CodeObject targetCodeObject = null;
		if (taregtMap.containsKey(qkey)) {
			targetCodeObject = taregtMap.get(qkey);
		} else {
			for (String key : taregtMap.keySet()) {
				if (qkey.endsWith(key)) { // if qkey is a subclass of key
					targetCodeObject = taregtMap.get(key);
					break;
				}
			}
		}
		// returning target code object
		return targetCodeObject;
	}
	
	protected boolean checkSuperObject(CodeObject queryObject, CodeObject targetObject)
	{
		//code for checking if target object is a superclass of query object
		boolean response=false;
		if(!queryObject.className.equals(targetObject.className))
		if(queryObject.className.endsWith(targetObject.className))response=true; //ByteArrayInputStream InputStream 
		return response;
	}
	
	protected boolean checkSameCodeObject(CodeObject queryObject, CodeObject targetObject)
	{
		//code for checking if two objects are the same codeobject
		boolean response=false;
		if(queryObject.className.equals(targetObject.className))response=true;
		return response;
	}
	
	protected double getEdgeMatchScore(Dependence qdep, Dependence tdep)
	{
		double edgeMatchScore = 0;
		try {
			// query dependence
			CodeObject node1 = qdep.fromObject;
			CodeObject node2 = qdep.destObject;
			String depPointName = qdep.dependenceName;
			// target dependence
			CodeObject tnode1 = tdep.fromObject;
			CodeObject tnode2 = tdep.destObject;
			String tdepPointName = tdep.dependenceName;
			if (checkSameCodeObject(node1, tnode1)) {
				if (checkSameCodeObject(node2, tnode2)) {
					if (depPointName.equals(tdepPointName)) {
						edgeMatchScore = ScoreWeights.CodeObjectDependencyCompleteMatchWeight;
					} else {
						edgeMatchScore = ScoreWeights.CodeObjectDependencyPartialMatchWeight;
					}
				} else if (checkSuperObject(node2, tnode2)) {
					if (depPointName.equals(tdepPointName)) {
						edgeMatchScore = ScoreWeights.CodeObjectDependencyCompleteMatchWeight;
					} else {
						edgeMatchScore = ScoreWeights.CodeObjectDependencyPartialMatchWeight;
					}
				} else
					edgeMatchScore = 0;
			} else
				edgeMatchScore = 0;
		} catch (Exception exc) {
			// handle the exception
		}
		return edgeMatchScore;
	}
}
