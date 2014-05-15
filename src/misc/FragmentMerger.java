package misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import core.StaticData;

public class FragmentMerger {

	protected static String getContextCode(int exceptionID) {
		String content = new String();
		try {
			String ccontext = StaticData.Surf_Data_Base + "/ccontext/"
					+ exceptionID + ".txt";
			Scanner scanner = new Scanner(new File(ccontext));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				content += line + "\n";
			}
		} catch (Exception exc) {
			// handle the exception
		}
		return content;
	}

	public static void main(String[] args) {

		//for (int i = 1; i <= 150; i++) {
		    int i=105;
			int exceptionID = i;
			String fragmentFolder = StaticData.Surf_Data_Base + "/fragments/"
					+ exceptionID;
			String mergedFile = StaticData.Surf_Data_Base + "/fmerged/"
					+ exceptionID + ".java";
			try {

				File fDir = new File(fragmentFolder);
				if (fDir.isDirectory() && fDir.exists()) {

					String contextcode = getContextCode(exceptionID);
					FileWriter fwriter = new FileWriter(new File(mergedFile));
					fwriter.write(contextcode + "\n=========================\n");
					File[] files = fDir.listFiles();
					int total=files.length;
					for (int j=0;j<total;j++) {
						try {
							File f2=new File(fragmentFolder+"/"+j+".txt");
							String content = new String("\n");
							BufferedReader breader = new BufferedReader(
									new FileReader(f2));
							String line = new String();
							fwriter.write("######################## "
									+ f2.getName()
									+ " ########################");
							while ((line = breader.readLine()) != null) {
								content += line + "\n";
							}
							fwriter.write(content);

						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					fwriter.close();
					System.out.println("Fragment merged:" + exceptionID);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	//}
}
