package misc;

import java.io.File;

import core.StaticData;

public class MisMatchChecker {
	
	public static void main(String args[]){
		for(int i=1;i<=150;i++){
		String folder1=StaticData.Surf_Data_Base+"/fragmentsIndex/"+i;
		String folder2=StaticData.Surf_Data_Base+"/hIndex/"+i;
		try{
		File f1=new File(folder1);
		File f2=new File(folder2);
		System.out.println(i+": "+f1.list().length+" "+f2.list().length);
		}catch(Exception exc){}
		}
	}
}
