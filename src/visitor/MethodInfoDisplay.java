package visitor;

import java.util.ArrayList;
import java.util.HashMap;

public class MethodInfoDisplay {
	
	public static void showMethodDetails(HashMap<String,CodeObject> codeObjectMap)
	{
		// code for showing method details
		for (String key : codeObjectMap.keySet()) {
			CodeObject codeObject = codeObjectMap.get(key);
			System.out.println(codeObject.className);
			System.out.println("Fields:");
			for (String field : codeObject.fields) {
				System.out.println(field);
			}
			System.out.println("Methods:");
			for (String method : codeObject.methods) {
				System.out.println(method);
			}
			System.out.println("======================");
			
		}
	}
	public static void showDependencies(ArrayList<Dependence> dependencies)
	{
		//code for showing the dependencies
		for(Dependence ddence:dependencies){
			try{
			System.out.println(ddence.fromObject.className+"\t"+ddence.destObject.className+"\t"+ddence.dependenceName);
			}catch(Exception ec){}
		}
	}
	

}
