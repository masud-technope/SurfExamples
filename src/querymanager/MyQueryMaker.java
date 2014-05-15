package querymanager;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import structural.InputDocProcessor;
import utility.MyItemSorter;
import visitor.CodeObject;
import visitor.Dependence;
import core.CodeFragment;
import core.StaticData;

public class MyQueryMaker {
	
	int exceptionID;
	String queryContextCode;
	String exceptionName;
	
	public MyQueryMaker(int exceptionID, String exceptionName) {
		// initialization
		this.exceptionID = exceptionID;
		this.queryContextCode=getContextCode();
		this.exceptionName=exceptionName;
	}
	
	protected String getContextCode() {
		// code for collecting the code
		String content = new String();
		try {
			String ccontextFile = StaticData.Surf_Data_Base + "/ccontext/"
					+ exceptionID + ".txt";
			File f = new File(ccontextFile);
			Scanner scanner = new Scanner(f);
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				content += line + "\n";
			}
		} catch (Exception exc) {
			// handle the exception
		}
		return content;
	}
	
	public String getGitHubSearchQuery() {
		// code for GitHub query
		String query = new String();
		try {
			InputDocProcessor processor = new InputDocProcessor(exceptionName,
					queryContextCode);
			CodeFragment queryFragment = processor.extractInputDocumentInfo();
			// The map contains the actions and interactions of a class
			HashMap<CodeObject, Integer> LinkCount = new HashMap<>();

			// collecting from simple codeobject dict
			for (CodeObject cobject : queryFragment.codeObjectMap.values()) {
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
			for (Dependence dep : queryFragment.dependencies) {
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
			for (CodeObject cobject : sorted.keySet()) {
				query += cobject.className + " ";
			}
			query = exceptionName + " " + query.trim();
		} catch (Exception exc) {
			// handle the exception`
		}
		return query;
	}
	public static void main(String[] args){
		int exceptionID=113;
		String exceptionName="IIOException";
		MyQueryMaker maker=new MyQueryMaker(exceptionID, exceptionName);
		String query=maker.getGitHubSearchQuery();
		System.out.println("Returned query: "+query);
	}
}
