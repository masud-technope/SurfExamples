package misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import core.CodeFragment;
import core.StaticData;

public class MyCodeFragMaker {

	/**
	 * @param args
	 */
	
	protected static void saveTheCode(String serFileName, String destFileName){
		//code for saving the file
		try{
			String completeCode=new String();
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(new File(serFileName)));
			CodeFragment codefragment=(CodeFragment)ois.readObject();
			completeCode=codefragment.CompleteCode;
			FileWriter fwriter=new FileWriter(new File(destFileName));
			fwriter.write(completeCode);
			fwriter.close();
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fragIndex = StaticData.Surf_Data_Base + "/fragmentsIndex/fragmentsIndex";
		String fragGH = StaticData.Surf_Data_Base + "/fragments-gh";
		File fdir = new File(fragIndex);
		if (fdir.isDirectory()) {
			File[] dirs = fdir.listFiles();
			for (File f2 : dirs) {
				String destFolderName = fragGH + "/" + f2.getName();
				File f22 = new File(destFolderName);
				if (!f22.exists())
					f22.mkdir();

				if (f2.isDirectory()) {
					File[] codes = f2.listFiles();
					int length = codes.length;
					for (int i = 0; i < length; i++) {
						String serFileName = fragIndex + "/" + f2.getName()
								+ "/" + i + ".ser";
						String destFileName = fragGH + "/" + f22.getName()
								+ "/" + i + ".txt";
						saveTheCode(serFileName, destFileName);
					}
				}
				System.out.println("Completed:" + f2.getName());
			}
		}
	}
}
