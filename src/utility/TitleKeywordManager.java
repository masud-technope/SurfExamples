package utility;

import java.util.HashMap;
import visitor.CodeObject;
import visitor.Dependence;
import core.CodeFragment;

public class TitleKeywordManager {
	
	CodeFragment codeFragment;
	String keywordTitle;
	final int maxkeywords=5;
	public TitleKeywordManager(CodeFragment codeFragment)
	{
		this.codeFragment=codeFragment;
		this.keywordTitle=new String();
	}
	
	public String getFragmentTitle()
	{
		//code for getting the fragment title
		HashMap<CodeObject, Integer> LinkCount = new HashMap<>();
		// collecting from simple codeobject dict
		for (CodeObject cobject : this.codeFragment.codeObjectMap.values()) {
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
		for (Dependence dep : this.codeFragment.dependencies) {
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
		//select top 5
		int count=0;
		for (CodeObject cobject : sorted.keySet()) {
			this.keywordTitle += cobject.className + " ";
			count++;
			if(count==maxkeywords)break;
		}
		
		return this.keywordTitle;
	}
	
	
	
	

}
