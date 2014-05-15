package misc;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import core.StaticData;

public class ShowQueryCode {
	
	public static void main(String[] args){
		//show me the query codes
		try{
			String folderName=StaticData.Surf_Data_Base+"/ccontext";
			String outFile=StaticData.Surf_Data_Base+"/query/allquery.txt";
			FileWriter fwriter=new FileWriter(new File(outFile));
			for(int i=1;i<=150;i++){
				String singleFile=folderName+"/"+i+".txt";
				File f2=new File(singleFile);
				if(!f2.exists())continue;
				Scanner scanner=new Scanner(f2);
				String content=new String();
				while(scanner.hasNextLine()){
					content+=scanner.nextLine()+"\n";
				}
				scanner.close();
				fwriter.write("###############"+i+"###############\n");
				fwriter.write(content+"\n");
			}
			fwriter.close();
		}catch(Exception exc){
			
		}
	}
}
