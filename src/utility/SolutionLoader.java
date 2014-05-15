package utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import core.StaticData;

public class SolutionLoader {
	
	public static HashMap<Integer, ArrayList<Integer>> loadSolutionIDs()
	{
		HashMap<Integer, ArrayList<Integer>> solMap=new HashMap<>();
		String solfile=StaticData.Surf_Data_Base+"/solution/solution.txt";
		try{
		Scanner scanner=new Scanner(new File(solfile));
		while(scanner.hasNext()){
			String line=scanner.nextLine();
			String[] parts=line.split("\\s+");
			int key=Integer.parseInt(parts[0].trim());
			ArrayList<Integer> solIDs=new ArrayList<>();
			for(int i=1;i<parts.length;i++){
				int ID=Integer.parseInt(parts[i].trim());
				solIDs.add(ID);
			}
			//now add in the HashMap
			solMap.put(key, solIDs);
		}
		scanner.close();
		}catch(Exception exc){
			//handle the exception
		}
		return solMap;
	}
	
	public static ArrayList<Integer> getSolutionIDs(int exceptionID)
	{
		HashMap<Integer, ArrayList<Integer>> mymap=loadSolutionIDs();
		//returning the first ID
		return mymap.get(exceptionID); //.get(0).intValue();
	}
	
	public static int getSolutionID(int exceptionID)
	{
		HashMap<Integer, ArrayList<Integer>> mymap=loadSolutionIDs();
		//returning the first ID
		return mymap.get(exceptionID).get(0).intValue();
	}
	

}
