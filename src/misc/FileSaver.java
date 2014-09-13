package misc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import core.StaticData;

public class FileSaver {
	
	public static void main(String[] args) throws IOException{
		String ccontext1=StaticData.Surf_Data_Base+"/ccontext1";
		File f=new File(ccontext1);
		File[] files=f.listFiles();
		String destDir=StaticData.Surf_Data_Base+"/ccontext";
		for(File f2:files){
			String id=f2.getName().split("\\.")[0];
			String newFileName=destDir+"/"+id+".java";
			Files.copy(f2.toPath(), new File(newFileName).toPath());
		}
	}
}
