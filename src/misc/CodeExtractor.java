package misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import core.CodeFragment;
import core.StaticData;

public class CodeExtractor {

	/**
	 * @param args
	 */
	
	static void saveCodeFragment(String fileName, String code){
		//code for saving the content
		try {
			FileWriter fwriter=new FileWriter(new File(fileName));
			fwriter.write(code);
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int caseID=1;
		for(int i=caseID;i<=150;i++){
			String indexFolder=StaticData.Surf_Data_Base+"/fragmentsIndex/"+i;
			File f1=new File(indexFolder);
			String fragmentFolder=StaticData.Surf_Data_Base+"/fragments/"+i;
			File f2=new File(fragmentFolder);
			if(f1.exists()){
				f2.mkdir();
				//now create the code fragments from them
				File[] files=f1.listFiles();
				for(File f3:files){
					try {
						ObjectInputStream ois=new ObjectInputStream(new FileInputStream(f3));
						CodeFragment cf=(CodeFragment) ois.readObject();
						String code=cf.CompleteCode;
						String fileName=fragmentFolder+"/"+f3.getName().split("\\.")[0];
						saveCodeFragment(fileName, code);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			System.out.println("Completed for case:"+i);	
			}
		}
	}

}
