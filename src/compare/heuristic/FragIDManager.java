package compare.heuristic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import core.CodeFragment;
import core.StaticData;

public class FragIDManager {
	public static void main(String[] args){
		
		int exceptionID=28;
		int startID=7;
		
		
		String hindex=StaticData.Surf_Data_Base+"/fragmentsIndex/"+exceptionID;
		int total=new File(hindex).list().length;
		for(int i=startID;i<total;i++){
			String fileName=hindex+"/"+i+".ser";
			CodeFragment ecf=null;
			try {
				FileInputStream fis=new FileInputStream(new File(fileName));
				ObjectInputStream ois=new ObjectInputStream(fis);
				ecf=(CodeFragment)ois.readObject();
				ois.close();
				fis.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//now update the id
			ecf.FragmentID=i;
			try{
				FileOutputStream fos=new FileOutputStream(new File(fileName));
				ObjectOutputStream oos=new ObjectOutputStream(fos);
				oos.writeObject(ecf);
				oos.close();
				fos.close();
			}catch(Exception ex){
				
			}
		}
	}
}
