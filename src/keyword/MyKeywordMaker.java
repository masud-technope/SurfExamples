package keyword;

import java.util.HashMap;

import utility.MyItemSorter;
import visitor.CodeObject;
import visitor.Dependence;
import core.CodeFragment;

public class MyKeywordMaker {
	
	CodeFragment codeFragment;
	public MyKeywordMaker(CodeFragment codeFragment)
	{
		this.codeFragment=codeFragment;
	}
	
	public CodeFragment getLabeledResult()
	{
		this.codeFragment.matchedKeywords=getRepresentativeKeywords();
		return this.codeFragment;
	}
	
	protected String getRepresentativeKeywords()
	{
		//code for getting the keywords
		String keywords=new String();
		try {
			// The map contains the actions and interactions of a class
			HashMap<CodeObject, Integer> LinkCount = new HashMap<>();
			// collecting from simple codeobject dict
			for (CodeObject cobject : codeFragment.codeObjectMap.values()) {
				int fieldcount = cobject.fields.size();
				int methodcount = cobject.methods.size();
				if (LinkCount.containsKey(cobject)) {
					int count = LinkCount.get(cobject).intValue();
					count += fieldcount + methodcount;
					LinkCount.put(cobject, count);
				} else {
					int count = fieldcount + methodcount;
					LinkCount.put(cobject, count);
				}
			}
			// collecting from dependencies
			for (Dependence dep : codeFragment.dependencies) {
				CodeObject fromObj = dep.fromObject;
				CodeObject toObject = dep.destObject;
				// adding from object
				if (LinkCount.containsKey(fromObj)) {
					int count = LinkCount.get(fromObj).intValue();
					count++;
					LinkCount.put(fromObj, count);

				} else {
					LinkCount.put(fromObj, 1);
				}
				// adding dest object
				if (LinkCount.containsKey(toObject)) {
					int count = LinkCount.get(toObject).intValue();
					count++;
					LinkCount.put(toObject, count);
				} else {
					LinkCount.put(toObject, 1);
				}
			}
			// now sort the item
			HashMap<CodeObject, Integer> sorted = MyItemSorter
					.sortCodeObjectMap(LinkCount);
			//collect top 5 items
			int count=0;
			for (CodeObject cobject : sorted.keySet()) {
				keywords += cobject.className + " ";
				count++;
				if(count==5)break;
			}
			
		} catch (Exception exc) {
			// handle the exception`
		}
		return keywords;
		
	}
	
}
