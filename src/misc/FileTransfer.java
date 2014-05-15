package misc;

import java.io.File;
import java.nio.file.Files;

import core.StaticData;

public class FileTransfer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String ccontext=StaticData.Surf_Data_Base+"/ccontext";
		String strace2=StaticData.Surf_Data_Base+"/strace";
		String strace=StaticData.Lucene_Data_Base+"/completeds/strace";
		File codeDir=new File(ccontext);
		if(codeDir.isDirectory()){
			File[] codes=codeDir.listFiles();
			try{
			for(File codeFile:codes){
				String fileName=codeFile.getName();
			    String srcFile=strace+"/"+fileName;	
			    String destFile=strace2+"/"+fileName;
			    Files.copy(new File(srcFile).toPath(), new File(destFile).toPath());
			}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		
		
		

	}

}
