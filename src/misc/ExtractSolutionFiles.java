package misc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import core.StaticData;
import utility.SolutionLoader;

public class ExtractSolutionFiles {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<Integer, ArrayList<Integer>> solList=SolutionLoader.loadSolutionIDs();
		int totalsol=0;
		for(Integer key:solList.keySet()){
			int exceptionID=key.intValue();
			String sourceFolder=StaticData.Surf_Data_Base+"/fragments/"+exceptionID;
			String destFolder=StaticData.Surf_Data_Base+"/solution/solfiles/"+exceptionID;
			File fdir=new File(destFolder);
			if(!fdir.exists())fdir.mkdir();
			ArrayList<Integer> sols=solList.get(key);
			totalsol+=sols.size();
			for(Integer fileID:sols){
				try{
				String srcFile=sourceFolder+"/"+fileID+".txt";
				String destFile=destFolder+"/"+fileID+".txt";
				Files.copy(new File(srcFile).toPath(), new File(destFile).toPath());
				}catch(Exception exc){
					
				}
			}
			System.out.println("Saved for :"+key);
		}
		System.out.println("Total solution:"+totalsol);
	}

}
