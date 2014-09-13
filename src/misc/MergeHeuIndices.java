package misc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import core.StaticData;

public class MergeHeuIndices {
	
	
	
	
	public static void main(String[] args){
		int currentCounter=0;
		for(int i=1;i<=150;i++){
			if(new File(StaticData.Surf_Data_Base+"/querycodes/"+i+".txt").exists()==false){
				continue;
			}
			int exceptionID=i;
			//String folderName=StaticData.Surf_Data_Base+"/fragmentsIndex/"+exceptionID;
			String folderName=StaticData.Surf_Data_Base+"/fragments/"+exceptionID;
			File[] files=new File(folderName).listFiles();
			int succeeded=0;
			for(File f:files){
				int destFile=Integer.parseInt(f.getName().split("\\.")[0].trim());
				destFile=destFile+currentCounter;
				//String destFileName=StaticData.Surf_Data_Base+"/fragmentsIndexAll/"+destFile+".ser";
				String destFileName=StaticData.Surf_Data_Base+"/fragmentsAll/"+destFile+".txt";
				try {
					Files.copy(f.toPath(), new File(destFileName).toPath());
					succeeded++;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			currentCounter+=succeeded;
		}
	}
}
